package org.caltech.miniswingpilot.web;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.caltech.miniswingpilot.service.CustService;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.CustCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.CustResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/swing/api/v1/customers")
public class CustController {
    @Autowired
    private CustService custService;

    @Autowired
    private SvcService svcService;

    @GetMapping("/{custNum}")
    public CustResponseDto getCustomer(@PathVariable("custNum") int custNum) {
        return custService.getCustomer(custNum);
    }

    @GetMapping
    public List<CustResponseDto> getCustomers(@RequestParam("custNm") String custNm,
                                              @RequestParam("birthDt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDt)  {
        return custService.getCustomers(custNm, birthDt);
    }

    @PostMapping
    public CustResponseDto createCustomer(@RequestBody CustCreateRequestDto dto) {
        return custService.createCustomer(dto);
    }

    @GetMapping("/{custNum}/services")
    public List<SvcResponseDto> getServicesByCust(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "includeTermSvc") boolean includeTermSvc,
            @PathVariable("custNum") int custNum) {
        return svcService.getServicesByCustomer(offset, limit, custNum, includeTermSvc);
    }

}
