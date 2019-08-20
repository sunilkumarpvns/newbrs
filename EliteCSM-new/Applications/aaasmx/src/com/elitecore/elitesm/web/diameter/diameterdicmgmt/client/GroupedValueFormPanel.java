package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AVPRuleData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GroupedValueFormPanel extends VerticalPanel {

	Grid requiredGroupedAttrGrid;
	Grid optionalGroupedAttrGrid;
	Grid fixedGroupedAttrGrid;
	AttributeItem attributeItem;
	ClickHandler removeClickHandler;
	ClickHandler addClickHandler;
	static boolean enabledExecute=false;
	boolean flag = true;
	List<AttributeItem> attributetItemList;
	List<DictionaryData> dictionaryDataList;
	public GroupedValueFormPanel(AttributeItem attributeItem,List<DictionaryData> dictionaryDataList, DiameterdicMgmtEntryPoint diameterdicMgmtEntryPoint){
		this.attributetItemList=diameterdicMgmtEntryPoint.getAttributeItemList();
		this.attributeItem = attributeItem;
		this.dictionaryDataList=dictionaryDataList;
		buildHandler();
		buildGroupedAttributePanel(attributeItem);

	}
 

	private void buildHandler() {
		
		
		addClickHandler = new ClickHandler(){

			public void onClick(ClickEvent event) {
				/*
				 * popup panel call
				 * 
				 */
				
				GroupedAttributePopupPanel groupedAttributePopupPanel = new GroupedAttributePopupPanel(GroupedValueFormPanel.this,attributeItem,attributetItemList,dictionaryDataList);
				groupedAttributePopupPanel.center();
				groupedAttributePopupPanel.show();

				
			}
			
	  };
		
		
		removeClickHandler = new ClickHandler(){

			public void onClick(ClickEvent event) {
				
				Log.info("Remove Button clicked"+event.getSource());
				
				RemoveButton clickedButton =(RemoveButton)event.getSource();
				try{
					
					if("R".equalsIgnoreCase(clickedButton.getType())){
						requiredGroupedAttrGrid.removeRow(clickedButton.getIndex());
						attributeItem.getAttributeData().getRequiredGroupedAttribute().remove(clickedButton.getObject());
						
					}else if("O".equalsIgnoreCase(clickedButton.getType())){
						optionalGroupedAttrGrid.removeRow(clickedButton.getIndex());
						attributeItem.getAttributeData().getOptionalGroupedAttribute().remove(clickedButton.getObject());
						
					}else if("F".equalsIgnoreCase(clickedButton.getType())){
						fixedGroupedAttrGrid.removeRow(clickedButton.getIndex());
						attributeItem.getAttributeData().getFixedGroupedAttribute().remove(clickedButton.getObject());
					}
				
					buildGroupedAttributePanel(attributeItem);
			

					
					
				}catch(Exception e){
					Log.error("Error in Key Press event:"+e.getMessage(),e);
				}
			}
			
		};
		

		
	}




	public GroupedValueFormPanel() {

	}





	 void buildGroupedAttributePanel(AttributeItem attributeItem) {

		try{
			this.clear();
			VerticalPanel verticalPanel = new VerticalPanel();
			Button addButton=new Button("Add",addClickHandler);
			verticalPanel.add(addButton);

			CaptionPanel reqCaptionPanel = new CaptionPanel("Required");
			reqCaptionPanel.setVisible(true);
			reqCaptionPanel.setWidth("600px");

			CaptionPanel opCaptionPanel = new CaptionPanel("Optional");
			opCaptionPanel.setVisible(true);
			opCaptionPanel.setWidth("600px");

			CaptionPanel fixedCaptionPanel = new CaptionPanel("Fixed");
			fixedCaptionPanel.setVisible(true);
			fixedCaptionPanel.setWidth("600px");

			Collection<AVPRuleData> reqGroupedAttr=attributeItem.getAttributeData().getRequiredGroupedAttribute();
			Collection<AVPRuleData> opGroupedAttr=attributeItem.getAttributeData().getOptionalGroupedAttribute();
			Collection<AVPRuleData> fixedGroupedAttr=attributeItem.getAttributeData().getFixedGroupedAttribute();

			/*
			 * Build Required Panel
			 */
			requiredGroupedAttrGrid = createGroupAttrGrid(reqGroupedAttr,"R");
			reqCaptionPanel.add(requiredGroupedAttrGrid);	

			/*
			 * Build Optional Panel
			 */
			optionalGroupedAttrGrid =createGroupAttrGrid(opGroupedAttr,"O");
			opCaptionPanel.add(optionalGroupedAttrGrid);
			/*
			 * Build fixed Panel
			 */
			fixedGroupedAttrGrid =createGroupAttrGrid(fixedGroupedAttr,"F");
			fixedCaptionPanel.add(fixedGroupedAttrGrid);

			/*
			 * set all panels to main panel
			 */

			verticalPanel.add(reqCaptionPanel);
			verticalPanel.add(opCaptionPanel);
			verticalPanel.add(fixedCaptionPanel);
			this.add(verticalPanel);
		}catch(Exception e){
			Log.trace("Error in buildGroupedAttributePanel", e);
		}


	}


	private Grid createGroupAttrGrid(Collection<AVPRuleData> groupedAttr,String type) {
		 this.clear();
		Grid groupedAttrGrid=null;
		try{
			int groupedAttrGridLen=groupedAttr.size();
			groupedAttrGrid = new Grid(groupedAttrGridLen+1,7);

			Label srnoLabel = new Label("Sr. No");
			srnoLabel.getAbsoluteLeft();
			Label vendorIdLabel = new Label("Vendor Id");
			Label attrIdLabel = new Label("Attribute Id");
			Label nameLabel = new Label("Attribute Rule Name");
			Label maxLabel = new Label("Maximum");
			Label minLabel = new Label("Minimum");
			Label removeLabel = new Label("Remove");



			groupedAttrGrid.setWidget(0, 0,srnoLabel);
			groupedAttrGrid.setWidget(0, 1,vendorIdLabel);
			groupedAttrGrid.setWidget(0, 2,attrIdLabel);
			groupedAttrGrid.setWidget(0, 3,nameLabel);
			groupedAttrGrid.setWidget(0, 4,maxLabel);
			groupedAttrGrid.setWidget(0, 5,minLabel);
			groupedAttrGrid.setWidget(0, 6,removeLabel);


			groupedAttrGrid.getCellFormatter().setWidth(0,0,"200px");
			groupedAttrGrid.getCellFormatter().setWidth(0,1,"200px");
			groupedAttrGrid.getCellFormatter().setWidth(0,2,"200px");
			groupedAttrGrid.getCellFormatter().setWidth(0,3,"500px");
			groupedAttrGrid.getCellFormatter().setWidth(0,4,"200px");
			groupedAttrGrid.getCellFormatter().setWidth(0,5,"200px");
			groupedAttrGrid.getCellFormatter().setWidth(0,6,"200px");

			int index=1;

			for (Iterator iterator = groupedAttr.iterator(); iterator.hasNext();) {
				int count=1;


				AVPRuleData ruleData = (AVPRuleData) iterator.next();



				Label srnolabel = new Label(" "+index+" ");
				Label nameValueLabel = new Label(ruleData.getName());
				Label maxValueLabel = new Label(ruleData.getMaximum());
				Label minValueLabel = new Label(ruleData.getMinimum());
				
				String vendorIdValue=String.valueOf(ruleData.getVendorId());
				String attrIdValue=String.valueOf(ruleData.getAttrId());
				
				if("-1".equals(vendorIdValue)){
					vendorIdValue="*";
				}
				if("-1".equals(attrIdValue)){
					attrIdValue="*";	
				}
				
				Label  vendorIdValueLabel = new Label(vendorIdValue);
				Label  attrIdValueLabel = new Label(attrIdValue);
				
				RemoveButton removeButton = new RemoveButton(index,"Remove",type,ruleData);
				removeButton.addClickHandler(removeClickHandler);

				groupedAttrGrid.setWidget(index, 0,srnolabel);
				groupedAttrGrid.setWidget(index, 1,vendorIdValueLabel);
				groupedAttrGrid.setWidget(index, 2,attrIdValueLabel);
				groupedAttrGrid.setWidget(index, 3,nameValueLabel);
				groupedAttrGrid.setWidget(index, 4,maxValueLabel);
				groupedAttrGrid.setWidget(index, 5,minValueLabel);
				groupedAttrGrid.setWidget(index, 6,removeButton);
				
				groupedAttrGrid.getCellFormatter().setWidth(index,0,"200px");
				groupedAttrGrid.getCellFormatter().setWidth(index,1,"200px");
				groupedAttrGrid.getCellFormatter().setWidth(index,2,"200px");
				groupedAttrGrid.getCellFormatter().setWidth(index,3,"500px");
				groupedAttrGrid.getCellFormatter().setWidth(index,4,"200px");
				groupedAttrGrid.getCellFormatter().setWidth(index,5,"200px");
				groupedAttrGrid.getCellFormatter().setWidth(index,6,"200px");
				index++;

			}

			groupedAttrGrid.ensureDebugId("cwGrid");
		}catch(Exception e){
			Log.trace("Error in buildGroupAttrGrid", e);
		}
		return groupedAttrGrid;
	}


	

	/**
	 * @return the enabledExecute
	 */
	public boolean isEnabledExecute() {
		return enabledExecute;
	}


	/**
	 * @param enabledExecute the enabledExecute to set
	 */
	public void setEnabledExecute(boolean enabledExecute) {
		this.enabledExecute = enabledExecute;
	}
      
	


}










