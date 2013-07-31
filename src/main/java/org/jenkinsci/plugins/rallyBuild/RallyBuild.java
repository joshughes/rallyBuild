package org.jenkinsci.plugins.rallyBuild;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.rallyBuild.rallyActions.Action;
import org.jenkinsci.plugins.rallyBuild.rallyActions.CommentAction;
import org.jenkinsci.plugins.rallyBuild.rallyActions.ReadyAction;
import org.jenkinsci.plugins.rallyBuild.rallyActions.StateAction;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link RallyBuild} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class RallyBuild extends Builder {

    public final String issueString;

    public final Boolean updateOnce;
    

    public final String issueRegex;
    
    public String issueRallyState;  
    public String commentText;
    public Boolean issueReady;
    public Boolean changeRallyState=false;
    public Boolean createComment=false;
    public Boolean changeReady=false;
    public Set<String> updatedIssues = new HashSet<String>();
    private static final Logger logger = Logger.getLogger(RallyBuild.class.getName());
  

    //Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public RallyBuild(String issueString, String issueRegex, Boolean updateOnce,
    		 EnableReadyBlock changeReady,CreateCommentBlock createComment, ChangeStateBlock changeRallyState) {
        this.issueString = issueString;
        this.issueRegex= issueRegex;
        this.updateOnce=updateOnce;
  
        if(changeRallyState!=null){
        	this.changeRallyState=true;
        	this.issueRallyState=changeRallyState.getIssueState();
        }
        if(createComment!=null){
        	this.createComment=true;
        	this.commentText=createComment.getComment();
        }
        
        if(changeReady!=null){
        	this.changeReady=true;
        	this.issueReady=changeReady.getIssueReady();
        }
       
    }

    

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
    	
    	EnvVars env = build.getEnvironment(listener); 
        String expandedCommentText = env.expand(commentText); 
        String expandedIssueString = env.expand(issueString);
    	List<Action> rallyActions = new ArrayList<Action>();
    	Rally rally = null;
    	
    	try {
    		logger.info("Server "+getDescriptor().getRallyServer());
			rally = new Rally(updatedIssues,getDescriptor().getRallyUser(),getDescriptor().getRallyPassword(),getDescriptor().getRallyServer(),listener);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	
    	if(rally!=null){
    		logger.info("Create Comment "+createComment);
	    	if(createComment){
	    		CommentAction comment = new CommentAction(expandedCommentText);
	    		rallyActions.add(comment);
	    	}
	    	
	    	logger.info("Create Comment "+changeReady);
	    	if(changeReady){
	    		ReadyAction ready = new ReadyAction(issueReady);
	    		rallyActions.add(ready);
	    	}
	    	
	    	logger.info("Create Comment "+changeRallyState);
	    	if(changeRallyState){
	    		StateAction state = new StateAction(issueRallyState);
	    		rallyActions.add(state);
	    	}
	    	HashSet<String> issues = rally.getIssues(expandedIssueString, issueRegex);
	    	rally.updateIssues(issues, rallyActions,updateOnce);
    	}
    	return true;
    }
    
    public static class EnableReadyBlock
    {
        private Boolean issueReady;

        @DataBoundConstructor
        public EnableReadyBlock(Boolean issueReady)
        {
            this.issueReady = issueReady;
        }

		public Boolean getIssueReady() {
			return issueReady;
		}

    }
    
    public static class ChangeStateBlock
    {
        private String issueRallyState;

        @DataBoundConstructor
        public ChangeStateBlock(String issueRallyState)
        {
            this.issueRallyState = issueRallyState;
        }

		public String getIssueState() {
			return issueRallyState;
		}

    }
    
    public static class CreateCommentBlock
    {
        private String commentText;

        @DataBoundConstructor
        public CreateCommentBlock(String commentText)
        {
            this.commentText = commentText;
        }

		public String getComment() {
			return commentText;
		}

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link RallyBuild}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String rallyServer; 
        private String rallyPassword;
        private String rallyUser;
       
        public DescriptorImpl(){
			load();
		}
        
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Rally Builder";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            
        	//name          = formData.getString("name");
            rallyServer   = formData.getString("rallyServer");
            rallyUser     = formData.getString("rallyUser");
            rallyPassword = formData.getString("rallyPassword");
            
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        
        public String getRallyServer(){
        	return rallyServer;
        }
        
        public String getRallyUser(){
        	return rallyUser;
        }
        
        public String getRallyPassword(){
        	return rallyPassword;
        }
        

    }
}

