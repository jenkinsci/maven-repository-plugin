package com.nirima.jenkins.update;

import hudson.maven.reporters.MavenArtifact;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by magnayn on 01/11/15.
 */
public class RepositoryArtifactRecord implements Serializable {
  public List<MavenArtifact> attachedArtifacts = new ArrayList<MavenArtifact>();
  public MavenArtifact mainArtifact;
  public MavenArtifact pomArtifact;
  public String repositoryUrl;
  public String repositoryId;
  public Map<MavenArtifact, File> fileMap = new HashMap<MavenArtifact, File>();

  public static RepositoryArtifactRecord parse(BufferedReader br) throws IOException {
    // Get the job


    RepositoryArtifactRecord record = new RepositoryArtifactRecord();

    String line;
    while ((line = br.readLine()) != null) {
      if (line.equals("[pom]")) {
        record.pomArtifact = record.parseArtifact(br).get(0);
      } else if (line.equals("[main]")) {
        List<MavenArtifact> artifacts = record.parseArtifact(br);

        record.mainArtifact = artifacts.get(0);
        for(int i=1;i<artifacts.size();i++)
          record.attachedArtifacts.add(artifacts.get(i));

      } else if (line.equals("[attached]") || line.equals("[artifact]")) {
        record.attachedArtifacts.addAll(record.parseArtifact(br));
      } else if (line.equals("[repositoryUrl]")) {
        record.repositoryUrl = br.readLine();
      } else if (line.equals("[repositoryId]")) {
        record.repositoryId = br.readLine();
      } else {
        System.out.println("Unexpected line: " + line);
      }
    }
    return record;
  }

  private RepositoryArtifactRecord() {

  }

  private List<MavenArtifact> parseArtifact(BufferedReader br) throws IOException {

    String file = br.readLine();
    String groupId = br.readLine();
    String artifactId = br.readLine();
    String version = br.readLine();
    String classifier = br.readLine();
    String type = br.readLine();
    String fileName = br.readLine();
    String md5sum = br.readLine();

    List<MavenArtifact> result = new ArrayList<MavenArtifact>();

    MavenArtifact a = new MavenArtifact(groupId, artifactId, version, classifier, type, fileName, md5sum);
    fileMap.put(a, new File(file) );
    result.add(a);
    // Seems type may be reported as (say) maven-plugin and artifact as .jar, but it's being
    // stored as .maven-plugin

    int idx = fileName.lastIndexOf('.');
    if( idx > -1 ) {
      String fileType = fileName.substring(idx+1);
      if( !fileType.equals(type) ) {
        MavenArtifact b = new MavenArtifact(groupId, artifactId, version, classifier, fileType, fileName, md5sum);
        fileMap.put(b, new File(file) );
        result.add(b);
      }
    }


    return result;
  }

  @Override
  public String toString() {
    return "RepositoryArtifactRecord{"
        + "attachedArtifacts={"
        + attachedArtifacts
        + "}, mainArtifact="
        + mainArtifact
        + ", pomArtifact="
        + pomArtifact
        + ", repositoryUrl="
        + repositoryUrl
        + ", repositoryId="
        + repositoryId
        + ", fileMap={"
        + fileMap
        + "}}";
  }
}
