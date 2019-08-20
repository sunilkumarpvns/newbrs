package com.elitecore.core.util.logger;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.elitecore.core.util.logger.EliteRollingFileLogger.SIZE_BASED_ROLLING_TYPE;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class EliteRollingFileLoggerTest {
    private EliteRollingFileLogger log4j2EliteRollingFileLogger;
    private String testLogFile;
    private String serverInstanceName;
    @Rule public TemporaryFolder temporaryFolder;

    @Before
    public void setUp() throws IOException {
        temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        testLogFile = "testLogFile";
        serverInstanceName = "log4jTester";
        log4j2EliteRollingFileLogger = new EliteRollingFileLogger.Builder(serverInstanceName, temporaryFolder.getRoot().getAbsolutePath() + File.separator + testLogFile).rollingType(SIZE_BASED_ROLLING_TYPE)
                .maxRolledUnits(5)
                .rollingUnit(10).build();

        log4j2EliteRollingFileLogger.setLogLevel(org.apache.logging.log4j.Level.ALL.name());

    }

    public class loggerFormatShouldBe_LEVEL_THREAD_NAME_MODULE_THEN_MESSAGE {
        private String anyString;
        private String module;

        @Before
        public void setUp() {
            log4j2EliteRollingFileLogger.setLogLevel(org.apache.logging.log4j.Level.ALL.name());
            anyString = UUID.randomUUID().toString();
            module = "Testing";
        }

        @Test
        public void debugLevelFormat() throws IOException, InterruptedException {
            log4j2EliteRollingFileLogger.debug(module, anyString);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[DEBUG] " + Thread.currentThread().getName() + " ["  + module + "]: " + anyString);
        }

        @Test
        public void infoLevelFormat() throws IOException {
            log4j2EliteRollingFileLogger.info(module, anyString);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[INFO] " + Thread.currentThread().getName() + " ["  + module + "]: " + anyString);
        }

        @Test
        public void errorLevelFormat() throws IOException {
            log4j2EliteRollingFileLogger.error(module, anyString);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[ERROR] " + Thread.currentThread().getName() + " ["  + module + "]: " + anyString);
        }

        @Test
        public void warnLevelFormat() throws IOException {

            log4j2EliteRollingFileLogger.warn(module, anyString);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[WARN] " + Thread.currentThread().getName() + " ["  + module + "]: " + anyString);
        }

        @Test
        public void traceLevelFormat() throws IOException {

            log4j2EliteRollingFileLogger.trace(module, anyString);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[TRACE] " + Thread.currentThread().getName() + " ["  + module + "]: " + anyString);
        }

        private void checkLogContains(String expected) throws IOException {
            String path = temporaryFolder.getRoot().getAbsolutePath() + File.separator + testLogFile + "-" + serverInstanceName + ".log";
            String actual = readLogFiles();
            assertTrue("File is " +  path + ", Expected is " + expected + ", actual is " + actual, actual.contains(expected));
        }

        private String readLogFiles() throws IOException {
            Path path = Paths.get( temporaryFolder.getRoot().getAbsolutePath(),testLogFile + "-" + serverInstanceName + ".log");
            return Files.lines(path).collect(Collectors.joining());
        }

    }

    public class traceLoggerFormatShouldBe {
        private Exception exception;
        private String module;

        @Before
        public void setUp() {
            exception = new Exception("Testing");
            module = "Testing";
        }

        @Test

        public void level_threadName_Module_then_exceptionMessage_when_traceExceptionWithModule() throws IOException, InterruptedException {
            log4j2EliteRollingFileLogger.trace(module, exception);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[TRACE] " + Thread.currentThread().getName() + " ["  + module + "] ST KEY : [0] " + exception.getMessage());
        }

        @Test
        public void level_threadName_then_exceptionMessage_when_traceExceptionWithModule() throws IOException, InterruptedException {
            log4j2EliteRollingFileLogger.trace(exception);
            log4j2EliteRollingFileLogger.close();
            checkLogContains("[TRACE] " + Thread.currentThread().getName() + " ST KEY : [0] " + exception.getMessage());
        }

        @Test
        public void level_threadName_then_stack_trace() throws IOException, InterruptedException {
            log4j2EliteRollingFileLogger.trace(module, exception);
            log4j2EliteRollingFileLogger.close();
            checkTraceLogContains("[TRACE] " + Thread.currentThread().getName() + " ["  + module + "] ST KEY : [0] java.lang.Exception: Testing");
        }

        private String readLogFiles() throws IOException {
            Path path = Paths.get( temporaryFolder.getRoot().getAbsolutePath(),testLogFile + "-" + serverInstanceName + ".log");
            return Files.lines(path).collect(Collectors.joining());
        }

        private String readTraceLogFiles() throws IOException {
            Path path = Paths.get( temporaryFolder.getRoot().getAbsolutePath(),testLogFile + "-trace-" + serverInstanceName + ".log");
            return Files.lines(path).collect(Collectors.joining());
        }

        private void checkLogContains(String expected) throws IOException {
            String path = temporaryFolder.getRoot().getAbsolutePath() + File.separator + testLogFile + "-" + serverInstanceName + ".log";
            String actual = readLogFiles();
            assertTrue("File is " +  path + ", Expected is " + expected + ", actual is " + actual, actual.contains(expected));
        }

        private void checkTraceLogContains(String expected) throws IOException {
            String path = temporaryFolder.getRoot().getAbsolutePath() + File.separator + testLogFile + "-trace-" + serverInstanceName + ".log";
            String actual = readTraceLogFiles();
            assertTrue("File is " +  path + ", Expected is " + expected + ", actual is " + actual, actual.contains(expected));
        }


    }


    public class updationInCurrentLogger {


        @Test
        public void loogerSetToProvidedLevelWhenValidLogLevelProvided() {
            int pick = new Random().nextInt(org.apache.logging.log4j.Level.values().length);
            org.apache.logging.log4j.Level randomLevel = org.apache.logging.log4j.Level.values()[pick];
            log4j2EliteRollingFileLogger.setLogLevel(randomLevel.name());
            assertEquals(randomLevel.intLevel(), log4j2EliteRollingFileLogger.getCurrentLogLevel());
        }

        @Test
        public void doesNotChangeLevelWhenInValidLogLevelProvided() {
            int pick = new Random().nextInt(org.apache.logging.log4j.Level.values().length);
            org.apache.logging.log4j.Level randomLevel = org.apache.logging.log4j.Level.values()[pick];
            log4j2EliteRollingFileLogger.setLogLevel(randomLevel.name());

            log4j2EliteRollingFileLogger.setLogLevel("InvalidLevel");

            assertEquals(randomLevel.intLevel(), log4j2EliteRollingFileLogger.getCurrentLogLevel());
        }

        @Test
        public void OnTraceLogOFFSetTraceLoggerLevelToOFF() {
            log4j2EliteRollingFileLogger.setTraceLogLevelOFF();
            assertFalse(log4j2EliteRollingFileLogger.isTraceLogEnabled());
        }

        @Test
        public void OnTraceLogOnSetTraceLoggerLevelToTrace() {
            log4j2EliteRollingFileLogger.setTraceLogLevelOn();
            assertTrue(log4j2EliteRollingFileLogger.isTraceLogEnabled());
        }


        @After
        public void closeLogger() throws IOException {

            log4j2EliteRollingFileLogger.close();

        }
    }


    @After
    public void deleteFolder() throws IOException {

        temporaryFolder.delete();

    }


}