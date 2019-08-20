package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.core.util.DefaultJsonReaderAndWriter;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerInstanceDBInfoWriterTest {

	private final String SYSTEM_PATH = "system";
	private ConfigurationDatabase configurationDB;
	private ServerInstanceDBInfoWriter serverInstanceDatabaseProcessor;

	private String serverHome;
	private String infoFilePath;

	private DefaultJsonReaderAndWriter jsonReaderAndWriter;

	String dbDriverClassName;
	String dbUsername;
	String dbPasssword;
	int maxIdle;
	int maxTotal;
	int validationQueryTimeout;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
	public ExpectedException exception = ExpectedException.none();

    @Before
	public void setup(){
		dbDriverClassName = "oracle.jdbc.driver.OracleDriver";
		dbUsername = "netvertex";
		dbPasssword = "netvertex";
		maxIdle = 10;
		maxTotal = 20;
		validationQueryTimeout = 3;
		infoFilePath = folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH + File.separator + "database.json";
    	jsonReaderAndWriter = new DefaultJsonReaderAndWriter();
		configurationDB =buildConfigurationDatabaseObject();
		serverHome = folder.getRoot().getPath();
		serverInstanceDatabaseProcessor =new ServerInstanceDBInfoWriter(serverHome,jsonReaderAndWriter);
	}

	@Test(expected = IOException.class)
	public void throwsIOExceptionWhenDatabaseConfigurationFileNotFound() throws  IOException{
		exception.expect(IOException.class);
		exception.expectMessage("No such file or directory");
		serverInstanceDatabaseProcessor.writeDBInfo(configurationDB);
	}

	@Test
	public void writeDbInfoWhenConfigurationDbIsProvided() throws IOException{
		Files.createDirectory(Paths.get(serverHome + File.separator + SYSTEM_PATH));
		File file = new File(infoFilePath);
		file.createNewFile();
		serverInstanceDatabaseProcessor.writeDBInfo(configurationDB);
		configurationDB = GsonFactory.defaultInstance().fromJson(new FileReader(infoFilePath), ConfigurationDatabase.class);
		Assert.assertEquals(configurationDB.getPassword(),dbPasssword);
		Assert.assertEquals(configurationDB.getUsername(),dbUsername);
		Assert.assertEquals(configurationDB.getDriverClassName(),dbDriverClassName);
		Assert.assertEquals(configurationDB.getValidationQueryTimeout(),validationQueryTimeout);
		Assert.assertEquals(configurationDB.getMaxIdle(),maxIdle);
		Assert.assertEquals(configurationDB.getMaxTotal(),maxTotal);
	}

	private ConfigurationDatabase buildConfigurationDatabaseObject(){
		ConfigurationDatabase configurationDB = new ConfigurationDatabase();
		configurationDB.setDriverClassName(dbDriverClassName);
		configurationDB.setUsername(dbUsername);
		configurationDB.setPassword(dbPasssword);
		configurationDB.setValidationQueryTimeout(validationQueryTimeout);
		configurationDB.setMaxIdle(maxIdle);
		configurationDB.setMaxTotal(maxTotal);
		return configurationDB;
	}

    @After
    public void deleteFolder(){
		folder.delete();
	}

}
