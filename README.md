#RallyBuild 
A project for updating rally through Jenkins Builds

###Configurataion
####Configuration
#####Global Configuration 
**Rally Server Url** - Should be set to the url where your rally instance resides

**Rally Username** - Should be set to the user you want Jenkins to use to update Rally 

**Rally Password** - Password for the configured Rally user

####Preconditions
You can configure RallyBuild to look at an issue and see if the following criteria are met

* Artifact has a certain state
* Arfifact has a certain comment in the disccusion
* Arfifact is in a ready state

####Avaliable Actions 
RallyBuild can update an Artifact with the following 

* Issue a new comment to the discussion 
* Update an Artifact's Schedule State 
* Update a Defect's State 
* Set an Artifact's Ready State


###To Build RallyBuild
	mvn clean install

Find the rallyBuild.hpi file in the target directory 
Install the hpi file through the "Advanced" tab in the Jenkins Plugin
Management tool. 
