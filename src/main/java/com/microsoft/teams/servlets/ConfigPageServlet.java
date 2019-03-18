package com.microsoft.teams.servlets;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.microsoft.teams.oauth.PropertiesClient;
import com.microsoft.teams.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.microsoft.teams.oauth.PropertiesClient.ATLAS_ID;
import static com.microsoft.teams.oauth.PropertiesClient.PUBLIC_KEY;

@Component
public class ConfigPageServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigPageServlet.class);

    private final TemplateRenderer renderer;
    private final PropertiesClient propertiesClient;

    @Autowired
    public ConfigPageServlet(@ComponentImport TemplateRenderer renderer,
                             PropertiesClient propertiesClient) {
        this.renderer = renderer;
        this.propertiesClient = propertiesClient;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=utf-8");
            renderer.render("templates/admin.vm", buildContext(), response.getWriter());
        } catch (RenderingException | IOException e){
            LOG.error(e.getMessage());
        }
    }

    private Map<String, Object> buildContext() {
        Map<String, Object> teamsContext = new HashMap<>();
        Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();

        teamsContext.put("publicKey", properties.get(PUBLIC_KEY));
        teamsContext.put("consumerKey", AppUtils.getConsumerKey());
        teamsContext.put("consumerName", "MsTeamsIntegration");
        teamsContext.put("atlasHome", AppUtils.getAtlasHome());
        teamsContext.put("atlasId", properties.get(ATLAS_ID));
        return teamsContext;
    }

}
