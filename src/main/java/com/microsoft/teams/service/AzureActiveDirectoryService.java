package com.microsoft.teams.service;

import com.nimbusds.jwt.JWTParser;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@Component
class AzureActiveDirectoryService {

    private static final Logger LOG = LoggerFactory.getLogger(AzureActiveDirectoryService.class);
    private static final String AZURE_PUBLIC_KEY_URL = "https://login.microsoftonline.com/common/discovery/keys";
//    private static final String AZURE_PUBLIC_KEY_URL = "https://login.botframework.com/v1/.well-known/keys";

    boolean isValidToken(String token) {

        RestOperations restTemplate = new RestTemplate();
        String publicKeySetJson = null;
        try {
            publicKeySetJson = restTemplate.getForObject(new URI(AZURE_PUBLIC_KEY_URL), String.class);
        } catch (URISyntaxException e) {
            LOG.debug(e.getMessage());
        }

        JsonWebSignature jws = new JsonWebSignature();
        boolean isValidToken = false;
        try {
            JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(publicKeySetJson);

            jws.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                    AlgorithmIdentifiers.RSA_USING_SHA256));
            jws.setCompactSerialization(token);

            VerificationJwkSelector jwkSelector = new VerificationJwkSelector();
            JsonWebKey jwk = jwkSelector.select(jws, jsonWebKeySet.getJsonWebKeys());
            if (jwk != null) {
                RSAPublicKey rsaPublicKey = (RSAPublicKey) jwk.getKey();
                jws.setKey(rsaPublicKey);
            }
            isValidToken = jws.verifySignature();
        } catch (JoseException e) {
            LOG.debug(e.getMessage());
        }
        return isValidToken;
    }

    String getClaimValueFromToken(String token, String claimKey) {
        String claim = "";
        try {
            claim = (String) JWTParser.parse(token).getJWTClaimsSet().getClaim(claimKey);
        } catch (ParseException e) {
            LOG.debug(e.getMessage());
        }
        return claim;
    }
}
