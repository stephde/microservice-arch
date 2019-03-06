package com.dm.zipkinconsumer;

import com.dm.zipkinconsumer.models.Dependency;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private ZipkinApi zipkinApi;

    @GetMapping("/dependencies")
    public List<Dependency> getDependencies() {
        return zipkinApi.fetchDependencies(DateTime.now());
    }

    @PostMapping("/start")
    public void startLoop() {
        zipkinApi.startUpdating();
    }

    @PostMapping("/stop")
    public void stopLoop() {
        zipkinApi.stopUpdating();
    }
}
