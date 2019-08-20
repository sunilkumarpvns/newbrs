package com.elitecore.aaa.license.nfv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.elitecore.commons.io.Closeables;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * Can be used to store and read information required by
 * EliteAAA to make web service calls.
 * 
 * <p> 
 * At the time an instance of EliteAAA server is created,
 * the details of server manager like ip, port, context path and
 * authentication credential are written in a file _smWs under
 * AAA's file system in encrypted format. These details are read 
 * and utilized at time of making web service calls to Server manager.
 * </p>
 * 
 * @author vicky
 *
 */
public class WebServiceParams {

	private static final String WSFILE = File.separator + "_smWs";

	private String fileName;
	private String userName;
	private String password;
	private String address;
	private int port;
	private String contextPath;

	private PasswordEncryption encryption;
	
	public WebServiceParams( String systemPath) {
		this.fileName = systemPath + WSFILE;
		this.encryption = PasswordEncryption.getInstance();
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getContextPath() {
		return contextPath;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String passwd) {
		this.password = passwd;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}



	public void readDetails() throws IOException, NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException{
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(this.fileName)); //NOSONAR - Reason: Resources should be closed
			this.userName = decrypt(bufferedReader.readLine());
			this.password = decrypt(bufferedReader.readLine());
			this.address = decrypt(bufferedReader.readLine());
			this.port = Integer.parseInt(decrypt(bufferedReader.readLine()));
			this.contextPath = decrypt(bufferedReader.readLine());
		}  finally {
			Closeables.closeQuietly(bufferedReader);
		}
	}


	public void writeToFile() throws NoSuchEncryptionException, EncryptionFailedException, IOException {
		File webServiceDetailsFile = new File(this.fileName);
		if (webServiceDetailsFile.exists() == false) {
			if (webServiceDetailsFile.getParentFile().exists() == false) {
				webServiceDetailsFile.getParentFile().mkdirs();
			}
			if (webServiceDetailsFile.createNewFile() == false)
				throw new IOException("unable to create file " + webServiceDetailsFile.getAbsolutePath());
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(webServiceDetailsFile)); // NOSONAR - Resources should be closed
			writer.println(crypt(userName));
			writer.println(crypt(password));
			writer.println(crypt(address));
			writer.println(crypt(String.valueOf(port)));
			writer.println(crypt(contextPath));
		} finally {
			if (writer != null) {
				writer.flush(); // this is the reason Closeable was not used to close the resource. 
			writer.close();
		}
	}
	}
	
	private String crypt (String plainText) throws NoSuchEncryptionException, EncryptionFailedException {
		return encryption.crypt(plainText, PasswordEncryption.ELITECRYPT);
	}
	
	private String decrypt (String encryptedText) throws NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException {
		return encryption.decrypt(encryptedText, PasswordEncryption.ELITECRYPT);
	}
}
