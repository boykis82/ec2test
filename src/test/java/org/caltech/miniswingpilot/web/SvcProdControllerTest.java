package org.caltech.miniswingpilot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.caltech.miniswingpilot.domain.*;
import org.caltech.miniswingpilot.service.ProdService;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.CustCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.ProdSubscribeRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcProdResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SvcProdControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    /*
    @MockBean(name = "svcService")
    private SvcService svcService;

     */

    @Autowired
    private SvcRepository svcRepository;

    @Autowired
    private CustRepository custRepository;

    @Autowired
    private ProdRepository prodRepository;

    @Autowired
    private SvcProdRepository svcProdRepository;

    private MockMvc mvc;

    private String urlPrefix;

    private Cust c;
    private Svc s;
    private List<Prod> prods;
    private List<Prod> suplProds;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // print()시 한글이 깨져서 추가
                .alwaysDo(print())
                .build();

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/services";

        c = Cust.builder()
                .custNm("강인수")
                .custTypCd(CustTypCd.C01)
                .custRgstDt(LocalDate.now())
                .build();
        c = custRepository.save(c);

        prods = TestDataFactory.createManyProds_1();
        prodRepository.saveAll(prods);
        prodRepository.saveAll(TestDataFactory.createManyProds_2());
        suplProds = TestDataFactory.createManyProds_3();
        prodRepository.saveAll(suplProds);

        s = Svc.builder()
                .svcCd(SvcCd.C)
                .svcStCd(SvcStCd.AC)
                .svcNum("0101234567")
                .svcScrbDt(LocalDate.now())
                .feeProd(prods.get(0))
                .cust(c)
                .build();
        s.subscribe(LocalDateTime.now());
        s.subscribeProduct(suplProds.get(0), LocalDateTime.now());
        s = svcRepository.save(s);
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
        custRepository.deleteAll();
        prodRepository.deleteAll();
    }

    @Test
    public void test_상품가입() throws Exception {
        String prodId = "NA00000007";
        String url = urlPrefix + "/" + s.getSvcMgmtNum() + "/products";

        ProdSubscribeRequestDto dto = ProdSubscribeRequestDto.builder().prodId(prodId).build();

        mvc.perform(post(url)
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString(dto) )
        ).andExpect( status().isOk() );

        assertThat(svcProdRepository.findAllSvcProds(s)).hasSize(3);
    }

    @Test
    public void test_가입상품조회() throws Exception {
        int svcMgmtNum = 1;
        String url = urlPrefix + "/" + svcMgmtNum + "/products";

        mvc.perform(get(url).param("includeTermProd", "false")
                .contentType( MediaType.APPLICATION_JSON )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.length()").value(2) );
    }

    @Test
    public void test_상품해지() throws Exception {
        assertThat(svcProdRepository.findAllSvcProds(s)).hasSize(2);
        List<SvcProd> activeSvcProds = svcProdRepository.findActiveSvcProds(s);
        assertThat(activeSvcProds).hasSize(2);

        String url = urlPrefix + "/" + s.getSvcMgmtNum() + "/products/" + activeSvcProds.get(1).getId();

        mvc.perform(delete(url)
                .contentType( MediaType.APPLICATION_JSON )
        ).andExpect( status().isOk() );

        assertThat(svcProdRepository.findAllSvcProds(s)).hasSize(2);
        assertThat(svcProdRepository.findActiveSvcProds(s)).hasSize(1);
    }

}


















