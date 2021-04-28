package org.caltech.miniswingpilot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.vm.ci.meta.Local;
import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.CustRepository;
import org.caltech.miniswingpilot.domain.CustTypCd;
import org.caltech.miniswingpilot.service.CustService;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.CustCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustRepository custRepository;

    @MockBean(name = "svcService")
    SvcService svcService;

    private MockMvc mvc;

    private String urlPrefix;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // print()시 한글이 깨져서 추가
                .alwaysDo(print())
                .build();

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/customers";
    }

    @After
    public void tearDown() {
        custRepository.deleteAll();
    }

    @Test
    public void test_고객생성() throws Exception {
        String url = urlPrefix;

        CustCreateRequestDto dto = CustCreateRequestDto.builder()
                .custNm("강인수")
                .birthDt(LocalDate.of(1982,1,1))
                .custTypCd(CustTypCd.C01)
                .build();

        mvc.perform(
                post(url)
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(dto) )
        ).andExpect( status().isOk() );

        assertThat(custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc("강인수", LocalDate.of(1982,1,1))).hasSize(1);
    }

    @Test
    public void test_고객조회_고객번호() throws Exception {
        Cust c = custRepository.save(Cust.builder()
                .custNm("강인수")
                .custRgstDt(LocalDate.of(2021,4,28))
                .custTypCd(CustTypCd.C01)
                .birthDt(LocalDate.of(1982,1,1))
                .build());

        String url = urlPrefix + "/" + c.getCustNum();

        mvc.perform(get(url))
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.custNm").value("강인수") )
                .andExpect( jsonPath("$.birthDt").value("1982-01-01") )
                .andExpect( jsonPath("$.custRgstDt").value("2021-04-28") )
                .andExpect( jsonPath("$.custTypCd.key").value(CustTypCd.C01.getKey()));
    }

    @Test
    public void test_고객조회_이름과생일() throws Exception {
        String url = urlPrefix;

        custRepository.save(Cust.builder()
                .custNm("강인수")
                .custRgstDt(LocalDate.of(2021,4,28))
                .custTypCd(CustTypCd.C01)
                .birthDt(LocalDate.of(1982,1,1))
                .build());

        mvc.perform(
                get(url).param("custNm", "강인수")
                        .param("birthDt", "1982-01-01")
        )
        .andExpect( status().isOk() )
        .andExpect( content().contentType("application/json") )
        .andExpect( jsonPath("$[0].custNm").value("강인수") )
        .andExpect( jsonPath("$[0].birthDt").value("1982-01-01") )
        .andExpect( jsonPath("$[0].custRgstDt").value("2021-04-28") )
        .andExpect( jsonPath("$[0].custTypCd.key").value(CustTypCd.C01.getKey()));
    }

    @Test
    public void test_고객조회_이름과생일_여러명() throws Exception {
        String url = urlPrefix;

        custRepository.saveAll(Arrays.asList(
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2021,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1982,1,1))
                        .build(),
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2018,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1983,1,1))
                        .build(),
                Cust.builder()
                        .custNm("강인수")
                        .custRgstDt(LocalDate.of(2019,4,28))
                        .custTypCd(CustTypCd.C01)
                        .birthDt(LocalDate.of(1982,1,1))
                        .build()
        ));

        mvc.perform(
                get(url).param("custNm", "강인수")
                        .param("birthDt", "1982-01-01")
        )
        .andExpect( status().isOk() )
        .andExpect( content().contentType("application/json") )
        .andExpect( jsonPath("$.length()").value(2) )
        .andExpect( jsonPath("$[0].custNm").value("강인수") )
        .andExpect( jsonPath("$[0].birthDt").value("1982-01-01") )
        .andExpect( jsonPath("$[0].custRgstDt").value("2021-04-28") )
        .andExpect( jsonPath("$[0].custTypCd.key").value(CustTypCd.C01.getKey()) )
        .andExpect( jsonPath("$[1].custNm").value("강인수") )
        .andExpect( jsonPath("$[1].birthDt").value("1982-01-01") )
        .andExpect( jsonPath("$[1].custRgstDt").value("2019-04-28") )
        .andExpect( jsonPath("$[1].custTypCd.key").value(CustTypCd.C01.getKey()) );
    }

    @Test
    public void test_동일명의서비스조회() throws Exception {
        int offset = 0;
        int limit = 4;
        int custNum = 1;
        String url = urlPrefix + "/" + custNum + "/services";

        given( svcService.getServicesByCustomer(offset, limit, custNum, true) )
                .willReturn(Arrays.asList(
                        SvcResponseDto.builder().build(),
                        SvcResponseDto.builder().build(),
                        SvcResponseDto.builder().build(),
                        SvcResponseDto.builder().build()));

        mvc.perform(
                get(url).param("offset", Integer.toString(offset))
                        .param("limit", Integer.toString(limit))
                        .param("includeTermSvc", "true")
        )
        .andExpect( status().isOk() )
        .andExpect( content().contentType("application/json") )
        .andExpect( jsonPath("$.length()").value(4) );

        //-- paging
        offset += limit;
        given( svcService.getServicesByCustomer(offset, limit, custNum, true) )
                .willReturn(Arrays.asList(
                        SvcResponseDto.builder().build(),
                        SvcResponseDto.builder().build()));
        mvc.perform(
                get(url).param("offset", Integer.toString(offset))
                        .param("limit", Integer.toString(limit))
                        .param("includeTermSvc", "true")
        )
        .andExpect( status().isOk() )
        .andExpect( content().contentType("application/json") )
        .andExpect( jsonPath("$.length()").value(2) );
    }
}
