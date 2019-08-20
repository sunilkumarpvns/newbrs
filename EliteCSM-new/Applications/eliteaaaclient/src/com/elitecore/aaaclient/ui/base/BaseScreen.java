package com.elitecore.aaaclient.ui.base;

import javax.swing.JInternalFrame;

import com.elitecore.aaaclient.ui.app.IApplicationContext;

public class BaseScreen extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private IApplicationContext appContext;
	
	public BaseScreen(IApplicationContext appContext) {
		this("", appContext);
	}
	
	public BaseScreen(String title, IApplicationContext appContext) {
		super(title, true, true, true, true);
		this.appContext = appContext;
	}
	
	protected final IApplicationContext getApplicationContext() {
		return appContext;
	}
	
}
