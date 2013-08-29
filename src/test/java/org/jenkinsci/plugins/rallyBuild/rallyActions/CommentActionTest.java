package org.jenkinsci.plugins.rallyBuild.rallyActions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import hudson.model.BuildListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.rallyBuild.UpdateArtifact;
import org.junit.Test;
import org.springframework.util.Assert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.response.GetResponse;

public class CommentActionTest {

	@Test
	public void isExectuedTest() throws IOException, URISyntaxException {
		File output = new File("test.txt");
		InputStream defaultImage = CommentActionTest.class.getResourceAsStream("discussionResponse.json");
		
		String response =IOUtils.toString(defaultImage);

		PrintStream stream = new PrintStream(output);
		BuildListener listener = mock(BuildListener.class);
		RallyRestApi restApi = mock(RallyRestApi.class);
		when(listener.getLogger()).thenReturn(stream);
		
		GetResponse getResponse = new GetResponse(response);

		when(restApi.get(any(GetRequest.class))).thenReturn(getResponse);
		
		CommentAction action = new CommentAction("A branch of the name origin/reporting-DE20409 was created     for this artifact. Marking it as In-Progress    ");
		UpdateArtifact artifact = new UpdateArtifact("Test","123","123");
		Assert.isTrue(action.isExecuted(artifact, restApi, listener));
		
	}

}
