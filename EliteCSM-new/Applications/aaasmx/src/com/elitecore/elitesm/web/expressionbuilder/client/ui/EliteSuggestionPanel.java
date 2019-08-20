package com.elitecore.elitesm.web.expressionbuilder.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

public class EliteSuggestionPanel extends PopupPanel implements ClickHandler {

	private ListBox choices = new ListBox();
	private boolean visible=false;
	private AutoCompleteTextBox autoCompleteTextBox;
	private ClickHandler clickHandler;
	public EliteSuggestionPanel(AutoCompleteTextBox autoCompleteTextBox) {
		super(true);
		
		this.add(choices);
		this.setStyleName("elite-suggestion");
		choices.setStyleName("labeltext");
		choices.setWidth("240px");
		this.setHeight("100px");
		createHandler();
		this.autoCompleteTextBox=autoCompleteTextBox;
	}

	

	public ListBox getChoices() {
		return choices;
	}

	public void setChoices(ListBox choices) {
		this.choices = choices;
	}

	public EliteSuggestionPanel getEliteSuggestionPanel(){
		return this;	
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public AutoCompleteTextBox getAutoCompleteTextBox() {
		return autoCompleteTextBox;
	}

	public void setAutoCompleteTextBox(AutoCompleteTextBox autoCompleteTextBox) {
		this.autoCompleteTextBox = autoCompleteTextBox;
	}



	public void onClick(ClickEvent event) {
		autoCompleteTextBox.complete();
	}

	private void createHandler() {
		clickHandler = new ClickHandler(){
			public void onClick(ClickEvent event) {
				autoCompleteTextBox.complete();
			}
		};
		choices.addClickHandler(clickHandler);
	}

	
	
}

















