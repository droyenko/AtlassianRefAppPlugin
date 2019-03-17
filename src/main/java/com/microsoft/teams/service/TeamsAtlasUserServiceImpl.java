package com.microsoft.teams.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.microsoft.teams.ao.TeamsAtlasUser;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.microsoft.teams.oauth.PropertiesClient.*;

@Scanned
@Named
@Component
public class TeamsAtlasUserServiceImpl implements TeamsAtlasUserService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamsAtlasUserServiceImpl.class);


    @ComponentImport
    private final ActiveObjects activeObjects;

    @Inject
    public TeamsAtlasUserServiceImpl(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @Override
    public TeamsAtlasUser add(Map<String, String> properties) {

        List<TeamsAtlasUser> usersByTeamsId = getUserByTeamsId(properties.get(TEAMS_ID));
        final TeamsAtlasUser user = (usersByTeamsId.isEmpty()) ? activeObjects.create(TeamsAtlasUser.class) : retrieveFirstTeamsAtlasUser(usersByTeamsId);

        for (Map.Entry entry : properties.entrySet()) {
            if (TEAMS_ID.equals(entry.getKey()))
                user.setMsTeamsUserId(properties.get(TEAMS_ID));
            else if (SECRET.equals(entry.getKey()))
                user.setAtlasSecret(properties.get(SECRET));
            else if (REQUEST_TOKEN.equals(entry.getKey()))
                user.setAtlasRequestToken(properties.get(REQUEST_TOKEN));
            else if (ACCESS_TOKEN.equals(entry.getKey()))
                user.setAtlasAccessToken(properties.get(ACCESS_TOKEN));
            else
                LOG.debug("Wrong property name for user entity");
        }
        user.save();
        return user;
    }

    private TeamsAtlasUser retrieveFirstTeamsAtlasUser(List<TeamsAtlasUser> usersByTeamsId) {
        return usersByTeamsId.get(0);
    }

    @Override
    public List<TeamsAtlasUser> all() {
        return Arrays.asList(activeObjects.find(TeamsAtlasUser.class));
    }

    @Override
    public List<TeamsAtlasUser> getUserByTeamsId(String teamsId) {
        return Arrays.asList(activeObjects.find(TeamsAtlasUser.class, Query.select().where("LOWER(MS_TEAMS_USER_ID) LIKE LOWER(?)", "%" + teamsId + "%")));
    }

    @Override
    public void deleteAoObject(String msTeamsUserId) {
        List<TeamsAtlasUser> users = getUserByTeamsId(msTeamsUserId);
        if (!users.isEmpty()) {
            activeObjects.delete(retrieveFirstTeamsAtlasUser(users));
        }
    }

}
