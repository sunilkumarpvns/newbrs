/**
 * 
 */
package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AVPRuleData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.widgetideas.client.event.KeyboardHandler;
/**
 * @author pratik.chauhan
 *
 */
public class GroupedAttributePopupPanel extends PopupPanel{

	private AVPRuleData avpRuleData;
	private CaptionPanel container;
	private boolean isSubmit = false;
	private AttributeItem attributeItem;
	private GroupedValueFormPanel groupedValueFormPanel;
	private ClickHandler addClickHandler;
	private ClickHandler closeClickHandler;
	private SelectionHandler<SuggestOracle.Suggestion> nameSelectionHandler;
    
	final String nameErrorMsg="Attribute Rule Name must be specified";
	final String vendorErrorMsg="Vendor Id must be specified";
	final String attrIdErrorMsg="Attribute Id must be specified";
	final String maxErrorMsg="Maximum value must be specified";
	final String minErrorMsg="Minimum value must be specified";
	final String maxNumErrorMsg="Maximum value must be Numeric";
	final String minNumErrorMsg="Minimum value must be Numeric";
	final Label errorMsg = new Label("");
	final Label vendorIdLable= new Label("Vendor Id");
	final Label attrIdLable= new Label("Attribute Id");
	final Label nameLable = new Label("Attribute Name");
	final Label maxLable = new Label("Maximum");
	final Label minLable = new Label("Minimum");
	final Label typeLable = new Label("Attribute Rule Type");
	
	EliteMultiwordSuggest attributeNameSuggestions = new EliteMultiwordSuggest("-:");
	SuggestBox nameTextBox=null;
	TextBox  vendorTextBox=new TextBox();
	TextBox  attributeIdTextBox=new TextBox();
	
	
	final TextBox maxTextBox=new TextBox();
	final TextBox minTextBox=new TextBox();
	final ListBox ruleTypeCombobox = new ListBox();
	final Map<String,Object> vendorMap = new HashMap<String,Object>();
	final Map<String,AttributeData> attrMap=new HashMap<String, AttributeData>();
	final List<String> vendoridSuggestion = new ArrayList<String>();
	final List<String> vendordisplaySuggestion = new ArrayList<String>();
	final List<String> attributeidSuggestion = new ArrayList<String>();
	final List<String> attributedisplaySuggestion = new ArrayList<String>();



	public GroupedAttributePopupPanel(GroupedValueFormPanel panel,AttributeItem attributeItem, List<AttributeItem> attributetItemList, List<DictionaryData> dictionaryDataList) {
		
		/*set vendor id suggestion*/
		Log.debug("GroupedAttributePopupPanel Call Start");
		
		/*
		 *   Special Case :
		 *    vendor id=*[-1],attribute id =*[-1],attribute name=attribute
		 */
		DictionaryData dicData = new DictionaryData();
		dicData.setVendorId(-1L);
		AttributeData attrData=new AttributeData();
		attrData.setVendorParameterId(-1);
		attrData.setName("attribute");
		attrData.setVendorId(-1L);
		List<AttributeData> attributeList = new ArrayList<AttributeData>();
		attributeList.add(attrData);
		dicData.setAttributeList(attributeList);
		dictionaryDataList.add(dicData);
		
		for(int i =0;i<dictionaryDataList.size();i++){
			DictionaryData dicdata = dictionaryDataList.get(i);
			init(dicdata);
		}
		
		setSuggestions(attrMap);
		this.nameTextBox=new SuggestBox(this.attributeNameSuggestions);
		nameTextBox.setFocus(true);
		
		this.attributeItem=attributeItem;
		this.groupedValueFormPanel = panel;
		this.setGlassEnabled(true);
		this.setSize("400px","300px");
		avpRuleData=new AVPRuleData();
		container=new CaptionPanel("Grouped Attribute");
		createHandler();
		createPanel();

	}



	private void setSuggestions(Map<String, AttributeData> attrMap) {
		attributeNameSuggestions.clear();
		attributeNameSuggestions.setSuggestions(attrMap);
	}



