#RallyBuild 
A project for updating rally through Jenkins Builds. The goal being that your daily coding activities, and the builds those activies trigger, will keep your tickets up to date for you.

##Configurataion

####Global Configuration 
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


###Example of use with GitFlow
####Move ticket to in progress 
1. Create a branch with your formatted artifact id in the branch name. `US1232-Some-Story`
2. Jenkins job that builds new branches and passes branch name to RallyBuild
![image](https://raw.github.com/joshughes/rallyBuild/gh-pages/images/NewBranchBuildJob.png)

Now the branch has been found by Jenkins and a comment with the branch name is in the Rally artifact and it has been moved to In-Progress

####Update the ticket with pull request infromation 
1. Install the [GitHub Pull Request Builder](https://github.com/jenkinsci/ghprb-plugin)
2. Setup the pull request job to comment on the ticket with the pull request infromation and also ensure the ticket is in the In-Progress State. 

![image](https://raw.github.com/joshughes/rallyBuild/gh-pages/images/PullRequestJob.png)

Now the ticket has infromation about the pull request and any memeber of the team can find the branch and the pull request to review the code. 

####Detect branches being merged to develop/master
1. Setup Jenkins Job like the following 

![image](https://raw.github.com/joshughes/rallyBuild/gh-pages/images/MergeDetectJob.png)

This job will find changes in your main branch since the last build. It passes those commits to RallyBuild which finds artifacts and sets them to Ready. If the artifact is a defect we also mark that defect as fixed. 


###To Build RallyBuild
	mvn clean install

Find the rallyBuild.hpi file in the target directory 
Install the hpi file through the "Advanced" tab in the Jenkins Plugin
Management tool. 


