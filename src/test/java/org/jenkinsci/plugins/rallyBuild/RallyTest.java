package org.jenkinsci.plugins.rallyBuild;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.BuildListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.junit.Test;

import com.rallydev.rest.RallyRestApi;

public class RallyTest {
	
	

	@Test
	public void getIssuesTest() throws URISyntaxException, FileNotFoundException {
		File output = new File("test.txt");
		PrintStream stream = new PrintStream(output);
		BuildListener listener = mock(BuildListener.class);
		RallyRestApi restApi = mock(RallyRestApi.class);
		when(listener.getLogger()).thenReturn(stream);
		
		Rally rally = new Rally(restApi,listener);
		
		HashSet<String> matched_issues = rally.getIssues("Us1234 DE1234 DE US us us12345");
		
		assertEquals(3,matched_issues.size());
		
	}
	
	

}
