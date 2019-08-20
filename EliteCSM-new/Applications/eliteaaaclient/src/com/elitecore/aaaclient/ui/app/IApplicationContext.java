package com.elitecore.aaaclient.ui.app;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;


public interface IApplicationContext {

	public JFrame getMainWindowRef();
	
	public void showInternalFrame(JInternalFrame internalFrame);
	
}
