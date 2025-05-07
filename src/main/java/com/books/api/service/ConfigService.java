package com.books.api.service;

import com.books.api.repository.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigService {

    private final Map<String, String> configMap = new HashMap<>();

    public ConfigService(ConfigRepository configRepository) {
        configRepository.findAll()
                .forEach(conf -> configMap.put(conf.getVarName(), conf.getVarValue()));
    }

    public String get(String varName) {
        return configMap.get(varName);
    }

    public Integer getInt(String varName) {
        return Integer.parseInt(configMap.get(varName));
    }

    public Boolean getBoolean(String varName) {
        return Boolean.parseBoolean(configMap.get(varName));
    }

    public String getOrDefault(String varName, String defaultValue) {
        return configMap.getOrDefault(varName, defaultValue);
    }
}
