package ws.slink.statuspage.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigService {

    private static class PluginConfigServiceSingleton {
        private static final ConfigService INSTANCE = new ConfigService();
    }
    public static ConfigService instance () {
        return PluginConfigServiceSingleton.INSTANCE;
    }

    public static final String CONFIG_PREFIX                  = "ws.slink.status-page-plugin";
    public static final String CONFIG_ADMIN_PROJECTS          = "admin.projects";
    public static final String CONFIG_ADMIN_ROLES             = "admin.roles";
    public static final String CONFIG_ADMIN_CUSTOM_FIELD_NAME = "admin.custom_field_name";
    public static final String CONFIG_MGMT_ROLES              = "config.mgmt.roles";
    public static final String CONFIG_VIEW_ROLES              = "config.view.roles";
    public static final String CONFIG_API_KEY                 = "config.api.key";

    private static final String DEFAULT_CUSTOM_FIELD_ID       = "status-page-incident";

    private PluginSettings pluginSettings;

    private ConfigService() {
//        System.out.println("---- created config service");
    }

    public void setPluginSettings(PluginSettings pluginSettings) {
        if (null == this.pluginSettings)
            this.pluginSettings = pluginSettings;
    }

    public Collection<String> getAdminProjects() { // returns list of projectKey
        return getListParam(CONFIG_ADMIN_PROJECTS);
    }
    public Collection<String> getAdminRoles() { // returns list of roleId
        return getListParam(CONFIG_ADMIN_ROLES);
    }
    public void setAdminProjects(String projects) {
        pluginSettings.put(CONFIG_PREFIX + "." + CONFIG_ADMIN_PROJECTS, projects);
    }
    public void setAdminRoles(String roles) {
        pluginSettings.put(CONFIG_PREFIX + "." + CONFIG_ADMIN_ROLES, roles);
    }
    public String getAdminCustomFieldName() {
        String result = getParam(CONFIG_ADMIN_CUSTOM_FIELD_NAME);
        if (StringUtils.isBlank(result))
            return DEFAULT_CUSTOM_FIELD_ID;
        else
            return result;
    }
    public void setAdminCustomFieldName(String value) {
        String key = CONFIG_PREFIX + "." + CONFIG_ADMIN_CUSTOM_FIELD_NAME;
        System.out.println("--- SET " + key + " : " + value);
        pluginSettings.put(key, value);
    }

    public Collection<String> getConfigMgmtRoles(String projectKey) {
        return getListParam(CONFIG_MGMT_ROLES + "." + projectKey);
    }
    public void setConfigMgmtRoles(String projectKey, String roles) {
        pluginSettings.put(CONFIG_PREFIX + "." + CONFIG_MGMT_ROLES + "." + projectKey, roles);
    }
    public Collection<String> getConfigViewRoles(String projectKey) {
        return getListParam(CONFIG_VIEW_ROLES + "." + projectKey);
    }
    public void setConfigViewRoles(String projectKey, String roles) {
        pluginSettings.put(CONFIG_PREFIX + "." + CONFIG_VIEW_ROLES + "." + projectKey, roles);
    }
    public String getConfigApiKey(String projectKey) {
        String value = getParam(CONFIG_API_KEY + "." + projectKey);
        return value;
    }
    public void setConfigApiKey(String projectKey, String value) {
        String key = CONFIG_PREFIX + "." + CONFIG_API_KEY + "." + projectKey;
        System.out.println("--- SET " + key + " : " + value);
        pluginSettings.put(key, value);
    }

    private List<String> getListParam(String param) {
        try {
            Object value = pluginSettings.get(CONFIG_PREFIX + "." + param);
            if (null == value || StringUtils.isBlank(value.toString())) {
                return Collections.EMPTY_LIST;
            } else {
                return Arrays.stream(value.toString().split(",")).map(s -> s.trim()).collect(Collectors.toList());
            }
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }
    private String getParam(String param) {
        String key = CONFIG_PREFIX + "." + param;
        String value = (String) pluginSettings.get(key);
//        System.out.println("--- GET " + key + " : " + value);
        if (StringUtils.isBlank(value))
            return "";
        else
            return value;
    }

/*
    // --- administration
    public String getRoles() {
        return (String) pluginSettings.get(CONFIG_PREFIX + ".roles");
    }
    public String getProjects() {
        return (String) pluginSettings.get(CONFIG_PREFIX + ".projects");
    }
    public List<String> rolesList() {
        return getListParam("roles");
    }
    public List<String> projectsList() {
        return getListParam("projects");
    }

    // --- configuration
    public String getList(String projectKey, int id) {
        return getConfigValue(projectKey, ".list" + id);
    }
    public void setList(String projectKey, int id, String value) {
        setConfigValue(projectKey, ".list" + id, setString(value, "", ""));
    }
    public String getStyle(String projectKey, int id) {
        return getConfigValue(projectKey, ".style" + id);
    }
    public void setStyle(String projectKey, int id, String value) {
        setConfigValue(projectKey, ".style" + id, value);
    }
    public String getText(String projectKey, int id) {
        return getConfigValue(projectKey, ".text" + id);
    }
    public void setText(String projectKey, int id, String value) {
        setConfigValue(projectKey, ".text" + id, setString(value, "", ""));
    }
    public String getViewers(String projectKey) {
        return getConfigValue(projectKey, ".viewers");
    }
    public void setViewers(String projectKey, String value) {
        setConfigValue(projectKey, ".viewers", setString(value, "", ""));
    }
    public String getColor(String projectKey, int id) {
        return getConfigValue(projectKey, ".color" + id);
    }
    public void setColor(String projectKey, int id, String value) {
        if (value.startsWith("#"))
            setConfigValue(projectKey, ".color" + id, value);
        else
            setConfigValue(projectKey, ".color" + id, "#" + value.trim());
    }

    // --- tools
    private String setString(String value, String defaultValue, String newLineReplacement) {
        if (null == value || value.isEmpty())
            return defaultValue;
        else
            return value
                .replaceAll(" +", " ")
                .replaceAll(";" , " ")
                .replaceAll("," , " ")
                .replaceAll("\n", newLineReplacement);
    }
    private String getConfigValue(String projectKey, String key) {
        String result = (StringUtils.isBlank(projectKey))
                      ? (String) pluginSettings.get(CONFIG_PREFIX + key)
                      : (String) pluginSettings.get(CONFIG_PREFIX + "." + projectKey + key);
        return StringUtils.isBlank(result) ? "" : result;
    }
    private void setConfigValue(String projectKey, String key, String value) {
        String cfgKey = (StringUtils.isBlank(projectKey))
                      ? CONFIG_PREFIX + "key"
                      : CONFIG_PREFIX + "." + projectKey + key;
        pluginSettings.put(cfgKey, value);
    }
*/

}
