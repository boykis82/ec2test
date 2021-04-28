package org.caltech.miniswingpilot.domain;


import com.fasterxml.jackson.databind.ser.Serializers;
import jdk.vm.ci.meta.Local;
import lombok.*;
import org.caltech.miniswingpilot.exception.DataIntegrityViolationException;
import org.caltech.miniswingpilot.exception.IllegalServiceStatusException;
import org.caltech.miniswingpilot.exception.InvalidInputException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "svc")
public class Svc extends BaseEntity {
    @Id
    @Column(name = "svc_mgmt_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20, nullable = false)
    private String svcNum;

    @Column(nullable = false)
    private LocalDate svcScrbDt;

    @Column
    private LocalDate svcTermDt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=1)
    private SvcCd svcCd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=2)
    private SvcStCd svcStCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_num")
    private Cust cust;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_prod_id")
    private Prod feeProd;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "svc", cascade = CascadeType.ALL)
    private List<SvcProd> svcProds = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "svc", cascade = CascadeType.ALL)
    private List<SvcStHst> svcStHsts = new ArrayList<>();

    @Builder
    public Svc(String svcNum, LocalDate svcScrbDt, SvcCd svcCd, SvcStCd svcStCd, Cust cust, Prod feeProd) {
        this.svcNum    = svcNum;
        this.svcScrbDt = svcScrbDt;
        this.svcStCd   = svcStCd;
        this.svcCd     = svcCd;
        this.cust      = cust;
        this.feeProd   = feeProd;
    }

    public int getSvcMgmtNum() {
        return id;
    }

    //-- 서비스 가입
    public void subscribe(LocalDateTime svcScrbDtm) {
        //-- 가입일, 서비스상태 셋팅
        this.svcScrbDt = svcScrbDtm.toLocalDate();
        this.svcStCd = SvcStCd.AC;

        //-- AC 상태 밀어넣고
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.AC, svcScrbDtm) );

        if (feeProd.getSvcProdCd() != SvcProdCd.P1)
            throw new InvalidInputException("신규 가입시에는 기본요금제만 가입할 수 있습니다!");

        //-- 기본요금제 가입
        subscribeBasicProd(feeProd, svcScrbDtm);
    }

    //-- 기본요금제 가입
    private void subscribeBasicProd(Prod basicProd, LocalDateTime scrbDtm) {
        subscribeProduct(basicProd, scrbDtm);
        this.feeProd = basicProd;
    }

    //-- 서비스 해지
    public void terminate(LocalDateTime svcTermDtm) {
        if (this.svcStCd == SvcStCd.TG || this.svcTermDt != null)
            throw new IllegalServiceStatusException("이미 해지되었는데 다시 해지할 수 없음!");

        //-- 해지일, 상태 등 셋팅
        this.svcTermDt = svcTermDtm.toLocalDate();
        this.svcStCd = SvcStCd.TG;

        //-- 상품 다 끊자.
        this.svcProds.forEach(sp -> sp.terminate(svcTermDtm));

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(svcTermDtm);

        //-- 신규 해지 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.TG, svcTermDtm.plusSeconds(1)) );
    }

    //-- 정지
    public void suspend(LocalDateTime suspDtm) {
        if (this.svcStCd != SvcStCd.AC)
            throw new IllegalServiceStatusException("AC상태가 아닌데 정지걸 수 없음!");
        this.svcStCd = SvcStCd.SP;

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(suspDtm);

        //-- 신규 정지 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.SP, suspDtm.plusSeconds(1)) );
    }

    //-- 활성화
    public void activate(LocalDateTime activeDtm) {
        if (this.svcStCd != SvcStCd.SP)
            throw new IllegalServiceStatusException("SP상태가 아닌데 정지해제할 수 없음!");
        this.svcStCd = SvcStCd.AC;

        //-- 최종 서비스상태 이력 끊고,
        terminateLastSvcStHst(activeDtm);

        //-- 신규 AC 이력 끼워넣자.
        this.svcStHsts.add( SvcStHst.createNewSvcStHst(this, SvcStCd.AC, activeDtm.plusSeconds(1)) );
    }

    //-- 최종 이력 종료
    private void terminateLastSvcStHst(LocalDateTime termDtm) {
        this.svcStHsts.stream()
                .filter(SvcStHst::isLastHst)
                .findFirst()
                .orElseThrow( () -> {
                    throw new DataIntegrityViolationException("최종 서비스상태 이력이 없음!");
                } )
                .terminate(termDtm);
    }

    //-- 주어진 상품 가입
    public void subscribeProduct(Prod prod, LocalDateTime scrbDateTime) {
        //-- 동일한 상품 가입되어 있으면 예외 처리
        if ( this.svcProds.stream().anyMatch(svcProd -> svcProd.isSubscribingProd(prod)) )
            throw new InvalidInputException( String.format("동일한 상풍 가입 못함! svc_mgmt_num = [%d], prod_id = [%s]", getSvcMgmtNum(), prod.getProdId()) );

        //-- svc_prod_Cd가 1,2 면 기존 이력 종료해야 함
        if (prod.getSvcProdCd() == SvcProdCd.P1 || prod.getSvcProdCd() == SvcProdCd.P2) {
            //-- svcProd에서 svcProdCd가 1,2인 거 찾아서 이력 종료 후 가입
            List<SvcProd> prevSvcProds = this.svcProds.stream()
                    .filter( svcProd -> svcProd.getProd().getSvcProdCd() == prod.getSvcProdCd() && svcProd.isActive() )
                    .collect(Collectors.toList());

            //-- 살아있는 기본요금제나 부가요금제가 1개 초과면 데이터 정합성 오류
            if (prevSvcProds.size() > 1)
                throw new DataIntegrityViolationException(String.format("사용중인 기본요금제/부가요금제가 여러 개임! svc_mgmt_num = [%d], svc_prod_Cd = [%s]", getSvcMgmtNum(), prod.getSvcProdCd().getValue()));
            //-- 살아있는 기본요금제나 부가요금제 있으면 종료
            else if (prevSvcProds.size() == 1)
                prevSvcProds.get(0).terminate(scrbDateTime.minusSeconds(1));

            //-- 기본요금제 가입 시에는 feeProd 교체
            if (prod.getSvcProdCd() == SvcProdCd.P1)
                feeProd = prod;
        }
        //-- 신규 svc prod 생성
        this.svcProds.add( SvcProd.createNewSvcProd(this, prod, scrbDateTime) );
    }

    //-- 상품 해지
    public void terminateProduct(int svcProdId, LocalDateTime termDtm) {
        SvcProd termSvcProd = this.svcProds.stream()
                .filter(sp -> sp.getId() == svcProdId && sp.isActive())
                .findFirst()
                .orElseThrow( () -> new InvalidInputException("존재하지 않는 svc_prod_id! svc_mgmt_num = " + getSvcMgmtNum() + " , svcprodid = " + svcProdId) );

        if (SvcProdCd.P1 == termSvcProd.getProd().getSvcProdCd())
            throw new InvalidInputException("기본요금제는 해지 불가!");

        termSvcProd.terminate(termDtm);
    }

    //-- 살아있는 상품만 조회
    public List<SvcProd> getActiveProdsOnly() {
        return this.svcProds.stream()
                .filter(SvcProd::isActive)
                .collect(Collectors.toList());
    }
}
