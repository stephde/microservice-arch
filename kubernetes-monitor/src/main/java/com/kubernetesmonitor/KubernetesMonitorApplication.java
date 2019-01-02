package com.kubernetesmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KubernetesMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesMonitorApplication.class, args);
	}

	// Enable CORS globally
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurerAdapter() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/api/*")
//						.allowedOrigins("http://localhost:8080", "http://localhost:8080/")
//						.allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS")
//						.allowedHeaders("*");
//			}
//		};
//	}

//	@Bean
//	CorsConfigurationSource corsConfigurationSource() {
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		CorsConfiguration corsConfiguration = new CorsConfiguration();
//		corsConfiguration.addAllowedOrigin("http://localhost:8080");
//		corsConfiguration.setAllowedMethods(Arrays.asList(
//				HttpMethod.GET.name(),
//				HttpMethod.HEAD.name(),
//				HttpMethod.POST.name(),
//				HttpMethod.PUT.name(),
//				HttpMethod.DELETE.name()));
//		corsConfiguration.addAllowedHeader("*");
//		source.registerCorsConfiguration("/**", corsConfiguration); // you restrict your path here
//		return source;
//	}
}
