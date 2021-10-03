package com.nirima.jenkins.repo.build;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.reporters.MavenArtifact;
import hudson.model.Run;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Ignore;
import org.junit.Test;
public class MetadataRepositoryItemTest {
    @Ignore
    @Test
    public void formatDateVersion_run_formatted() {
        //arrange
        String expected = "19700101.010001-0";
        long timeInMilliSeconds = 1000L;
        Run<?, ?> buildRun = createMavenBuildMock(timeInMilliSeconds);
        //act
        String actual = MetadataRepositoryItem.formatDateVersion(buildRun);
        //assert
        assertEquals(expected, actual);
    }

    
//    @Test
//    public void getContent_artifactWithModificatedDate_formattedInUpdatedElement() throws Exception {
//        //arrange
//        String expected = "<updated>19700101010001</updated>";
//        long timeInMilliSeconds = 1000L;
//        MavenArtifact artifact=new MavenArtifact("","","","","","","");
//        MetadataRepositoryItem testee = new MetadataRepositoryItem(mock(MavenBuild.class), artifact);
//        ArtifactRepositoryItem item = createArtifactRepositoryItemMock(timeInMilliSeconds);
//        testee.addArtifact(artifact, item);
//        //act
//        String actual = testee.generateContent();
//        //assert
//        assertTrue(actual.contains(expected));
//    }

//    private ArtifactRepositoryItem createArtifactRepositoryItemMock(long timeInMilliSeconds) {
//        ArtifactRepositoryItem item=mock(ArtifactRepositoryItem.class);
//        MavenBuild mavenBuild = createMavenBuildMock(timeInMilliSeconds);
//        when(item.getBuild()).thenReturn(mavenBuild);
//        when(item.getLastModified()).thenReturn(mavenBuild.getTime());
//        return item;
//    }


    private MavenBuild createMavenBuildMock(long timeInMilliSeconds) {
        MavenModule job=mock(MavenModule.class);
        Calendar calendar=new GregorianCalendar();
        calendar.setTimeInMillis(timeInMilliSeconds);
        return new MavenBuild(job,calendar);
    }

}
