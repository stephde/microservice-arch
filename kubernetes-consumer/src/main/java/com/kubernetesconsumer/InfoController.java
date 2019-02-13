package com.kubernetesconsumer;

import com.dm.events.DeploymentEvent;
import com.dm.events.ServiceEvent;
import com.google.common.collect.Lists;
import com.kubernetesconsumer.events.Publisher;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.watcher.ComponentStatusWatcher;
import com.kubernetesconsumer.watcher.PodWatcher;
import com.kubernetesconsumer.watcher.ServiceWatcher;
import io.kubernetes.client.ApiException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class InfoController {

    private static final String API_ERROR = "Encountered exception while trying to access kubernetes api";
    @Autowired
    private KubernetesConnector kubernetesConnector;
    @Autowired
    Publisher publisher;

    @PostMapping("/namespace")
    public void setNamespace(@RequestBody String namespace) {
        kubernetesConnector.setNamespace(namespace);
    }

    @GetMapping("/publish")
    public void publish() {
        publisher.publishEvent(new DeploymentEvent("AFakePod", DateTime.now(), DateTime.now()));
    }

    @GetMapping("/publish/service")
    public void publishService() {
        publisher.publishEvent(new ServiceEvent("AuthService", Collections.singletonList(8080), DateTime.now(), DateTime.now()));
    }

    @GetMapping("/pods")
    public List<String> getPods() {
        try {
            return kubernetesConnector.getAllPods();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }

    @GetMapping("/podwatch")
    public String watchPods() {
        PodWatcher watcher = new PodWatcher(kubernetesConnector);
        watcher.watch();
        return "Initialized pod watch";
    }

    @GetMapping("/componentstatus")
    public List<String> getComponentStati() {
        try {
            return kubernetesConnector.getAllComponentStati();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }

    @GetMapping("/statuswatch")
    public String watchStatus() {
        ComponentStatusWatcher watcher = new ComponentStatusWatcher(kubernetesConnector);
        watcher.watch();
        return "Initialized component status watcher";
    }

    @GetMapping("/services")
    public List<String> getServices() {
        try {
            return kubernetesConnector.getAllServices();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }

    @GetMapping("/serviceswatch")
    public String watchServices() {
        ServiceWatcher watcher = new ServiceWatcher(kubernetesConnector);
        watcher.watch();
        return "Initialized service watch";
    }

    @GetMapping("/events")
    public List<String> getEvents() {
        try {
            return kubernetesConnector.getAllEvents();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }

    @GetMapping("/resourcequotas")
    public List<String> getResourceQuotas() {
        try {
            return kubernetesConnector.getAllResorceQuotas();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }

    @GetMapping("/nodes")
    public List<String> getNodes() {
        try {
            return kubernetesConnector.getAllNodes();
        } catch (ApiException ex) {
            return Lists.newArrayList(API_ERROR, ex.getResponseBody());
        }
    }
}
