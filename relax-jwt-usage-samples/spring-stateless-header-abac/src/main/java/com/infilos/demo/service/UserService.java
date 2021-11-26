package com.infilos.demo.service;

import com.infilos.demo.model.ProjectUser;

import java.util.List;

public interface UserService {
	ProjectUser findUserByName(String name);
	List<ProjectUser> findUserByProject(Integer projectId);
}
