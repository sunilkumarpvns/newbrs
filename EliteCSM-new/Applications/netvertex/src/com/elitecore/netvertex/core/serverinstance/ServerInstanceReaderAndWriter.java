package com.elitecore.netvertex.core.serverinstance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.netvertex.core.util.PasswordEncryptAndDecrypt;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.commons.lang.StringUtils;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServerInstanceReaderAndWriter {

    private static final String MODULE = "SERVER-INSTANCE-READER-WRITER";
    private String path;
    private String id;
    private String name;
    private PasswordEncryptAndDecrypt passwordEncryptAndDecrypt;
    private final String SYSINFO_SERVER_ID = "id";
    private final String SYSINFO_SERVER_NAME = "name";

    public ServerInstanceReaderAndWriter(String path,PasswordEncryptAndDecrypt passwordEncryptAndDecrypt) {
        this.path = path;
        this.passwordEncryptAndDecrypt = passwordEncryptAndDecrypt;
    }

    public void read() {

        try {
            File infoFile = new File(path);
            if (isFileExits() == false) {
                if (getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, path + " file not found.");
                }
                return;
            }
            String currentId = readId(infoFile);
            if (StringUtils.isNotBlank(currentId)) {
                id = passwordEncryptAndDecrypt.decrypt(currentId);
            }
            String currentName = readName(infoFile);
            if (StringUtils.isNotBlank(currentName)) {
                name = passwordEncryptAndDecrypt.decrypt(currentName);
            }

        } catch (Exception e) {
            LogManager.getLogger().trace(MODULE, e);
            LogManager.getLogger().error(MODULE,"Error occurred while reading server info. Reason: "+ e.getMessage());
        }
    }


    private String readName(File infoFile) throws IOException {

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(infoFile))) { //NOSONAR - Reason: Resource should be closed
            String strTemp = bufferedReader.readLine();
            if (strTemp != null && strTemp.indexOf('=') != -1) {
                if(getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Server ID is : " + strTemp);
                }
            } else {
                if(getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Server ID is : " + strTemp + " invalid.");
                }
            }
            strTemp = bufferedReader.readLine();
            if (strTemp != null && strTemp.indexOf('=') != -1) {
                int index = strTemp.indexOf('=');
                return strTemp.substring(index + 1);
            } else {
                if(getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Server Name is : " + strTemp + " invalid.");
                }
            }
        }
        return null;
    }

    private String readId(File infoFile) throws IOException {

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(infoFile))) {
            String strTemp = bufferedReader.readLine();
            if (strTemp != null && strTemp.indexOf('=') != -1) {
                int index = strTemp.indexOf('=');
                return strTemp.substring(index + 1);
            } else {
                if(getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Server ID is : " + strTemp + " invalid.");
                }
            }
        }

        return null;
    }

    public void writeServerInfo(String id, String name) throws IOException,NoSuchEncryptionException,EncryptionFailedException {

        name = passwordEncryptAndDecrypt.crypt(name);
        id =  passwordEncryptAndDecrypt.crypt(id);

        id = SYSINFO_SERVER_ID + "=" + id.trim();
        name = SYSINFO_SERVER_NAME + "=" + name.trim();

        try (BufferedWriter fileWriter = Files.newBufferedWriter(Paths.get(path), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)){
            fileWriter.write(id);
            fileWriter.newLine();
            fileWriter.write(name);
        }
    }

    public boolean isFileExits(){
        File infoFile = new File(path);
        return infoFile.exists();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

}