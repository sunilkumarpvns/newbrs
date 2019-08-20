package com.elitecore.aaaclient.ui.session.radius;

import javax.swing.JTabbedPane;

import com.elitecore.aaaclient.ui.app.IApplicationContext;
import com.elitecore.aaaclient.ui.base.BaseScreen;

public class RadiusSessionScreen extends BaseScreen {

	private static final long serialVersionUID = 1L;
	
	private RadiusSessionBuilderPanel sessionBuilderPanel;
	private RadiusSessionLogPanel sessionLogPanel;
	private RadiusSessionReportPanel sessionReportPanel;
	
	private static int screenCounter;
	
	public RadiusSessionScreen(IApplicationContext applicationContext) {
		super("Radius Session <" + ++screenCounter + ">", applicationContext);
		
		initComponents();
	}
	
	private void initComponents() {
		
		JTabbedPane mainTabbedPane = new JTabbedPane();
		
		sessionBuilderPanel = new RadiusSessionBuilderPanel(getApplicationContext());
		sessionLogPanel = new RadiusSessionLogPanel();
		sessionReportPanel = new RadiusSessionReportPanel();
		
		mainTabbedPane.addTab("Session", sessionBuilderPanel);
		mainTabbedPane.addTab("Logs", sessionLogPanel);
		mainTabbedPane.addTab("Reports", sessionReportPanel);
		
		//mainTabbedPane.seta
		
		getContentPane().add(mainTabbedPane);
		
	}

	
}
