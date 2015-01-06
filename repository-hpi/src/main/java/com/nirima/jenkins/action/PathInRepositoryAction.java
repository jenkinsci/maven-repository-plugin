package com.nirima.jenkins.action;

import jenkins.model.Jenkins;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PathInRepositoryAction extends RepositoryAction {

    String subPath;

    public PathInRepositoryAction(String subPath) {
        this.subPath = subPath;
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        URL url = new URL(Jenkins.getInstance().getRootUrl());
        url = new URL(url, "plugin/repository");
        url = new URL(url, subPath);

        return url;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        final PathInRepositoryAction rhs = (PathInRepositoryAction) obj;
        return new EqualsBuilder().append(this.subPath, rhs.subPath).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.subPath).toHashCode();
    }

}
