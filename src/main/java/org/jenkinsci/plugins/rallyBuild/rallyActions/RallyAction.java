package org.jenkinsci.plugins.rallyBuild.rallyActions;

import hudson.model.BuildListener;

import java.io.IOException;
import java.util.logging.Logger;

import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.UpdateResponse;

public abstract class RallyAction implements Action{
	private static final Logger logger = Logger.getLogger(RallyAction.class.getName());
	
	
	protected void updateArtifact(UpdateArtifact artifact, JsonObject updatedRequirement, RallyRestApi restApi,  BuildListener listener) throws IOException{
		 logger.info(("Updating Artifact : "+artifact.getObjectId()));
		 listener.getLogger().println("/"+artifact.get_type()+"/"+artifact.getObjectId()+".js");
		 UpdateRequest updateRequest = new UpdateRequest("/"+artifact.get_type()+"/"+artifact.getObjectId()+".js", updatedRequirement); 
	     UpdateResponse updateResponse = restApi.update(updateRequest);
	     for(String error:updateResponse.getErrors()){
	        logger.info(("ERROR: "+error));
	     	}
	}
	
	protected JsonObject getArtifact(UpdateArtifact artifact,RallyRestApi restApi) throws IOException{
		 logger.info(("Getting Artifact : "+artifact.getObjectId()));
		 GetRequest getRequest = new GetRequest("/"+artifact.get_type()+"/"+artifact.getObjectId()); 
	     GetResponse getResponse = restApi.get(getRequest);
	     return getResponse.getObject();
	}
	

}
