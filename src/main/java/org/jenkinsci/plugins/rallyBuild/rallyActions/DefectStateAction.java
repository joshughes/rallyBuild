package org.jenkinsci.plugins.rallyBuild.rallyActions;

import hudson.model.BuildListener;

import java.io.IOException;
import java.util.logging.Logger;

import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;

public class DefectStateAction extends RallyAction {
	
	private static final Logger logger = Logger.getLogger(DefectStateAction.class.getName());
	private final String state;
	public DefectStateAction(String state){
		this.state=state;
	}
	@Override
	public void execute(UpdateArtifact artifact, RallyRestApi restApi, BuildListener listener)
			throws IOException {
		listener.getLogger().println("Setting "+artifact.getObjectId() +" to State= "+state);
		logger.fine("Setting "+artifact.getObjectId() +" to State= "+state);
		JsonObject updatedArtifact = new JsonObject(); 
	    updatedArtifact.addProperty("State", state); 
	    updateArtifact(artifact,updatedArtifact,restApi,listener);
	}
	@Override
	public Boolean isExecuted(UpdateArtifact artifact, RallyRestApi restApi,
			BuildListener listener) throws IOException {
		if(artifact.get_ref().contains("defect")){
			JsonObject currentArtifact =this.getArtifact(artifact, restApi);
			String currentState = currentArtifact.get("State").getAsString();
			if(currentState.equals(this.state)){
				listener.getLogger().println("Artifact is already at state "+this.state);
				logger.info("Artifact is already at state "+this.state);
				return true;
			}
			listener.getLogger().println("Artifact is not at state "+this.state+" so updating.");
			logger.info("Artifact is not at state "+this.state);
			return false;
		}
		listener.getLogger().println("Artifact is not a defect. Can not change defect state");
		return false;
	}

}
