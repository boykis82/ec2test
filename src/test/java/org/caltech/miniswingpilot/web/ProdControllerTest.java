package org.caltech.miniswingpilot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.caltech.miniswingpilot.domain.CustRepository;
import org.caltech.miniswingpilot.domain.ProdRepository;
import org.caltech.miniswingpilot.domain.SvcProdCd;
import org.caltech.miniswingpilot.domain.TestDataFactory;
import org.caltech.miniswingpilot.service.ProdService;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProdControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean(name = "prodService")
    private ProdService prodService;

    private MockMvc mvc;

    private String urlPrefix;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // print()시 한글이 깨져서 추가
                .alwaysDo(print())
                .build();

        urlPrefix = "http://localhost:" + port + "/swing/api/v1/products";
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test_상품단건조회() throws Exception {
        String prodId = "NA00000001";
        given( prodService.getProduct(prodId) )
                .willReturn(ProdResponseDto.builder().prodId(prodId).build());

        String url = urlPrefix + "/" + prodId;

        mvc.perform(get(url))
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.prodId").value(prodId) );
    }

    @Test
    public void test_상품전체조회()  throws Exception{
        given( prodService.getAllProducts() )
                .willReturn(Arrays.asList(
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build(),
                        ProdResponseDto.builder().build()
                ));

        String url = urlPrefix;

        mvc.perform(get(url))
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.length()").value(10) );
    }

    @Test
    public void test_상품명으로조회() throws Exception {
        String prodNm = "스페셜";
        given( prodService.getProducts(prodNm) )
                .willReturn(Arrays.asList(
                        ProdResponseDto.builder().prodNm("스페셜1").build(),
                        ProdResponseDto.builder().prodNm("스페셜2").build(),
                        ProdResponseDto.builder().prodNm("스페셜3").build()
                ));

        String url = urlPrefix;

        mvc.perform(get(url).param("prodNm", prodNm))
                .andExpect( status().isOk() )
                .andExpect( content().contentType("application/json") )
                .andExpect( jsonPath("$.length()").value(3) );
    }
}
