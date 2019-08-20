package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Font;

import javax.swing.JLabel;

public class SimpleLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;

	public SimpleLabel(String label) {
		setText(" "+label);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
	
	public void setText(String text) {
		super.setText(" " + text);
	}

	
}
