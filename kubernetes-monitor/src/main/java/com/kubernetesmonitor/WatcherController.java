package com.kubernetesmonitor;

import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcherController {

    @Autowired
    KubernetesConnector kubernetesConnector;

    @GetMapping("/api/namespace")
    public String index() {
        return kubernetesConnector.getNamespace();
    }
}
