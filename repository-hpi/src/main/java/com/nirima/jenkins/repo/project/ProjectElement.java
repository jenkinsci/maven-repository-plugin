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

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.repo.build.ProjectBuildRepositoryRoot;

import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Run;

public class ProjectElement extends AbstractRepositoryDirectory implements RepositoryDirectory {

    private final Item item;

    public ProjectElement(final RepositoryDirectory parent, final Item project) {
        super(parent);
        if (project == null) {
            throw new IllegalArgumentException("project must not be null");
        }

        this.item = project;
    }

    @Override
    public Collection<? extends RepositoryElement> getChildren() {
        if (this.item instanceof BuildableItemWithBuildWrappers) {
            final BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) this.item;
            final List<RepositoryElement> children = Lists.<RepositoryElement>newArrayList(
                    new ProjectBuildList(this, bi, ProjectBuildList.Type.SHA1),
                    new ProjectBuildList(this, bi, ProjectBuildList.Type.Build)
            );

            final Run<?, ?> lastSuccessfulBuild = bi.asProject().getLastSuccessfulBuild();
            if (lastSuccessfulBuild != null) {
                children.add(new ProjectBuildRepositoryRoot(this, lastSuccessfulBuild, "LastSuccessful"));
            }

            return children;
        }
        if (this.item instanceof ItemGroup<?>) {
            final ItemGroup<?> group = (ItemGroup<?>) this.item;
            return Collections2.transform(group.getItems(), new Function<Item, RepositoryElement>() {

                public RepositoryElement apply(final Item input) {
                    return new ProjectElement(ProjectElement.this, input);
                }

            });
        }

        throw new IllegalStateException("unsupported type [" + this.item.getClass() + "].");
    }

    public String getName() {
        return this.item.getName();
    }

    @Override
    public String getDescription() {
        return "Project " + item.getName();
    }

    @Override
    public String toString() {
        return "ProjectElement{" + item.getName() + "}";
    }

}
