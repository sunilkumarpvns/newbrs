package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;

public class EliteButton extends JButton {
	
	private static final long serialVersionUID = 1L;

	public EliteButton(String label) {
		super(label);
		setMargin(new Insets(1, 1, 1, 1));
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
}
