package org.jenkinsci.plugins.rallyBuild.rallyActions;

import hudson.model.BuildListener;

import java.io.IOException;

import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;

import com.rallydev.rest.RallyRestApi;

public interface Action {
	
	public void execute(UpdateArtifact artifact, RallyRestApi restApi, BuildListener listener)throws IOException;
	
	public Boolean isExecuted(UpdateArtifact artifact, RallyRestApi restApi, BuildListener listener)throws IOException;
}
