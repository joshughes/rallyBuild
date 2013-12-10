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

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.rallyBuild.rallyActions.Action;
import org.jenkinsci.plugins.rallyBuild.rallyActions.StateAction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;


public class Rally {
	
	private final RallyRestApi restApi;
	
	private static final Logger logger = Logger.getLogger(Rally.class.getName());
	private final BuildListener listener;
	private List<UpdateArtifact> artifacts = new ArrayList<UpdateArtifact>();
	
	public Rally(RallyRestApi api, BuildListener listener) throws URISyntaxException{
		//restApi = new RallyRestApi(new URI(host), userName, password);
		restApi=api;
		this.listener=listener;
	}
	
	
	public HashSet<String> getIssues(String issueString){
		String issueRegex = "(US\\d+|DE\\d+)";
		List<String> foundIssues = new ArrayList<String>();
		Pattern pattern = Pattern.compile(issueRegex,Pattern.CASE_INSENSITIVE);
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
	
	public void updateIssues(HashSet<String> issues, List<Action> preConditions, List<StateAction> preStates, List<Action> actions, Boolean updateOnce) throws IOException{
		for(String issue: issues){
			UpdateArtifact artifact = getUpdateArtifact(issue);
				if(artifact != null){
				artifacts.add(artifact);
				listener.getLogger().println("Updating Issue "+issue);
				logger.info("Updating Issue "+issue);
				if(preConditionsMet(artifact,preConditions,preStates)){
					executActions(artifact,actions,updateOnce);
				}
			}
		}

	}
	
	private Boolean preConditionsMet(UpdateArtifact artifact,List<Action> preConditions, List<StateAction> preStates) throws IOException{
		listener.getLogger().println("Looking at "+preConditions.size()+" preConditions before we update");
		for(Action condition: preConditions){
			listener.getLogger().println("Checking PreCondition for artifact "+artifact.get_ref());
			if(!condition.isExecuted(artifact, restApi, listener)){
				listener.getLogger().println("Precondition failed "+condition.toString());
				return false;
			}
		}
		for(StateAction stateAction: preStates){
			listener.getLogger().println("Checking if artifact is in correct state to update "+artifact.get_ref());
			if(stateAction.isExecuted(artifact, restApi, listener)){
				return true;
			}
		}
		if(preStates.size()>0){
			return false;
		}
		return true;
	}
	
	
	private void executActions(UpdateArtifact artifact, List<Action> actions, Boolean updateOnce) throws IOException{
		for(Action action:actions){
			if(!updateOnce){
				action.execute(artifact, restApi, listener);
			}
			else if(!action.isExecuted(artifact, restApi, listener)){
				action.execute(artifact, restApi, listener);
			}
			
		}
	}
	
	public UpdateArtifact getUpdateArtifact(String formattedId) throws IOException{
		JsonObject artifact = getArtifact(formattedId);
		if(artifact != null){
			String _ref     = artifact.get("_ref").getAsString();
			String _type    = artifact.get("_type").getAsString();
			String _refObjectName = artifact.get("_refObjectName").getAsString();
			String description = artifact.get("Description").getAsString();
			String urlParts[] = _ref.split("/");
			String objectId ="";
			if(urlParts.length>0){
				objectId= urlParts[urlParts.length-1];
			}
			return new UpdateArtifact(objectId,_ref,_type,description,formattedId, _refObjectName);
		}
		return null;
		
	}
	
	public JsonObject getArtifact(String formattedId) throws IOException{
		QueryRequest defectRequest = new QueryRequest("artifact"); 
		defectRequest.setQueryFilter(new QueryFilter("FormattedID", "=", formattedId)); 
		defectRequest.setOrder("Priority ASC,FormattedID ASC"); 
		QueryResponse queryResponse = restApi.query(defectRequest);
		System.out.println(queryResponse.getResults().toString());
		if(queryResponse.getResults().size()>0){
			JsonArray results = queryResponse.getResults();
			
			if(StringUtils.containsIgnoreCase(formattedId,"us")){
				return getResultofType("HierarchicalRequirement",results);
			}
			if(StringUtils.containsIgnoreCase(formattedId,"de")){
				return getResultofType("Defect",results);
			}
		}
		return null;
	}
	
	private JsonObject getResultofType(String type, JsonArray results) throws IOException{
		for(JsonElement result: results){
			try{
				JsonObject resultObject = (JsonObject)result;
				if(resultObject.get("_type").getAsString().equals(type)){
					return resultObject;
				}
			}
			catch(RuntimeException ex){
				System.out.println("Caught exception while trying to find type of object "+ex.toString());
			}
		}
		return null;
	}
	
	public String getHTMLIssueSummary(){
		StringBuilder builder = new StringBuilder();
		for(UpdateArtifact artifact: artifacts){
			builder.append(artifact.getFormattedId()+" - ");
			builder.append(artifact.get_refObjectName());
			builder.append(" <br>");
		}
		return builder.toString();
	}


	public List<UpdateArtifact> getArtifacts() {
		return artifacts;
	}
	

	


	
	
	
	
}
