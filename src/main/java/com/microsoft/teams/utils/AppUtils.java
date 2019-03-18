package com.microsoft.teams.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.microsoft.teams.oauth.PropertiesClient.*;

public class AppUtils {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (final InputStream stream =
                     AppUtils.class.getResourceAsStream("/integration.properties")) {
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AppUtils() {}

    public static String getSecret() {
        return PROPERTIES.getProperty(SECRET);
    }

    public static String getAtlasHome() {
        return PROPERTIES.getProperty(ATLAS_HOME);
    }

    public static String getConsumerKey() {
        return PROPERTIES.getProperty(CONSUMER_KEY);
    }

    public static String getPublicKey() {
        return PROPERTIES.getProperty(PUBLIC_KEY);
    }

    public static String getPrivateKey() {
        return PROPERTIES.getProperty(PRIVATE_KEY);
    }

    public static String getRequestToken() {
        return PROPERTIES.getProperty(REQUEST_TOKEN);
    }

    public static String getAccessToken() {
        return PROPERTIES.getProperty(ACCESS_TOKEN);
    }

    public static String getAtlasId() {
        return PROPERTIES.getProperty(ATLAS_ID);
    }

    public static void setPublicKey(String publicKey) {
        PROPERTIES.setProperty(PUBLIC_KEY, publicKey);
    }

    public static void setPrivateKey(String privateKey) {
        PROPERTIES.setProperty(PRIVATE_KEY, privateKey);
    }

    public static String getKey() {
        return PROPERTIES.getProperty("plugin_key");
    }
}
