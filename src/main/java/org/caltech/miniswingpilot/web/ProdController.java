package org.caltech.miniswingpilot.web;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.caltech.miniswingpilot.service.ProdService;
import org.caltech.miniswingpilot.web.dto.ProdResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/products")
public class ProdController {
    @Autowired
    private ProdService prodSerice;

    @GetMapping("/{prodId}")
    public ProdResponseDto getProduct(@PathVariable("prodId") String prodId) {
        return prodSerice.getProduct(prodId);
    }

    @GetMapping
    public List<ProdResponseDto> getProducts(@RequestParam(value = "prodNm", required = false) String prodNm) {
        return (prodNm == null || prodNm.length() == 0)
            ?   prodSerice.getAllProducts()
            :   prodSerice.getProducts(prodNm);
    }
}
