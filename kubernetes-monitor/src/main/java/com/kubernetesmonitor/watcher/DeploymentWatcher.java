package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Deployment;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1ReplicaSet;
import io.kubernetes.client.util.Watch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeploymentWatcher extends AbstractWatcher<V1Deployment> {

    @Autowired
    Publisher publisher;

    public DeploymentWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1Deployment> response) {
        Deployment deployment = this.responseParser.parseDeploymentResponse(response.object);
        System.out.printf("#### Deployment Watcher ---- Event type: %s : %s %n", response.type, deployment.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        System.out.println("#### Executing Deployment call...");
        return this.kubernetesConnector.getDeploymentCall();
    }

    @Override
    protected Watch<V1Deployment> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Deployment>>() {
        }.getType());
    }
}
