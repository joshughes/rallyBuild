package org.jenkinsci.plugins.rallyBuild.rallyActions;

import hudson.model.BuildListener;

import java.io.IOException;
import java.util.logging.Logger;

import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;

public class ReadyAction extends RallyAction {
	private final Boolean ready;
	private static final Logger logger = Logger.getLogger(ReadyAction.class.getName());
	public ReadyAction (Boolean ready){
		this.ready=ready;
	}
	
	@Override
	public void execute(UpdateArtifact artifact, RallyRestApi restApi, BuildListener listener)
			throws IOException {
		 listener.getLogger().println("Setting "+artifact.getObjectId() +" to Ready= "+ready);
		 logger.info("Setting "+artifact.getObjectId() +" to Ready= "+ready);
		 JsonObject updatedArtifact = new JsonObject(); 
	     updatedArtifact.addProperty("Ready", ready);
	     updateArtifact(artifact,updatedArtifact,restApi,listener);
	}

	@Override
	public Boolean isExecuted(UpdateArtifact artifact, RallyRestApi restApi,
			BuildListener listener) throws IOException {
		JsonObject currentArtifact =this.getArtifact(artifact, restApi);
		Boolean currentReadiness = currentArtifact.get("Ready").getAsBoolean();
		if(currentReadiness==this.ready){
			listener.getLogger().println("Artifact is already ready status "+this.ready+" so not updating.");
			logger.info("Artifact is already ready status "+this.ready);
			return true;
		}
		listener.getLogger().println("Artifact is not ready status "+this.ready+" so updating.");
		logger.info("Artifact is not ready status "+this.ready);
		return false;
	}

}
