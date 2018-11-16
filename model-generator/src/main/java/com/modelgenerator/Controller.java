package com.modelgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired CompArchLoader loader;

    @GetMapping("/load")
    public String load() {
        return this.loader.loadModel().toString();
    }
}
