package com.elitecore.aaaclient.ui.session.radius;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class RadiusSessionLogPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public RadiusSessionLogPanel() {
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		add (new JButton("Log Panel"));
	}
	
}
