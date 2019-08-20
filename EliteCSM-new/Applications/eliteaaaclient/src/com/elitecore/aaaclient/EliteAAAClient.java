/*
 * CCApplication.java
 *
 * Created on __DATE__, __TIME__
 */

package com.elitecore.aaaclient;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.elitecore.aaaclient.ui.app.IApplicationContext;
import com.elitecore.aaaclient.ui.session.radius.RadiusSessionScreen;


public class EliteAAAClient extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
    private JMenuBar menuBar;
    private JDesktopPane desktopPane;
    private IApplicationContext applicationContext;
    
	
	public EliteAAAClient() {
        super("EliteAAA Client - Elitecore Technologies Ltd.");
        
    	initComponents();
    	//setIconImage(new ImageIcon(getClass().getResource("/images/logo_img.jpg")).getImage());
    }

    private void initComponents() {
    	
    	desktopPane = new JDesktopPane();
        setContentPane(desktopPane);
        
    	menuBar = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        //Menus
        JMenu sessionMenu = new JMenu("Session");
        menuBar.add(sessionMenu);
        
        
        //Menu Items
        //1.
        JMenuItem newRadiusSession = new JMenuItem("New Radius Session");
        newRadiusSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleNewRadiusSessionRequest();
			}
		});
        
        sessionMenu.add(newRadiusSession);
        
        setJMenuBar(menuBar);


        
        
        
        /* Create new Application Context */
        
        applicationContext = new IApplicationContext() {

			public JFrame getMainWindowRef() {
				return EliteAAAClient.this;
			}

			public void showInternalFrame(JInternalFrame internalFrame) {
				desktopPane.add(internalFrame);
				internalFrame.setVisible(true);
				try {
					internalFrame.setSelected(true);
				} catch (PropertyVetoException e) {
				}
			}
			
		};
        
        setSize(800, 400);
        setMinimumSize(new Dimension(800, 400));
        setVisible(true);
    }

    private void handleNewRadiusSessionRequest() {
    	RadiusSessionScreen radiusSessionScreen = new RadiusSessionScreen(applicationContext);
    	desktopPane.add(radiusSessionScreen);
    	
    	radiusSessionScreen.setBounds(10, 10, this.getWidth() - 30, getHeight() - 100);
    	
    	radiusSessionScreen.setVisible(true);
    	
    }
   
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }
    
    public static void main(String aaa[]) {
    	new EliteAAAClient();
    }
    
}