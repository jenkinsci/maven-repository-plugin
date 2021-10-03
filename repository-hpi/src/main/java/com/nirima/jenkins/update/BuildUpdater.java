package com.nirima.jenkins.update;

import hudson.model.Run;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by magnayn on 12/08/2015.
 */
public class BuildUpdater {

  Run build;
  RepositoryArtifactRecord bup;

  public BuildUpdater(StaplerRequest req, StaplerResponse rsp) throws IOException {
    InputStream is = req.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);

    BufferedReader br = new BufferedReader(isr);

    build = ProjectReference.read(br).getBuild();
    bup = RepositoryArtifactRecord.parse(br);

    if (build == null) {
      throw new RuntimeException("Build was not found");
    }
  }




  public void execute() {

    RepositoryArtifactRecords records = build.getAction(RepositoryArtifactRecords.class);
    if( records == null )
      records = new RepositoryArtifactRecords();

    records.recordList.add(bup);
    build.addAction(records);

  }
}
