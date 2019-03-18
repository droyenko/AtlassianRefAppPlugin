package com.microsoft.teams.lifecycle;

import com.microsoft.teams.oauth.PropertiesClient;
//import com.microsoft.teams.service.SignalRService;
import com.microsoft.teams.utils.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static com.microsoft.teams.oauth.PropertiesClient.*;

@Component
public class PluginLifeCycleEventHandler {

    private final PropertiesClient propertiesClient;
//    private final SignalRService signalRService;

    @Autowired
    public PluginLifeCycleEventHandler(PropertiesClient propertiesClient
//                                       , SignalRService signalRService
    ) {
        this.propertiesClient = propertiesClient;
//        this.signalRService = signalRService;
    }

    void onInstalled() {
        //If public/private key pair have been generated - do NOT create new
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();

        if ("".equals(properties.get(PUBLIC_KEY)) || "".equals(properties.get(PRIVATE_KEY))) {
            Map<String, String> keyPair = KeyUtils.generateKeyPair();
            properties.put(PUBLIC_KEY, keyPair.get(PUBLIC_KEY));
            properties.put(PRIVATE_KEY, keyPair.get(PRIVATE_KEY));
            propertiesClient.savePropertiesToFile(properties);
        }
        if ("".equals(properties.get(ATLAS_ID))) {
            UUID atlasId = UUID.randomUUID();
            properties.put(ATLAS_ID, atlasId.toString());
            propertiesClient.savePropertiesToFile(properties);
        }

//        signalRService.startSignalRConnection();
    }

    void onUninstalled() {
    }
}
