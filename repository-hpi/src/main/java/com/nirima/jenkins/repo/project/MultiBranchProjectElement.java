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
import com.nirima.jenkins.repo.RepositoryElement;

import hudson.model.Job;

import jenkins.branch.MultiBranchProject;

import java.util.Collection;

public class MultiBranchProjectElement extends AbstractRepositoryDirectory implements RepositoryDirectory {

    MultiBranchProject item;

    public MultiBranchProjectElement(RepositoryDirectory parent, MultiBranchProject project)
    {
        super(parent);
        if( project == null )
            throw new IllegalArgumentException("project must not be null");

        this.item = project;
    }

    public @Override Collection<? extends RepositoryElement> getChildren() {

        Collection<Job> jobs = item.getAllJobs();

        return ProjectUtils.getChildren(this, jobs );

    }

    public String getName() {
       return ProjectUtils.sanitizeName(item.getName());
    }

    public String getDescription() {
        return "Project " + getName();
    }

    @Override
    public String toString() {
        return "ProjectElement{" + getName() + "}";
    }
}
