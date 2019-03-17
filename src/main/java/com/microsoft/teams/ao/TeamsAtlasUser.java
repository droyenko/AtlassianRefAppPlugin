package com.microsoft.teams.ao;

import net.java.ao.Entity;

public interface TeamsAtlasUser extends Entity {

    String getMsTeamsUserId();

    void setMsTeamsUserId(String msTeamsUserId);

    String getAtlasSecret();

    void setAtlasSecret(String atlasSecret);

    String getAtlasRequestToken();

    void setAtlasRequestToken(String atlasRequestToken);

    String getAtlasAccessToken();

    void setAtlasAccessToken(String atlasAccessToken);
}
