package com.elitecore.aaaclient.ui.app.common.components;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class EliteTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	
	private Component nextFocusComponent;

	public EliteTextField(int columns) {
		this("", columns);
	}

	public EliteTextField(String text, int columns) {
		super(text, columns);
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10){
					if (nextFocusComponent != null){
						nextFocusComponent.requestFocus();
					}
				}
			}
			});
	}
	
	public Component getNextFocusComponent() {
		return nextFocusComponent;
	}

	public void setNextFocusComponent(Component nextFocusComponent) {
		this.nextFocusComponent = nextFocusComponent;
	}
	
	
	
}
