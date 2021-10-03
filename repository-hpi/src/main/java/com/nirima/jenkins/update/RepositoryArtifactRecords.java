package com.nirima.jenkins.update;

import hudson.model.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magnayn on 12/08/2015.
 */
public class RepositoryArtifactRecords implements Action, Serializable {

  public List<RepositoryArtifactRecord> recordList = new ArrayList<>();

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "Artifacts";
  }

  @Override
  public String getUrlName() {
    return null;
  }

}
