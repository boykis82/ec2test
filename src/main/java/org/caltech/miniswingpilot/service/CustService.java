package org.caltech.miniswingpilot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswingpilot.domain.Cust;
import org.caltech.miniswingpilot.domain.CustRepository;
import org.caltech.miniswingpilot.exception.NotFoundDataException;
import org.caltech.miniswingpilot.service.mapper.CustCreateRequestMapper;
import org.caltech.miniswingpilot.service.mapper.CustResponseMapper;
import org.caltech.miniswingpilot.web.dto.CustCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.CustResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustService {
    private final CustRepository custRepository;

    private final CustResponseMapper custResponseMapper;
    private final CustCreateRequestMapper custCreateRequestMapper;

    @Transactional(readOnly = true)
    public CustResponseDto getCustomer(int custNum) {
        return custResponseMapper.entityToDto(
                custRepository.findById(custNum)
                        .orElseThrow( () -> new NotFoundDataException("고객이 없습니다.! cust_num = " + custNum) )
        );
    }

    @Transactional(readOnly = true)
    public List<CustResponseDto> getCustomers(String custNm, LocalDate birthDt) {
        return custResponseMapper.entityListToDtoList(
                custRepository.findByCustNmAndBirthDtOrderByCustRgstDtDesc(custNm, birthDt)
        );
    }

    @Transactional
    public CustResponseDto createCustomer(CustCreateRequestDto dto) {
        Cust c = custCreateRequestMapper.dtoToEntity(dto);
        c.setCustRgstDt(LocalDate.now());
        return custResponseMapper.entityToDto(custRepository.save(c));
    }
}
