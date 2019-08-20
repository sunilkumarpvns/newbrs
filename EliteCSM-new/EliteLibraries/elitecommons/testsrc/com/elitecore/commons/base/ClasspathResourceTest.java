package com.elitecore.commons.base;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author narendra.pathai
 */
public class ClasspathResourceTest {

	private static final String UNKNOWN_RESOURCE = "base/no-such-file.txt";
	private static final String RELATIVE_PATH_TO_RESOURCE_IN_CLASSPATH = "base/test classpath locate.txt";
	private static final String RELATIVE_PATH_WITH_SPACES_TO_RESOURCE_IN_CLASSPATH = "base/test classpath locate.txt";
	
	@Rule public ExpectedException exception = ExpectedException.none();

	@Test
	public void testAt_ShouldReturnTheAnInstanceOfClasspathResouce_IrrespectiveOfWhetherTheResourceIsPresentInTheClasspath() {
		ClasspathResource resourceInClasspath = ClasspathResource.at(RELATIVE_PATH_TO_RESOURCE_IN_CLASSPATH);
		ClasspathResource resourceNotInClasspath = ClasspathResource.at(UNKNOWN_RESOURCE);
		
		assertNotNull(resourceInClasspath);
		assertNotNull(resourceNotInClasspath);
	}
	
	@Test
	public void testResolve_ShouldThrowNPE_IfRelativePathIsNull() {
		exception.expect(NullPointerException.class);
		
		ClasspathResource.at(null);
	}
	
	@Test
	public void testGetAbsolutePath_ShouldReturnTheAbsolutePath() throws UnsupportedEncodingException, FileNotFoundException {
		ClasspathResource resource = ClasspathResource.at(RELATIVE_PATH_TO_RESOURCE_IN_CLASSPATH);
		
		String absolutePathToResource = resource.getAbsolutePath();
		
		assertTrue(new File(absolutePathToResource).isAbsolute());
	}
	
	@Test
	public void testExists_ShouldReturnTrue_IfTheResourceExistsInTheClasspath() {
		ClasspathResource resource = ClasspathResource.at(RELATIVE_PATH_TO_RESOURCE_IN_CLASSPATH);
		
		assertTrue(resource.exists());
	}
	
	@Test
	public void testGetAbsolutePath_ShouldReturnAbsolutePath_AsUTF8EncodedString() throws UnsupportedEncodingException, FileNotFoundException {
		ClasspathResource resource = ClasspathResource.at(RELATIVE_PATH_WITH_SPACES_TO_RESOURCE_IN_CLASSPATH);
		
		assertTrue(resource.getAbsolutePath().contains(" "));
	}
	
	@Test
	public void testGetAbsolutePath_ShouldThrowFileNotFoundException_IfResourceDoesNotExistInClasspath() throws UnsupportedEncodingException, FileNotFoundException {
		
		ClasspathResource resource = ClasspathResource.at(UNKNOWN_RESOURCE);
		
		exception.expect(FileNotFoundException.class);
		exception.expectMessage(UNKNOWN_RESOURCE + " not found in classpath");
		
		resource.getAbsolutePath();
	}
	
}
