package org.caltech.miniswingpilot.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest     //-- 표준 repository만 쓸거면 DataJpaTest 써도 되는데 querydsl은 이거 써줘야 하는듯
public class ProdRepositoryTest {
    @Autowired
    ProdRepository prodRepository;

    @Before
    public void setUp() {
        prodRepository.saveAll(TestDataFactory.createManyProds_1());
        prodRepository.saveAll(TestDataFactory.createManyProds_2());
        prodRepository.saveAll(TestDataFactory.createManyProds_3());
    }

    @After
    public void tearDown() {
        prodRepository.deleteAll();
    }

    @Test
    public void test_상품ID로조회() {
        Optional<Prod> p = prodRepository.findById("NA00000001");
        assertThat(p.isPresent()).isTrue();
        assertThat(p.get().getProdNm()).isEqualTo("표준요금제");
    }

    @Test
    public void test_상품명like로조회() {
        List<Prod> prods = prodRepository.findByProdNmContainingOrderByProdId("가요");
        assertThat(prods).hasSize(2);
        assertThat(prods.get(0).getProdNm()).isEqualTo("부가요금제1");
        assertThat(prods.get(1).getProdNm()).isEqualTo("부가요금제2");
    }
}
