package com.elitecore.netvertex.ws.cli;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.CommandExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebServiceCommandInterfaceTest {
    @Mock
    private CommandExecutor commandExecutor;
    private WebServiceCommandInterface webServiceCommandInterface;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        webServiceCommandInterface = new WebServiceCommandInterface(commandExecutor);
    }

    @Test
    public void executeCliCommand_callAdminServiceToExecuteCommand() throws Exception {
        String command = "anyCommand";
        webServiceCommandInterface.executeCommand(command, null);
        verify(commandExecutor).executeCommand(command, "");
    }

    @Test
    public void executeCliCommand_return_INVALID_INPUT_PARAMETER_whenCommandIsNull() throws Exception {
        String expected = ResultCode.INVALID_INPUT_PARAMETER.name();
        String actual = webServiceCommandInterface.executeCommand(null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void executeCliCommand_return_INVALID_INPUT_PARAMETER_whenCommandIsEmpty() throws Exception {
        String expected = ResultCode.INVALID_INPUT_PARAMETER.name();
        String actual = webServiceCommandInterface.executeCommand("    ", null);
        assertEquals(expected, actual);
    }

    @Test
    public void executeCliCommand_returnsOutputOfCommandExecutionAsReturnedByCommandExecutor() throws Exception {
        when(commandExecutor.executeCommand("help", "")).thenReturn("Test");
        String result = webServiceCommandInterface.executeCommand("help", null);
        assertEquals("Test", result);
    }
}
