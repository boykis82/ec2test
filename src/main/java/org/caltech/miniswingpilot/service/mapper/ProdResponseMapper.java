package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.Svc;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdResponseMapper {
    ProdResponseDto entityToDto(Prod entity);
    List<ProdResponseDto> entityListToDtoList(List<Prod> entity);
}
