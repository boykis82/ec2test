package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.Svc;
import org.caltech.miniswingpilot.domain.SvcProd;
import org.caltech.miniswingpilot.web.dto.SvcCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcProdResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SvcCreateRequestMapper {
    @ToEntity
    public Svc dtoToEntity(SvcCreateRequestDto dto, Cust cust, Prod prod) {
        return Svc.builder()
                .svcCd(dto.getSvcCd())
                .svcNum(dto.getSvcNum())
                .cust(cust)
                .feeProd(prod)
                .build();
    }
}
