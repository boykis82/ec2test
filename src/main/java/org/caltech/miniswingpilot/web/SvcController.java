package org.caltech.miniswingpilot.web;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.caltech.miniswingpilot.service.SvcService;
import org.caltech.miniswingpilot.web.dto.SvcCreateRequestDto;
import org.caltech.miniswingpilot.web.dto.SvcResponseDto;
import org.caltech.miniswingpilot.web.dto.SvcUpdateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/swing/api/v1/services")
public class SvcController {
    @Autowired
    private SvcService svcService;

    @GetMapping("/{svcMgmtNum}")
    public SvcResponseDto getService(@PathVariable("svcMgmtNum") int svcMgmtNum) {
        return svcService.getService(svcMgmtNum);
    }

    @PostMapping
    public SvcResponseDto createService(@RequestBody SvcCreateRequestDto dto) {
        return svcService.createService(dto);
    }

    @PutMapping("/{svcMgmtNum}")
    public void updateService(@PathVariable("svcMgmtNum") int svcMgmtNum, @RequestBody SvcUpdateRequestDto dto) {
        svcService.updateService(svcMgmtNum, dto);
    }
}
