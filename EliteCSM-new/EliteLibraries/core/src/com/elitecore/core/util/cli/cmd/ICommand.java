package com.elitecore.core.util.cli.cmd;

public interface ICommand {

	public String execute(String parameter);
	public String getCommandName();
	public String getDescription();
	public String getHotkeyHelp();
}
