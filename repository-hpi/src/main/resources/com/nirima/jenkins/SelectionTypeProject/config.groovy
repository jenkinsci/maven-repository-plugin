package com.nirima.jenkins

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")


f.entry(title:_("Project"), field:"project") {
     select(name:"project","class":"setting-input") {
        descriptor.getJobs().each() { item ->
            f.option(selected:item.fullName==instance?.project, value:item.fullName, _(item.fullDisplayName))
        }
     }
}

f.entry(title:_("Build"), field:"build") {
     select(name:"build","class":"setting-input") {
        f.option(selected:instance?.build=="repository", value:"repository", _("Last Successful / repository"))
        f.option(selected:instance?.build=="repositoryChain", value:"repositoryChain", _("Last Successful / repositoryChain"))
        f.option(selected:instance?.build=="promotedRepository", value:"promotedRepository", _("Promoted Build / repository"))
        f.option(selected:instance?.build=="promotedRepositoryChain", value:"promotedRepositoryChain", _("Promoted Build / repositoryChain"))
    }
}

f.entry(title:_("Promoted Build"), field:"promoted") {
    f.textbox()
}