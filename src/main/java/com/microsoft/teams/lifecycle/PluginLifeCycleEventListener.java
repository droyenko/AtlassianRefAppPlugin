package com.microsoft.teams.lifecycle;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.RefappImport;
import com.microsoft.teams.PluginConfiguration;
import com.microsoft.teams.lifecycle.events.EventType;
//import com.microsoft.teams.service.SignalRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

import static com.microsoft.teams.lifecycle.events.EventType.INSTALLED;
import static com.microsoft.teams.lifecycle.events.EventType.UNINSTALLED;

@Component
public class PluginLifeCycleEventListener implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(PluginLifeCycleEventListener.class);


    private static final String SOURCE_CLASS = UUID.randomUUID().toString();
    private EventType currentPluginStatus = null;
    private boolean registered = false;

    private PluginLifeCycleEventHandler pluginLifeCycleEventHandler;
    private PluginEventManager pluginEventManager;
//    private SignalRService signalRService;

    @Autowired
    public PluginLifeCycleEventListener(PluginLifeCycleEventHandler pluginLifeCycleEventHandler
                                        , @RefappImport PluginEventManager pluginEventManager
//                                        , SignalRService signalRService
    ) {
        this.pluginLifeCycleEventHandler = pluginLifeCycleEventHandler;
        this.pluginEventManager = pluginEventManager;
//        this.signalRService = signalRService;
    }

    @Override
    public void afterPropertiesSet() {
        LOG.debug("Building PluginLifeCycleEventListener {}%nCurrent pluginStatus = {}", SOURCE_CLASS, currentPluginStatus);
        register();
    }

    @Override
    public void destroy() {
//        signalRService.stopConnection();
        unregister();
    }

    // fired on plugin enabled, INSTALLED, or updated
    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        String pluginKey = event.getPlugin().getKey();
        if (PluginConfiguration.getPluginKey().equals(pluginKey)) {
            LOG.debug("Handling PluginEnabledEvent");
            handle(INSTALLED, event.getPlugin());
        }
    }

    @PluginEventListener
    public void onPluginUninstalledEvent(PluginUninstalledEvent event) {
        if (PluginConfiguration.getPluginKey().equals(event.getPlugin().getKey())) {
            LOG.debug("Handling PluginUninstalledEvent");
            handle(UNINSTALLED, event.getPlugin());
        }
    }

    private void handle(EventType nextPluginStatus, Plugin plugin) {
        Assert.notNull(nextPluginStatus, "EventType should not be null");
        LOG.info("Changing plugin status: {} -> {}", currentPluginStatus, nextPluginStatus);
        PluginConfiguration.setPlugin(plugin);

        if (nextPluginStatus == INSTALLED)
            setPluginStatus(INSTALLED);
        else if (nextPluginStatus == UNINSTALLED)
            setPluginStatus(UNINSTALLED);
        else
            throw new IllegalArgumentException("Unsupported pluginStatus");
    }

    private void setPluginStatus(EventType nextPluginStatus) {
        LOG.debug("Changing plugin status: {} -> {}", currentPluginStatus, nextPluginStatus);
        currentPluginStatus = nextPluginStatus;
        if (currentPluginStatus == INSTALLED)
            pluginLifeCycleEventHandler.onInstalled();
        else if (currentPluginStatus == UNINSTALLED)
            pluginLifeCycleEventHandler.onUninstalled();
        else
            throw new IllegalArgumentException("Unsupported pluginStatus");
    }

    private void register() {
        if (!registered) {
            LOG.info("Registering PluginLifeCycleEventListener {}", SOURCE_CLASS);
            pluginEventManager.register(this);
            registered = true;
        }
    }

    private void unregister() {
        if (registered) {
            pluginEventManager.unregister(this);
            registered = false;
        }
    }
}
