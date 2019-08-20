package com.elitecore.aaaclient.ui.session.radius.request;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import com.elitecore.aaaclient.radius.IRadiusRequest;
import com.elitecore.aaaclient.radius.RadiusAuthRequest;
import com.elitecore.aaaclient.ui.app.common.components.EliteButton;
import com.elitecore.aaaclient.ui.app.common.components.EliteLabel;
import com.elitecore.aaaclient.ui.app.common.components.EliteTextField;

public class RadiusAuthRequestFrame extends BaseRadiusRequestFrame {

	private static final long serialVersionUID = 1L;
	
	private static int instanceCounter;
	
	private GeneralPanel generalPanel;
	private AuthTypePanel authTypePanel;
	private AttributesInputPanel attributesInputPanel;
	
	
	public RadiusAuthRequestFrame() {
		super("Radius Auth Request - Case: " + ++instanceCounter, true, true, true, true);
		initComponents();
	}
	
	
	private void initComponents() {

		generalPanel = new GeneralPanel();
		authTypePanel = new AuthTypePanel();
		attributesInputPanel = new AttributesInputPanel();

		generalPanel.setPort(1812);
		
		setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        int gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 10);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = 2;
        
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 10);
		
		add(generalPanel, gridBagConstraints);
		
        gridBagConstraints.gridy = ++gridy;
		add(authTypePanel, gridBagConstraints);
        
		
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridy = ++gridy;
		add(attributesInputPanel, gridBagConstraints);
		
		JPanel verticalButtonPanel = new JPanel(new GridLayout(0,1,5,5));
		EliteLabel title = new EliteLabel("Authentication Attributes");
		title.setFont(new Font(null, Font.BOLD, 12));
		
		EliteButton basicAuthenticationButton = new EliteButton("Basic Authentication");
		EliteButton wimaxAuthenticationButton = new EliteButton("WiMAX Authentication");
		EliteButton wimaxAuthorizationButton = new EliteButton("WiMAX Authorization");
		EliteButton _3GPP2AuthenticationButton = new EliteButton("3GPP2 Authentication");
		EliteButton _3GPP2AuthorizationButton = new EliteButton("3GPP2 Authorization");
		
		basicAuthenticationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleBasicAuthenticationAttributesRequest();
			}
		});
		
		wimaxAuthenticationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		wimaxAuthorizationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		_3GPP2AuthenticationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		_3GPP2AuthorizationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		
		verticalButtonPanel.add(title);
		verticalButtonPanel.add(basicAuthenticationButton);
		verticalButtonPanel.add(wimaxAuthenticationButton);
		verticalButtonPanel.add(wimaxAuthorizationButton);
		verticalButtonPanel.add(_3GPP2AuthenticationButton);
		verticalButtonPanel.add(_3GPP2AuthorizationButton);
		
		
		gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 1, 10);
		add(verticalButtonPanel, gridBagConstraints);
		
		
		
		EliteButton okButton = new EliteButton("    OK    ");
		EliteButton cancelButton = new EliteButton("  Cancel  ");
		
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 10);
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridy = ++gridy;
		gridBagConstraints.gridx = 0;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		add(buttonPanel, gridBagConstraints);

		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RadiusAuthRequestFrame.this.fireRequestDataUpdatedEvent();
				RadiusAuthRequestFrame.this.setVisible(false);
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RadiusAuthRequestFrame.this.setVisible(false);
			}
		});
		
		
	}
	
	
	private void handleBasicAuthenticationAttributesRequest() {
		StringBuilder requestText = new StringBuilder();

		// TODO - need to get the attribute name from dictionary...
		//requestText.append(Dictionary.getInstance().getAttributeName(4) + "=127.0.0.1");
		requestText.append("NAS-IP-Address=127.0.0.1,NAS-Identifier=Elitecore-NAS");
		attributesInputPanel.setRequestAttributesText(requestText.toString());
		attributesInputPanel.setFocusOnTextArea();
	}
	
	
	private class AuthTypePanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private EliteTextField usernameTxtField;
		private EliteTextField passwordTxtField;
		
		public AuthTypePanel() {
			initComponents();
		}
		
		private void initComponents() {
			
			setMinimumSize(getMinimumPanelSize());
			
			setBorder(new TitledBorder("Authentication"));
			
			setLayout(new GridBagLayout());
	        GridBagConstraints gridBagConstraints = new GridBagConstraints();
	        
	        int gridy = 0;

	        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;

	        //Row 1
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = gridy;
	        gridBagConstraints.insets = new java.awt.Insets(1, 5, 1, 10);
	        add(new EliteLabel("Username          "), gridBagConstraints);

	        usernameTxtField = new EliteTextField(25);
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 3;
	        add(usernameTxtField, gridBagConstraints);

	        //Row 1
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        add(new EliteLabel("Password"), gridBagConstraints);

	        passwordTxtField = new EliteTextField(25);
	        gridBagConstraints.gridx = 1;
	        gridBagConstraints.gridwidth = 3;
	        add(passwordTxtField, gridBagConstraints);


	        //Row 2
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        gridBagConstraints.gridwidth = 4;
	        add(new EliteLabel("Authentication Type"), gridBagConstraints);

	        JRadioButton papAuth = new JRadioButton("PAP");
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        gridBagConstraints.gridwidth = 1;
	        add(papAuth, gridBagConstraints);
	        
	        JRadioButton chapAuth = new JRadioButton("CHAP");
	        gridBagConstraints.gridx = 1;
	        add(chapAuth, gridBagConstraints);
	        
	        JRadioButton digestAuth = new JRadioButton("DIGEST");
	        gridBagConstraints.gridx = 2;
	        add(digestAuth, gridBagConstraints);
	        
	        JRadioButton eapAuth = new JRadioButton("EAP");
	        gridBagConstraints.gridx = 3;
	        add(eapAuth, gridBagConstraints);
	        
	        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.gridy = ++gridy;
	        gridBagConstraints.gridwidth = 2;
	        EliteButton chapSettingsButton = new EliteButton("CHAP Settings");
	        chapSettingsButton.setEnabled(false);
	        add(chapSettingsButton, gridBagConstraints);
	        
	        gridBagConstraints.gridx = 2;
	        gridBagConstraints.gridwidth = 1;
	        EliteButton digestSettingsButton = new EliteButton("Digest Settings");
	        digestSettingsButton.setEnabled(false);
	        add(digestSettingsButton, gridBagConstraints);
	        
	        gridBagConstraints.gridx = 3;
	        EliteButton eapSettingsButton = new EliteButton("EAP Settings");
	        eapSettingsButton.setEnabled(false);
	        add(eapSettingsButton, gridBagConstraints);
	        
	        
	        ButtonGroup buttonGroup = new ButtonGroup();
	        buttonGroup.add(papAuth);
	        buttonGroup.add(chapAuth);
	        buttonGroup.add(digestAuth);
	        buttonGroup.add(eapAuth);
	        
	        papAuth.setSelected(true);
        
		}
	}
	

	@Override
	protected String getRequestType() {
		return "Authentication Request";
	}


	@Override
	protected int getRequestTypeId() {
		return 1;
	}

	public RadiusAuthRequest getRadiusAuthRequest(){
		RadiusAuthRequest radiusAuthRequest = new RadiusAuthRequest();
		radiusAuthRequest.setServerAddress(generalPanel.getServerAddress());
		radiusAuthRequest.setRequestPort(generalPanel.getPort());
		radiusAuthRequest.setTestCaseName(generalPanel.getTestCaseName());
		radiusAuthRequest.setRequestTypeId(getRequestTypeId());
		radiusAuthRequest.setRequestTimeout(generalPanel.getRequestTimeout());
		radiusAuthRequest.setRetryCount(generalPanel.getRetryCount());
		radiusAuthRequest.setSharedSecret(generalPanel.getSharedSecret());
		
		
		return radiusAuthRequest;
	}


	@Override
	public IRadiusRequest getRadiusRequest() {
		return getRadiusAuthRequest();
	}
	
}
