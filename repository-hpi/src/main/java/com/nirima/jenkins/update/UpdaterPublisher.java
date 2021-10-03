package com.nirima.jenkins.update;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.maven.reporters.MavenArtifact;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import jenkins.model.ArtifactManager;
import jenkins.tasks.SimpleBuildStep;
import jenkins.util.BuildListenerAdapter;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Archive all the items that we were told about.
 */
public class UpdaterPublisher extends Recorder implements Serializable, SimpleBuildStep {

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }


  @DataBoundConstructor
  public UpdaterPublisher() {

  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {

    Execution e = new Execution(build, launcher, listener);

    try {
      e.execute();
    } catch(Exception ex) {
      ex.printStackTrace();
      return false;
    }

    return true;


  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    Execution e = new Execution(run, launcher, listener, workspace);

    try {
      e.execute();
    } catch(Exception ex) {
      ex.printStackTrace();

    }

  }

  public static class Execution {
    final Run<?, ?> build;
    final Launcher launcher;
    final BuildListener listener;
    ArtifactManager am;
    final FilePath workspace;

    public Execution(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
        throws IOException {
      this.build = build;
      this.launcher = launcher;
      this.listener = listener;
      am = build.pickArtifactManager();
      workspace = build.getWorkspace();
    }

    public Execution(Run<?, ?> run, Launcher launcher, TaskListener listener, FilePath workspace)
        throws IOException {
      this.build = run;
      this.launcher = launcher;
      this.listener = new BuildListenerAdapter(listener);
      am = build.pickArtifactManager();
      this.workspace = workspace;
    }

    public void execute() throws IOException, InterruptedException {

      Map<String,String> artifacts = new HashMap<>();

      RepositoryArtifactRecords mar = build.getAction(RepositoryArtifactRecords.class);
      if( mar == null )
        return;

      for(RepositoryArtifactRecord xmar : mar.recordList)
      {
        for(MavenArtifact a : xmar.fileMap.keySet()) {
          File f = xmar.fileMap.get(a);

          // Archive area - > workspace path
          String archivePath = a.groupId + "/" + a.artifactId + "/" + a.version + "/" + a.canonicalName;
          artifacts.put(archivePath, f.toString().substring(workspace.toString().length()+1) );
        }
      }


      am.archive(workspace, launcher, listener, artifacts);

    }
  }




  @Extension
  public static final class DescriptorImpl extends
                                           BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Publish Maven Artifacts";
    }
  }
  }
