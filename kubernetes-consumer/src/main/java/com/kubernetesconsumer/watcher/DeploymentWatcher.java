package com.kubernetesconsumer.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesconsumer.events.Publisher;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.models.Deployment;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeploymentWatcher extends AbstractWatcher<V1Deployment> {

    @Autowired
    Publisher publisher;

    public DeploymentWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.DEPLOYMENT_WATCHER);
    }

    @Override
    void watchCallback(Watch.Response<V1Deployment> response) {
        Deployment deployment = this.responseParser.parseDeploymentResponse(response.object);
        log.info("#### Deployment Watcher ---- Event type: {} : {}", response.type, deployment.toString());

      // #### Deployment Watcher ---- Event type: DELETED : Deployment(super=WatchableEntity(name=queryservice, namespace=dm, serviceName=queryservice, creationTime=2018-12-19T12:44:20.000Z), replicas=1, unavailableReplicas=null, availableReplicas=null, collisionCount=null)
        //ToDo: handle deleted deployments
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing Deployment call...");
        return this.kubernetesConnector.getDeploymentCall();
    }

    @Override
    protected Watch<V1Deployment> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Deployment>>() {
        }.getType());
    }
}
