package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;

/**
 * @author rudradutt
 * Life Cycle of the interface.
 * getConnection
 * makeDirectory
 * changeWorkingLocation
 * transferFile
 * disconnect
 */
public interface IFileLocationAllocater {

	public boolean connect() throws FileAllocatorException;;

	public boolean getPermission();

	public File transferFile(File file) throws FileAllocatorException;

	public boolean disconnect();

	public void initialize(String user, String password, String address, String destinationLocation, int port, String postOperation,String folderSepretor,String archiveLocation,String originalExtension) throws FileAllocatorException;

	public void setFolderSepretor(String folderSepretor);

	public String getUploadPrefix();

	public File manageExtension(File file,String srcExt,String destExt,String fileName);

	public boolean postOperation(File file);

	public String getFolderSepretor();
}
