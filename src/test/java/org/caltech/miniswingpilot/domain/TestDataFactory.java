package org.caltech.miniswingpilot.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataFactory {
    public static List<Cust> createManyCusts() {
        return Arrays.asList(
            Cust.builder()
                    .custNm("강인수")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1982,1,1))
                    .build(),
            Cust.builder()
                    .custNm("김도희")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1992,1,1))
                    .build(),
            Cust.builder()
                    .custNm("김선혁")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1986,1,1))
                    .build(),
            Cust.builder()
                    .custNm("김범수")
                    .custTypCd(CustTypCd.C01)
                    .custRgstDt(LocalDate.now())
                    .birthDt(LocalDate.of(1996,1,1))
                    .build()
        );
    }

    public static List<Prod> createManyProds_1() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000001").prodNm("표준요금제").svcProdCd(SvcProdCd.P1).description("111").build(),
                Prod.builder().prodId("NA00000002").prodNm("기본요금제").svcProdCd(SvcProdCd.P1).description("111").build(),
                Prod.builder().prodId("NA00000003").prodNm("스페셜").svcProdCd(SvcProdCd.P1).description("111").build()
        );
    }

    public static List<Prod> createManyProds_2() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000004").prodNm("부가요금제1").svcProdCd(SvcProdCd.P2).description("111").build(),
                Prod.builder().prodId("NA00000005").prodNm("부가요금제2").svcProdCd(SvcProdCd.P2).description("111").build()
        );

    }

    public static List<Prod> createManyProds_3() {
        return Arrays.asList(
                Prod.builder().prodId("NA00000006").prodNm("V컬러링").svcProdCd(SvcProdCd.P3).description("111").build(),
                Prod.builder().prodId("NA00000007").prodNm("Wavve").svcProdCd(SvcProdCd.P3).description("111").build(),
                Prod.builder().prodId("NA00000008").prodNm("Flo").svcProdCd(SvcProdCd.P3).description("111").build()
        );

    }
}
