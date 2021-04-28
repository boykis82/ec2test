package org.caltech.miniswingpilot.domain;

import org.caltech.miniswingpilot.exception.DataIntegrityViolationException;
import org.caltech.miniswingpilot.exception.InvalidInputException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SvcTest {
    @Autowired
    SvcRepository svcRepository;

    @Autowired
    CustRepository custRepository;

    @Autowired
    ProdRepository prodRepository;

    @Autowired
    SvcProdRepository svcProdRepository;

    List<Cust> custs;
    List<Prod> prods_p1;
    List<Prod> prods_p2;
    List<Prod> prods_p3;

    @Before
    public void setUp() {
        //-- cust pool
        custs = TestDataFactory.createManyCusts();
        custRepository.saveAll( custs );

        //-- prod pool
        prods_p1 = TestDataFactory.createManyProds_1();
        prodRepository.saveAll( prods_p1 );
        prods_p2 = TestDataFactory.createManyProds_2();
        prodRepository.saveAll( prods_p2 );
        prods_p3 = TestDataFactory.createManyProds_3();
        prodRepository.saveAll( prods_p3 );
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
        custRepository.deleteAll();
        prodRepository.deleteAll();
    }

    @Test
    public void test_서비스를_서비스번호와_서비스상태로_검색() {
        List<Svc> svcs = new ArrayList<>();
        svcs.add(Svc.builder()
                .svcCd(SvcCd.C)
                .svcNum("01012345678")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.AC)
                .cust(custs.get(0))
                .build());
        svcs.add(Svc.builder()
                .svcCd(SvcCd.I)
                .svcNum("7132423123")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.SP)
                .cust(custs.get(1))
                .build());
        svcs.add(Svc.builder()
                .svcCd(SvcCd.I)
                .svcNum("01123231232")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.AC)
                .cust(custs.get(2))
                .build());
        svcs.add(Svc.builder()
                .svcCd(SvcCd.I)
                .svcNum("7231311231")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.TG)
                .cust(custs.get(3))
                .build());

        svcRepository.saveAll(svcs);

        //-- 존재하는 서비스 테스트
        Optional<Svc> fonudSvc = svcRepository.findBySvcNumAndSvcStCd("7132423123", SvcStCd.SP);
        assertThat(fonudSvc.isPresent()).isTrue();

        //-- 서비스번호가 존재하지만 서비스상태가 다른 경우 테스트
        assertThat(svcRepository.findBySvcNumAndSvcStCd("7132423123", SvcStCd.AC).isPresent()).isFalse();
        //-- 서비스번호가 존재하지 않는 경우 테스트
        assertThat(svcRepository.findBySvcNumAndSvcStCd("7132423124", SvcStCd.AC).isPresent()).isFalse();
    }

    @Test
    public void test_신규가입() {
        Svc svc = subscribeSampleSvc();
        assertThat(svc.getFeeProd()).isEqualTo(prods_p1.get(0));
        assertThat(svc.getSvcProds()).hasSize(3);
        assertThat(svc.getSvcStHsts()).hasSize(1);
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.AC);
        assertThat(svc.getSvcScrbDt()).isEqualTo(LocalDate.now());
    }

    @Test
    public void test_SvcProd로만조회() {
        Svc svc = subscribeSampleSvc();
        assertThat(svcProdRepository.findAllSvcProds(svc)).hasSize(3);
        assertThat(svcProdRepository.findActiveSvcProds(svc)).hasSize(3);

        svc.terminateProduct(svc.getSvcProds().get(2).getId(), LocalDateTime.now());
        assertThat(svcProdRepository.findAllSvcProds(svc)).hasSize(3);
        assertThat(svcProdRepository.findActiveSvcProds(svc)).hasSize(2);
    }

    @Test(expected = InvalidInputException.class)
    public void test_중복상품가입_예외발생() {
        Svc svc = subscribeSampleSvc();
        svc.subscribeProduct(prods_p1.get(0), LocalDateTime.now());
    }

    @Test
    public void test_기본요금제_존재하는상태에서_다른기본요금제_가입() {
        Svc svc = subscribeSampleSvc();

        //-- 다른 기본요금제 가입
        svc.subscribeProduct(prods_p1.get(1), LocalDateTime.now());
        svcRepository.save(svc);

        assertThat( svc.getSvcProds() ).hasSize(4);
        //-- 이전 요금제는 종료
        assertThat( svc.getSvcProds()
                .stream()
                .filter(sp -> sp.getProd().equals(prods_p1.get(0)))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("...") )
                .getEffEndDtm()
        ).isBefore(LocalDateTime.of(9999,12,31,23,59,59));

        //-- 이후 요금제는 유지
        assertThat( svc.getSvcProds()
                .stream()
                .filter(sp -> sp.getProd().equals(prods_p1.get(1)))
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("...") )
                .getEffEndDtm()
        ).isEqualTo(LocalDateTime.of(9999,12,31,23,59,59));
    }

    @Test
    public void test_부가서비스추가() {
        Svc svc = subscribeSampleSvc();

        //-- 다른 부가서비스 가입
        svc.subscribeProduct(prods_p3.get(1), LocalDateTime.now());
        svc = svcRepository.save(svc);

        //-- 상품 총 4개여야 함
        assertThat( svc.getSvcProds() ).hasSize(4);

        //-- 종료된 부가서비스 없어야 함
        assertThat( svc.getSvcProds()
                .stream()
                .anyMatch( sp -> sp.getEffEndDtm().isBefore(LocalDateTime.of(9999,12,31,23,59,59)) )
        ).isFalse();

        //-- 새 부가서비스 있어야 함
        assertThat( svc.getSvcProds()
                .stream()
                .anyMatch( sp -> sp.getProd().equals(prods_p3.get(1)) )
        ).isTrue();
    }

    @Test
    public void test_상품해지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        SvcProd sp = svc.getSvcProds().stream()
                .filter(sp_ -> sp_.getProd().getSvcProdCd() == SvcProdCd.P3)
                .findFirst()
                .orElseThrow( () -> new DataIntegrityViolationException("...") );

        svc.terminateProduct(sp.getId(), now);
        assertThat(sp.getEffEndDtm()).isEqualTo(now);
        assertThat(sp.getTermDt()).isEqualTo(now.toLocalDate());
    }

    @Test(expected = InvalidInputException.class)
    public void test_상품해지_기본요금제_예외발생() {
        Svc svc = subscribeSampleSvc();

        svc.terminateProduct(1, LocalDateTime.now());
        fail("여기 오면 안됨");
    }

    @Test
    public void test_서비스정지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();
        svc.suspend(now);

        //-- 현재 상태가 SP?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.SP);

        //-- 최종이력의 유효시작일시가 now + 1초 and SP?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh -> ssh.isLastHst() && ssh.getSvcStCd() == SvcStCd.SP && ssh.getEffStaDtm().equals(now.plusSeconds(1)))
        ).isTrue();
    }

    @Test
    public void test_서비스활성화() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();

        //-- 어제 정지
        svc.suspend(now.minusDays(1));

        //-- 오늘 활성화
        svc.activate(now);

        //-- 현재 상태가 AC?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.AC);

        //-- 최종이력의 유효시작일시가 now + 1초 && AC?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh -> ssh.isLastHst() && ssh.getSvcStCd() == SvcStCd.AC && ssh.getEffStaDtm().equals(now.plusSeconds(1)))
        ).isTrue();
    }

    @Test
    public void test_서비스해지() {
        Svc svc = subscribeSampleSvc();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysLater = now.plusDays(2);

        //-- 이틀 뒤 해지로 가정
        svc.terminate(twoDaysLater);

        //-- 현재 상태가 TG?
        assertThat(svc.getSvcStCd()).isEqualTo(SvcStCd.TG);

        //-- 최종이력의 유효시작일시가 now + 1초 && TG?
        assertThat(svc.getSvcStHsts()
                .stream()
                .anyMatch(ssh -> ssh.isLastHst() && ssh.getSvcStCd() == SvcStCd.TG && ssh.getEffStaDtm().equals(twoDaysLater.plusSeconds(1)))
        ).isTrue();

        //-- 살아 있는 상품 없어야 함
        assertThat(svc.getSvcProds()
                .stream()
                .noneMatch(SvcProd::isActive)
        ).isTrue();
    }

    //-- 서비스 개통 & 상품 3개 가입한 샘플 서비스
    private Svc subscribeSampleSvc() {
        Svc svc = Svc.builder()
                .svcCd(SvcCd.C)
                .svcNum("01012345678")
                .svcScrbDt(LocalDate.now())
                .svcStCd(SvcStCd.AC)
                .cust(custs.get(0))
                .feeProd(prods_p1.get(0))
                .build();

        LocalDateTime now = LocalDateTime.now();

        svc.subscribe(now);
        svc.subscribeProduct(prods_p2.get(0), now);
        svc.subscribeProduct(prods_p3.get(0), now);

        return svcRepository.save(svc);
    }
}
