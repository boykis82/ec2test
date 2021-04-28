package org.caltech.miniswingpilot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.CustTypCd;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class CustCreateRequestDto {
    private String custNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    private CustTypCd custTypCd;

    @Builder
    public CustCreateRequestDto(String custNm, LocalDate birthDt, CustTypCd custTypCd) {
        this.custNm = custNm;
        this.birthDt = birthDt;
        this.custTypCd = custTypCd;
    }
}
