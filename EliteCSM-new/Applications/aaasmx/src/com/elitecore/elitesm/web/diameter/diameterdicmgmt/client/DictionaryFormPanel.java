package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DictionaryFormPanel extends FormPanel{

	

	public DictionaryFormPanel(DictionaryItem dictionaryItem) {
		
		initDictionaryFormPanel(dictionaryItem);
	}


	public DictionaryFormPanel() {
		
	}


	private void initDictionaryFormPanel(final DictionaryItem dictionaryItem) {
		
		final DictionaryData dictionaryData = dictionaryItem.getDictionaryData();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		HorizontalPanel horizontalPanel= new HorizontalPanel();
		
		// build form
		/*
		 *  private long dictionaryId;
		    private String name;
		    private String description;
		    private String modalNumber;
		    private String editable;
		    private String systemGenerated;
		    private long dictionaryNumber;
		    private String commonStatusId;
		    private Timestamp lastModifiedDate;
		    private Long lastModifiedByStaffId;
		    private long vendorId;
		    private Timestamp createDate;
		    private Long createdByStaffId;
		    private Timestamp statusChangedDate;

		 */
		
		// name
		Label nameLable = new Label("Dictionary Name");
		nameLable.getAbsoluteLeft();

		TextBox nameTextBox=new TextBox();
		nameTextBox.setName("nameTextBox");
		nameTextBox.setValue(dictionaryData.getApplicationName());
		
		// description
       
		Label descriptionLable = new Label("Description");
		descriptionLable.getAbsoluteLeft();

		TextBox descriptionTextBox=new TextBox();
		descriptionTextBox.setName("descriptionTextBox");
		descriptionTextBox.setValue(dictionaryData.getDescription());
		
		
		CaptionPanel captionalPanel = new CaptionPanel("Dictionary Information");
		captionalPanel.setWidth("530px");
		//fill form 
		
		Grid grid = new Grid(2,2);
		
		grid.setWidget(0,0,nameLable);
		grid.setWidget(0,1,nameTextBox);
		
		grid.setWidget(1,0, descriptionLable);
		grid.setWidget(1,1, descriptionTextBox);
		
		
		
		
		grid.ensureDebugId("cwGrid");
		captionalPanel.setContentWidget(grid);
        verticalPanel.add(captionalPanel);			
		this.add(verticalPanel);

		/*
		 * Dictionary Name event	
		 */
		
		
				
		KeyPressHandler nameKeyPressHandler = new KeyPressHandler(){

			public void onKeyPress(KeyPressEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setApplicationName(box.getValue());
					dictionaryItem.setName(box.getValue());
				}
				
			}
			
		};
		nameTextBox.addKeyPressHandler(nameKeyPressHandler);
	    KeyDownHandler nameKeyDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setApplicationName(box.getValue());
					dictionaryItem.setName(box.getValue());
				}
				
				
			}
	    	
	    };
		nameTextBox.addKeyDownHandler(nameKeyDownHandler);
   
		KeyUpHandler nameKeyUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setApplicationName(box.getValue());
					dictionaryItem.setName(box.getValue());
				}
				
			}
			
		};
		nameTextBox.addKeyUpHandler(nameKeyUpHandler);
		

		
		/*
		 * Description event
		 */
		
		KeyPressHandler descriptionKeyPressHandler = new KeyPressHandler(){

			public void onKeyPress(KeyPressEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setDescription(box.getValue());
					dictionaryData.setDescription(box.getValue());
				}
				
			}
			
		};
		descriptionTextBox.addKeyPressHandler(descriptionKeyPressHandler);
	    KeyDownHandler descriptionKeyDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setDescription(box.getValue());
					dictionaryData.setDescription(box.getValue());
				}
				
				
			}
	    	
	    };
	    descriptionTextBox.addKeyDownHandler(descriptionKeyDownHandler);
   
		KeyUpHandler descriptionKeyUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				Object obj = event.getSource();
				if(obj instanceof TextBox){
					TextBox box = (TextBox)obj;
					dictionaryData.setDescription(box.getValue());
					dictionaryData.setDescription(box.getValue());
				}
				
			}
			
		};
		descriptionTextBox.addKeyUpHandler(descriptionKeyUpHandler);
		
		
	}
	
}
