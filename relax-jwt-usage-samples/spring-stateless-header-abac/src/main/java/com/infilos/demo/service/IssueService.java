package com.infilos.demo.service;

import com.infilos.demo.model.Issue;
import com.infilos.demo.model.Project;

import java.util.List;

public interface IssueService {
	public List<Issue> getIssues(Project project);
	public Issue getIssue(Integer id);
	public void createIssue(Issue issue);
	public void updateIssue(Issue issue);
	public void deleteIssue(Issue issue);
}
