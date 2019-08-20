package com.elitecore.netvertex;

/**
 * CommandExecutor
 * @author Prakashkumar Pala, Harsh Patel
 * @since 16-Nov-2018
 * Used as interface to execute commands like CLI commands from EliteAdminService.
 */
public interface CommandExecutor {
    public String executeCommand(String command, String parameters);
}
