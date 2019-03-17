package com.microsoft.teams.oauth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.microsoft.teams.ao.TeamsAtlasUser;
import com.microsoft.teams.service.TeamsAtlasUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PropertiesClient {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesClient.class);

    public static final String TEAMS_ID = "teams_id";
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String PRIVATE_KEY = "private_key";
    public static final String PUBLIC_KEY = "public_key";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET = "secret";
    public static final String ATLAS_HOME = "atlas_home";
    public static final String ATLAS_ID = "atlas_id";


    private static final Map<String, String> DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
            .put(ATLAS_HOME, "http://localhost:5990/refapp")
            .put(CONSUMER_KEY, "OauthKey")
            .put(PRIVATE_KEY, "")
            .put(PUBLIC_KEY, "")
            .put(REQUEST_TOKEN, "")
            .put(TEAMS_ID, "")
            .put(ATLAS_ID, "")
            .build();

    private final String fileUrl;
    private static final String PROP_FILE_NAME = "integration.properties";

    private final TeamsAtlasUserServiceImpl userService;

    @Autowired
    public PropertiesClient(TeamsAtlasUserServiceImpl userService) {
        this.userService = userService;
        fileUrl = "./" + PROP_FILE_NAME;
    }

    public Map<String, String> getPropertiesOrDefaults() {
        try {
        Map<String, String> map = toMap(tryGetProperties());
        map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
        return map;
    } catch (FileNotFoundException e) {
        tryCreateDefaultFile();
        return new HashMap<>(DEFAULT_PROPERTY_VALUES);
    } catch (IOException e) {
        return new HashMap<>(DEFAULT_PROPERTY_VALUES);
    }
    }

    Map<String, String> getPropertiesFromDb(String teamsId) {
        Map<String, String> map = toMap(tryGetPropertiesFromDb(teamsId));
        map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
        return map;
    }

    private Map<String, String> toMap(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
    }

    private Properties toProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        propertiesMap.forEach(properties::put);
        return properties;
    }

    private Properties tryGetProperties() throws IOException {
        InputStream inputStream = new FileInputStream(new File(fileUrl));
        Properties prop = new Properties();
        prop.load(inputStream);
        inputStream.close();
        return prop;
    }

    private Properties tryGetPropertiesFromDb(String teamsId) {
        Properties prop = new Properties();
        List<TeamsAtlasUser> users = userService.getUserByTeamsId(teamsId);
        if (!users.isEmpty()) {
            TeamsAtlasUser user = users.get(0);
            if (user.getAtlasSecret() != null)
                prop.setProperty(SECRET, user.getAtlasSecret());
            if (user.getAtlasAccessToken() != null)
                prop.setProperty(ACCESS_TOKEN, user.getAtlasAccessToken());
            if (user.getAtlasRequestToken() != null)
                prop.setProperty(REQUEST_TOKEN, user.getAtlasRequestToken());
            prop.setProperty(TEAMS_ID, teamsId);
        }
        return prop;
    }

    public void savePropertiesToFile(Map<String, String> properties) {

        try (OutputStream outputStream = new FileOutputStream(new File(fileUrl))) {
            Properties p = toProperties(properties);
            p.store(outputStream, null);
        } catch (IOException e) {
            LOG.debug(e.getMessage());
        }
    }

    void savePropertiesToDatabase(Map<String, String> properties) {
        userService.add(properties);
    }

    private void tryCreateDefaultFile() {
        LOG.info("Creating default properties file: {}", PROP_FILE_NAME);
        tryCreateFile().ifPresent(file -> savePropertiesToFile(DEFAULT_PROPERTY_VALUES));
    }

    private Optional<File> tryCreateFile() {
        try {
            File file = new File(fileUrl);
            file.createNewFile();
            return Optional.of(file);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
