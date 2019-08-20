/**
 * 
 */
package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * @author pratik.chauhan
 *
 */
public class AttributeTabPanel extends TabPanel {

     
	public AttributeTabPanel() {

	}

	public void create(AttributeItem attributeItem, String dataType, DictionaryMgmtEntryPoint dictionaryMgmtEntryPoint) {
		Log.info("AttributeTabPanel's create() method called");
		/*if("Grouped".equalsIgnoreCase(dataType)){
			  FlowPanel grpAttrFlowPanel = new FlowPanel();
		      GroupedValueFormPanel groupAttrFormPanel=new GroupedValueFormPanel(attributeItem,dictionaryMgmtEntryPoint);
			  groupAttrFormPanel.setSize("500px","280px");
			  groupAttrFormPanel.setVisible(true);
			  grpAttrFlowPanel.add(groupAttrFormPanel);
			  this.setSize("550px", "280px");
			  this.setVisible(true);
			  this.insert(grpAttrFlowPanel,"Grouped Attribute",0);
			  this.selectTab(0);
			
		}else*/ if("integer".equalsIgnoreCase(dataType)){
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
