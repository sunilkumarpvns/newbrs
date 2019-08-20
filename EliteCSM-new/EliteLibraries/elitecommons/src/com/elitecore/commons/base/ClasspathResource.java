package com.elitecore.commons.base;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Represents a resource in the classpath.
 * <p>
 * It can be used to get hold of file located in classpath.
 * 
 * @author narendra.pathai
 *
 */
public class ClasspathResource {

	private final URL url;
	private final String classpathRelativePath;

	private ClasspathResource(String classpathRelativePath, URL url) {
		this.classpathRelativePath = classpathRelativePath;
		this.url = url;
	}

	/**
	 * Returns a new {@link ClasspathResource} pointing at the provided relative resource path
	 * @param classpathRelativePath relative path to resource in classpath  
	 */
	public static ClasspathResource at(String classpathRelativePath) {
		URL url = ClasspathResource.class.getClassLoader().getResource(classpathRelativePath);
		return new ClasspathResource(classpathRelativePath, url);
	}

	/**
	 * Returns absolute path to resource in classpath as UTF-8 encoded string
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
	 * @throws FileNotFoundException if resource is not found in classpath
	 */
	public String getAbsolutePath() throws UnsupportedEncodingException, FileNotFoundException {
		if (url == null) {
			throw new FileNotFoundException(classpathRelativePath + " not found in classpath");
		}
		return URLDecoder.decode(url.getPath(), "UTF-8");
	}

	/**
	 * Returns true if resource exists in classpath, false otherwise
	 */
	public boolean exists() {
		return url != null;
	}
}
