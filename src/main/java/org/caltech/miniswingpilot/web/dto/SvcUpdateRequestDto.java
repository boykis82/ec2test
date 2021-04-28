package org.caltech.miniswingpilot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.caltech.miniswingpilot.domain.SvcStCd;

@NoArgsConstructor
@Setter
@Getter
public class SvcUpdateRequestDto {
    public enum SvcUpdateTyp { SVC_STATUS_UPDATE }

    SvcUpdateTyp svcUpdateTyp;
    SvcStCd afterSvcStCd;

    @Builder
    public SvcUpdateRequestDto(SvcUpdateTyp svcUpdateTyp, SvcStCd afterSvcStCd) {
        this.svcUpdateTyp = svcUpdateTyp;
        this.afterSvcStCd = afterSvcStCd;
    }

    public static SvcUpdateRequestDto createSuspendServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SvcUpdateTyp.SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.SP)
                .build();
    }

    public static SvcUpdateRequestDto createActivateServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SvcUpdateTyp.SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.AC)
                .build();
    }

    public static SvcUpdateRequestDto createTerminateServiceDto() {
        return SvcUpdateRequestDto.builder()
                .svcUpdateTyp(SvcUpdateTyp.SVC_STATUS_UPDATE)
                .afterSvcStCd(SvcStCd.TG)
                .build();
    }
}
