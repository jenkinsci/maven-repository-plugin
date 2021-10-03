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

import com.nirima.jenkins.bridge.BridgeRepository;
import com.nirima.jenkins.repo.RepositoryContent;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.update.BuildUpdater;
import com.nirima.jenkins.webdav.impl.MethodFactory;
import com.nirima.jenkins.webdav.impl.ServletContextMimeTypeResolver;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;
import com.nirima.jenkins.webdav.interfaces.IMethod;
import com.nirima.jenkins.webdav.interfaces.IMethodFactory;

import hudson.Extension;
import hudson.Functions;
import hudson.Plugin;
import hudson.model.Build;
import hudson.model.Project;
import hudson.model.RootAction;
import hudson.plugins.git.util.BuildData;

import jenkins.model.Jenkins;

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@Extension
public class RepositoryPlugin extends Plugin implements RootAction, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryPlugin.class);

    public String getIconFileName() {
        return Functions.getResourcePath()+"/plugin/repository/static/icons/repository.svg";
    }

    public String getDisplayName() {
        return "Maven Repository";
    }

    public String getUrlName() {
        return "plugin/repository";
    }

    private ServletContext context;

    private IMethodFactory methodFactory = new MethodFactory();

    public RepositoryPlugin() {

    }

    @Override
    public void start() {
        // Unpack tools
        File root = new File(Jenkins.get().getRootDir(), "repositoryPlugin");
        root.mkdirs();


        String file = this.getClass().getResource('/'+this.getClass().getName().replace('.', '/')+".class").getFile();


        if( file.startsWith("file:") )
            file = file.substring(5,file.indexOf('!'));

        try {
            logger.info("Expanding " + file + " into " + root);
            expand(new File(file), root, "tools");
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void expand(File jarFile, File destDir, String prefix) throws IOException {
        java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
        java.util.Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();

            if(!file.getName().startsWith(prefix))
                continue;

            java.io.File f = new java.io.File(destDir, file.getName());
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(file); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
    }

    public void setServletContext(ServletContext context) {
        this.context = context;
    }

    @Override
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        String path = req.getRestOfPath();
        String fullPath = req.getPathInfo();
        if (path.length() == 0)
            path = "/";

        if( req.getMethod().equals("POST") && path.startsWith("/add_info") ) {
          BuildUpdater bu = new BuildUpdater(req,rsp);
          bu.execute();
          return;
        }

        if (path.contains("..") || path.length() < 1) {
            // don't serve anything other than files in the sub directory.
            rsp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if( path.startsWith("/static") ) {
            super.doDynamic(req, rsp);
            return;
        }

        serveRequest(new BridgeRepository(null), req.getContextPath()+"/plugin/repository");
    }

    public void serveRequest(IDavRepo repo, String root) {
        StaplerRequest req = Stapler.getCurrentRequest();
        StaplerResponse rsp = Stapler.getCurrentResponse();
        try
        {
            if (repo.getMimeTypeResolver() == null)
            {
                ServletContextMimeTypeResolver ctx = new ServletContextMimeTypeResolver();
                ctx.setServletContext(req.getSession().getServletContext());
                repo.setMimeTypeResolver(ctx);
            }
            IMethod method = methodFactory.createMethod(req, rsp);
            method.init(req, rsp, null, repo, root);
            method.invoke();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error trying to serve request");
            //s_logger.error(e.getMessage());
            //s_logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private void displayElement(StaplerRequest req, StaplerResponse rsp, RepositoryElement currentItem) throws Exception {
        OutputStream os = rsp.getOutputStream();

        if (currentItem instanceof RepositoryDirectory) {

            rsp.setContentType("text/html;charset=UTF-8");

            printHeader(os, req, (RepositoryDirectory) currentItem);

            for (RepositoryElement element : ((RepositoryDirectory) currentItem).getChildren()) {
                printDirEntry(os, element);
            }

            printFooter(os);
        } else {
            RepositoryContent content = (RepositoryContent) currentItem;
            String contentType = content.getContentType();
            if( contentType != null )
                rsp.setContentType(contentType);

            InputStream is = content.getContent();
            // DL Element
            IOUtils.copy(is, os);

            os.flush();

        }
    }


    private Build getBuild(Project theProject, String type, String ref) {
        if (type.equals("build")) {
            int nbr = Integer.parseInt(ref);

            for (Object object : theProject.getBuilds()) {
                Build r = (Build) object;
                if (r.getNumber() == nbr)
                    return r;
            }
        } else {
            for (Object object : theProject.getBuilds()) {
                Build r = (Build) object;
                BuildData bd = r.getAction(BuildData.class);
                if (bd != null && bd.getLastBuiltRevision().getSha1String().equals(ref))
                    return r;
            }
        }

        return null;
    }


    private void printHeader(OutputStream os,StaplerRequest req, RepositoryDirectory directory) throws IOException {
        String title = "<html>\n" +
                "  <head>\n" +
                "    <title>Index of " + directory.getPath() + "</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
                "    <link rel=\"stylesheet\" href=\"" + req.getContextPath() + "/plugin/repository/css/repository-style.css\" type=\"text/css\" media=\"screen\" title=\"no title\" charset=\"utf-8\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Index of " + directory.getPath() + "</h1>\n" +
                "    <table cellspacing=\"10\">\n" +
                "      <tr>\n" +
                "        <th align=\"left\">Name</th>\n" +
                "        <th>Last Modified</th>\n" +
                "        <th>Size</th>\n" +
                "        <th>Description</th>\n" +
                "      </tr>";

        String parent = "<tr>\n" +
                "        <td>\n" +
                "          <a href=\"../\">Parent Directory</a>\n" +
                "        </td>\n" +
                "      </tr>";

        os.write(title.getBytes(StandardCharsets.UTF_8));

        if (directory.getParent() != null) {
            os.write(parent.getBytes(StandardCharsets.UTF_8));
        }


    }

    private void printFooter(OutputStream os) throws IOException {
        String footer =
                "            </table>\n" +
                        "  </body>\n" +
                        "</html>";

        os.write(footer.getBytes(StandardCharsets.UTF_8));
    }

    private void printDirEntry(OutputStream os, RepositoryElement item) throws IOException {

        String name = item.getName();
        String lastModified = "";
        String size = "";
        String description = "";

        if (item instanceof RepositoryDirectory)
            name += "/";
        if ( item instanceof RepositoryContent)
        {
         //   lastModified = ((RepositoryContent)item).getLastModified();
            size =  "" + ((RepositoryContent)item).getSize();
        }

        description =   item.getDescription();

         String entry = "      <tr>\n" +
                "            <td>\n" +
                "                              <a href=\"" + name + "\">" + name + "</a>\n" +
                "                          </td>\n" +
                "            <td>\n" +
                "              " + lastModified + "\n" +
                "            </td>\n" +
                "            <td align=\"right\">\n" +
                "                   " + size + "\n" +
                "                          </td>\n" +
                "            <td>\n" +
                "              " + description + "\n" +
                "            </td>\n" +
                "          </tr>";

        os.write(entry.getBytes(StandardCharsets.UTF_8));

    }


    private Project getProject(String pathElement) {
        for (Project project : Jenkins.get().getProjects()) {
            if (project.getName().equals(pathElement))
                return project;
        }
        return null;
    }


    public static String DISPLAY_NAME = "Jenkins Maven Repository Server";
    public static String URL = "repository";
}
