package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Pod;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class PodWatcher extends AbstractWatcher<V1Pod> {

    @Autowired
    Publisher publisher;

    public PodWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1Pod> item) {
        Pod pod = this.responseParser.parsePodResponse(item.object);
        log.info("{}", pod.toString());

        DateTime eventTime = DateTime.now();
        publisher.publishEvent(new DeploymentEvent(pod.getName(), pod.getStatus(), pod.getNodeName(), pod.getServiceName(), eventTime, pod.getCreationTime()));
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing podwatch call...");
        return this.kubernetesConnector.getListPodCall();
    }

    @Override
    protected Watch<V1Pod> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Pod>>() {
        }.getType());
    }
}
