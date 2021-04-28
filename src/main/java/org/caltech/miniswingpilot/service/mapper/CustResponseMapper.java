package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.Svc;
import org.caltech.miniswingpilot.web.dto.CustResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CustResponseMapper {
    CustResponseDto entityToDto(Cust entity);
    List<CustResponseDto> entityListToDtoList(List<Cust> entities);
}
