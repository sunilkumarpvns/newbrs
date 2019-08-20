package com.elitecore.aaaclient.ui.base;

import javax.swing.JPanel;

import com.elitecore.aaaclient.ui.app.IApplicationContext;

public class BaseScreenPanel extends JPanel {

	
	private IApplicationContext appContext;
	
	public BaseScreenPanel(IApplicationContext appContext) {
		this.appContext = appContext;
	}
	
	protected final IApplicationContext getApplicationContext() {
		return appContext;
	}
}
