package ws.slink.statuspage.servlet;

import com.atlassian.jira.component.pico.ComponentManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import ws.slink.statuspage.service.ConfigService;
import ws.slink.statuspage.tools.Common;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Scanned
public class AdminServlet extends HttpServlet {
    @ComponentImport private final UserManager userManager;
    @ComponentImport private final TemplateRenderer renderer;
    @ComponentImport private final LoginUriProvider loginUriProvider;

    @Inject
    public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            UserKey userKey = userManager.getRemoteUser().getUserKey();
            if (userKey == null || !userManager.isSystemAdmin(userKey)) {
                Common.redirectToLogin(loginUriProvider, request, response);
            } else {
                response.setContentType("text/html;charset=utf-8");

                Collection<String> selectedProjectsParam = ConfigService.instance().getAdminProjects();
                Collection<String> selectedRolesParam    = ConfigService.instance().getAdminRoles();

                ProjectManager projectManager            = ComponentManager.getInstance().getComponentInstanceOfType(ProjectManager.class);
                Collection<Project> allProjects          = projectManager.getProjectObjects();
                Collection<Project> selectedProjects     = allProjects.stream().filter(p ->  selectedProjectsParam.contains(p.getKey())).collect(Collectors.toList());
                Collection<Project> availableProjects    = allProjects.stream().filter(p -> !selectedProjectsParam.contains(p.getKey())).collect(Collectors.toList());

                ProjectRoleManager projectRoleManager    = ComponentManager.getInstance().getComponentInstanceOfType(ProjectRoleManager.class);
                Collection<ProjectRole> allProjectRoles  = projectRoleManager.getProjectRoles();
                Collection<ProjectRole> selectedRoles    = allProjectRoles.stream().filter(p ->  selectedRolesParam.contains(p.getId().toString())).collect(Collectors.toList());
                Collection<ProjectRole> availableRoles   = allProjectRoles.stream().filter(p -> !selectedRolesParam.contains(p.getId().toString())).collect(Collectors.toList());

                Map<String, Object> context = new HashMap<>();
                context.put("availableProjects", availableProjects);
                context.put("selectedProjects" , selectedProjects);
                context.put("availableRoles"   , availableRoles);
                context.put("selectedRoles"    , selectedRoles);
                context.put("customFieldName"    , ConfigService.instance().getAdminCustomFieldName());

                renderer.render("templates/admin.vm", context, response.getWriter());
            }
        } catch (Exception e) {
            Common.redirectToLogin(loginUriProvider, request, response);
        }
    }

}
