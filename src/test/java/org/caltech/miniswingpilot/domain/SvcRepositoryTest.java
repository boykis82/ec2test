package org.caltech.miniswingpilot.domain;

import org.caltech.miniswingpilot.exception.DataIntegrityViolationException;
import org.caltech.miniswingpilot.exception.InvalidInputException;
import org.caltech.miniswingpilot.service.SvcService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest     //-- 표준 repository만 쓸거면 DataJpaTest 써도 되는데 querydsl은 이거 써줘야 하는듯
public class SvcRepositoryTest {
    @Autowired
    SvcRepository svcRepository;

    @Autowired
    SvcRepositorySupport svcRepositorySupport;

    @Autowired
    CustRepository custRepository;

    @Autowired
    ProdRepository prodRepository;


    @Test
    public void test_서비스_생성() {
        Cust c = Cust.builder()
                .custNm("강인수")
                .custTypCd(CustTypCd.C01)
                .custRgstDt(LocalDate.now())
                .build();
        Cust savedCust = custRepository.save(c);

        Svc s = Svc.builder()
                .svcCd(SvcCd.C)
                .svcStCd(SvcStCd.AC)
                .svcNum("01012345678")
                .svcScrbDt(LocalDate.now())
                .cust(savedCust)
                .build();
        Svc savedSvc = svcRepository.save(s);

        assertThat(savedSvc.getSvcMgmtNum()).isNotEqualTo(0);
        assertThat(savedSvc.getCust().getCustNum()).isNotEqualTo(0);
    }

    private List<Cust> createCusts(int count) {
        List<Cust> custs = new ArrayList<>();
        for (int i = 0 ; i < count ; ++i)
            custs.add(Cust.builder()
                    .custNm("강인수" + i)
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .build());
        return custRepository.saveAll(custs);
    }

    @Test
    public void test_동일명의서비스_조회() {
        List<Cust>    custs    = createCusts(3);
        List<Cust>    svcCusts = Arrays.asList(custs.get(0), custs.get(0), custs.get(0), custs.get(1), custs.get(2));
        List<SvcStCd> svcSts   = Arrays.asList(SvcStCd.AC, SvcStCd.SP, SvcStCd.TG, SvcStCd.AC, SvcStCd.TG);
        List<Svc>     svcs     = new ArrayList<>();
        List<Prod>    feeProds = TestDataFactory.createManyProds_1();
        Random        rand     = new Random();

        for (int i = 0 ; i < svcCusts.size() ; ++i)  {
            svcs.add( Svc.builder()
                    .svcCd(SvcCd.C)
                    .svcStCd( svcSts.get(i) )
                    .svcNum("0101234567" + rand.nextInt(9))
                    .svcScrbDt(LocalDate.now())
                    .feeProd( feeProds.get(rand.nextInt(3)) )
                    .cust( svcCusts.get(i) )
                    .build() );
        }
        prodRepository.saveAll(feeProds);
        svcRepository.saveAll(svcs);

        int offset = 0;
        int limit = 2;

        List<Svc> foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(0), false);
        assertThat(foundSvcs).hasSize(2);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(1), false);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(1), true);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(2), true);
        assertThat(foundSvcs).hasSize(1);

        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(2), false);
        assertThat(foundSvcs).isEmpty();

        //-- paging
        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(0), true);
        assertThat(foundSvcs).hasSize(2);
        offset += limit;
        foundSvcs = svcRepositorySupport.findByCustAndSvcStCd(offset, limit, custs.get(0), true);
        assertThat(foundSvcs).hasSize(1);

    }

}
