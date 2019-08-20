package com.elitecore.commons.io;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * 
 * @author narendra.pathai
 *
 */
public class FilesTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testReadFullyFromFile_ShouldReadCompleteFileBytes() throws IOException{
		File tempFile = folder.newFile("testFile.test");
		FileOutputStream outStream = new FileOutputStream(tempFile);
		byte[] expectedBytes = new byte[]{1,1,1};
		outStream.write(expectedBytes);
		outStream.flush();
		Closeables.closeQuietly(outStream);
		
		byte[] actualBytes = Files.readFully(tempFile);
		
		assertArrayEquals(expectedBytes, actualBytes);
	}
	
	@Test
	public void testReadFullyFromFile_ShouldThrowNullPointerException_IfFilePassedIsNull() throws FileNotFoundException, IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("file is null");
		
		Files.readFully((File)null);
	}
	
	@Test
	public void testReadFullyFromFile_ShouldThrowIllegalArgumentException_IfFilePassedIsDirectory() throws FileNotFoundException, IOException{
		File tempFolder = folder.newFolder("testFolder");
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(tempFolder.getPath() + " is not a file");
		
		Files.readFully(tempFolder);
	}
	
	@Test
	public void testReadFullyFromPath_ShouldReadCompleteFileBytes() throws IOException{
		File tempFile = folder.newFile("testFile.test");
		FileOutputStream outStream = new FileOutputStream(tempFile);
		byte[] expectedBytes = new byte[]{1,1,1};
		outStream.write(expectedBytes);
		outStream.flush();
		Closeables.closeQuietly(outStream);
		
		byte[] actualBytes = Files.readFully(tempFile.getAbsolutePath());
		
		assertArrayEquals(expectedBytes, actualBytes);
	}
	
	@Test
	public void testReadFullyFromPath_ShouldThrowNullPointerException_WhenPathPassedIsNull() throws FileNotFoundException, IOException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("filePath is null");
		
		Files.readFully((String)null);
	}
	
	@Test
	public void testReadFullyFromPath_ShouldThrowIllegalArgumentException_WhenPathPassedPointsToDirectory() throws FileNotFoundException, IOException{
		File tempFolder = folder.newFolder("testFolder");
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(tempFolder.getAbsolutePath() + " is not a file");
		
		Files.readFully(tempFolder.getAbsolutePath());
	}
}
