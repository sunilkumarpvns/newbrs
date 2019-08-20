package com.elitecore.elitesm.web.radius.dictionarymgmt.client;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.AttributeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DictionaryData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DictionaryMgmtEntryPoint implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final DictionaryMgmtServiceAsync dictionaryService = GWT
	.create(DictionaryMgmtService.class);

	/**
	 * This is the entry point method.
	 */

	//ArrayList<> attributeNodeArrayList = new ArrayList<PersonItem>();


	private static final boolean logEnabled = false;
	private static DictionaryItem dictionaryItem = new DictionaryItem();
	private static DictionaryData dictionaryData = new DictionaryData();
	private static DataTypeData defaultDataTypeData = null;
	private static List<DataTypeData> dataTypeList = new ArrayList<DataTypeData>(); 
	private static DictionaryTree dictionaryTree;
	private static ArrayList<AttributeItem> attributeItemList = new ArrayList<AttributeItem>();
	private static TextArea textArea = new TextArea();

	String strDictionaryId  = null;
	private final VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
	AttributeTabPanel attrTabPanel=new AttributeTabPanel();
	public void onModuleLoad() {


		Log.setUncaughtExceptionHandler();
		if(!logEnabled){
			Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
			logWidget.setVisible(false);
		}
		strDictionaryId = Window.Location.getParameter("dictionaryId");
		initDictionaryMgmtGUI();
		// loading image stop



	}


	protected void initDictionaryMgmtGUI() {

		//get dictionary data 
		AsyncCallback<DictionaryData> callback = new AsyncCallback<DictionaryData>(){


			public void onFailure(Throwable caught) {
				Log.error("Error in fetching  Dictionary data fetching",caught);
			}


			public void onSuccess(DictionaryData result) {
				//loading image display stop.
				DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));
				dictionaryItem = getAttributeList(result); 

			}};

			dictionaryService.getDictionaryData(strDictionaryId, callback);

			/*
			 *  get datatype list    
			 */

			AsyncCallback<List<DataTypeData>> dataTypeListcallback = new AsyncCallback<List<DataTypeData>>(){

				public void onFailure(Throwable caught) {

					Log.error("Error in fetching datatype list",caught);

				}

				public void onSuccess(List<DataTypeData> result) {
					dataTypeList = result;

				}
			};  
			dictionaryService.getDataTypeList(dataTypeListcallback);  

			/*
			 * get XML String
			 * 
			 */
			AsyncCallback<String> xmlStringCallback =  new AsyncCallback<String>(){

				public void onFailure(Throwable caught) {
					Log.error("Error in converting XML String",caught.getCause());

				}

				public void onSuccess(String result) {

					textArea.setText(result);
					
				}

			};
			dictionaryService.getDictionaryAsXML(strDictionaryId, xmlStringCallback);


	}






	protected DictionaryItem getAttributeList(DictionaryData result) {
		final DictionaryItem dictionaryItem = new DictionaryItem();

		final String strDictionaryId = String.valueOf(result.getDictionaryId());
		final ArrayList<AttributeData> arrayList = new ArrayList<AttributeData>();

		AsyncCallback<List<AttributeData>> callback = new AsyncCallback<List<AttributeData>>(){


			public void onFailure(Throwable caught) {

				Log.error("Error ::::",caught);
			}


			public void onSuccess(List<AttributeData> result) {

				for (Iterator<AttributeData> iterator = result.iterator(); iterator.hasNext();) {
					AttributeData attributeData = (AttributeData) iterator.next();
					AttributeItem  attributeItem = new AttributeItem(attributeData);
					attributeItemList.add(attributeItem);
					arrayList.add(attributeData);
				}
				dictionaryItem.setAttributeItemList(attributeItemList);


				verticalSplitPanel.setSize("580px", "580px");
				verticalSplitPanel.setSplitPosition("50%");


				dictionaryTree = new DictionaryTree(dictionaryItem);
				dictionaryTree.addSelectionHandler(new SelectionHandler<TreeItem>(){

					public void onSelection(SelectionEvent<TreeItem> event) {
						try{
							TreeItem item=event.getSelectedItem();

							// set node
							//final PersonItem treeItem=(PersonItem)event.getSelectedItem();


							if(item instanceof DictionaryItem){
								DictionaryItem dictionaryItem=(DictionaryItem)item;	
								//lable.setText("Dictionary Name is:"+dictionaryItem.getText());
								//if(!dictionaryItem.isInitialized()){
								//fetch attribute list base on dictionary id...


								DictionaryFormPanel dictionaryFormPanel = new DictionaryFormPanel(dictionaryItem);
								dictionaryFormPanel.setSize("550px", "280px");
								verticalSplitPanel.setTopWidget(dictionaryFormPanel);
								dictionaryFormPanel.setVisible(true);
								dictionaryItem.setName(dictionaryItem.getDictionaryData().getName());

								dictionaryItem.initialized();
								//}

							}else if(item instanceof AttributeItem){

								final AttributeItem attributeItem=(AttributeItem)item;
								Log.info("===========================Debug Start========================================");

								Log.info("AttributeFormPanel is creating.............");
								attributeItem.setState(true);
								final AttributeFormPanel attributeFormPanel =new AttributeFormPanel(attributeItem,dataTypeList,DictionaryMgmtEntryPoint.this);
								Log.info("AttributeFormPanel is created");
								Log.info("===========================Debug End========================================");
								attributeFormPanel.setSize("550px", "280px");
								verticalSplitPanel.setTopWidget(attributeFormPanel);
								verticalSplitPanel.setBottomWidget(getAttrTabPanel());
								attributeFormPanel.setVisible(true);
								attributeItem.setName(attributeItem.getAttributeData().getName());

								/*attributeItem.setName(attributeItem.getAttributeData().getName());
							SupportedValueFormPanel supportedValueFormPanel = new SupportedValueFormPanel(attributeItem);
							supportedValueFormPanel.setSize("550px", "280px");
							verticalSplitPanel.setBottomWidget(supportedValueFormPanel);
							supportedValueFormPanel.setVisible(true);*/




							}
						}catch(Exception e){
							Log.trace("Error:",e);
						}
					}});


				// update dictionary tree

				ClickHandler updateTreeHandler=new ClickHandler(){



					public void onClick(ClickEvent event) {

						//DOM.appendChild(RootPanel.getBodyElement(),  DOM.getElementById("loading"));

						/*
						 * build updated dictionary data object
						 */


						DictionaryData dictionaryData = DictionaryMgmtEntryPoint.dictionaryItem.getDictionaryData();	
						final List<AttributeItem> attrItemList = new ArrayList<AttributeItem>();
						AsyncCallback<DictionaryData> updateDictionarycallback = new AsyncCallback<DictionaryData>(){

							public void onFailure(Throwable caught) {
								Log.error("Error in update dictionary tree:",caught);

							}
							public void onSuccess(DictionaryData result) {

								//clear all panel

								resetPanel();
								//DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));

								final DictionaryItem dictionaryItem = new DictionaryItem();

								for (Iterator iterator = result.getAttributeList().iterator(); iterator.hasNext();) {
									AttributeData attributeData = (AttributeData) iterator.next();
									AttributeItem  attributeItem = new AttributeItem(attributeData);

									attrItemList.add(attributeItem);

								}
								dictionaryItem.setDictionaryData(result);
								dictionaryItem.setAttributeItemList(attrItemList);
								DictionaryMgmtEntryPoint.dictionaryItem = dictionaryItem;
								dictionaryTree.setDictionary(DictionaryMgmtEntryPoint.dictionaryItem);	

								AsyncCallback<String> xmlStringCallback =  new AsyncCallback<String>(){

									public void onFailure(Throwable caught) {
										Log.error("Error in converting XML String",caught.getCause());

									}

									public void onSuccess(String result) {

										textArea.setText(result);
									}

								};

								dictionaryService.getDictionaryAsXML(strDictionaryId, xmlStringCallback);
							}
						};
						dictionaryService.updateDictionary(dictionaryData,updateDictionarycallback); 




					}};
					Button dictionaryTreeButton  = new Button("Update",updateTreeHandler);



					final HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();

					horizontalSplitPanel.setLeftWidget(dictionaryTree);
					horizontalSplitPanel.add(verticalSplitPanel);
					horizontalSplitPanel.setSplitPosition("30%");
					dictionaryTree.setVisible(true);
					dictionaryTree.setAnimationEnabled(true);

					horizontalSplitPanel.setVisible(true);
					horizontalSplitPanel.setSize("800px","600px");



					// Tab panel

					TabPanel tabPanel = new TabPanel();

					FlowPanel treeTabFlowPanel = new FlowPanel();


					/*
					 * =============================================================
					 * 
					 * Add Remove Button for Tree Item
					 * 
					 * ============================================================= 
					 *  
					 */

					/// add new tree item

					ClickHandler addTreeItemHandler=new ClickHandler(){

						public void onClick(ClickEvent event) {

							TreeItem selectedItem=DictionaryMgmtEntryPoint.dictionaryTree.getSelectedItem();
							try{
								if(selectedItem instanceof DictionaryItem){
									DictionaryItem dictionaryItem = (DictionaryItem)selectedItem;
									AttributeItem newAttributeItem = getDefaultAttributeItem();
									if(newAttributeItem!=null){
										selectedItem.addItem(newAttributeItem);
										DictionaryMgmtEntryPoint.dictionaryItem.getAttributeItemList().add(newAttributeItem);
										DictionaryMgmtEntryPoint.dictionaryItem.getDictionaryData().getAttributeList().add(newAttributeItem.getAttributeData());
										//dictionaryTree.setDictionary(DictionaryMgmtEntryPoint.dictionaryItem);
										selectedItem.setSelected(false);
										newAttributeItem.setSelected(true);
										selectedItem.setState(true);
									}

								}else if(selectedItem instanceof AttributeItem){


									AttributeItem parentAttributeItem =(AttributeItem)selectedItem;
									if("DTT11".equalsIgnoreCase(parentAttributeItem.getAttributeData().getDataTypeId()))
									{

										AttributeItem newAttributeItem = getDefaultAttributeItem(parentAttributeItem);
										if(newAttributeItem!=null){
											parentAttributeItem.addItem(newAttributeItem);
											parentAttributeItem.getAttributeData().getChildAttributeList().add(newAttributeItem.getAttributeData());
											//dictionaryTree.setDictionary(DictionaryMgmtEntryPoint.dictionaryItem);
											selectedItem.setSelected(false);
											newAttributeItem.setSelected(true);
											selectedItem.setState(true);
										}


									}else{

										Window.alert("Please Select Grouped Data Type");

									}



								}
							}catch(Exception e){
								Log.trace("Error", e);
							}

						}

					};

					/// remove tree item

					ClickHandler removeTreeItemHandler=new ClickHandler(){

						public void onClick(ClickEvent event) {

							TreeItem selectedItem=DictionaryMgmtEntryPoint.dictionaryTree.getSelectedItem();
							if(selectedItem instanceof AttributeItem){

								/*
								 * Remove Item node from Tree
								 */


								AttributeItem childItem = (AttributeItem)selectedItem;
								Boolean confirm=Window.confirm("Do you want to remove '"+childItem.getText()+"'?");
								if(confirm){

									AttributeData childAttributeData=childItem.getAttributeData();

									TreeItem parentItem=selectedItem.getParentItem();

									if(parentItem instanceof AttributeItem){

										AttributeItem item=(AttributeItem)parentItem;
										AttributeData parentAttributeData=item.getAttributeData();

										parentAttributeData.getChildAttributeList().remove(childAttributeData);
										childAttributeData=null;

									}else if(parentItem instanceof DictionaryItem){

										DictionaryItem item=(DictionaryItem)parentItem;
										DictionaryData parentDictionaryData=item.getDictionaryData();
										parentDictionaryData.getAttributeList().remove(childAttributeData);

									}

									selectedItem.removeItems();  
									selectedItem.remove();
								}

							}


						}

					};


					Button addTreeItemButton = new Button("Add",addTreeItemHandler);
					//addTreeItemButton.setSize("100px", "30px");
					//addTreeItemButton.setVisible(true);

					Button removeTreeItemButton = new Button("Remove",removeTreeItemHandler);
					//removeTreeItemButton.setSize("100px", "30px");
					//removeTreeItemButton.setVisible(true);

					HorizontalSplitPanel dicTreeAddRemovePanel = new HorizontalSplitPanel();
					dicTreeAddRemovePanel.add(addTreeItemButton);
					dicTreeAddRemovePanel.add(removeTreeItemButton);
					dicTreeAddRemovePanel.setVisible(true);


					//treeTabFlowPanel.add(dicTreeAddRemovePanel);
					treeTabFlowPanel.add(addTreeItemButton);
					treeTabFlowPanel.add(removeTreeItemButton);
					treeTabFlowPanel.add(horizontalSplitPanel);
					treeTabFlowPanel.add(dictionaryTreeButton);
					tabPanel.add(treeTabFlowPanel,"Dictionary Tree");

					FlowPanel xmlTabFlowPanel =  new FlowPanel();
					//Label label = new Label("Insert XML File here.");

					textArea.setSize("800px","600px");
					textArea.setReadOnly(true);
					textArea.setStyleName("xmltext");
					xmlTabFlowPanel.add(textArea);
					tabPanel.add(xmlTabFlowPanel,"XML");
					tabPanel.selectTab(0);
					tabPanel.setSize("800px","600px");
					tabPanel.addSelectionHandler(new SelectionHandler<Integer>(){

						public void onSelection(SelectionEvent<Integer> event) {

							Integer selectedTabIndex=event.getSelectedItem();
							if(selectedTabIndex==0){
								//build dictionary tree from XML
								//DictionaryUtility.buildDictionaryItem(textArea.getText());


							}else if(selectedTabIndex==1){

								// parse XML String and build Dictionary Tree
								//String xmlString=DictionaryUtility.buildXML(dictionaryItem);
								/*
								 * Temp..
								 * call server side and get xml view in popup window..
								 */

							}

						}



					});


					Button xmlSaveButton = new Button("Save",new ClickHandler(){


						public void onClick(ClickEvent event) {
							String xmlString = textArea.getValue();
							final List<AttributeItem> attrItemList = new ArrayList<AttributeItem>();
							AsyncCallback<DictionaryData> asyncCallback = new AsyncCallback<DictionaryData>(){

								public void onFailure(Throwable caught) {
									Log.error("Error while creating tree from xml",caught);

								}

								public void onSuccess(DictionaryData result) {
									final DictionaryItem dictionaryItem = new DictionaryItem();

									for (Iterator iterator = result.getAttributeList().iterator(); iterator.hasNext();) {
										AttributeData attributeData = (AttributeData) iterator.next();
										AttributeItem  attributeItem = new AttributeItem(attributeData);
										attrItemList.add(attributeItem);

									}
									dictionaryItem.setDictionaryData(result);
									dictionaryItem.setAttributeItemList(attrItemList);
									dictionaryTree.setDictionary(dictionaryItem);
								}


							};

							dictionaryService.saveDictionary(xmlString,DictionaryMgmtEntryPoint.this.strDictionaryId,asyncCallback);

						}});


					xmlSaveButton.setVisible(true);
					xmlSaveButton.setSize("100px", "30px");
					//xmlTabFlowPanel.add(xmlSaveButton);

					RootPanel.get("demo").clear();
					RootPanel.get("demo").add(tabPanel);



			}};

			dictionaryService.getAttributeList(DictionaryMgmtEntryPoint.this.strDictionaryId,callback);


			result.setAttributeList(arrayList);
			dictionaryItem.setDictionaryData(result);
			return dictionaryItem;
	}

	public  void resetPanel() {
		verticalSplitPanel.clear();
	}
	private  DataTypeData getDefaultDataType(){
		if(defaultDataTypeData==null){
			for (Iterator iterator = dataTypeList.iterator(); iterator.hasNext();) {
				DataTypeData dataTypeData= (DataTypeData) iterator.next();
				if("string".equalsIgnoreCase(dataTypeData.getName())){
					defaultDataTypeData = dataTypeData;
					return defaultDataTypeData;
				}
			}
		}
		return defaultDataTypeData;
	}
	private AttributeItem getDefaultAttributeItem(AttributeItem parentItem){
		AttributeItem newAttributeItem = null;
		try{
			newAttributeItem =	new AttributeItem();
			newAttributeItem.setName("untitled");
			newAttributeItem.getAttributeData().setName("untitled");

			newAttributeItem.getAttributeData().setVendorParameterId(-1);
			newAttributeItem.getAttributeData().setDataType(getDefaultDataType());
			newAttributeItem.getAttributeData().setAlias("default");
			newAttributeItem.getAttributeData().setRadiusRFCDictionaryParameterId(-1);
			newAttributeItem.getAttributeData().setVendorParameterOveridden("N");
			newAttributeItem.getAttributeData().setHasTag("no");
			newAttributeItem.getAttributeData().setIgnoreCase("no");
			newAttributeItem.getAttributeData().setAvPair("no");
			newAttributeItem.getAttributeData().setLengthFormat("tlv");
			newAttributeItem.getAttributeData().setPaddingType("none");

			DictionaryData dictionaryData = DictionaryMgmtEntryPoint.dictionaryItem.getDictionaryData();
			newAttributeItem.getAttributeData().setDictionaryNumber(dictionaryData.getDictionaryNumber());
			newAttributeItem.getAttributeData().setDictionaryId(dictionaryData.getDictionaryId());
			newAttributeItem.getAttributeData().setVendorId(dictionaryData.getVendorId());

			if(parentItem != null){
				newAttributeItem.getAttributeData().setParentDetailId(parentItem.getAttributeData().getDictionaryParameterDetailId());
			}
		}catch(Exception e){
			Log.trace("DefaultAttributeItem:",e);
		}
		return newAttributeItem;
	}
	private AttributeItem getDefaultAttributeItem(){
		return getDefaultAttributeItem(null);
	}



	public AttributeTabPanel getAttrTabPanel() {
		return attrTabPanel;
	}

	public void setAttrTabPanel(AttributeTabPanel attrTabPanel) {
		this.attrTabPanel = attrTabPanel;
	}


	public VerticalSplitPanel getVerticalSplitPanel() {
		return verticalSplitPanel;
	}




}










