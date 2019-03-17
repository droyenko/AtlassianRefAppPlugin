package com.microsoft.teams.utils;

import com.atlassian.oauth.util.RSAKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.microsoft.teams.oauth.PropertiesClient.PRIVATE_KEY;
import static com.microsoft.teams.oauth.PropertiesClient.PUBLIC_KEY;

public class KeyUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KeyUtils.class);

    private KeyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, String> generateKeyPair() {
        Map<String, String> keyPair= new HashMap<>();
        try {
            KeyPair pair = RSAKeys.generateKeyPair();
            String publicKey = RSAKeys.toPemEncoding(pair.getPublic());
            String privateKey = RSAKeys.toPemEncoding(pair.getPrivate());
            keyPair.put(PUBLIC_KEY, publicKey);
            keyPair.put(PRIVATE_KEY, privateKey);
        } catch (NoSuchAlgorithmException e) {
            LOG.debug(e.getMessage());
        }
        return keyPair;
    }

}
