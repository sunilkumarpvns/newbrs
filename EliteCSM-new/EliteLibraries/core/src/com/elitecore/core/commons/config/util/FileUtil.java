package com.elitecore.core.commons.config.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Predicate;

/**
 * 
 * @author narendra.pathai
 *
 */
public class FileUtil {
	
	private static final int EOF = -1;
	private static final int ONE_KB = 1024;
	private static final long ONE_MB = ONE_KB * ONE_KB;
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

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile); //NOSONAR - Reason: Resources should be closed
            fos = new FileOutputStream(destFile); //NOSONAR - Reason: Resources should be closed
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            closeQuietly(output);
            closeQuietly(fos);
            closeQuietly(input);
            closeQuietly(fis);
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
            // ignore
        }
    }
	
	public static List<File> getRecursiveFileFromPath (String path) throws IOException {
		File file = new File(path);
		
		if(!file.exists()){
			throw new FileNotFoundException(path + "");
		}
		
		List<File> files = new ArrayList<File>();
		
		if(file.isDirectory()){
			for(File childFile : file.listFiles(new FileFilter() { @Override public boolean accept(File pathname) { return !pathname.isHidden();}})){
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
		
		InputStream fileStream = null;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			fileStream = new FileInputStream(file);
			byte[] buffer = new byte[ONE_KB];
			int i;
			while(true) {
				i = fileStream.read(buffer);
				
				if(i == EOF)
					break;
				
				out.write(buffer, 0, i);
			}
			return out.toByteArray();
		} finally {
			closeQuietly(fileStream);
			closeQuietly(out);
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
    public static void deleteDirectory(File directory) throws IOException {
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
    
    public static List<File> listFiles(File file, FileFilter filter) {
		ArrayList<File> files = new ArrayList<File>();
		
		if(file.exists() == false)
			return files;
		
		if (file.isFile()) {
			if (filter.accept(file)) {
				files.add(file);
			}
		} else {
			for (File innerFile : file.listFiles()) {
				files.addAll(listFiles(innerFile, filter));
			}
		}
		return files;
	}
    

    /**
     * Moves a file from one path to another. This method can rename a file and/or move it to a
     * different directory. In either case {@code to} must be the target path for the file itself; not
     * just the new name for the file or the path to the new parent directory.
     *
     * <p><b>{@link java.nio.file.Path} equivalent:</b> {@link java.nio.file.Files#move}.
     *
     * @param from the source file
     * @param to the destination file
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if {@code from.equals(to)}
     */
    public static void move(File from, File to) throws IOException {
      Preconditions.checkNotNull(from, "from is null");
      Preconditions.checkNotNull(to, "to is null");
      Preconditions.checkArgument(!from.equals(to), "Source " + from + " and destination " + to + " must be different");

      if (!from.renameTo(to)) {
        doCopyFile(from, to, false);
        if (!from.delete()) {
          if (!to.delete()) {
            throw new IOException("Unable to delete " + to);
          }
          throw new IOException("Unable to delete " + from);
        }
      }
    }
    
	public static class ExtensionFilter implements FileFilter {
		private String extension;

		public ExtensionFilter(String extension) {
			this.extension = extension;
		}
		
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith("." + extension);
		}
	}
	
	public static class WithoutExtensionFilter implements FileFilter {
		
		@Override
		public boolean accept(File pathname) {
			return !pathname.getName().contains(".");
		}
	}
	
	public static class LastDateModifiedComparator implements Comparator<File> {
		
		/**
		 * @return  difference between last Modified dates of first and second File. </br>
	  	 *		valueReturned < 0 , if lastModifieDateOf(file1) < lastModifiedDateOf(file2) </br>
	  	 * 		valueReturned = 0 , if lastModifieDateOf(file1) = lastModifiedDateOf(file2) </br>
	  	 *		valueReturned > 0 , if lastModifieDateOf(file1) > lastModifiedDateOf(file2) </br>
		 */
		@Override
		public int compare(File file1, File file2) {
			
			return (int) (file1.lastModified() - file2.lastModified());
		}
		
	}
}
