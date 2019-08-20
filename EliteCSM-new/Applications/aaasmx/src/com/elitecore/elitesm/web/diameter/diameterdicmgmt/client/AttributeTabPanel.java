/**
 * 
 */
package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * @author pratik.chauhan
 *
 */
public class AttributeTabPanel extends TabPanel {

     
	public AttributeTabPanel() {

	}

	public void create(AttributeItem attributeItem, List<DictionaryData> dictionaryDataList, String dataType, DiameterdicMgmtEntryPoint diameterdicMgmtEntryPoint) {
		
		if("Grouped".equalsIgnoreCase(dataType)){
			  FlowPanel grpAttrFlowPanel = new FlowPanel();
		      GroupedValueFormPanel groupAttrFormPanel=new GroupedValueFormPanel(attributeItem,dictionaryDataList,diameterdicMgmtEntryPoint);
			  groupAttrFormPanel.setSize("500px","280px");
			  groupAttrFormPanel.setVisible(true);
			  grpAttrFlowPanel.add(groupAttrFormPanel);
			  this.setSize("550px", "280px");
			  this.setVisible(true);
			  this.insert(grpAttrFlowPanel,"Grouped Attribute",0);
			  this.selectTab(0);
			
		}else if("Enumerated".equalsIgnoreCase(dataType)){
			FlowPanel suppValueFlowPanel = new FlowPanel();
			SupportedValueFormPanel supportedValueFormPanel = new SupportedValueFormPanel(attributeItem);
			supportedValueFormPanel.setSize("500px", "280px");
			supportedValueFormPanel.setVisible(true);
			suppValueFlowPanel.add(supportedValueFormPanel);
			this.insert(suppValueFlowPanel,"Supported Value",0);
			this.setSize("550px", "280px");
			this.setVisible(true);
			this.selectTab(0);
		
		}
		
		
	}
	
	public AttributeTabPanel getPanel(){
		return this;
	}

	


}
