package org.caltech.miniswingpilot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswingpilot.domain.*;
import org.caltech.miniswingpilot.exception.InvalidInputException;
import org.caltech.miniswingpilot.exception.NotFoundDataException;
import org.caltech.miniswingpilot.service.mapper.SvcCreateRequestMapper;
import org.caltech.miniswingpilot.service.mapper.SvcProdResponseMapper;
import org.caltech.miniswingpilot.service.mapper.SvcResponseMapper;
import org.caltech.miniswingpilot.web.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.caltech.miniswingpilot.web.dto.SvcUpdateRequestDto.SvcUpdateTyp.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SvcService {
    private final SvcRepository svcRepository;
    private final CustRepository custRepository;
    private final SvcRepositorySupport svcRepositorySupport;
    private final ProdRepository prodRepository;

    private final SvcResponseMapper svcResponseMapper;
    private final SvcCreateRequestMapper svcCreateRequestMapper;
    private final SvcProdResponseMapper svcProdResponseMapper;

    @Transactional(readOnly = true)
    public SvcResponseDto getService(int svcMgmtNum) {
        return svcResponseMapper.entityToDto(
                svcRepository.findById(svcMgmtNum)
                        .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) )
        );
    }

    @Transactional(readOnly = true)
    public List<SvcResponseDto> getServicesByCustomer(int offset, int limit, int custNum, boolean includeTermSvc) {
        Cust cust = custRepository.findById(custNum)
                .orElseThrow( () -> new NotFoundDataException("고객이 없습니다.! cust_num = " + custNum) );

        return svcResponseMapper.entityListToDtoList(
                svcRepositorySupport.findByCustAndSvcStCd(offset, limit, cust, includeTermSvc)
        );
    }

    @Transactional
    public SvcResponseDto createService(SvcCreateRequestDto dto) {
        Cust cust = custRepository.findById(dto.getCustNum())
                .orElseThrow( () -> new NotFoundDataException("고객이 없습니다.! cust_num = " + dto.getCustNum()) );

        Prod prod = prodRepository.findById(dto.getFeeProdId())
                .orElseThrow( () -> new NotFoundDataException("상품이 없습니다.! prod_id = " + dto.getFeeProdId()) );

        Svc s = svcCreateRequestMapper.dtoToEntity(dto, cust, prod);
        s.subscribe(LocalDateTime.now());
        return svcResponseMapper.entityToDto(svcRepository.save(s));
    }

    @Transactional
    public void updateService(int svcMgmtNum, SvcUpdateRequestDto dto) {
        if (SVC_STATUS_UPDATE == dto.getSvcUpdateTyp()) {
            updateServiceStatus(svcMgmtNum, dto.getAfterSvcStCd());
        } else {
            throw new InvalidInputException("알 수 없는 서비스 변경 구분입니다!");
        }
    }

    @Transactional
    public void subscribeProduct(int svcMgmtNum, ProdSubscribeRequestDto dto) {
        Svc s = svcRepository.findById(svcMgmtNum)
                .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) );

        Prod p = prodRepository.findById(dto.getProdId())
                .orElseThrow( () -> new NotFoundDataException("상품이 없습니다.! prod_id = " + dto.getProdId()) );

        s.subscribeProduct(p, LocalDateTime.now());
    }

    @Transactional
    public void terminateProduct(int svcMgmtNum, int svcProdId) {
        Svc s = svcRepository.findById(svcMgmtNum)
                .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) );

        s.terminateProduct(svcProdId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<SvcProdResponseDto> getServiceProducts(int svcMgmtNum, boolean includeTermProd) {
        Svc s = svcRepository.findById(svcMgmtNum)
                .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) );

        return includeTermProd
                ? svcProdResponseMapper.entityListToDtoList(s.getSvcProds())
                : svcProdResponseMapper.entityListToDtoList(s.getActiveProdsOnly());
    }

    private void updateServiceStatus(int svcMgmtNum, SvcStCd afterSvcStCd) {
        Svc s = svcRepository.findById(svcMgmtNum)
                .orElseThrow( () -> new NotFoundDataException("서비스가 없습니다.! svc_mgmt_num = " + svcMgmtNum) );
        LocalDateTime now = LocalDateTime.now();

        if (SvcStCd.AC == afterSvcStCd) {
            s.activate(now);
        } else if (SvcStCd.SP == afterSvcStCd) {
            s.suspend(now);
        } else if (SvcStCd.TG == afterSvcStCd) {
            s.terminate(now);
        }
    }
}
