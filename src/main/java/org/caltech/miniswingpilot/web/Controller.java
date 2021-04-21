package org.caltech.miniswingpilot.web;

import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoArgsConstructor
public class Controller {

    @GetMapping("/greetings/{name}")
    public String getMenu(
            @PathVariable("name") String name) {
        return "hello " + name + "! nice to meet you";
    }
}
