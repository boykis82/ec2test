package org.caltech.miniswingpilot.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cust")
public class Cust  extends BaseEntity {
    @Id
    @Column(name = "cust_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 80, nullable = false)
    private String custNm;

    @Column(nullable = false)
    private LocalDate custRgstDt;

    @Column
    private LocalDate birthDt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=3)
    private CustTypCd custTypCd;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cust")
    private Set<Svc> svc = new HashSet<>();

    @Builder
    public Cust(String custNm, LocalDate custRgstDt, LocalDate birthDt, CustTypCd custTypCd) {
        this.custNm     = custNm;
        this.custRgstDt = custRgstDt;
        this.birthDt    = birthDt;
        this.custTypCd  = custTypCd;
    }

    public int getCustNum() {
        return id;
    }
}
