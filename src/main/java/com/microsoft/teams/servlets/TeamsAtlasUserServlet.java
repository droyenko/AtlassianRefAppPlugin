package com.microsoft.teams.servlets;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.microsoft.teams.ao.TeamsAtlasUser;
import com.microsoft.teams.service.TeamsAtlasUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.microsoft.teams.oauth.PropertiesClient.*;

//TODO remove this servlet on production

@Scanned
public class TeamsAtlasUserServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TeamsAtlasUserServlet.class);

    private final TeamsAtlasUserService userService;

    public TeamsAtlasUserServlet(TeamsAtlasUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final PrintWriter w = resp.getWriter();
            w.write("<h1>Teams-Atlas Mapping</h1>");
            w.write("<form method=\"post\">");
            w.write("MsTeamsUserID:<br>");
            w.write("<input type=\"text\" name=\"MsTeamsUserId\" size=\"25\"/><br>");
            w.write("AtlasSecret:<br>");
            w.write("<input type=\"text\" name=\"AtlasSecret\" size=\"25\"/><br>");
            w.write("AtlasRequestToken:<br>");
            w.write("<input type=\"text\" name=\"AtlasRequestToken\" size=\"25\"/><br>");
            w.write("AtlasAccessToken:<br>");
            w.write("<input type=\"text\" name=\"AtlasAccessToken\" size=\"25\"/><br>");
            w.write("  ");
            w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
            w.write("</form>");

            w.write("<table>");
            w.write("<tr>");
            w.write("<th>MsTeamsUserId</th>");
            w.write("<th>AtlasSecret</th>");
            w.write("<th>AtlasRequestToken</th>");
            w.write("<th>AtlasAccessToken</th>");
            w.write("</tr>");
            for (TeamsAtlasUser user : userService.all()) {
                w.write("<tr>");
                w.printf("<td> %s </td>", user.getMsTeamsUserId());
                w.printf("<td> %s </td>", user.getAtlasSecret());
                w.printf("<td> %s </td>", user.getAtlasRequestToken());
                w.printf("<td> %s </td>", user.getAtlasAccessToken());
                w.write("</tr>");
            }

            w.write("</table>");
            w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");

            w.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String> properties = new HashMap<>();
            properties.put(TEAMS_ID, req.getParameter("MsTeamsUserId"));
            properties.put(SECRET, req.getParameter("AtlasSecret"));
            properties.put(REQUEST_TOKEN, req.getParameter("AtlasRequestToken"));
            properties.put(ACCESS_TOKEN, req.getParameter("AtlasAccessToken"));
            userService.add(properties);

            resp.sendRedirect(req.getContextPath() + "/plugins/servlet/user/mapping");
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
