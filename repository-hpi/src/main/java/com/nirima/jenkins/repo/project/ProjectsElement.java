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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import hudson.maven.MavenModule;
import hudson.model.BuildableItem;
import hudson.model.BuildableItemWithBuildWrappers;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import hudson.model.Hudson;
import jenkins.branch.MultiBranchProject;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class ProjectsElement extends AbstractRepositoryDirectory implements RepositoryDirectory {
    public ProjectsElement(RepositoryDirectory parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "project";
    }

    @Nonnull
    public Collection<RepositoryElement> getChildren() {

        return ProjectUtils.getChildren(this,
                Collections2.filter(Jenkins.getInstance().getAllItems(BuildableItem.class), new Predicate<BuildableItem>() {
                    @Override
                    public boolean apply(@Nullable BuildableItem input) {
                        if( input == null )
                            return false;

                        // top level only.
                        if( input.getParent() instanceof Hudson)
                            return true;

                        if( input.getParent() instanceof jenkins.branch.OrganizationFolder )
                            return true;

                        return false;
                    }
                }));

    }

    public RepositoryElement getChild(String element) {
         for( RepositoryElement e : getChildren())
        {
            if( e.getName().equals(element) )
                return e;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return "ProjectsElement{}";
    }
}
