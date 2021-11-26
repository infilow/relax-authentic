package com.infilos.demo.service;

import com.infilos.demo.model.Project;

import java.util.List;

public interface ProjectService {
	public List<Project> getProjects();
	public Project getProject(Integer id);
	public void createProject(Project project);
	public void updateProject(Project project);
	public void deleteProject(Project project);
}
