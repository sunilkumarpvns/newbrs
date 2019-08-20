package com.elitecore.core.serverx.manager.scripts;

import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.ServerContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertTrue;

public class ExternalScriptsManagerTest {

	private ExternalScriptsManager externalScriptManager;
	@Mock private ServerContext serverContext; 

	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);	
	}

	@Test
	public void loadsTheGivenGroovyScriptsWhichCompileSucessfully() throws UnsupportedEncodingException, FileNotFoundException, InitializationFailedException {
		File file = new File(ClasspathResource.at("com/elitecore/core/scripts/SampleDriverScript.groovy").getAbsolutePath());
		List<File> files = new ArrayList<File>();
		files.add(file);

		Map<String, List<File>> scriptMap = new HashMap<String, List<File>>();
		scriptMap.put("driver_script",files);

		externalScriptManager = new ExternalScriptsManager(serverContext, scriptMap);
		externalScriptManager.init();
		Object groovyObject = externalScriptManager.getScript("driver_script", DriverScript.class);
		
		
		Assert.assertThat(groovyObject, instanceOf(DriverScript.class));
		assertTrue(groovyObject instanceof DriverScript);
	}

}
