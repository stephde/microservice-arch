package com.kubernetesmonitor;

import com.google.common.collect.Lists;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.WatcherDTO;
import com.kubernetesmonitor.watcher.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class WatcherController {

    private final KubernetesConnector kubernetesConnector;

    private final NamespaceWatcher namespaceWatcher;
    private final EventWatcher eventWatcher;
    private final ServiceWatcher serviceWatcher;
    private final PodWatcher podWatcher;

    private List<AbstractWatcher> watchers;

    @Autowired
    public WatcherController(KubernetesConnector kubernetesConnector, NamespaceWatcher namespaceWatcher, EventWatcher eventWatcher, ServiceWatcher serviceWatcher, PodWatcher podWatcher) {
        watchers = Lists.newArrayList(namespaceWatcher, eventWatcher, serviceWatcher, podWatcher);
        this.kubernetesConnector = kubernetesConnector;
        this.namespaceWatcher = namespaceWatcher;
        this.eventWatcher = eventWatcher;
        this.serviceWatcher = serviceWatcher;
        this.podWatcher = podWatcher;
    }

    @GetMapping("/api/namespace")
    public String getNamespace() {
        return kubernetesConnector.getNamespace();
    }

    @PostMapping("/api/namespace")
    public void setNamespace(@RequestBody String namespace) {
        kubernetesConnector.setNamespace(namespace);
    }

    @GetMapping("/api/watchers")
    public List<WatcherDTO> getWatchers() {
        return this.watchers.stream().map(AbstractWatcher::getDTO).collect(Collectors.toList());
    }

    @PostMapping("/api/watchers/{type}")
    public void setWatcherActive(@PathVariable String type, @RequestBody String active) {
        Optional<AbstractWatcher> watcher = this.watchers.stream()
                .filter(w -> w.getType().toString().equals(type))
                .findFirst();

        log.info("Updating watcher - {} to {}", type, active);
        watcher.ifPresent(w -> {
            if (active.equals("true")) {
                w.watch();
            } else {
                w.stop();
            }
        });
    }

}
