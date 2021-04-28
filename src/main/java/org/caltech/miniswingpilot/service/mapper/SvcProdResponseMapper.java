package org.caltech.miniswingpilot.service.mapper;

import org.caltech.miniswingpilot.domain.Prod;
import org.caltech.miniswingpilot.domain.SvcProd;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcProdResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SvcProdResponseMapper {
    public SvcProdResponseDto entityToDto(SvcProd entity) {
        SvcProdResponseDto dto = new SvcProdResponseDto();
        dto.setProdId( entity.getProd().getProdId() );
        dto.setProdNm( entity.getProd().getProdNm() );
        dto.setScrbDt( entity.getScrbDt() );
        dto.setTermDt( entity.getTermDt() );
        dto.setId( entity.getId() );

        return dto;
    }

    public abstract List<SvcProdResponseDto> entityListToDtoList(List<SvcProd> entities);
}
