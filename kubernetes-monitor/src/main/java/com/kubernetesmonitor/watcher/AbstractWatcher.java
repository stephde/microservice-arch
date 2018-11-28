package com.kubernetesmonitor.watcher;

import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.kubernetes.KubernetesResponseParser;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Watch;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractWatcher<T> {

    protected KubernetesConnector kubernetesConnector;
    protected KubernetesResponseParser responseParser;
    private boolean watchIsActive;

    protected AbstractWatcher(KubernetesConnector connector) {
        this.kubernetesConnector = connector;
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

    private void doWatch() {
        Watch<T> watch = null;

        // "Watches will timeout once in a while, you should wrap any Watch in a while (true) { ... } loop."
        // https://github.com/kubernetes-client/java/issues/266
        while (this.isWatchIsActive()) {

            try {
                System.out.println("Initializing watcher " + getClass().getCanonicalName());

                watch = initWatch();
                for (Watch.Response<T> item : watch) {
                    this.watchCallback(item);
                }
            } catch (ApiException ex) {
                System.out.println("Error during Kubernetes APU call due to: " + ex.getResponseBody());
            } catch (Throwable ex) {
                System.out.println(ex.getMessage());
            } finally {
                try {
                    Thread.sleep(3000); // sleep for 3sec before trying again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            System.out.println("Stopping watch of " + getClass().getCanonicalName());
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
}
