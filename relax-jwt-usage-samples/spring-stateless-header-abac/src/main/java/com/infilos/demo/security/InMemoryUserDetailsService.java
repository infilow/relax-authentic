package com.infilos.demo.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.infilos.demo.model.*;
import com.infilos.demo.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InMemoryUserDetailsService implements UserService {
    private Map<String, ProjectSecurityUser> users = new HashMap<>();

    public InMemoryUserDetailsService() {
    }

    @PostConstruct
    private void init() {
        this.users = new HashMap<>();
        users.put("admin", new ProjectSecurityUser("admin", "password", UserRole.ADMIN));
        users.put("pm1", new ProjectSecurityUser("pm1", "password", UserRole.PM));
        users.put("pm2", new ProjectSecurityUser("pm2", "password", UserRole.PM));
        users.put("dev1", new ProjectSecurityUser("dev1", "password", UserRole.DEVELOPER));
        users.put("dev2", new ProjectSecurityUser("dev2", "password", UserRole.DEVELOPER));
        users.put("test1", new ProjectSecurityUser("test1", "password", UserRole.TESTER));
        users.put("test2", new ProjectSecurityUser("test2", "password", UserRole.TESTER));
    }

    public void createUser(ProjectSecurityUser user) {
        Assert.isTrue(!userExists(user.getName()));

        users.put(user.getName().toLowerCase(), user);
    }

    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    @Override
    public ProjectUser findUserByName(String name) {
        return users.get(name.toLowerCase());
    }

    @Override
    public List<ProjectUser> findUserByProject(Integer projectId) {
        if (projectId == null)
            return null;
        List<ProjectUser> result = new LinkedList<>();
        for (ProjectSecurityUser user : users.values()) {
            Project project = user.getProject();
            if (project != null && projectId.equals(project.getId())) {
                result.add(user);
            }
        }
        return result;
    }
}
