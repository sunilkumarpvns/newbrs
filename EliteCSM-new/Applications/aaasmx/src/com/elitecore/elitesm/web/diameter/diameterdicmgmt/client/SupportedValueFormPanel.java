package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SupportedValueFormPanel extends VerticalPanel {
	Grid supportedValueListGrid;
	AttributeItem attributeItem;
	ClickHandler removeClickHandler;
	ClickHandler addClickHandler;
	
	boolean flag = true;
	public SupportedValueFormPanel(AttributeItem attributeItem){
        this.attributeItem = attributeItem;
        buildHandler();
        buildSupportedValuePanel(attributeItem);
        
	}

	
	

	private void buildHandler() {
		
		removeClickHandler = new ClickHandler(){

			public void onClick(ClickEvent event) {
				RemoveButton clickedButton =(RemoveButton)event.getSource();
				supportedValueListGrid.removeRow(clickedButton.getIndex());
				try{

					String predefinedValueString=getCurrentPredefinedValue().toString();
					attributeItem.getAttributeData().setPredefinedValues(predefinedValueString);
					buildSupportedValuePanel(attributeItem);
				}catch(Exception e){
					Log.error("Error in Key Press event:"+e.getMessage(),e);
				}
			}
			
		};
		
		addClickHandler = new ClickHandler(){

			public void onClick(ClickEvent event) {
				String predefinedValues = attributeItem.getAttributeData().getPredefinedValues();
				if(predefinedValues==null || predefinedValues.length()<=0){
					predefinedValues ="unknown:-1";
				}else{
					predefinedValues = predefinedValues + ",unknown:-1";
				}
				attributeItem.getAttributeData().setPredefinedValues(predefinedValues);
				buildSupportedValuePanel(attributeItem);
			}
			
		};
		
	}




	private void buildSupportedValuePanel(AttributeItem attributeItem) {
        this.clear();
		VerticalPanel verticalPanel = new VerticalPanel();
		CaptionPanel captionPanel = new CaptionPanel("Supported Value List");
		captionPanel.setWidth("530px");

		String supportedValueString = attributeItem.getAttributeData().getPredefinedValues();
		int supportedValueLen=0;

		if(supportedValueString != null && supportedValueString.trim().length()>0 ){

			String[] supportedValueList = supportedValueString.split(",");
			supportedValueLen = supportedValueList.length;

			if(supportedValueLen>0){
				int index = 1;
				supportedValueListGrid = new Grid(supportedValueLen+1,4);

				Label srnoLabel = new Label("Sr. No");
				srnoLabel.getAbsoluteLeft();
				Label displayNameLabel = new Label("Display Name");
				Label actualValueLabel = new Label("Actual Value");
				Label removeLabel = new Label("Remove");
				supportedValueListGrid.setWidget(0, 0,srnoLabel);
				supportedValueListGrid.setWidget(0, 1,displayNameLabel);
				supportedValueListGrid.setWidget(0, 2,actualValueLabel);
				supportedValueListGrid.setWidget(0, 3,removeLabel);
				supportedValueListGrid.getCellFormatter().setWidth(0,0,"100px");
				supportedValueListGrid.getCellFormatter().setWidth(0,1,"100px");
				supportedValueListGrid.getCellFormatter().setWidth(0,2,"100px");
				supportedValueListGrid.getCellFormatter().setWidth(0,3,"100px");
				
				TextBoxKeyPressHandler keyPressHandler = new TextBoxKeyPressHandler();
				TextBoxKeyDownHandler keyDownHandler = new TextBoxKeyDownHandler();
				TextBoxKeyUpHandler keyUpHandler = new TextBoxKeyUpHandler();
				
				
				for (int i = 0; i < supportedValueList.length; i++) {

					String[] predefinedValue = supportedValueList[i].split(":");
					if(predefinedValue != null && predefinedValue.length==2){
						 

                        Label srnolabel = new Label(" "+index+" ");  
						//srnolabel.getAbsoluteLeft();
						TextBox displayValueTxtBox = new TextBox();
						displayValueTxtBox.setName("displayValueTxtBox");
						displayValueTxtBox.setValue(predefinedValue[0]);
						
						displayValueTxtBox.addKeyPressHandler(keyPressHandler);
						displayValueTxtBox.addKeyDownHandler(keyDownHandler);
						displayValueTxtBox.addKeyUpHandler(keyUpHandler);
						
						TextBox actualValueTxtBox = new TextBox();
						actualValueTxtBox.setName("actualValueTxtBox");
						actualValueTxtBox.setValue(predefinedValue[1]);
						
						actualValueTxtBox.addKeyPressHandler(keyPressHandler);
						actualValueTxtBox.addKeyDownHandler(keyDownHandler);
						actualValueTxtBox.addKeyUpHandler(keyUpHandler);
						
                        
						RemoveButton removeButton = new RemoveButton(i+1,"Remove","none",null);
						removeButton.addClickHandler(removeClickHandler);
						
						supportedValueListGrid.setWidget(i+1, 0,srnolabel);
						supportedValueListGrid.setWidget(i+1, 1,displayValueTxtBox);
						supportedValueListGrid.setWidget(i+1, 2,actualValueTxtBox);
						supportedValueListGrid.setWidget(i+1, 3,removeButton);
                        index++;

					}
				}
				supportedValueListGrid.ensureDebugId("cwGrid");
				
				
		        captionPanel.add(supportedValueListGrid);	
		        
		        

			}else{
				
				captionPanel.setHeight("300px");
			}


		}
		Button addButton=new Button("Add",addClickHandler);
		verticalPanel.add(addButton);
		verticalPanel.add(captionPanel);			
		this.add(verticalPanel);
		
	
	
	}
	private class TextBoxKeyPressHandler implements KeyPressHandler{
		public void onKeyPress(KeyPressEvent event) {
            try{
			
			String predefinedValueString=getCurrentPredefinedValue().toString();
			attributeItem.getAttributeData().setPredefinedValues(predefinedValueString);
			
            }catch(Exception e){
            	Log.error("Error in Key Press event:"+e.getMessage(),e);
            }
		}
	}
	private class TextBoxKeyDownHandler implements KeyDownHandler{

		public void onKeyDown(KeyDownEvent event) {
			try{
				
				String predefinedValueString=getCurrentPredefinedValue().toString();
				attributeItem.getAttributeData().setPredefinedValues(predefinedValueString);
	            }catch(Exception e){
	            	Log.error("Error in Key Down event:"+e.getMessage(),e);
	            }
		}
		
	}
	private class TextBoxKeyUpHandler implements KeyUpHandler{
        
		public void onKeyUp(KeyUpEvent event) {
			try{
				
				String predefinedValueString=getCurrentPredefinedValue().toString();
				attributeItem.getAttributeData().setPredefinedValues(predefinedValueString);
	            }catch(Exception e){
	            	Log.error("Error in Key Up event:"+e.getMessage(),e);
	            }
		}
		
	}
	
	private StringBuffer getCurrentPredefinedValue(){
		
		StringBuffer predefinedValue=new StringBuffer();
		String supportedValueString=null;
		for(int i=1;i<supportedValueListGrid.getRowCount();i++){
			
			 
			TextBox displayTextBox = (TextBox)supportedValueListGrid.getWidget(i, 1);
			String displayName =displayTextBox.getValue();
			
			TextBox actualValueTextBox = (TextBox)supportedValueListGrid.getWidget(i, 2);
			String actualValue =actualValueTextBox.getValue();
			
			supportedValueString=displayName+":"+actualValue;
			if(predefinedValue.length()>0){
				predefinedValue.append(",");
				predefinedValue.append(supportedValueString);
				
			}else{
				predefinedValue.append(supportedValueString);
			}
			
		}
		
		return predefinedValue;
	}
}










