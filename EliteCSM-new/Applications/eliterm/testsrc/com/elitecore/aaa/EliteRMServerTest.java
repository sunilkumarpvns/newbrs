package com.elitecore.aaa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.unitils.util.FileUtils;

import com.elitecore.passwordutil.PasswordEncryption;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class EliteRMServerTest {

	private static final String INSTANCE_ID = "1";
	protected static final String SYSTEM_PATH = "system";
	protected static final String SYS_INFO_FILE = "_sys.info";

	private EliteRMServer eliteRMServer;
	private File serverHome;

	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws IOException {
		serverHome = temporaryFolder.newFolder("serverhome");
		eliteRMServer = new EliteRMServer(serverHome.getPath());
	}

	public class ServerInstanceId {

		@Test
		public void willBeNullIfSysInfoFileNotExist() throws IOException {
			assertThat(eliteRMServer.getServerInstanceID(), is(nullValue()));
		}

		@Test
		public void isReadFromSystemInfoFileAndReturnsWhenFileExists() throws Exception {
			createSysInfoFileWithInstanceId(INSTANCE_ID);

			assertThat(eliteRMServer.getServerInstanceID(), is(equalTo(INSTANCE_ID)));
		}

		@Test
		public void onceReadFromSysInfoFileReturnsCachedIdOnSubsequentCalls() throws Exception {
			createSysInfoFileWithInstanceId(INSTANCE_ID);

			String serverInstanceID1 = eliteRMServer.getServerInstanceID();
			String serverInstanceID2 = eliteRMServer.getServerInstanceID();

			assertThat(serverInstanceID1, is(sameInstance(serverInstanceID2)));
		}

	}

	private void createSysInfoFileWithInstanceId(String instanceId) throws Exception {
		File systemFolder = temporaryFolder.newFolder(serverHome.getName(), SYSTEM_PATH);
		File sysInfo = new File(systemFolder.getAbsolutePath() + File.separator + SYS_INFO_FILE);
		sysInfo.createNewFile();
		FileUtils.writeStringToFile(sysInfo.getAbsoluteFile(), "id=" + PasswordEncryption.getInstance().crypt(instanceId, PasswordEncryption.ELITECRYPT));
	}

}
