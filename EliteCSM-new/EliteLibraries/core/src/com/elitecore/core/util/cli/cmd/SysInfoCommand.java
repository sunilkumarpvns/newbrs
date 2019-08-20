package com.elitecore.core.util.cli.cmd;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.server.data.EliteSystemDetail;

public class SysInfoCommand extends EliteBaseCommand{
	
	@Override
	public String execute(String parameter) {
		return EliteSystemDetail.getSummary();
	}

	@Override
	public String getCommandName() {
		return "sysinfo";
	}

	@Override
	public String getDescription() {
		return "Gives the system detail";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'sysinfo':{}}";
	}

}
