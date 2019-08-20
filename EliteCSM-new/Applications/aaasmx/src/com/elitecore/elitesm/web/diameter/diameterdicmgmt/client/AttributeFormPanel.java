package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AttributeFormPanel extends FormPanel {

	
	
	
	public AttributeFormPanel(AttributeItem attributeItem,List<DataTypeData> dataTypeList, List<DictionaryData> dictionaryDataList, DiameterdicMgmtEntryPoint diameterdicMgmtEntryPoint) {
		
		initAttributeFormPanel(attributeItem,dataTypeList,dictionaryDataList,diameterdicMgmtEntryPoint);

	}


	private void initAttributeFormPanel(final AttributeItem attributeItem, final List<DataTypeData> dataTypeList,final List<DictionaryData> dictionaryDataList, final DiameterdicMgmtEntryPoint diameterdicMgmtEntryPoint) {

           
			try{
			final AttributeData attributeData = attributeItem.getAttributeData();
		    
			VerticalPanel verticalPanel = new VerticalPanel();
			HorizontalPanel horizontalPanel= new HorizontalPanel();
			

			
			Label idLable = new Label("Vendor Parameter Id");
			idLable.getAbsoluteLeft();
			
			
			TextBox idTextBox=new TextBox();
		    idTextBox.setName("idTextBox");
			idTextBox.setValue(attributeData.getVendorParameterId().toString());
			Log.info("Vendor Parameter Id build");
			
			Label nameLable = new Label("Attribute Name");
			nameLable.getAbsoluteLeft();
			
		
			TextBox nameTextBox=new TextBox();
			nameTextBox.setName("nameTextBox");
			nameTextBox.setValue(attributeData.getName());
			
			Log.info("Attribute Name build");
			
			
			Label dataTypeLable = new Label("Data Type");
			dataTypeLable.getAbsoluteLeft();
			Log.info("dataTypeLable  build");
		    
			ListBox datatypeCombobox = new ListBox();
			int selectedIndex=0;
			 
			 
			
			 
			 for (Iterator iterator = dataTypeList.iterator(); iterator.hasNext();) {
				DataTypeData dataTypeData = (DataTypeData) iterator.next();
				datatypeCombobox.addItem(dataTypeData.getName());
				if(dataTypeData.getName().equals(attributeData.getDataTypeData().getName())){
					datatypeCombobox.setSelectedIndex(selectedIndex);
				}
				
				selectedIndex++;
			}
			 
			String dataType= datatypeCombobox.getValue(datatypeCombobox.getSelectedIndex());
			if("Grouped".equals(dataType)){
				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
				attributeTabPanel.create(attributeItem,dictionaryDataList,"Grouped",diameterdicMgmtEntryPoint);
				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
 			}else if("Enumerated".equals(dataType)){
 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
				attributeTabPanel.create(attributeItem,dictionaryDataList,"Enumerated",diameterdicMgmtEntryPoint);
				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
				
 			}else {
 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
 				attributeTabPanel.setVisible(false);
 				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
 			}
			
			
			/*
			 * Mandatory
			 */
		
			Label mandatoryLable = new Label("Mandatory");
			mandatoryLable.getAbsoluteLeft();
		
		
			
			ListBox mandatoryCombobox = new ListBox();
			mandatoryCombobox.addItem("yes");
			mandatoryCombobox.addItem("no");
			int index1 = 0;
			if("yes".equalsIgnoreCase(attributeData.getMandatory())){
				index1 = 0;
			}else{
				index1 = 1;
			}
			mandatoryCombobox.setSelectedIndex(index1);
			
			Log.info("Mandatory build");
			
			/*
			 * Protected
			 */
			
			Label protectedLable = new Label("Protected");
			protectedLable.getAbsoluteLeft();
		
			
			ListBox protectedCombobox = new ListBox();
			protectedCombobox.addItem("yes");
			protectedCombobox.addItem("no");
			int index2 = 0;
			if("yes".equalsIgnoreCase(attributeData.getStrProtected())){
				index2 = 0;
			}else{
				index2 = 1;
			}
			protectedCombobox.setSelectedIndex(index2);
			
			
			/*
			 * Encryption
			 * 
			 */
			
			Label encryptionLable = new Label("Encryption");
			encryptionLable.getAbsoluteLeft();
		
			
			ListBox encryptionCombobox = new ListBox();
			encryptionCombobox.addItem("yes");
			encryptionCombobox.addItem("no");
			int index3 = 0;
			if("yes".equalsIgnoreCase(attributeData.getEncryption())){
				index3 = 0;
			}else{
				index3 = 1;
			}
			encryptionCombobox.setSelectedIndex(index3);
			
			
			
						
			CaptionPanel captionalPanel = new CaptionPanel("Attribute Information");
			captionalPanel.setWidth("530px");
			//fill form 
			
			Grid grid = new Grid(6,2);
			
			grid.setWidget(0,0,idLable);
			grid.setWidget(0,1,idTextBox);
			
			grid.setWidget(1,0, nameLable);
			grid.setWidget(1,1, nameTextBox);
			
			grid.setWidget(2,0,dataTypeLable);
			grid.setWidget(2,1,datatypeCombobox);
             
			grid.setWidget(3,0,mandatoryLable);
			grid.setWidget(3,1,mandatoryCombobox);
		
			grid.setWidget(4,0,protectedLable);
			grid.setWidget(4,1,protectedCombobox);
		    
			grid.setWidget(5,0,encryptionLable);
			grid.setWidget(5,1,encryptionCombobox);
		    
			
			
			grid.ensureDebugId("cwGrid");
			captionalPanel.setContentWidget(grid);
            verticalPanel.add(captionalPanel);			
			this.add(verticalPanel);
			
		   
			BlurHandler blurHandler = new BlurHandler(){
		
			public void onBlur(BlurEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
	            	attributeData.setName(box.getValue());
	            	attributeItem.setName(box.getValue());
				}
				
			}
		};
		
			
		KeyPressHandler nameKeyPressHandler = new KeyPressHandler(){

			public void onKeyPress(KeyPressEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
	            	attributeData.setName(box.getValue());
	            	attributeItem.setName(box.getValue());
				}
				
			}
			
		};
		nameTextBox.addKeyPressHandler(nameKeyPressHandler);
	    KeyDownHandler nameKeyDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
	            	attributeData.setName(box.getValue());
	            	attributeItem.setName(box.getValue());
				}
				
				
			}
	    	
	    };
		nameTextBox.addKeyDownHandler(nameKeyDownHandler);
   
		KeyUpHandler nameKeyUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					attributeData.setName(box.getValue());
	            	attributeItem.setName(box.getValue());
				}
				
			}
			
		};
		nameTextBox.addKeyUpHandler(nameKeyUpHandler);
		
		
		/// vendor parameter id event handling
		
		
		KeyPressHandler idKeyPressHandler = new KeyPressHandler(){
			
			public void onKeyPress(KeyPressEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					attributeData.setVendorParameterId(Integer.parseInt(box.getValue()));
	            	
				}
				
			}
			
		};
		idTextBox.addKeyPressHandler(idKeyPressHandler);
        
		KeyDownHandler idKeyDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					attributeData.setVendorParameterId(Integer.parseInt(box.getValue()));
	            	
				}
				
				
			}
	    	
	    };
		idTextBox.addKeyDownHandler(idKeyDownHandler);
   
		KeyUpHandler idKeyUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					attributeData.setVendorParameterId(Integer.parseInt(box.getValue()));
	            	
				}
				
			}
			
		};
		idTextBox.addKeyUpHandler(idKeyUpHandler);
   
		/*
		 * Mandatory event
		 */
       
		ChangeHandler mandatoryChangeHandler = new ChangeHandler(){

			public void onChange(ChangeEvent event) {
         
				Object obj = event.getSource();
				if(obj instanceof ListBox){
					ListBox listBox = (ListBox)obj;
					attributeData.setMandatory(listBox.getValue(listBox.getSelectedIndex()));
	            	
				}
				
			}};
		
			mandatoryCombobox.addChangeHandler(mandatoryChangeHandler);
		
		/*
		 * Protected event
		 */
       
		ChangeHandler protectedChangeHandler = new ChangeHandler(){

			public void onChange(ChangeEvent event) {
         
				Object obj = event.getSource();
			    if(obj instanceof ListBox){
					ListBox listBox = (ListBox)obj;
		          	attributeData.setStrProtected(listBox.getValue(listBox.getSelectedIndex()));
	            	
				}
				
			}};
		
			protectedCombobox.addChangeHandler(protectedChangeHandler);
		
		/*
		 * Encryption event	
		 */

			ChangeHandler encryptionChangeHandler = new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	         
					Object obj = event.getSource();
				    if(obj instanceof ListBox){
						ListBox listBox = (ListBox)obj;
			          	attributeData.setEncryption(listBox.getValue(listBox.getSelectedIndex()));
		            	
					}
					
				}};
			
				encryptionCombobox.addChangeHandler(encryptionChangeHandler);
	
			
			
  
		// Data Type List
		
		ChangeHandler datatypeChangeHandler = new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				
				Object obj = event.getSource();
				
				if(obj instanceof ListBox){
					ListBox listBox = (ListBox)obj;
					
	              	int count = 0;
	            	for(int i=0;i<dataTypeList.size();i++){
	                    DataTypeData dataTypeData =dataTypeList.get(i);
	            		if(listBox.getValue(listBox.getSelectedIndex()).equals(dataTypeData.getName())){
	            			
	            			attributeData.setDataTypeData(dataTypeData);
	            			attributeData.setDataTypeId(dataTypeData.getDataTypeId());
	            			break;
	            		}
	            		
	            		count++;
	            	}
	            	
	            	String dataType= listBox.getValue(listBox.getSelectedIndex());
	    			if("Grouped".equals(dataType)){
	    				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
	    				attributeTabPanel.create(attributeItem,dictionaryDataList,"Grouped",diameterdicMgmtEntryPoint);
	    				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
	    				diameterdicMgmtEntryPoint.getVerticalSplitPanel().setBottomWidget(attributeTabPanel);
	     			}else if("Enumerated".equals(dataType)){
	     				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
	    				attributeTabPanel.create(attributeItem,dictionaryDataList,"Enumerated",diameterdicMgmtEntryPoint);
	    				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
	    				diameterdicMgmtEntryPoint.getVerticalSplitPanel().setBottomWidget(attributeTabPanel);
	    				
	     			}else {
	     				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
	     				attributeTabPanel.setVisible(false);
	     				diameterdicMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
	     				diameterdicMgmtEntryPoint.getVerticalSplitPanel().setBottomWidget(attributeTabPanel);
	     			}
	    			
	            	
				}
				
			}};
		
		datatypeCombobox.addChangeHandler(datatypeChangeHandler);
		
			}catch(Exception e){
				Log.trace("InitAttributeFormPanel", e);
			}
	}



	
	
}
