package org.jenkinsci.plugins.rallyBuild.rallyActions;

import hudson.model.BuildListener;

import java.io.IOException;
import java.util.logging.Logger;

import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.GetResponse;

public class CommentAction implements Action{
	private final String comment;
	private static final Logger logger = Logger.getLogger(Action.class.getName());
	
	public CommentAction(String comment){
		this.comment=comment;
	}
	
	@Override
	public void execute(UpdateArtifact artifact, RallyRestApi restApi, BuildListener listener) throws IOException {
		listener.getLogger().println("Adding comment to issue: "+comment);
		JsonObject artifactJson = new JsonObject();
		
		artifactJson.addProperty("_ref", artifact.get_ref());
		
		this.updateConvo(restApi, artifactJson, listener);
	}
	
	
	private void updateConvo(RallyRestApi restApi, JsonObject artifact,BuildListener listener) throws IOException{
		JsonObject newConvo = new JsonObject();
        newConvo.addProperty("Text", comment);
        newConvo.add("Artifact", artifact);
		CreateRequest createRequest = new CreateRequest("conversationpost", newConvo);
        CreateResponse createResponse = restApi.create(createRequest);
        for(String error:createResponse.getErrors()){
        	listener.getLogger().println("Failed to update comment: "+comment);
        	listener.getLogger().println("ERROR: "+error);
        	logger.fine("ERROR: "+error);
        }
	}

	@Override
	public Boolean isExecuted(UpdateArtifact artifact, RallyRestApi restApi,
			BuildListener listener) throws IOException {
		JsonArray currentComments = getDiscussion(artifact,restApi).getAsJsonArray("Results"); 
		for(JsonElement currentComment:currentComments){
			JsonObject commentObject = currentComment.getAsJsonObject();
			String currentCommentText = commentObject.get("Text").getAsString();
			if(currentCommentText.equals(this.comment)){
				listener.getLogger().println("Found duplicate comment: "+comment);
				logger.info("Found duplicate comment: "+comment);
				return true;
			}
		}
		listener.getLogger().println("No dupplicate comments found of: "+this.comment);
		logger.info("No dupplicate comments found of: "+this.comment);
		return false;	
	}
	
	private JsonObject getDiscussion(UpdateArtifact artifact, RallyRestApi restApi) throws IOException{
		logger.info(("Getting Artifact : "+artifact.getObjectId()));
		GetRequest getRequest = new GetRequest("/"+artifact.get_type()+"/"+artifact.getObjectId()+"/Discussion"); 
	    GetResponse getResponse = restApi.get(getRequest);
	    return getResponse.getObject();
	}
	
}
