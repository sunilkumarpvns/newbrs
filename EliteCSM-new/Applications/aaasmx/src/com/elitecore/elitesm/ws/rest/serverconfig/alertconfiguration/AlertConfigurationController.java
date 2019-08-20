package com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration;

import javax.ws.rs.Path;

@Path("/")
public class AlertConfigurationController {
	
	@Path("/traplistener")
	public TrapListenerController getTrapListnerController() {
		return new TrapListenerController();
	}
	
	@Path("/filelistener")
	public FileListenerController getFileListenerController() {
		return new FileListenerController();
	}
	
	@Path("/sysloglistener")
	public SysLogListenerController getSysLogListenerController() {
		return new SysLogListenerController();
		
	}

}
