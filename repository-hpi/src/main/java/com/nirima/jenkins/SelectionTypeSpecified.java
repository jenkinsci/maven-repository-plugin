package com.nirima.jenkins;

import com.nirima.jenkins.action.PathInRepositoryAction;
import com.nirima.jenkins.action.RepositoryAction;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Run;

import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;

public class SelectionTypeSpecified extends SelectionType  {
    public String path;

    @DataBoundConstructor
    public SelectionTypeSpecified(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public RepositoryAction getAction(Run<?,?> build) throws MalformedURLException, RepositoryDoesNotExistException {
        return new PathInRepositoryAction(path);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SelectionType> {

         @Override
         public String getDisplayName() {
             return "Specified Path in Repository";
         }
     }
}
