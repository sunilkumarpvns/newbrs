package com.elitecore.config.util;

import com.elitecore.commons.base.Predicate;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

/**
 * 
 * @author narendra.pathai
 *
 */
public class FileUtil {
	
	private static final int EOF = -1;
	private static final int ONE_KB = 1024;
    private static final long ONE_MB = (long) ONE_KB * ONE_KB;
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;
	
	public static String formAbsolutePath(String path, String basePath){
		File file = new File(path);
		if(!file.isAbsolute()){
			path = basePath + File.separator + path;
		}

		return path;
	}
	
	public static void createDirectories(String path) throws IllegalArgumentException{
		File file = new File(path);
		//if the passed in argument is a file and not a directory then throw exception
		if(file.isFile()){
			throw new IllegalArgumentException("Invalid path : " + path + ", path points to file and is not a directory.");
		}
		
		if(!file.exists()){
			//create the directories if not existent
			file.mkdirs();
		}
	}
	
	public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && srcDir.isDirectory() == false) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }
	
	public static void copyDirectory(File srcDir, File destDir,
            boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }
	
	public static void copyDirectory(File srcDir, File destDir,
            FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }
	
	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
            boolean preserveFileDate, List<String> exclusionList) throws IOException {
        // recurse
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {  // null if abstract pathname does not denote a directory, or if an I/O error occurs
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(srcFile, dstFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }
	
	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }


        try (FileInputStream fis = new FileInputStream(srcFile);
             FileOutputStream fos = new FileOutputStream(destFile);
             FileChannel input = fis.getChannel();
             FileChannel output = fos.getChannel();
        ) {

            long size = input.size();
            long pos = 0;
            long count;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }
	
	public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) { 
        	ignoreTrace(ioe);
        }
    }
	
	public static List<File> getRecursiveFileFromPath (String path) throws IOException {
		File file = new File(path);
		
		if(!file.exists()){
			throw new FileNotFoundException(path + "");
		}
		
		List<File> files = new ArrayList<File>();
		
		if(file.isDirectory()){
            for (File childFile : file.listFiles(pathname -> !pathname.isHidden())) {
				if(childFile.isDirectory()){
					files.addAll(getRecursiveFileFromPath(childFile.getAbsolutePath()));
				} else {
					files.add(childFile);
				}	
			}
		}
		return files;
	}
	
	/**
	 * Reads bytes fully from the file specified by reading in chunks of bytes of size 1024 and convert it to an array of bytes. 
	 * This method is only meant to be used for relatively smaller files and should not be used for large files.
	 * <b><i>WARNING:</i></b> It may throw {@link OutOfMemoryError} if very large size file is given to get the bytes
	 * 
	 * @param filePath should be absolute path (does not convert relative path to absolute)
	 * @return byte[] - complete bytes of file
	 * @throws IOException - if any I/O error occurs
	 * @throws NullPointerException - if the argument filePath is null 
	 * @throws IllegalArgumentException - if file does not exist or path represents a directory or application does not have read permission.
	 */
	public static byte[] readBytesFully(String filePath) throws IOException{
		
		if(filePath == null) {
			throw new NullPointerException("filePath must not be null");
		}
		
		File file = new File(filePath); 
		
		if(!file.exists()) {
			throw new IllegalArgumentException("File: " + file.getAbsolutePath() + " does not exist");
		}
		
		if(file.isDirectory()) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " is a directory");
		}
		
		if(!file.canRead()) {
			throw new IllegalArgumentException("File: " + file.getAbsolutePath() + " can not be read");
		}
		
		if(file.length() > Integer.MAX_VALUE){
			throw new OutOfMemoryError("File: "  + file.getAbsolutePath() + " size too large.");
		}


        try (InputStream fileStream = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
			byte[] buffer = new byte[ONE_KB];
			int i;
			while(true) {
				i = fileStream.read(buffer);
				
				if(i == EOF)
					break;
				
				out.write(buffer, 0, i);
			}
			return out.toByteArray();
		}
	}
	
	/**
     * Recursively cleans a directory without deleting it.
     * It will delete the contents of the specified directory after applying predicate 
     * to every file recursively in the directory.
     * <br><br>
     * <b>Note: predicate is non null</b> 
     *
     * @param directory directory to clean
     * @throws IOException in case cleaning is unsuccessful, when failed to list the contents of the specified directory
     * 						and it keeps on deleting all the files even if IOException occurs while deleting some file in the middle and 
     *  					at the end it will throw this Exception to show that deletion of all the files is not successful
     */
	public static void cleanDirectory(File file, Predicate<File> predicate) throws IOException {
		
		checkForDirectoryPreconditions(file);

		File[] files = file.listFiles();

		if (files == null) {  // null if security restricted
			throw new IOException("Failed to list contents of " + file);
		}

		IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (predicate.apply(f) == false) {
            	continue;
            }
            try {
                forceDelete(f);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
	}
	
    /**
     * Recursively cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException in case cleaning is unsuccessful, when failed to list the contents of the specified directory
     * 						and it keeps on deleting all the files even if IOException occurs while deleting some file in the middle and 
     *  					at the end it will throw this Exception to show that deletion of all the files is not successful
     */
	public static void cleanDirectory(File file) throws IOException {
		
		checkForDirectoryPreconditions(file);

		File[] files = file.listFiles();
		
		if (files == null) {  // null if security restricted
			throw new IOException("Failed to list contents of " + file);
		}

		IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            try {
                forceDelete(f);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
	}

	private static void checkForDirectoryPreconditions(File file)
			throws IOException {
		if (!file.exists()) {
            String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!file.isDirectory()) {
            String message = file + " is not a directory";
            throw new IllegalArgumentException(message);
        }
	}

	 /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file  file or directory to delete, must not be <code>null</code>
     * @throws NullPointerException if the directory is <code>null</code>
     * @throws FileNotFoundException if the file was not found
     * @throws IOException in case deletion is unsuccessful
     */
	private static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent){
					throw new FileNotFoundException("File does not exist: " + file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}
	
	 /**
     * Deletes a directory recursively. 
     *
     * @param directory  directory to delete
     * @throws IOException in case deletion is unsuccessful
     */
    private static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message =
                "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }
}
