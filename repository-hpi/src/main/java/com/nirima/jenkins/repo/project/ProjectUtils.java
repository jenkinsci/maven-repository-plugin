package com.nirima.jenkins.repo.project;

import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;

import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Job;

import jenkins.branch.MultiBranchProject;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by magnayn on 12/10/2015.
 */
public class ProjectUtils {

    public static Collection<RepositoryElement> getChildren(final RepositoryDirectory parent, final Collection<?> items) {
        return items.stream().map(
                new Function<Object, RepositoryElement>() {
                    public RepositoryElement apply(Object from) {
                        if (from instanceof BuildableItemWithBuildWrappers) {
                            return new ProjectElement(parent, ((BuildableItemWithBuildWrappers) from).asProject());
                        }
                        if (from instanceof MultiBranchProject) {
                            return new MultiBranchProjectElement(parent, (MultiBranchProject) from);
                        }
                        if (from instanceof Job) {
                            return new ProjectElement(parent, (Job) from);
                        }

                        return null;
                    }
                })
                // Squash ones we couldn't sensibly find an element for.
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String sanitizeName(String name) {
        return name.replace("/","-").replace("%2F","-");
    }
}
