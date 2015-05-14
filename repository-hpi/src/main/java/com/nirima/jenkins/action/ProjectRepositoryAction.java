package com.nirima.jenkins.action;

import jenkins.model.Jenkins;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Action storing build repository URL.
 */
public class ProjectRepositoryAction extends RepositoryAction {

    private static final long serialVersionUID = 1L;

    private String projectName;
    private int    buildNumber;
    private String urlSuffix;

    public ProjectRepositoryAction(String project, int id, String s) {
        projectName = project;
        buildNumber = id;
        urlSuffix = s;
    }



    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    private String getUrlSuffix() {
        return this.urlSuffix;
    }

    public URL getUrl() throws MalformedURLException {
        URL url = new URL(Jenkins.getInstance().getRootUrl());
        url = new URL(url, "plugin/repository/project/");
        url = new URL(url, projectName + "/Build/" + buildNumber + "/" + (urlSuffix!=null?urlSuffix:"") );

        return url;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final ProjectRepositoryAction rhs = (ProjectRepositoryAction) obj;
        return new EqualsBuilder()
                .append(this.getProjectName(), rhs.getProjectName())
                .append(this.getBuildNumber(), rhs.getBuildNumber())
                .append(this.getUrlSuffix(), rhs.getUrlSuffix())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.getProjectName())
                .append(this.getBuildNumber())
                .append(this.getUrlSuffix())
                .toHashCode();
    }

}
