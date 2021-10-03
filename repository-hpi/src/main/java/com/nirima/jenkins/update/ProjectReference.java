package com.nirima.jenkins.update;

import hudson.model.Job;
import hudson.model.Run;

import jenkins.branch.MultiBranchProject;
import jenkins.model.Jenkins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

/**
 * Store a reference to a project.
 */
public class ProjectReference implements Serializable {
  public final String project;
  public final String buildNumber;

  private ProjectReference(String project, String buildNumber) {
    this.project = project;
    this.buildNumber = buildNumber;
  }

  public static ProjectReference read(BufferedReader br) throws IOException {

    String project = br.readLine();
    String buildNumber = br.readLine();
    return new ProjectReference(project, buildNumber);
  }

  public Run getBuild() {
    return getBuild(project, buildNumber);
  }

  private Run getBuild(String project, String buildNumber) {

    if( project.indexOf(' ') > 0 ) {
      String[] elements = project.split(" ");

      if( elements.length == 2 ) {
        // Assume that this is a traditional multi-branch project
        return getMultiBranchProject(elements[0], elements[1], Integer.parseInt(buildNumber));
      } else if( elements.length == 3 ) {
        // This is likely a folder, like github or bitbucket. So we'll just ignore the folder.
        return getMultiBranchProject(elements[1], elements[2], Integer.parseInt(buildNumber));
      }
    }

    Run build = null;
    for (Job j : Jenkins.get().getAllItems(Job.class)) {
      if (j.getName().equals(project)) {
        // Correct job
        build = j.getBuildByNumber(Integer.parseInt(buildNumber));
      }
    }
    return build;
  }

  private Run getMultiBranchProject(String element, String job, int buildNumber) {
    for (MultiBranchProject j : Jenkins.get().getAllItems(MultiBranchProject.class)) {
      if (j.getName().equals(element)) {
        for (Object j2 : j.getAllJobs() )  {
          if (((Job)j2).getName().equals(job)) {
            // Correct job
            return ((Job)j2).getBuildByNumber(buildNumber);
          }
        }
      }
    }
    return null;
  }



}
