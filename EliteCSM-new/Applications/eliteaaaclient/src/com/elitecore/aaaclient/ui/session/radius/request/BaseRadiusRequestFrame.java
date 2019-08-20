package com.elitecore.aaaclient.ui.session.radius.request;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.elitecore.aaaclient.radius.IRadiusRequest;
import com.elitecore.aaaclient.ui.app.common.components.EliteLabel;
import com.elitecore.aaaclient.ui.app.common.components.EliteTextField;
import com.elitecore.aaaclient.ui.app.common.components.SimpleLabel;
import com.elitecore.aaaclient.ui.session.radius.request.event.IRequestDataUpdateListener;
import com.elitecore.aaaclient.ui.session.radius.request.event.RequestDataUpdateEvent;

public abstract class BaseRadiusRequestFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private List<IRequestDataUpdateListener> listenerList;
	
	private int requestSerialNo;
	
	public BaseRadiusRequestFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable){
		super(title, resizable, closable, maximizable, iconifiable );
		listenerList = new ArrayList<IRequestDataUpdateListener>();
	}

	protected abstract String getRequestType();
	
	protected abstract int getRequestTypeId();

	public abstract IRadiusRequest getRadiusRequest();
	
	public int getRequestSerialNo() {
		return requestSerialNo;
	}

	public void setRequestSerialNo(int requestSerialNo) {
		this.requestSerialNo = requestSerialNo;
	}

	protected Dimension getMinimumPanelSize() {
		return new Dimension(400,120);
	}
	
	public final void addRequestDataUpdateListener(IRequestDataUpdateListener dataUpdateListener) {
		if (dataUpdateListener != null){
			if (!listenerList.contains(dataUpdateListener)) {
				listenerList.add(dataUpdateListener);
			}
		}
	}
	
	protected final void fireRequestDataUpdatedEvent() {
		
		Iterator<IRequestDataUpdateListener> iterator = listenerList.iterator();
		RequestDataUpdateEvent event = new RequestDataUpdateEvent(this);
		
		while(iterator.hasNext()) {
			iterator.next().requestDataUpdated(event);
		}
		
	}
	
	protected class GeneralPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private EliteTextField nameTxtField;
		private EliteTextField radiusServerTxtField;
		private EliteTextField radiusPortTxtField;
		private EliteTextField replyTimeoutTxtField;
		private EliteTextField retryTxtField;
		private EliteTextField sharedSecretTxtField;
		
		public GeneralPanel() {
			initComponents();
		}
		
		private void initComponents() {
			
			setMinimumSize(getMinimumPanelSize());
			
			setBorder(new TitledBorder("General"));
			
			setLayout(new GridBagLayout());
	        GridBagConstraints gridBagConstraints = new GridBagConstraints();
	        
	        int gridy = 0;

	        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;

	        //Row 4
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = gridy;
	        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 10);
	        add(new EliteLabel("Request Type"), gridBagConstraints);
	        
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 3;
	        add(new EliteLabel("Authentication Request (1)"), gridBagConstraints);
	        
	        
	        //Row 1
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        add(new EliteLabel("Test Case Name"), gridBagConstraints);

	        nameTxtField = new EliteTextField(getTitle(), 25);
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 3;
	        add(nameTxtField, gridBagConstraints);

	        
	        //Row 2
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        gridBagConstraints.gridwidth = 1;
	        add(new EliteLabel("Radius Server/port"), gridBagConstraints);

	        
	        radiusServerTxtField = new EliteTextField("127.0.0.1",18);
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 2;
	        add(radiusServerTxtField, gridBagConstraints);
	        
	        radiusPortTxtField = new EliteTextField(5);
	        gridBagConstraints.gridx = 3;
	        gridBagConstraints.gridwidth = 1;
	        add(radiusPortTxtField, gridBagConstraints);
	        
	        //Row 3
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        add(new EliteLabel("Reply timeout (ms)"), gridBagConstraints);

	        
	        replyTimeoutTxtField = new EliteTextField("3000",10);
	        gridBagConstraints.gridx = 1;
	        add(replyTimeoutTxtField, gridBagConstraints);
	        
	        gridBagConstraints.gridx = 2;
	        add(new SimpleLabel("Retries"), gridBagConstraints);

	        retryTxtField = new EliteTextField("0",3);
	        gridBagConstraints.gridx = 3;
	        add(retryTxtField, gridBagConstraints);
	        
	        
	        //Row 5
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        add(new EliteLabel("Shared Secret"), gridBagConstraints);

	        sharedSecretTxtField = new EliteTextField("elitekey", 18);
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 3;
	        add(sharedSecretTxtField, gridBagConstraints);

	        
		}
		
		public void setTestCaseName(String caseName) {
			nameTxtField.setText(caseName);
		}
		
		public String getTestCaseName() {
			return nameTxtField.getText();
		}
		
		public void setServerAddress(String serverAddress) {
			radiusServerTxtField.setText(serverAddress);
		}
		
		public String getServerAddress() {
			return radiusServerTxtField.getText();
		}
		
		public void setPort(int port) {
			radiusPortTxtField.setText(String.valueOf(port));
		}
		
		public int getPort(){
			int port = 0;
			try {
				port = Integer.parseInt(radiusPortTxtField.getText().trim());
			}catch(Exception e) {}

			return port;
		}
		
		public void setRequestTimeout(int timeout) {
			replyTimeoutTxtField.setText(String.valueOf(timeout));
		}
		
		public int getRequestTimeout() {
			int timeout = 3000;
			try {
				timeout = Integer.parseInt(replyTimeoutTxtField.getText().trim());
			}catch(Exception e) {}
			
			return timeout;
		}

		public void setRetryCount(int retryCount) {
			retryTxtField.setText(String.valueOf(retryCount));
		}
		
		public int getRetryCount() {
			int retryCount = 0;
			try {
				retryCount = Integer.parseInt(retryTxtField.getText().trim());
			}catch(Exception e) {}
			
			return retryCount;
		}
		
		public void setSharedSecret(String sharedSecret){
			sharedSecretTxtField.setText(sharedSecret);
		}
		
		public String getSharedSecret(){
			return sharedSecretTxtField.getText();
		}
		
	}

	protected class AttributesInputPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;

		private JTextArea attributesTextArea;
		
		public AttributesInputPanel() {
			initComponents();
		}
		
		private void initComponents() {
			
			setLayout(new BorderLayout());
			setMinimumSize(getMinimumPanelSize());
			setBorder(new TitledBorder("Request Attributes"));

			attributesTextArea = new JTextArea(10, 30);
			
			JScrollPane attributesScrollPane = new JScrollPane(attributesTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			add(attributesScrollPane, BorderLayout.CENTER);
			
		}
		
		public void setFocusOnTextArea() {
			if (attributesTextArea != null)
				attributesTextArea.requestFocus();
			
		}
		public String getReqeustAttributesText() {
			return attributesTextArea.getText();
		}
		
		public void setRequestAttributesText(String attributesText) {
			attributesTextArea.setText(attributesText);
		}
	}
	
}
