package com.example.first.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mistral.api")
public class AIConfig {
    private String key;
    private String endpoint;
    private String model;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
