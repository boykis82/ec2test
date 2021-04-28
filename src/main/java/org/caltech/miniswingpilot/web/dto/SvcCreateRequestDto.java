package org.caltech.miniswingpilot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.SvcCd;
import org.caltech.miniswingpilot.domain.SvcStCd;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
public class SvcCreateRequestDto {
    private String svcNum;
    private SvcCd svcCd;
    private int custNum;
    private String feeProdId;

    @Builder
    public SvcCreateRequestDto(String svcNum, SvcCd svcCd, int custNum, String feeProdId) {
        this.svcNum = svcNum;
        this.svcCd = svcCd;
        this.custNum = custNum;
        this.feeProdId = feeProdId;
    }
}
