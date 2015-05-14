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
package com.nirima.jenkins;

import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.nirima.jenkins.action.RepositoryAction;

import hudson.model.Cause.UpstreamCause;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.tasks.Fingerprinter;

public final class RepositoryDefinitionPropertyTest {

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void shouldAddSingleUniqueRepositoryAction() throws Exception {
        final SelectionType selectionType = new SelectionTypeUpstream("1");

        final FreeStyleProject projectA = j.createFreeStyleProject("projectA");
        final Run<?, ?> upstreamRun = j.buildAndAssertSuccess(projectA);

        final FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildWrappersList().add(new RepositoryDefinitionProperty(selectionType));
        project.getPublishersList().add(new Fingerprinter("", false));
        project.getPublishersList().add(new Fingerprinter("", false));

        final FreeStyleBuild build = project.scheduleBuild2(0, new UpstreamCause(upstreamRun)).get();
        final List<RepositoryAction> actions = build.getActions(RepositoryAction.class);
        Assert.assertEquals(1, actions.size());
    }

}
