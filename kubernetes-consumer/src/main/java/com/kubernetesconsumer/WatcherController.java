package com.kubernetesconsumer;

import com.google.common.collect.Lists;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.models.WatcherDTO;
import com.kubernetesconsumer.watcher.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "http://fb14srv7.hpi.uni-potsdam.de:1801", maxAge = 3600)
public class WatcherController {

    private final KubernetesConnector kubernetesConnector;
    private final List<AbstractWatcher> watchers;

    @Autowired
    public WatcherController(KubernetesConnector kubernetesConnector, NamespaceWatcher namespaceWatcher, EventWatcher eventWatcher, ServiceWatcher serviceWatcher, PodWatcher podWatcher) {
        this.kubernetesConnector = kubernetesConnector;
        this.watchers = Lists.newArrayList(namespaceWatcher, eventWatcher, serviceWatcher, podWatcher);
    }

    @GetMapping("/api/namespace")
    public String getNamespace() {
        return kubernetesConnector.getNamespace();
    }

    @PostMapping("/api/namespace")
    public void setNamespace(@RequestBody @NotEmpty String namespace) {
        kubernetesConnector.setNamespace(namespace);
    }

    @GetMapping("/api/watchers")
    public List<WatcherDTO> getWatchers() {
        return this.watchers
                .stream()
                .map(AbstractWatcher::getDTO)
                .collect(Collectors.toList());
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
