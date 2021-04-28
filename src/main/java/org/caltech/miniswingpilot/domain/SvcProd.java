package org.caltech.miniswingpilot.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        /*
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"svc_mgmt_num", "prod_id", "eff_sta_dtm", "eff_end_dtm"})
        },*/
        name = "svc_prod",
        indexes = {
                @Index(name = "svc_prod_n1",
                        columnList = "svc_mgmt_num, prod_id, eff_end_dtm desc, eff_sta_dtm desc",
                        unique = true)
        }
)
public class SvcProd  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "svc_mgmt_num")
    private Svc svc;

    @ManyToOne
    @JoinColumn(name = "prod_id")
    private Prod prod;

    @Column(nullable = false, name = "eff_sta_dtm")
    private LocalDateTime effStaDtm;

    @Column(nullable = false, name = "eff_end_dtm")
    private LocalDateTime effEndDtm;

    @Column(nullable = false)
    private LocalDate scrbDt;

    @Column
    private LocalDate termDt;

    @Builder
    public SvcProd(Svc svc, Prod prod, LocalDateTime effStaDtm, LocalDateTime effEndDtm, LocalDate scrbDt, LocalDate termDt) {
        this.svc       = svc;
        this.prod      = prod;
        this.effStaDtm = effStaDtm;
        this.effEndDtm = effEndDtm;
        this.scrbDt    = scrbDt;
        this.termDt    = termDt;
    }

    public static SvcProd createNewSvcProd(Svc svc, Prod prod, LocalDateTime scrbDtm) {
        return SvcProd.builder()
                .svc(svc)
                .prod(prod)
                .effEndDtm(LocalDateTime.of(9999,12,31,23,59,59))
                .effStaDtm(scrbDtm)
                .scrbDt(scrbDtm.toLocalDate())
                .build();
    }

    public void terminate(LocalDateTime termDtm) {
        effEndDtm = termDtm;
        termDt = termDtm.toLocalDate();
    }


    public boolean isActive() {
        return termDt == null;
    }

    public boolean isSubscribingProd(Prod p) {
        return prod.equals(p) && isActive();
    }
}
