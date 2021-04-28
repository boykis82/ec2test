package org.caltech.miniswingpilot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.CustTypCd;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class CustResponseDto {
    private int custNum;
    private String custNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate custRgstDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    private CustTypCd custTypCd;
}
