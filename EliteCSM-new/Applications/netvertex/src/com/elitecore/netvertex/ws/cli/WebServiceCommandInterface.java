package com.elitecore.netvertex.ws.cli;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.CommandExecutor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * @author Prakashkumar Pala
 * @since 16-Nov-2018
 * This class is used as a medium to execute CLI commands from REST.
 */

@Path("/cli")
public class WebServiceCommandInterface {

    private static final String MODULE = "WEB-SERVICE-COMMAND-INTERFACE";
    private CommandExecutor commandExecutor;

    public WebServiceCommandInterface(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @GET
    @Path("/execute-command/{command}")
    @Produces(MediaType.TEXT_PLAIN)
    public String executeCommand(@PathParam("command") String command
            , @QueryParam("parameters") String parameters) throws Exception {
        if(Strings.isNullOrBlank(command)){
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "Invalid Inputs. "+command);
            }
            return ResultCode.INVALID_INPUT_PARAMETER.name();
        }
        return commandExecutor.executeCommand(command, (parameters==null)?"":parameters);
    }
}
