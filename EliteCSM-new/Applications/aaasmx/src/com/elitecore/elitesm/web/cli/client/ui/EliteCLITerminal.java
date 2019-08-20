package com.elitecore.elitesm.web.cli.client.ui;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.cli.client.CLIServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EliteCLITerminal extends VerticalPanel {
    
	 
	//private VerticalPanel vp;
	private TextArea cmdLineArea;
	
	private KeyPressHandler keyPressHandler ;
	private KeyDownHandler keyDownHandler;
	private KeyUpHandler keyUpHandler;
	
	
	public EliteCLITerminal(CLIServiceAsync cliService) {
		
		Log.debug("====================ELiteCLITerminal Start========================");
		//vp=new VerticalPanel();
		cmdLineArea=new TextArea();
		createHandler(cliService);
		
		cmdLineArea.setHeight("100%");
		cmdLineArea.setWidth("100%");
		cmdLineArea.setText("ERS >");
		cmdLineArea.getElement().setAttribute("wrap", "off");
		cmdLineArea.setStyleName("blackterminal");
		cmdLineArea.setFocus(true);
		cmdLineArea.getElement().setScrollTop(Integer.MAX_VALUE);
		this.add(cmdLineArea);
		this.setVisible(true);
		this.setWidth("100%");
		this.setHeight(Window.getClientHeight() + "px");
		RootPanel.get("cliTerminal").add(this);
		
		Log.debug("====================ELiteCLITerminal End========================");
	}
	
	
	
	
	
	
	private void createHandler(final CLIServiceAsync cliService) {
		
		Log.debug("====================createHandler Start========================");
		
		
		Log.debug("====================keyPressHandler Start========================");
		keyPressHandler = new KeyPressHandler(){
			public void onKeyPress(final KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){

					final TextArea box=(TextArea)event.getSource();
					final String consoleValue=box.getValue();

					String str = box.getValue();
					int pos = str.lastIndexOf('>');					 

					String command = str.substring(pos+1);
					
					/*
					 *  mbean call get response
					 */
					
					AsyncCallback<String> callBackHandler= new AsyncCallback<String>(){

						public void onFailure(Throwable caught) {
                                  
							Log.error("Error in Responce Back:"+caught.getCause());
							
						}

						public void onSuccess(String result) {
							String responce=result+"\n";
							String concol="ERS >";
							String finalStr=consoleValue+"\n"+responce+concol;
							
							box.setText(finalStr);
							box.setFocus(true);
							box.getElement().setScrollTop(Integer.MAX_VALUE);
							event.preventDefault();
							
						}
						
					};
					
					cliService.getResponce(command,callBackHandler);
					
					

				}
			}

		};
		
		
		Log.debug("====================keyPressHandler End========================");
		
		
		Log.debug("====================keyDownHandler Start========================");
		
		
		
		keyDownHandler = new KeyDownHandler(){
			
			public void onKeyDown(KeyDownEvent event) {
				
				int kepresscode=event.getNativeEvent().getKeyCode();
				TextArea box=(TextArea)event.getSource();
				int currerntCurrsorPos=box.getCursorPos();
				GWT.log("kepresscode is"+kepresscode);
				String str = box.getValue();
				int pos = str.lastIndexOf('>');
				GWT.log("pos is:"+pos);
				if(currerntCurrsorPos<(pos+1)){
					   event.preventDefault();
					   return;
				}
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE  || event.isLeftArrow()){
					
					if(currerntCurrsorPos<=(pos+1))
					   event.preventDefault();
				}else if(event.isDownArrow() ||event.isUpArrow()){
					event.preventDefault();
				}
				
			}
			
		};
		
		Log.debug("====================keyDownHandler End========================");
		
		Log.debug("====================keyUpHandler Start========================");
		
		keyUpHandler = new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {

				if(event.isDownArrow() ||event.isUpArrow()){
					event.preventDefault();
				}

			}
		};
		Log.debug("====================keyUpHandler End========================");
		
		cmdLineArea.addKeyDownHandler(keyDownHandler);
		cmdLineArea.addKeyPressHandler(keyPressHandler);
		cmdLineArea.addKeyUpHandler(keyUpHandler);
		Log.debug("====================createHandler End========================");
		
		/*
		 * Resize window
		 */
		
		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
				EliteCLITerminal.this.setHeight(height + "px");
			}
		});
		
	}
	
	
	
	
	
	
	
}
