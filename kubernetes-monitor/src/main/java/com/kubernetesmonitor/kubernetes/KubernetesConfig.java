package com.kubernetesmonitor.kubernetes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {
    @Value("${kubernetes.namespace}")
    String NAMESPACE;
}
