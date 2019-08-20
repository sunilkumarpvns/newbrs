package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Font;

import javax.swing.JRadioButton;

public class EliteRadioButton extends JRadioButton {
	
	private static final long serialVersionUID = 1L;

	public EliteRadioButton(String label) {
		super(label);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
}
