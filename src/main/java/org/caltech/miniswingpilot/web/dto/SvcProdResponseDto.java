package org.caltech.miniswingpilot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.Svc;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class SvcProdResponseDto {
    private int id;
    private String prodId;
    private String prodNm;
    private LocalDate scrbDt;
    private LocalDate termDt;

}
