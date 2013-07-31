package org.jenkinsci.plugins.rallyBuild;

import hudson.model.BuildListener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.rallyBuild.rallyActions.Action;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;


public class Rally {
	
	private final RallyRestApi restApi;
	
	private static final Logger logger = Logger.getLogger(Rally.class.getName());
	private final BuildListener listener;
	
	public Rally(Set<String> updatedIssues, String userName, String password, String host, BuildListener listener) throws URISyntaxException{
		restApi = new RallyRestApi(new URI(host), userName, password);
		this.listener=listener;
	}
	
	
	public HashSet<String> getIssues(String issueString, String issueRegex){
		List<String> foundIssues = new ArrayList<String>();
		Pattern pattern = Pattern.compile(issueRegex);
		Matcher matcher = pattern.matcher(issueString);
		logger.info("Looking for issue in: "+issueString +" with REGEX: "+issueRegex);
		listener.getLogger().println("Looking for issue in: "+issueString +" with REGEX: "+issueRegex);
		while (matcher.find()) {
			listener.getLogger().println("Found Issue "+matcher.group(1));
			logger.info("Found Issue "+matcher.group(1));
		    foundIssues.add(matcher.group(1));
		}
		HashSet<String> unquieIssues = new HashSet<String>();
		unquieIssues.addAll(foundIssues);
		return unquieIssues;
	}
	
	public void updateIssues(HashSet<String> issues, List<Action> actions, Boolean updateOnce) throws IOException{
		for(String issue: issues){
			listener.getLogger().println("Updating Issue "+issue);
			logger.info("Updating Issue "+issue);
			executActions(issue,actions,updateOnce);
		}

	}
	
	private void executActions(String issue, List<Action> actions, Boolean updateOnce) throws IOException{
		for(Action action:actions){
			UpdateArtifact artifact = getUpdateArtifact(issue);
			if(!updateOnce){
				action.execute(artifact, restApi, listener);
			}
			else if(!action.isExecuted(artifact, restApi, listener)){
				action.execute(artifact, restApi, listener);
			}
			
		}
	}
	
	private UpdateArtifact getUpdateArtifact(String formattedId) throws IOException{
		JsonObject artifact = getArtifact(formattedId);
		String _ref     = artifact.get("_ref").getAsString();
		String _type    = artifact.get("_type").getAsString();
		String urlParts[] = _ref.split("/");
		String objectId ="";
		if(urlParts.length>0){
			objectId= urlParts[urlParts.length-1];
		}
		return new UpdateArtifact(objectId,_ref,_type);
	}
	
	
	
	private JsonObject getArtifact(String formattedId) throws IOException{
		QueryRequest defectRequest = new QueryRequest("artifact"); 
		defectRequest.setQueryFilter(new QueryFilter("FormattedID", "=", formattedId)); 
		defectRequest.setOrder("Priority ASC,FormattedID ASC"); 
		QueryResponse queryResponse = restApi.query(defectRequest);
		System.out.println(queryResponse.getResults().toString());
		return (JsonObject) queryResponse.getResults().get(0);
	}
	


	
	
	
	
}
