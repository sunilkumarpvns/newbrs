package com.elitecore.elitesm.web.expressionbuilder.client.ui;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.expressionbuilder.client.core.ListUtility;
import com.elitecore.elitesm.web.expressionbuilder.client.core.StateMachine;
import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

public class AutoCompleteTextBox extends TextArea implements ChangeHandler {

	protected EliteSuggestionPanel choicesPopup;
	protected ListBox choices;
	protected ICompletionItems items ; 

	protected boolean popupAdded = false;
	protected boolean visible = false;
	protected String typedText=" ";
	String splitedStr="";

	/**
	 * Default Constructor
	 *
	 */
	public AutoCompleteTextBox(List<AttributeData> attributeList)
	{
		
		
		super();
		//pass attribute suggestion list to SimpleAutoCompletionItems constructor as argument
		items= new AutoCompletionItems(attributeList);
		this.setName("gwttextbox");
		this.setStyleName("AutoCompleteTextBox");
		choicesPopup = new EliteSuggestionPanel(this);
		choices = choicesPopup.getChoices();
		
		createHandler();

	}



	/**
	 * Sets an "algorithm" returning completion items
	 * You can define your own way how the textbox retrieves autocompletion items
	 * by implementing the CompletionItems interface and setting the according object
	 * @see SimpleAutoCompletionItem
	 * @param items CompletionItem implementation
	 */
	public void setCompletionItems(ICompletionItems items)
	{
		this.items = items;
	}

	/**
	 * Returns the used CompletionItems object
	 * @return CompletionItems implementation
	 */
	public ICompletionItems getCompletionItems()
	{
		return this.items;
	}



	// add selected item to textbox
	public void complete()
	{
		try{
			if(choices.getItemCount() > 0)
			{   
				String text = this.getText();

				int currPos=this.getCursorPos();
				String preStr="";
				String lastStr="";
				int additionalCaretCnt = 0;
				int subtractCarentCnt =0;
				String currentSelection=choices.getValue(choices.getSelectedIndex());
				if(text.length()>0){
					if(currPos!=0){
						preStr=text.substring(0,currPos);
					}
					if(currPos!=text.length()){
						lastStr=text.substring(currPos,text.length());
					}
					String finalStr="";

					Log.debug("Current Expression is:"+StateMachine.currentExpr.toString());

					String txt=StateMachine.currentExpr.toString();

					if(currentSelection.toLowerCase().indexOf(txt.toLowerCase()) != -1 && txt.length() > 0){

						//String extraStr = choices.getValue(choices.getSelectedIndex()).substring(choices.getValue(choices.getSelectedIndex()).indexOf(new String(StateMachine.currentExpr))+StateMachine.currentExpr.length(), (choices.getValue(choices.getSelectedIndex()).length()));
						String extraStr = currentSelection;
						//if(StateMachine.getCurrentState(splitedStr).equalsIgnoreCase("s2")){
						if(StateMachine.currentState.equalsIgnoreCase("s2")){
							extraStr="\""+extraStr+"\"";
							additionalCaretCnt+=2;
						}
						int index = preStr.lastIndexOf(txt);

						if(index!=-1){
							finalStr = preStr.substring(0, index)+ extraStr +lastStr;
							subtractCarentCnt =preStr.length()-index;
						}else{
							finalStr = preStr + extraStr +lastStr;
						}

					}else{

						String extraStr=currentSelection;
						if(StateMachine.currentState.equalsIgnoreCase("s2")){
							extraStr="\""+currentSelection+"\"";
							additionalCaretCnt+=2;
						}
						finalStr=preStr+extraStr+lastStr;
					}

					this.setText(finalStr+" ");  
					this.setFocus(true);

				}else{

					text += currentSelection;
					this.setText(text+" ");
					this.setFocus(true);

				}

				if(currPos != 0){
					this.setCursorPos(currPos+currentSelection.length()+additionalCaretCnt-subtractCarentCnt);
				}

			}


		}catch(Exception exp){
			Log.debug("Error in Complete Function",exp.getCause());
			Log.error("Error:", exp);

		}

		choices.clear();
		choicesPopup.hide();			


	}

