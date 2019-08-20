package com.elitecore.aaaclient.ui.session.radius;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.elitecore.aaaclient.radius.IRadiusRequest;
import com.elitecore.aaaclient.ui.app.IApplicationContext;
import com.elitecore.aaaclient.ui.app.common.components.EliteButton;
import com.elitecore.aaaclient.ui.base.BaseScreenPanel;
import com.elitecore.aaaclient.ui.session.radius.request.RadiusAuthRequestFrame;
import com.elitecore.aaaclient.ui.session.radius.request.event.IRequestDataUpdateListener;
import com.elitecore.aaaclient.ui.session.radius.request.event.RequestDataUpdateEvent;

public class RadiusSessionBuilderPanel extends BaseScreenPanel {

	private static final long serialVersionUID = 1L;
	
	private JTable sessionRequestTable;
	private SessionRequestTableModel sessionRequestTableModel;
	
	public RadiusSessionBuilderPanel(IApplicationContext applicationContext) {
		super(applicationContext);
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		sessionRequestTableModel = new SessionRequestTableModel();
		sessionRequestTable = new JTable(sessionRequestTableModel);
		
		JPanel buttonPanel = new JPanel();
		EliteButton addNewAuthRequestButton = new EliteButton("New Authentication Request");
		addNewAuthRequestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleAddNewAuthRequest();
			}
		});
		
		EliteButton addNewAcctRequestButton = new EliteButton("New Accounting Request");
		addNewAcctRequestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleAddNewAuthRequest();
			}
		});
		
		EliteButton addNewCustomRequestButton = new EliteButton("New Custom Request");
		addNewCustomRequestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleAddNewAuthRequest();
			}
		});
		
		buttonPanel.add(addNewAuthRequestButton);
		buttonPanel.add(addNewAcctRequestButton);
		buttonPanel.add(addNewCustomRequestButton);
		
		add(buttonPanel, BorderLayout.NORTH);
		
		JScrollPane requestTableScrollPane = new JScrollPane(sessionRequestTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(requestTableScrollPane, BorderLayout.CENTER);
		
		
	}
	
	private void handleAddNewAuthRequest() {
		
		final RadiusAuthRequestFrame radiusRequestPanel = new RadiusAuthRequestFrame();
		Rectangle rect = getApplicationContext().getMainWindowRef().getBounds();
		radiusRequestPanel.setBounds(rect.x + 30, rect.y + 40, 550, 600);
		radiusRequestPanel.setMinimumSize(new Dimension(550, 600));
		
		radiusRequestPanel.addRequestDataUpdateListener(new IRequestDataUpdateListener() {
			
			public synchronized void requestDataUpdated(RequestDataUpdateEvent event) {
				
				if (radiusRequestPanel.getRequestSerialNo() == 0){
					sessionRequestTableModel.addNewRow(radiusRequestPanel.getRadiusRequest());
					radiusRequestPanel.setRequestSerialNo(sessionRequestTable.getRowCount());
				}
			}

		});
		
		getApplicationContext().showInternalFrame(radiusRequestPanel);
		
	}
	
	private static class SessionRequestTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private List<IRadiusRequest> records;
		
		private static final String[] columnNames = {"Sr. No.", "Request Name", "Request Type", "Radius Server/Port", "Execution Status", "Result"};
		
		public SessionRequestTableModel() {
			records = new ArrayList<IRadiusRequest>();
		}
		  
		public int getColumnCount() {
			return columnNames.length;
		}

		public String getColumnName(int index) {
			if (index >=0 && index < columnNames.length) {
				return columnNames[index];
			}
			return "";
		}
		
		public synchronized int getRowCount() {
			return records.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {

			if (rowIndex >= records.size() )
				return "";
				
			String returnValue = "";
			if (columnIndex == 0) {
				returnValue = String.valueOf(rowIndex  + 1);
			} else if (columnIndex == 1) {
				returnValue = records.get(rowIndex).getTestCaseName();
			} else if (columnIndex == 2) {
				returnValue = records.get(rowIndex).getRequestTypeString();
			} else if (columnIndex == 3) {
				returnValue = records.get(rowIndex).getServerAddress() + "/" + records.get(rowIndex).getRequestPort();
			} else if (columnIndex == 4) {
				returnValue = records.get(rowIndex).getExecutionStatusText();
			} else if (columnIndex == 5) {
				returnValue = records.get(rowIndex).getFinalResultText();
			}
			return returnValue;
		}
		
		public synchronized void addNewRow(IRadiusRequest radiusRequest) {
			records.add(radiusRequest);
			fireTableRowsInserted(records.size(), records.size());
		}
	}
	
}
