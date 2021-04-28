package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Svc;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SvcResponseMapper {
    public SvcResponseDto entityToDto(Svc entity) {
        return SvcResponseDto.builder()
                .svcCd(entity.getSvcCd())
                .feeProdId((entity.getFeeProd().getProdId()))
                .feeProdNm((entity.getFeeProd().getProdNm()))
                .svcMgmtNum(entity.getSvcMgmtNum())
                .svcNum(entity.getSvcNum())
                .svcStCd(entity.getSvcStCd())
                .svcScrbDt(entity.getSvcScrbDt())
                .svcTermDt(entity.getSvcTermDt())
                .build();
    }

    public abstract List<SvcResponseDto> entityListToDtoList(List<Svc> entity);
}