	public void popupChoices(){
		String text = this.getText();
        Map<String,String> matches= new HashMap<String,String>();
		int curPos = this.getCursorPos();
		String prefix = text.substring(0, curPos);
		splitedStr=splitString(prefix,ListUtility.getLogicalOperatorList());
		if(text.length() >= 0)
		{
			matches = items.getCompletionItems(splitedStr	);
		}


		if(matches.size() > 0)
		{
			choices.clear();
             
			
			Collection<String> matchesKeys= matches.keySet();
			String[] match = new String[matchesKeys.size()];
			match=matchesKeys.toArray(match);
			
			for(int i = 0; i < match.length; i++)
			{
				choices.addItem(matches.get(match[i]),match[i]);
			}
			choicesPopup.setChoices(choices);
			// if there is only one match and it is what is in the
			// text field anyways there is no need to show autocompletion
			if(match.length == 1 && match[0].compareTo(text) == 0)
			{
				choicesPopup.hide();
			} else {
				choices.setSelectedIndex(0);
				choices.setVisibleItemCount(match.length + 1);

				if(!popupAdded)
				{
					RootPanel.get().add(choicesPopup);
					popupAdded = true;
				}
				choicesPopup.show();
				//visible = true;
				choicesPopup.setVisible(true);
				choicesPopup.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() + this.getOffsetHeight());
				//choices.setFocus(true);

			}

		} else {
			choicesPopup.hide();
			choicesPopup.setVisible(false);

		}

	}
	

	public void onChange(ChangeEvent event) {
		complete();		
	}




	/*
	 *  Get Multi Character Multi Delimiter 
	 */

	public static final String splitString(String stringToSplit,List<String> delimiter) {
		String token = stringToSplit;
		for(int i=0;i<delimiter.size();i++){
			token = stringToSplit(token,delimiter.get(i));
		}
		return token;

	}

	public static final String stringToSplit(String stringToSplit,String delimiter){
		String[] aRet;
		int iLast;
		int iFrom;
		int iFound;
		int iRecords;

		// return Blank Array if stringToSplit == "")
		if (stringToSplit.equals("")) {
			return new String();
		}

		// count Field Entries
		iFrom = 0;
		iRecords = 0;
		while (true) {
			iFound = stringToSplit.indexOf(delimiter, iFrom);
			if (iFound == -1) {
				break;
			}
			iRecords++;
			iFrom = iFound + delimiter.length();
		}
		iRecords = iRecords + 1;

		// populate aRet[]
		                 aRet = new String[iRecords];
		                 if (iRecords == 1) {
		                	 aRet[0] = stringToSplit;
		                 } else {
		                	 iLast = 0;
		                	 iFrom = 0;
		                	 iFound = 0;
		                	 for (int i = 0; i < iRecords; i++) {
		                		 iFound = stringToSplit.indexOf(delimiter, iFrom);
		                		 if (iFound == -1) { // at End
		                			 aRet[i] = stringToSplit.substring(iLast + delimiter.length(), stringToSplit.length());
		                		 } else if (iFound == 0) { // at Beginning
		                			 aRet[i] = "";
		                		 } else { // somewhere in middle
		                			 aRet[i] = stringToSplit.substring(iFrom, iFound);
		                		 }
		                		 iLast = iFound;
		                		 iFrom = iFound + delimiter.length();
		                	 }
		                 }
		                 return aRet[aRet.length-1];
	}

	/*
	 * Handlers
	 */

	private void createHandler() {

		/*
		 * Key press Handler
		 */

		KeyPressHandler keyPressHandler = new KeyPressHandler(){

			public void onKeyPress(KeyPressEvent event) {
				if(event.getUnicodeCharCode() == 32 && event.isAnyModifierKeyDown()){
					choices.fireEvent(event);
					event.preventDefault();
				}
			}	    	
		};

		KeyPressHandler choicesKeyPressHandler = new KeyPressHandler(){

			public void onKeyPress(KeyPressEvent event) {
				if(event.getUnicodeCharCode() == 32 && event.isAnyModifierKeyDown()){
					popupChoices();
				}
			}	    	
		};




		/*
		 * Key Down Handler
		 */


		KeyDownHandler autoCompleteDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if(event.isUpArrow() || event.isDownArrow()){					
					choices.setFocus(true);
					choices.fireEvent(event);
					choicesPopup.fireEvent(event);
					event.preventDefault();
				}else if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					event.preventDefault();
					popupChoices();
					choices.setItemSelected(0,true);
					complete();
				}else{
					choicesPopup.hide();
				}

			}

		};



		KeyDownHandler choicesDownHandler = new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if(!(event.isUpArrow() || event.isDownArrow())){					
					AutoCompleteTextBox.this.setFocus(true);
					event.preventDefault();
				}
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					complete();
				}				
			}

		};




		/*
		 *  Key Up Handler
		 */

		KeyUpHandler autoCompleteUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				if(event.isUpArrow() || event.isDownArrow()){
					choices.setFocus(true);
					choices.fireEvent(event);
					choicesPopup.fireEvent(event);
					event.preventDefault();

				}else if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE){
					choicesPopup.hide();
				}		

			}

		};


		KeyUpHandler choicesUpHandler = new KeyUpHandler(){

			public void onKeyUp(KeyUpEvent event) {
				if(!(event.isUpArrow() || event.isDownArrow())){
					AutoCompleteTextBox.this.setFocus(true);
					event.preventDefault();
				}else if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					complete();
				}
			}

		};


		choices.addKeyDownHandler(choicesDownHandler);
		choices.addKeyUpHandler(choicesUpHandler);
		choices.addKeyPressHandler(choicesKeyPressHandler);
		this.addKeyPressHandler(keyPressHandler);
		this.addKeyDownHandler(autoCompleteDownHandler);
		this.addKeyUpHandler(autoCompleteUpHandler);

	}



	public EliteSuggestionPanel getChoicesPopup() {
		return choicesPopup;
	}



	public void setChoicesPopup(EliteSuggestionPanel choicesPopup) {
		this.choicesPopup = choicesPopup;
	}
  
  


}
