package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Pod;
import com.kubernetesmonitor.models.ReplicaSet;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1ReplicaSet;
import io.kubernetes.client.util.Watch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplicaSetWatcher extends AbstractWatcher<V1ReplicaSet> {

    @Autowired
    Publisher publisher;

    public ReplicaSetWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1ReplicaSet> response) {
        ReplicaSet replicaSet = this.responseParser.parseReplicaSetResponse(response.object);
        System.out.printf("#### ReplicaSet Watcher ---- Event type: %s : %s %n", response.type, replicaSet.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        System.out.println("#### Executing ReplicaSet call...");
        return this.kubernetesConnector.getReplicaSetCall();
    }

    @Override
    protected Watch<V1ReplicaSet> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1ReplicaSet>>() {
        }.getType());
    }
}
