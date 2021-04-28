package org.caltech.miniswingpilot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.SvcProdCd;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor
@Setter
@Getter
public class ProdResponseDto {
    private String prodId;
    private String prodNm;
    private SvcProdCd svcProdCd;
    private String description;

    @Builder
    public ProdResponseDto(String prodId, String prodNm, SvcProdCd svcProdCd, String description) {
        this.prodId = prodId;
        this.prodNm = prodNm;
        this.svcProdCd = svcProdCd;
        this.description = description;
    }
}
