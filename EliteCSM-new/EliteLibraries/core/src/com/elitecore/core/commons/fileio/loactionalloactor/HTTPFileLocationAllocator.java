package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import sun.net.www.http.HttpClient;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;

public class HTTPFileLocationAllocator extends BaseCommonFileAllocator {

	private URL url ;
	private HttpURLConnection httpURLConnection;
	private boolean httpConnectionAvailable = false;
	public static final String MODULE = "HTTP_FILE_LOCATION_ALLOCATOR";

	public boolean disconnect() {
		if(getHTTPUrlConnection()!=null){
			httpURLConnection = null;
		}
		return false;
	}

	public boolean connect() throws FileAllocatorException {
		try {
			url = new URL("http://"+getAddress()+":"+getPort()+File.separator+getDestinationLocation());
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpConnectionAvailable = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new FileAllocatorException(e);
		}
		return true;
	}

	public boolean getPermission() {
		return httpConnectionAvailable;
	}

	public File transferFile(File file) throws FileAllocatorException {
		
		OutputStream os = null;
		try {
			HttpClient httpClient = new HttpClient(url, "",1, true, 1);
			os = httpClient.getOutputStream();
			os.write(getBytesFromFile(file));
			return file;
		} catch (Exception e1) {
			throw new FileAllocatorException(e1);
		} finally{
			if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogManager.getLogger().trace(MODULE, e);
				}
		}
	}
	
	protected HttpURLConnection getHTTPUrlConnection(){
		return httpURLConnection;
	}
	
	private static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = null;
        try {
		is = new FileInputStream(file);
        System.out.println("\nDEBUG: FileInputStream is " + file);

        // Get the size of the file
        long length = file.length();
        System.out.println("DEBUG: Length of " + file + " is " + length + "\n");

        /*
         * You cannot create an array using a long type. It needs to be an int
         * type. Before converting to an int type, check to ensure that file is
         * not loarger than Integer.MAX_VALUE;
         */
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large to process");
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ( (offset < bytes.length)
                &&
                ( (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) ) {

            offset += numRead;

        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        return bytes;
        } finally {
        	Closeables.closeQuietly(is);
        }

    }
}
