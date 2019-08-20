package com.elitecore.elitesm.web.expressionbuilder.client.ui;

import java.util.List;

import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EliteEditor extends Composite {
	
	private VerticalPanel vp;
	private AutoCompleteTextBox textArea;
	
	private HorizontalPanel hp;
	private Button andButton= new Button(" && ");
	private Button orButton= new Button(" || ");
	private Button equalButton= new Button(" = ");
	private Button notEqualButton= new Button(" != ");
	private Button lessThanButton= new Button(" < ");
	private Button greaterThanButton= new Button(" > ");
	private ClickHandler clickHandler;
	
	public EliteEditor(List<AttributeData> attributeList) {
		vp = new VerticalPanel();
		vp.setWidth("100%");
		
		
		hp= new HorizontalPanel();
		
		andButton.setStyleName("light-btn");
		andButton.setText("&&");
		andButton.setWidth("30");
		
		orButton.setStyleName("light-btn");
		orButton.setText("||");
		orButton.setWidth("30");
		
		equalButton.setStyleName("light-btn");
		equalButton.setText("=");
		equalButton.setWidth("30");
		
		notEqualButton.setStyleName("light-btn");
		notEqualButton.setText("!=");
		notEqualButton.setWidth("30");
		
		lessThanButton.setStyleName("light-btn");
		lessThanButton.setText("<");
		lessThanButton.setWidth("30");
		
		greaterThanButton.setStyleName("light-btn");
		greaterThanButton.setText(">");
		greaterThanButton.setWidth("30");
		
		hp.add(andButton);
		hp.add(orButton);
		hp.add(equalButton);
		hp.add(notEqualButton);
		hp.add(lessThanButton);
		hp.add(greaterThanButton);
		
		hp.setCellWidth(andButton,"30");
		hp.setCellWidth(orButton,"30");
		hp.setCellWidth(equalButton,"30");
		hp.setCellWidth(notEqualButton,"30");
		hp.setCellWidth(lessThanButton,"30");
		hp.setCellWidth(greaterThanButton,"30");
		
		hp.setSpacing(2);
		
		
		textArea = new AutoCompleteTextBox(attributeList);
		textArea.setWidth("820px");
		textArea.setHeight("100px");
		
		
		vp.add(hp);
		vp.add(textArea);
		createButtonHandler();
		initWidget(vp);
	}
	
	 public AutoCompleteTextBox getTextArea(){
		 return textArea;
	 }
	 
	 private void createButtonHandler() {
			
			clickHandler = new ClickHandler(){

				
				public void onClick(ClickEvent event) {
					
					String preStr="";
					String lastStr="";
					Button clickedButton=(Button) event.getSource();
					String name=clickedButton.getText();
					String text=textArea.getText();
					int cursorPos=textArea.getCursorPos();
					
					if(cursorPos!=0){
						preStr=text.substring(0,cursorPos);
						preStr.trim();
					}
					if(cursorPos!=text.length()){
						lastStr=text.substring(cursorPos,text.length());
						lastStr.trim();
					}
					
					if(name.equalsIgnoreCase("&&")){
						
						textArea.setText(preStr+"&&"+lastStr);
						textArea.setCursorPos(cursorPos+2);
					}else if(name.equalsIgnoreCase("||")){
						
						textArea.setText(preStr+"||"+lastStr);
						textArea.setCursorPos(cursorPos+2);
					}else if(name.equalsIgnoreCase("=")){
						textArea.setText(preStr+"="+lastStr);
						textArea.setCursorPos(cursorPos+1);
					}else if(name.equalsIgnoreCase("!=")){
						textArea.setText(preStr+"!="+lastStr);
						textArea.setCursorPos(cursorPos+2);
					}else if(name.equalsIgnoreCase("<")){
						textArea.setText(preStr+"<"+lastStr);
						textArea.setCursorPos(cursorPos+1);
					}else if(name.equalsIgnoreCase(">")){
						textArea.setText(preStr+">"+lastStr);
						textArea.setCursorPos(cursorPos+1);
					}
					
					textArea.setFocus(true);
					
				}
				
				
			};
			
			andButton.addClickHandler(clickHandler);
			orButton.addClickHandler(clickHandler);
			equalButton.addClickHandler(clickHandler);
			notEqualButton.addClickHandler(clickHandler);
			lessThanButton.addClickHandler(clickHandler);
			greaterThanButton.addClickHandler(clickHandler);
		}

}
