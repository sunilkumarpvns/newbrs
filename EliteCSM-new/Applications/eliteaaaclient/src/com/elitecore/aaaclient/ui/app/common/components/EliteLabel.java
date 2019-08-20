package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Font;

import javax.swing.JLabel;

public class EliteLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;

	public EliteLabel(String label) {
		super(" " + label);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
	
	public void setText(String text) {
		super.setText(" " + text);
	}

	
}
