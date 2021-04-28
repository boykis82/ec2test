package org.caltech.miniswingpilot.web;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.caltech.miniswingpilot.web.dto.ProdSubscribeRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcProdResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/services")
public class SvcProdController {
    @Autowired
    private SvcService svcService;

    @PostMapping("/{svcMgmtNum}/products")
    public void subscribeProduct(@PathVariable("svcMgmtNum") int svcMgmtNum, @RequestBody ProdSubscribeRequestDto dto) {
        svcService.subscribeProduct(svcMgmtNum, dto);
    }

    @GetMapping("/{svcMgmtNum}/products")
    public List<SvcProdResponseDto> getServiceProducts(@PathVariable("svcMgmtNum") int svcMgmtNum,
                                                       @RequestParam(value = "includeTermProd") boolean includeTermProd) {
        return svcService.getServiceProducts(svcMgmtNum, includeTermProd);
    }

    @DeleteMapping("/{svcMgmtNum}/products/{svcProdId}")
    public void terminateProduct(@PathVariable("svcMgmtNum") int svcMgmtNum,
                                 @PathVariable("svcProdId") int svcProdId) {
        svcService.terminateProduct(svcMgmtNum, svcProdId);
    }
}
