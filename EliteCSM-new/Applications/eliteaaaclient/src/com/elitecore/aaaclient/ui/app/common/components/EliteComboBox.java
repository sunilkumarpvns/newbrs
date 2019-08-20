package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Font;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class EliteComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;

	public EliteComboBox() {
	
	}
	
	public EliteComboBox(Object[] items) {
		super(items);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
	public EliteComboBox(ComboBoxModel aModel) {
		super(aModel);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
	public EliteComboBox(Vector arg0) {
		super(arg0);
		setFont(new Font(null, Font.PLAIN, 12));
	}
	
	

	
}
