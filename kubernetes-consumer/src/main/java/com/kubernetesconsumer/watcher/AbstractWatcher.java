package com.kubernetesconsumer.watcher;

import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.kubernetes.KubernetesResponseParser;
import com.kubernetesconsumer.models.WatcherDTO;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractWatcher<T> {

    public enum WATCHER_TYPE {
        DEPLOYMENT_WATCHER,
        EVENT_WATCHER,
        NAMESPACE_WATCHER,
        POD_WATCHER,
        REPLICASET_WATCHER,
        SERVICE_WATCHER,
        OTHER
    }

    protected KubernetesConnector kubernetesConnector;
    protected KubernetesResponseParser responseParser;
    private boolean watchIsActive = false;
    private WATCHER_TYPE watcherType;

    protected AbstractWatcher(KubernetesConnector connector, WATCHER_TYPE type) {
        this.kubernetesConnector = connector;
        this.watcherType = type;
        this.responseParser = new KubernetesResponseParser();
    }

    abstract void watchCallback(Watch.Response<T> item);
    abstract com.squareup.okhttp.Call watchCall() throws ApiException;
    abstract protected Watch<T> initWatch() throws ApiException;

    public void stop() {
        this.watchIsActive = false;
    }

    public boolean isWatchIsActive() {
        return this.watchIsActive;
    }

    public WATCHER_TYPE getType() {
        return this.watcherType;
    }

    private void doWatch() {
        Watch<T> watch = null;

        // "Watches will timeout once in a while, you should wrap any Watch in a while (true) { ... } loop."
        // https://github.com/kubernetes-client/java/issues/266
        while (this.isWatchIsActive()) {

            try {
                log.info("Initializing watcher {}", getClass().getCanonicalName());

                watch = initWatch();
                for (Watch.Response<T> item : watch) {
                    this.watchCallback(item);
                }
            } catch (ApiException ex) {
                log.error("Error during Kubernetes API call due to: ", ex);
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                try {
                    Thread.sleep(3000); // sleep for 3sec before trying again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            log.info("Stopping watch of {}", getClass().getCanonicalName());
            if(watch != null) {
                watch.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void watch() {
        this.kubernetesConnector.setInfiniteWatchTimout();
        this.watchIsActive = true;

        CompletableFuture.runAsync(this::doWatch);
    }

    public WatcherDTO getDTO() {
        return new WatcherDTO(this.getType(), this.isWatchIsActive());
    }
}
