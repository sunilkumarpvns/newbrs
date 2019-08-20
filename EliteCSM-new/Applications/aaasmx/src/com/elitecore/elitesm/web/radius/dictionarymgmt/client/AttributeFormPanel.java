package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.AttributeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DataTypeData;
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



	private static final int ATTRIBUTR_NAME_MAXLENGTH = 100;


	public AttributeFormPanel(AttributeItem attributeItem,List<DataTypeData> dataTypeList, DictionaryMgmtEntryPoint dictionaryMgmtEntryPoint ) {

		initAttributeFormPanel(attributeItem,dataTypeList,dictionaryMgmtEntryPoint);

	}


	private void initAttributeFormPanel(final AttributeItem attributeItem, final List<DataTypeData> dataTypeList, final DictionaryMgmtEntryPoint dictionaryMgmtEntryPoint) {
		try{

			final AttributeData attributeData = attributeItem.getAttributeData();

			VerticalPanel verticalPanel = new VerticalPanel();
			HorizontalPanel horizontalPanel= new HorizontalPanel();



			Label idLable = new Label("Vendor Parameter Id");
			idLable.getAbsoluteLeft();


			TextBox idTextBox=new TextBox();
			idTextBox.setName("idTextBox");
			idTextBox.setValue(attributeData.getVendorParameterId().toString());


			Label nameLable = new Label("Attribute Name");
			nameLable.getAbsoluteLeft();


			TextBox nameTextBox=new TextBox();
			nameTextBox.setName("nameTextBox");
			nameTextBox.setValue(attributeData.getName());
			nameTextBox.setMaxLength(ATTRIBUTR_NAME_MAXLENGTH);


			Label dataTypeLable = new Label("Data Type");
			dataTypeLable.getAbsoluteLeft();


			ListBox datatypeCombobox = new ListBox();
			int selectedIndex=0;

			for (Iterator iterator = dataTypeList.iterator(); iterator.hasNext();) {
				DataTypeData dataTypeData = (DataTypeData) iterator.next();
				datatypeCombobox.addItem(dataTypeData.getName());
				if(dataTypeData.getName().equals(attributeData.getDataType().getName())){
					datatypeCombobox.setSelectedIndex(selectedIndex);  
				}
				selectedIndex++;

			}
			String dataType= datatypeCombobox.getValue(datatypeCombobox.getSelectedIndex());
			Log.info("Selected DATATYPE is:"+dataType);
			if("integer".equals(dataType)){
				Log.info("Selected DATATYPE is integer");
 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
				attributeTabPanel.create(attributeItem,"integer",dictionaryMgmtEntryPoint);
				dictionaryMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
				
 			}else {
 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
 				attributeTabPanel.setVisible(false);
 				dictionaryMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
 			}



			//ListBox dataTypeCombo = new ListBox();


			// Attribute av pair

			Label avpairLable = new Label("AV Pair");
			dataTypeLable.getAbsoluteLeft();



			ListBox avpairCombobox = new ListBox();
			avpairCombobox.addItem("Yes","yes");
			avpairCombobox.addItem("No","no");
			int index1 = 0;
			if("yes".equalsIgnoreCase(attributeData.getAvPair())){
				index1 = 0;
			}else{
				index1 = 1;
			}
			avpairCombobox.setSelectedIndex(index1);


			Label hastagLable = new Label("Has-Tag");
			hastagLable.getAbsoluteLeft();


			ListBox hastagCombobox = new ListBox();
			hastagCombobox.addItem("Yes","yes");
			hastagCombobox.addItem("No","no");
			int index2 = 0;
			if("yes".equalsIgnoreCase(attributeData.getHasTag())){
				index2 = 0;
			}else{
				index2 = 1;
			}
			hastagCombobox.setSelectedIndex(index2);


			//Ignore Case
			Label ignoreCaseLable = new Label("Ignore Case");
			ignoreCaseLable.getAbsoluteLeft();
			ListBox ignoreCaseCombobox = new ListBox();
			ignoreCaseCombobox.addItem("Yes","yes");
			ignoreCaseCombobox.addItem("No","no");
			int indexIgnoreCase = 0;
			if("yes".equalsIgnoreCase(attributeData.getIgnoreCase())){
				indexIgnoreCase = 0;
			}else{
				indexIgnoreCase = 1;
			}
			ignoreCaseCombobox.setSelectedIndex(indexIgnoreCase);


			//Encrypt Standard Case
			Label encryptStandardLable = new Label("Encrypted Standard");
			encryptStandardLable.getAbsoluteLeft();
			ListBox encryptStandardCombobox = new ListBox();
			encryptStandardCombobox.addItem("Plain Password(0)","0");
			encryptStandardCombobox.addItem("RFC-2868(1)","1");
			encryptStandardCombobox.addItem("RFC-2865-Password(2)","2");
			encryptStandardCombobox.addItem("Cisco-Password(9)","9");

			int indexEncryptStandard = 0;
			if (0== attributeData.getEncryptStandard()){
				indexEncryptStandard = 0;
			}else if(1== attributeData.getEncryptStandard()){
				indexEncryptStandard = 1;
			}else if (2== attributeData.getEncryptStandard()){
				indexEncryptStandard = 2;
			}else {
				indexEncryptStandard = 3;	
			}
			encryptStandardCombobox.setSelectedIndex(indexEncryptStandard);
			
			
			// Length Format
			Label lengthFormatLable = new Label("Length Format");
			lengthFormatLable.getAbsoluteLeft();
			
			ListBox lengthFormatCombobox = new ListBox();
			lengthFormatCombobox.addItem("value","value");
			lengthFormatCombobox.addItem("tlv","tlv");
			int indexLengthFormat = 0;
			if("tlv".equalsIgnoreCase(attributeData.getLengthFormat())){
				indexLengthFormat = 1;
			}else{
				indexLengthFormat = 0;
			}
			lengthFormatCombobox.setSelectedIndex(indexLengthFormat);
			
			// Padding type
			Label paddingTypeLabel = new Label("Padding Type");
			paddingTypeLabel.getAbsoluteLeft();
			
			ListBox paddingTypetComboBox = new ListBox();
			paddingTypetComboBox.addItem("None","none");
			paddingTypetComboBox.addItem("dhcp", "dhcp");
			if("none".equalsIgnoreCase(attributeData.getPaddingType())){
				paddingTypetComboBox.setSelectedIndex(0);
			}else{
				paddingTypetComboBox.setSelectedIndex(1);
			}
		
			
			

			CaptionPanel captionalPanel = new CaptionPanel("Attribute Information");
			captionalPanel.setWidth("530px");
			//fill form 

			Grid grid = new Grid(9,2);

			grid.setWidget(0,0,idLable);
			grid.setWidget(0,1,idTextBox);

			grid.setWidget(1,0, nameLable);
			grid.setWidget(1,1, nameTextBox);

			grid.setWidget(2,0,dataTypeLable);
			grid.setWidget(2,1,datatypeCombobox);

			grid.setWidget(3,0,avpairLable);
			grid.setWidget(3,1,avpairCombobox);

			grid.setWidget(4,0,hastagLable);
			grid.setWidget(4,1,hastagCombobox);

			grid.setWidget(5,0,ignoreCaseLable);
			grid.setWidget(5,1,ignoreCaseCombobox);

			grid.setWidget(6,0,encryptStandardLable);
			grid.setWidget(6,1,encryptStandardCombobox);
			
			grid.setWidget(7, 0, lengthFormatLable);
			grid.setWidget(7, 1, lengthFormatCombobox);
			
			grid.setWidget(8, 0, paddingTypeLabel);
			grid.setWidget(8, 1, paddingTypetComboBox);
			/*grid.setWidget(5,0,caseSensitiveLable);
			grid.setWidget(5,1,caseSensitiveTextBox);
			 */


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

			//avpair event

			ChangeHandler avpairChangeHandler = new ChangeHandler(){

				public void onChange(ChangeEvent event) {

					Object obj = event.getSource();
					if(obj instanceof ListBox){
						ListBox listBox = (ListBox)obj;
						attributeData.setAvPair(listBox.getValue(listBox.getSelectedIndex()));

					}

				}};

				avpairCombobox.addChangeHandler(avpairChangeHandler);

				//hastag event

				ChangeHandler hastagChangeHandler = new ChangeHandler(){

					public void onChange(ChangeEvent event) {

						Object obj = event.getSource();
						if(obj instanceof ListBox){
							ListBox listBox = (ListBox)obj;
							attributeData.setHasTag(listBox.getValue(listBox.getSelectedIndex()));

						}

					}};

					hastagCombobox.addChangeHandler(hastagChangeHandler);

					
					//Ignore Case event

					ChangeHandler ignoreCaseChangeHandler = new ChangeHandler(){

						public void onChange(ChangeEvent event) {

							Object obj = event.getSource();
							if(obj instanceof ListBox){
								ListBox listBox = (ListBox)obj;
								attributeData.setIgnoreCase(listBox.getValue(listBox.getSelectedIndex()));

							}

						}};

						ignoreCaseCombobox.addChangeHandler(ignoreCaseChangeHandler);

						
					//Encrypted Standard event
					ChangeHandler encryptStandardChangeHandler = new ChangeHandler(){
						public void onChange(ChangeEvent event) {
							Object obj = event.getSource();
							if(obj instanceof ListBox){
								ListBox listBox = (ListBox)obj;
								Log.info("Value of encrypted standard:"+Integer.parseInt(listBox.getValue(listBox.getSelectedIndex())));
								attributeData.setEncryptStandard(Integer.parseInt(listBox.getValue(listBox.getSelectedIndex())));
							}
						}};
					encryptStandardCombobox.addChangeHandler(encryptStandardChangeHandler);
					
					//Length Format event
					ChangeHandler lengthFormatChangeHandler = new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							Object obj = event.getSource();
							if(obj instanceof ListBox){
								ListBox listBox = (ListBox) obj;
								attributeData.setLengthFormat(listBox.getValue(listBox.getSelectedIndex()));
							}
						}
					};
					lengthFormatCombobox.addChangeHandler(lengthFormatChangeHandler);
					
					// Padding type event
					ChangeHandler paddingTypeChangeHandler = new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							Object obj = event.getSource();
							if(obj instanceof ListBox){
								ListBox listBox = (ListBox) obj;
								attributeData.setPaddingType(listBox.getValue(listBox.getSelectedIndex()));
							}
						}
						
					};
					paddingTypetComboBox.addChangeHandler(paddingTypeChangeHandler);
					
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

										attributeData.setDataType(dataTypeData);
										attributeData.setDataTypeId(dataTypeData.getDataTypeId());
										break;
									}

									count++;
								}
								
								String dataType= listBox.getValue(listBox.getSelectedIndex());
								if("integer".equals(dataType)){
					 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
									attributeTabPanel.create(attributeItem,"integer",dictionaryMgmtEntryPoint);
									dictionaryMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
									dictionaryMgmtEntryPoint.getVerticalSplitPanel().setBottomWidget(attributeTabPanel);
					 			}else {
					 				AttributeTabPanel attributeTabPanel = new AttributeTabPanel();
					 				attributeTabPanel.setVisible(false);
					 				dictionaryMgmtEntryPoint.setAttrTabPanel(attributeTabPanel);
					 				dictionaryMgmtEntryPoint.getVerticalSplitPanel().setBottomWidget(attributeTabPanel);
					 			}
							}

						}};

						datatypeCombobox.addChangeHandler(datatypeChangeHandler);

		}catch(Exception e){
			Log.trace("InitAttributeFormPanel", e);
		}
	}






}
