package com.infilos.demo.security;

import com.infilos.demo.model.*;


public class ProjectSecurityUser implements ProjectUser {
    private Project project;
    private String username;
    private String password;
    private UserRole role;

    public ProjectSecurityUser(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public ProjectSecurityUser(String username, String password, Project project, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public UserRole getRole() {
        return this.role;
    }

    @Override
    public void setRole(UserRole role) {
        this.role = role;

    }

    @Override
    public String getName() {
        return username;
    }
}
