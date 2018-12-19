package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.NamespaceEvent;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Pod;
import com.kubernetesmonitor.models.WatchableEntity;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NamespaceWatcher extends AbstractWatcher<V1Namespace> {

    @Autowired
    Publisher publisher;

    public NamespaceWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1Namespace> item) {
        WatchableEntity namespace = this.responseParser.parseMetadata(item.object.getMetadata());
        log.info("{}", namespace.toString());

        // only publish if namespace is the right one
        if(namespace.getName().equals(kubernetesConnector.getNamespace())){
            DateTime eventTime = DateTime.now();
            publisher.publishEvent(new NamespaceEvent(namespace, eventTime));
        }
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing namespace call...");
        return this.kubernetesConnector.getNamespacesCall();
    }

    @Override
    protected Watch<V1Namespace> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Namespace>>() {
        }.getType());
    }
}
