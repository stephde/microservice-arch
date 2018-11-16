package com.kubernetesmonitor;

import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KubernetesMonitorApplication {

	@Autowired private KubernetesConnector kubernetesConnector;

	public static void main(String[] args) {
		SpringApplication.run(KubernetesMonitorApplication.class, args);
	}
}
