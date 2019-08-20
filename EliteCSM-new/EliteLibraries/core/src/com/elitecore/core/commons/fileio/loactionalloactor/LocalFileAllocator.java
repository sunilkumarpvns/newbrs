package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;

/**
 * @author Manjil Purohit
 *
 */
public class LocalFileAllocator extends BaseCommonFileAllocator {

	@Override
	public boolean connect() throws FileAllocatorException {
		return true;
	}

	@Override
	public boolean getPermission() {
		return true;
	}

	@Override
	public File transferFile(File file) throws FileAllocatorException {
		return manageExtension(file, BaseCommonFileAllocator.UIP_EXTENSION, originalExtension, null);
	}
	
	@Override
	public boolean disconnect() {
		return true;
	}
	
}
