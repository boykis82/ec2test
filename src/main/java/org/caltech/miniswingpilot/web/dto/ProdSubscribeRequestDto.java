package org.caltech.miniswingpilot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProdSubscribeRequestDto {
    private String prodId;

    @Builder
    public ProdSubscribeRequestDto(String prodId) {
        this.prodId = prodId;
    }
}
