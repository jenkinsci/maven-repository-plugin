/*
 * The MIT License
 *
 * Copyright (c) 2011, Nigel Magnay / NiRiMa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.nirima.jenkins.repo.project;

import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.build.ProjectBuildRepositoryRoot;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.git.util.BuildData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectBuildList extends AbstractRepositoryDirectory implements RepositoryDirectory {

    private static Logger log = LoggerFactory.getLogger(ProjectBuildList.class);

    public enum Type {
        SHA1,
        Build
    }

    Type type;
    Job item;

    protected ProjectBuildList(RepositoryDirectory parent, Job item, Type type) {
        super(parent);
        this.type = type;
        this.item = item;
    }

    @Override
    public String getName() {
        return type.name();  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Job<?,?> getJob() {
        return item;
    }

    public Collection<ProjectBuildRepositoryRoot> getChildren() {


        if (type == Type.Build) {
            Function<Run, ProjectBuildRepositoryRoot> fn;

            fn = new Function<Run, ProjectBuildRepositoryRoot>() {

                public ProjectBuildRepositoryRoot apply(Run r) {
                    return new ProjectBuildRepositoryRoot(ProjectBuildList.this, r, "" + r.getNumber());
                }
            };

            List<? extends Run> runs = getJob().getBuilds();
            return runs.stream()
                    // Transform builds into items
                    .map(fn)
                    // Remove NULL entries
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } else {

            Map<String, ProjectBuildRepositoryRoot> children = new HashMap<>();

            log.info("Getting builds from {}", item);

            for (Run run : getJob().getBuilds()) {
                try
                {
                    BuildData bd = run.getAction(BuildData.class);
                    if (bd != null && run.getResult() == Result.SUCCESS) {
                        String sha1 = bd.getLastBuiltRevision().getSha1String();

                        // Most recent, only if successful
                        if (!children.containsKey(sha1) && run.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
                            children.put(sha1, new ProjectBuildRepositoryRoot(this, run, sha1));
                        }
                    }
                }
                catch(Exception ex)
                {
                    log.error("Error processing {}", run);
                    log.error("Error", ex);
                }
            }

            return children.values();


        }


    }


}
