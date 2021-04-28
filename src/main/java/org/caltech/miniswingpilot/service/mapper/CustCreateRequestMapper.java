package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.Svc;
import org.caltech.miniswingpilot.web.dto.CustCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcCreateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustCreateRequestMapper {
    @ToEntity
    @Mappings({
            @Mapping(target = "custRgstDt", ignore = true),
            @Mapping(target = "svc", ignore = true)
    })
    Cust dtoToEntity(CustCreateRequestDto dto);
}
