package com.nirima.jenkins.action.ProjectRepositoryAction;

import lib.LayoutTagLib;

l=namespace(LayoutTagLib)


t = namespace(lib.JenkinsTagLib.class)

st=namespace("jelly:stapler")
f=namespace("/lib/form")

t.summary(icon:"/plugin/repository/static/icons/repository.svg") {

    p(){
        b("Upstream URL:");
        raw(_(" ${my.url}"));
    };


}