	private void init(DictionaryData data) {
		Log.debug("init Call Start");		
		try{
			//String displayString=data.getVendorName() + "[ "+data.getVendorId() +" ]";
			if(data !=null){	
				
				List<AttributeData> attributeList = data.getAttributeList();
				for(int i=0;i<attributeList.size();i++){
					AttributeData attrData=attributeList.get(i);
					attrMap.put(attrData.getName(),attrData);
				}
				
			}
			}catch(Exception e){
				Log.error("Error in init Method", e);
			}
	
		
	}


	
	


	
	private void createHandler() {
		
		
		
		/*attributeNameFocusHandler = new FocusHandler(){
			public void onFocus(FocusEvent event) {
				
				String vendorId=vendorTextBox.getText();
				String attributeId=attributeIdTextBox.getText();
				
				if( ("".equals(vendorId)) || ("".equals(attributeId)))
				 setSuggestions(attrMap);
			}
			
		};
		
		
		attributeNameValueChangeHandler = new ValueChangeHandler<String>(){

			public void onValueChange(ValueChangeEvent<String> event) {
				 try{
	   				    Log.debug("attributeNameValueChangeHandler Call Start");
	   				    SuggestBox attrNameBox =(SuggestBox) event.getSource();
	   				    String  attributeName=attrNameBox.getText();
						Log.debug("Attribute Name Change Handler :attributeName is"+attributeName);
						
						AttributeData attrData=attrMap.get(attributeName);
   						
						if(attrData != null){
							if("attribute".equalsIgnoreCase(attributeName)){
								attributeIdTextBox.setText("*");
								vendorTextBox.setText("*");
							}else{
							attributeIdTextBox.setText(attrData.getVendorParameterId().toString());
							vendorTextBox.setText(attrData.getVendorId().toString());
							}
						}else{
							attributeIdTextBox.setText("");
							vendorTextBox.setText("");
							maxTextBox.setText("");
							minTextBox.setText(" ");
							ruleTypeCombobox.setSelectedIndex(0);
							attrNameBox.setFocus(true);
						}
						
						
						
				}catch(Exception e){
					Log.error("attribute Name value change handler", e);
				}
				
			}

			
			
		};
		
		attributeIdValueChangeHandler = new ValueChangeHandler<String>(){

			public void onValueChange(ValueChangeEvent<String> event) {
				try{
				 String  attributeName=nameTextBox.getText();
				 Log.debug("attributeIdValueChangeHandler call Start");
				 Log.debug("attributeName is :"+attributeName);
				 if("".equalsIgnoreCase(attributeName))
				 {  
					 Log.debug("attributeName should be blank attributeName is  :"+attributeName);
				             
					    String strVendorId=vendorTextBox.getText();
					    String strAttributeId=attributeIdTextBox.getText();
					    Log.debug("vendorId is :"+strVendorId +"attributeId is :"+strAttributeId);
					    
					    if(!( "".equals(strVendorId) ) && !( "".equals(strAttributeId) ) ){
					    	Long vendorId=Long.parseLong(strVendorId);
					    	Integer attrId=Integer.parseInt(strAttributeId);
					    	setAttributeNameSuggestion(vendorId,attrId);
					    }

					   
				 
					 
				 }
				 
				}catch(NumberFormatException e){
					Log.error("Number Format Exception attributeIdValueChangeHandler:onValueChange Method");
				}catch(Exception e){
					Log.error("Error in attributeIdValueChangeHandler:onValueChange Method");
				}
				
				
			}
			
		};*/
		
		

		
	 addClickHandler = new ClickHandler(){

			public void onClick(ClickEvent event) {

				Log.info("onClicked called");
				/*if(vendorTextBox.getText().length()==0){
					errorMsg.setVisible(true);
					errorMsg.setText(vendorErrorMsg);
					isSubmit=false;
				}else if(attributeIdTextBox.getText().length()==0){
					errorMsg.setVisible(true);
					errorMsg.setText(attrIdErrorMsg);
					isSubmit=false;
				}else*/
				if(nameTextBox.getText().length()==0){
					errorMsg.setVisible(true);
					errorMsg.setText(nameErrorMsg);
					isSubmit=false;
				}else if(maxTextBox.getText().length()==0){
					errorMsg.setVisible(true);
					errorMsg.setText(maxErrorMsg);
					isSubmit=false;
					//event.cancel();
				}else if(!isNumber(maxTextBox.getText())){
					errorMsg.setVisible(true);
					errorMsg.setText(maxNumErrorMsg);
					isSubmit=false;
					//event.cancel();
				}else if(minTextBox.getText().length()==0){
					errorMsg.setVisible(true);
					errorMsg.setText(minErrorMsg);
					isSubmit=false;
					//event.cancel();
				}else if(!isNumber(minTextBox.getText())){
					errorMsg.setVisible(true);
					errorMsg.setText(minNumErrorMsg);
					isSubmit=false;
					//event.cancel();
				}else{
					
					Log.info("validation successful");
					String attributeName=nameTextBox.getText();
					AttributeData attributeData=attrMap.get(attributeName);
					if(attributeData != null){
						
						//String vendorIdValues=vendorTextBox.getValue();
						String vendorIdValues=String.valueOf(attributeData.getVendorId());
						if("*".equals(vendorIdValues)){
							vendorIdValues="-1";
						}
						//String attrIdValues=attributeIdTextBox.getValue();
						String attrIdValues=String.valueOf(attributeData.getVendorParameterId());
						if("*".equals(attrIdValues)){
							attrIdValues="-1";
						}
								
						avpRuleData.setVendorId(Integer.parseInt(vendorIdValues));
						avpRuleData.setAttrId(Integer.parseInt(attrIdValues));
						avpRuleData.setName(nameTextBox.getText());
						avpRuleData.setMaximum(maxTextBox.getValue());
						avpRuleData.setMinimum(minTextBox.getValue());
						if(ruleTypeCombobox.getSelectedIndex()==0){
							avpRuleData.setType("R");	
						}else if(ruleTypeCombobox.getSelectedIndex()==1){
							avpRuleData.setType("O");
						}else if(ruleTypeCombobox.getSelectedIndex()==2){
							avpRuleData.setType("F");
						}
						isSubmit=true;
						
						if("R".equals(avpRuleData.getType())){
							attributeItem.getAttributeData().getRequiredGroupedAttribute().add(avpRuleData);
	
						}else if("O".equals(avpRuleData.getType())){
							attributeItem.getAttributeData().getOptionalGroupedAttribute().add(avpRuleData);
	
						}else if("F".equals(avpRuleData.getType())){
							attributeItem.getAttributeData().getFixedGroupedAttribute().add(avpRuleData);
						}
						closePopup();
					
						}else{
							errorMsg.setVisible(true);
							errorMsg.setText("Attribute  [ " +attributeName + " ]  does not exist");
							isSubmit=false;	
						}
			}

				



			}

		};
			
			closeClickHandler=new ClickHandler(){

				public void onClick(ClickEvent event) {
					closePopup();
				}

			};
			
			nameSelectionHandler = new SelectionHandler<Suggestion>(){

				public void onSelection(SelectionEvent<Suggestion> event) {
					try{
					Suggestion selectedSuggestion = ((Suggestion) event.getSelectedItem());
					Log.debug("Selected Attribute Name DisplayString:"+selectedSuggestion.getDisplayString());
					Log.debug("Selected Attribute Name ReplacementString:"+selectedSuggestion.getReplacementString());
					String replacementString=selectedSuggestion.getReplacementString();
					String[] displayNameArray=GroupedAttributePopupPanel.split(replacementString,"[");
					/*Log.debug("=====DisplayName Array===================");
					Log.debug("=====DisplayName First Element is:"+displayNameArray[0]);
					Log.debug("=====DisplayName Second Element is:"+displayNameArray[1]);*/
					nameTextBox.getTextBox().setText(displayNameArray[0]);
					maxTextBox.setFocus(true);
					
					}catch(Exception exp){
						Log.error("Error in Attribute Name Selection Handler",exp.getCause());
					}
					
					
				}};
				
				
			

	}
	
	
	private boolean isNumber(String component) {
		try{
			Integer.parseInt(component);
		}catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/*
	 * GWT Split Functionality [Native Call] 
	 */

	public static final native String[] split(String string, String separator) /*-{
    return string.split(separator);
    }-*/;

	private void createPanel() {
		Log.info("createPanel() called");
		Grid formGrid = new Grid(5,2);
		VerticalPanel holder = new VerticalPanel(); 
		
		/*
		 * Attribute Rule Name
		 */
		nameLable.getAbsoluteLeft();
		formGrid.setWidget(0,0,nameLable);
		nameTextBox.setValue("");
		//nameTextBox.addValueChangeHandler(attributeNameValueChangeHandler);
		//nameTextBox.getTextBox().addFocusHandler(attributeNameFocusHandler);
		nameTextBox.addSelectionHandler(nameSelectionHandler);
		formGrid.setWidget(0,1,nameTextBox);
    	
		
		/*
		 *  Attribute Ids
		 
		
		vendorIdLable.getAbsoluteLeft();
		formGrid.setWidget(1,0,vendorIdLable);
		vendorTextBox.setValue("");
		formGrid.setWidget(1,1,vendorTextBox);
		

		
		 * Vendor Id
		 
		
		attrIdLable.getAbsoluteLeft();
		formGrid.setWidget(2,0,attrIdLable);
		attributeIdTextBox.setValue("");
		attributeIdTextBox.addValueChangeHandler(attributeIdValueChangeHandler);
		formGrid.setWidget(2,1,attributeIdTextBox);
*/
		/*
		 * Maximum
		 */

		maxLable.getAbsoluteLeft();
		formGrid.setWidget(1,0,maxLable);

		maxTextBox.setName("maxTextBox");
		maxTextBox.setValue("");
		formGrid.setWidget(1,1,maxTextBox);

		/*
		 * Minimum
		 */

		minLable.getAbsoluteLeft();
		formGrid.setWidget(2,0,minLable);

		minTextBox.setName("minTextBox");
		minTextBox.setValue("");
		formGrid.setWidget(2,1,minTextBox);

		/*
		 * Type of attribute rule 
		 */

		typeLable.getAbsoluteLeft();
		formGrid.setWidget(3,0,typeLable);

		ruleTypeCombobox.addItem("Required","R");
		ruleTypeCombobox.addItem("Optional","O");
		ruleTypeCombobox.addItem("Fixed","F");
		formGrid.setWidget(3,1,ruleTypeCombobox);
		
	
		Button addButton = new Button("Add");
		addButton.addClickHandler(addClickHandler);
		
		Button closeButton = new Button("Close");
        closeButton.addClickHandler(closeClickHandler);
        
		
		formGrid.setWidget(4,0,addButton);
		formGrid.setWidget(4,1,closeButton); 

		holder.add(formGrid);
		VerticalPanel containerVerticalPanel=new VerticalPanel();
        containerVerticalPanel.add(errorMsg);
        containerVerticalPanel.add(holder);
		container.add(containerVerticalPanel);
		this.add(container);

	}





	protected void closePopup() {

		if(isSubmit){
			groupedValueFormPanel.buildGroupedAttributePanel(attributeItem);
		}
		this.hide(true); 		

	}



	/**
	 * @return the avpRuleData
	 */
	public AVPRuleData getAvpRuleData() {
		return avpRuleData;
	}



	/**
	 * @param avpRuleData the avpRuleData to set
	 */
	public void setAvpRuleData(AVPRuleData avpRuleData) {
		this.avpRuleData = avpRuleData;
	}



	/**
	 * @return the isSubmit
	 */
	public boolean isSubmit() {
		return isSubmit;
	}



	/**
	 * @param isSubmit the isSubmit to set
	 */
	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}
	
    @Override
     public boolean onKeyDownPreview(char key, int modifiers) {
 
    	switch (key) {
    	 
         case KeyboardHandler.KEY_ENTER:
         case KeyboardHandler.KEY_ESCAPE:
             hide();
             break;
         }

         return true;
     }
	
}
   
	
		


