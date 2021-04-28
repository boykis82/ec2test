package org.caltech.miniswingpilot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswingpilot.domain.ProdRepository;
import org.caltech.miniswingpilot.exception.NotFoundDataException;
import org.caltech.miniswingpilot.service.mapper.ProdResponseMapper;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProdService {
    private final ProdRepository prodRepository;
    private final ProdResponseMapper prodResponseMapper;

    @Transactional(readOnly = true)
    public ProdResponseDto getProduct(String prodId) {
        return prodResponseMapper.entityToDto(
                prodRepository.findById(prodId)
                        .orElseThrow( () -> new NotFoundDataException("상품이 없습니다.! prod_id = " + prodId) )
        );
    }

    @Transactional(readOnly = true)
    public List<ProdResponseDto> getProducts(String prodNm) {
        return prodResponseMapper.entityListToDtoList(
                prodRepository.findByProdNmContainingOrderByProdId(prodNm)
        );
    }

    @Transactional(readOnly = true)
    public List<ProdResponseDto> getAllProducts() {
        return prodResponseMapper.entityListToDtoList(
                prodRepository.findAll()
        );
    }

}
