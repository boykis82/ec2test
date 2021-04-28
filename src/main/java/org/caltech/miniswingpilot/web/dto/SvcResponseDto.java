package org.caltech.miniswingpilot.web.dto;

import lombok.*;
import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.SvcCd;
import org.caltech.miniswingpilot.domain.SvcStCd;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SvcResponseDto {
    private int svcMgmtNum;
    private String svcNum;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate svcScrbDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate svcTermDt;

    private SvcCd svcCd;
    private SvcStCd svcStCd;

    private String feeProdId;
    private String feeProdNm;

    @Builder
    public SvcResponseDto(int svcMgmtNum,
                          String svcNum,
                          LocalDate svcScrbDt,
                          LocalDate svcTermDt,
                          SvcCd svcCd,
                          SvcStCd svcStCd,
                          String feeProdId,
                          String feeProdNm) {
        this.svcMgmtNum = svcMgmtNum;
        this.svcNum = svcNum;
        this.svcScrbDt = svcScrbDt;
        this.svcTermDt = svcTermDt;
        this.svcCd = svcCd;
        this.svcStCd = svcStCd;
        this.feeProdId = feeProdId;
        this.feeProdNm = feeProdNm;
    }
}
