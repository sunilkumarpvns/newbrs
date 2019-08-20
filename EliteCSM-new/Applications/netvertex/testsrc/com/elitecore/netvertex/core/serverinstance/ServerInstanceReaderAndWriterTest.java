package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.netvertex.core.util.DefaultEliteCryptEncryptAndDecrypt;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(HierarchicalContextRunner.class)
public class ServerInstanceReaderAndWriterTest {

    private final String SYSTEM_PATH = "system";
    private String serverid = "testid";
    private String servername = "testname";
    private String infoFilePath;
    private ServerInstanceReaderAndWriter serverInstanceReaderAndWriter;
    private DefaultEliteCryptEncryptAndDecrypt defaultEliteCryptEncryptAndDecrypt;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        infoFilePath = folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH + File.separator + "_sys.info";
        defaultEliteCryptEncryptAndDecrypt = spy(new DefaultEliteCryptEncryptAndDecrypt());
        serverInstanceReaderAndWriter = spy(new ServerInstanceReaderAndWriter(infoFilePath,defaultEliteCryptEncryptAndDecrypt));
    }

    public class WillNotWriteServerInfoWhen {

        @Test
        public void ioExceptionOccurs() throws IOException, NoSuchEncryptionException, EncryptionFailedException {
            exception.expect(IOException.class);
            exception.expectMessage("No such file or directory");
            Files.createDirectory(Paths.get(folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH));
            File file = new File(infoFilePath);
            file.createNewFile();
            serverInstanceReaderAndWriter.writeServerInfo(serverid, servername);

        }

        @Test(expected = NoSuchEncryptionException.class)
        public void noSuchEncryptionExceptionOccurs() throws IOException, NoSuchEncryptionException, EncryptionFailedException {
            doThrow(NoSuchEncryptionException.class).when(defaultEliteCryptEncryptAndDecrypt).crypt(serverid);
            verifyValidWrite();
        }

        @Test(expected = EncryptionFailedException.class)
        public void encryptionFailedExceptionOccurs() throws IOException ,NoSuchEncryptionException, EncryptionFailedException {
            doThrow(EncryptionFailedException.class).when(defaultEliteCryptEncryptAndDecrypt).crypt(serverid);
            verifyValidWrite();
        }

        private void verifyValidWrite() throws IOException , NoSuchEncryptionException, EncryptionFailedException {
            Files.createDirectory(Paths.get(folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH));
            File file = new File(infoFilePath);
            file.createNewFile();
            serverInstanceReaderAndWriter.writeServerInfo(serverid, servername);
            serverInstanceReaderAndWriter.read();
        }
    }

    public class whenReadActionPerformedOnServerInfoFile {
        @Test
        public void whenServerInstanceFileDoesNotExistsThenItShouldNotCallRead() throws IOException {
            when(serverInstanceReaderAndWriter.isFileExits()).thenReturn(false);
            Mockito.verify(serverInstanceReaderAndWriter, never()).read();
        }

        @Test
        public void whenServerInstanceFileExistsThenItShouldCallReadMethod() throws  IOException{
            when(serverInstanceReaderAndWriter.isFileExits()).thenReturn(true);
            Files.createDirectory(Paths.get(folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH));
            File file = new File(infoFilePath);
            file.createNewFile();
            serverInstanceReaderAndWriter.read();
            Mockito.verify(serverInstanceReaderAndWriter, times(1)).read();
        }

        @Test
        public void whenServerInstanceFileExitsThenReadServerInstanceIdAndName() throws IOException, NoSuchEncryptionException, EncryptionFailedException {
            Files.createDirectory(Paths.get(folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH));
            File file = new File(infoFilePath);
            file.createNewFile();
            serverInstanceReaderAndWriter.writeServerInfo(serverid, servername);
            serverInstanceReaderAndWriter.read();
            Assert.assertEquals(serverid, serverInstanceReaderAndWriter.getId());
            Assert.assertEquals(servername, serverInstanceReaderAndWriter.getName());
        }

        @Test
        public void itWillReturnNullIdAndNameWhenWeReadWithoutWritingIntoFile() throws IOException,NoSuchEncryptionException,EncryptionFailedException{
            Files.createDirectory(Paths.get(folder.getRoot().getAbsolutePath() + File.separator + SYSTEM_PATH));
            File file = new File(infoFilePath);
            file.createNewFile();
            serverInstanceReaderAndWriter.read();
            Assert.assertEquals(null,serverInstanceReaderAndWriter.getId());
            Assert.assertEquals(null,serverInstanceReaderAndWriter.getName());
        }

    }

    @After
    public void cleanup() {
        folder.delete();
    }
}
