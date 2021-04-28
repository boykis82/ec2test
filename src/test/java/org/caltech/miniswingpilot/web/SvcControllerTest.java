package org.caltech.miniswingpilot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.caltech.miniswingpilot.domain.*;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.ProdSubscribeRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcUpdateRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SvcControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustRepository custRepository;

    @Autowired
    private SvcRepository svcRepository;

    @Autowired
    private ProdRepository prodRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SvcService svcService;

    private MockMvc mvc;

    private String urlPrefix;

    private Cust c;

    private List<Prod> prods;
    private List<Prod> suplProds;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // print()시 한글이 깨져서 추가
                .alwaysDo(print())
                .build();

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

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/services";
    }

    @After
    public void tearDown() {
        svcRepository.deleteAll();
        prodRepository.deleteAll();
        custRepository.deleteAll();
    }

    @Test
    public void test_서비스생성() throws Exception {
        String url = urlPrefix;

        SvcCreateRequestDto dto = SvcCreateRequestDto.builder()
                .svcNum("01012345667")
                .svcCd(SvcCd.C)
                .custNum(c.getCustNum())
                .feeProdId(prods.get(0).getProdId())
                .build();

        mvc.perform(post(url)
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString(dto) )
        ).andExpect( status().isOk() );

        assertThat(svcRepository.findAll()).hasSize(1);
    }

    @Test
    public void test_서비스정지() throws Exception {
        Svc s = subscribeSampleSvc();
        String url = urlPrefix + "/" + s.getSvcMgmtNum();

        mvc.perform(put(url)
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString(SvcUpdateRequestDto.createSuspendServiceDto()) )
        ).andExpect( status().isOk() );

        assertThat(svcService.getService(1).getSvcStCd()).isEqualTo(SvcStCd.SP);
    }

    @Test
    public void test_서비스활성화() throws Exception {
        Svc s = subscribeSampleSvc();
        s.suspend(LocalDateTime.now().minusDays(3));
        svcRepository.save(s);

        String url = urlPrefix + "/" + s.getSvcMgmtNum();

        mvc.perform(put(url)
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString(SvcUpdateRequestDto.createActivateServiceDto()) )
        ).andExpect( status().isOk() );

        assertThat(svcService.getService(1).getSvcStCd()).isEqualTo(SvcStCd.AC);
    }

    @Test
    public void test_서비스해지() throws Exception {
        Svc s = subscribeSampleSvc();

        String url = urlPrefix + "/" + s.getSvcMgmtNum();

        mvc.perform(put(url)
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString(SvcUpdateRequestDto.createTerminateServiceDto()) )
        ).andExpect( status().isOk() );

        assertThat(svcService.getService(1).getSvcStCd()).isEqualTo(SvcStCd.TG);
    }

    @Test
    public void test_서비스조회() throws Exception {
        Svc s = subscribeSampleSvc();

        String url = urlPrefix + "/" + s.getSvcMgmtNum();

        mvc.perform(get(url)
                .contentType( MediaType.APPLICATION_JSON )
        )
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.svcNum").value("0101234567") )
                .andExpect( jsonPath("$.svcMgmtNum").value(1) );
    }

    private Svc subscribeSampleSvc() {
        Svc s = Svc.builder()
                .svcCd(SvcCd.C)
                .svcStCd(SvcStCd.AC)
                .svcNum("0101234567")
                .svcScrbDt(LocalDate.now())
                .feeProd(prods.get(0))
                .cust(c)
                .build();
        s.subscribe(LocalDateTime.now());
        s.subscribeProduct(suplProds.get(0), LocalDateTime.now());
        return svcRepository.save(s);
    }
}


