package com.dm.zipkinconsumer;

import com.dm.zipkinconsumer.models.Dependency;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://fb14srv7.hpi.uni-potsdam.de:1801", maxAge = 3600)
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

    @GetMapping("/zipkinurl")
    public String getZipkinUrl() {
        return zipkinApi.getZipkinUrl();
    }

    @PostMapping("/zipkinurl")
    public void setZipkinUrl(@RequestBody String url) {
        zipkinApi.setZipkinUrl(url);
    }
}
