package com.elitecore.elitesm.web.servermgr.server.forms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateServerForm extends BaseWebForm {
    
    private static final long serialVersionUID = -4177021900646531226L;
    private String name;
    private String netServerType;
    private String description;
    private String adminInterfaceIP;
    private int    adminInterfacePort;
    private String action;
    private String status;
    private String javaHome;
    private String serverHome;
    private String isInSync;
    private List<INetServiceTypeData> lstServiceType = null;
    private String selectedServices[];
    private List<StaffData> staffDataList;
	private String staff;
	private Long instanceStaffRelId;
	
	public String getServerHome() {
        return serverHome;
    }
    
    public void setServerHome( String serverHome ) {
        this.serverHome = serverHome;
    }
    
    public String getJavaHome() {
        return javaHome;
    }
    
    public void setJavaHome( String javaHome ) {
        this.javaHome = javaHome;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public String getAdminInterfaceIP() {
        return adminInterfaceIP;
    }
    
    public void setAdminInterfaceIP( String adminInterfaceIP ) {
        this.adminInterfaceIP = adminInterfaceIP;
    }
    
    public int getAdminInterfacePort() {
        return adminInterfacePort;
    }
    
    public void setAdminInterfacePort( int adminInterfacePort ) {
        this.adminInterfacePort = adminInterfacePort;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription( String description ) {
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getNetServerType() {
        return netServerType;
    }
    
    public void setNetServerType( String netServerType ) {
        this.netServerType = netServerType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }
  
    public String getIsInSync( ) {
        return isInSync;
    }
  
    public void setIsInSync( String isInSync ) {
        this.isInSync = isInSync;
    }

	public List<INetServiceTypeData> getLstServiceType() {
		return lstServiceType;
	}

	public void setLstServiceType(List<INetServiceTypeData> lstServiceType) {
		this.lstServiceType = lstServiceType;
	}
    public String[] getSelectedServices() {
		return selectedServices;
	}

	public void setSelectedServices(String[] selectedServices) {
		this.selectedServices = selectedServices;
	}
	
    public List<StaffData> getStaffDataList() {
		return staffDataList;
	}

	public void setStaffDataList(List<StaffData> staffDataList) {
		this.staffDataList = staffDataList;
	}
	
	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public Long getInstanceStaffRelId() {
		return instanceStaffRelId;
	}

	public void setInstanceStaffRelId(Long instanceStaffRelId) {
		this.instanceStaffRelId = instanceStaffRelId;
	}

	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------CreateServerForm-----------------");
        writer.println("name              =" +name);                                     
        writer.println("netServerType     =" +netServerType);                                     
        writer.println("description       =" +description);                                     
        writer.println("adminInterfaceIP  =" +adminInterfaceIP);                                     
        writer.println("adminInterfacePort=" +adminInterfacePort);
        writer.println("action            =" +action);
        writer.println("status            =" +status);
        writer.println("javaHome          =" +javaHome);
        writer.println("serverHome        =" +serverHome);
        writer.println("isInSync          =" +isInSync);
        writer.println("staffDataList          =" +staffDataList);
        writer.println("userName          =" +staff);
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();

    }

    
}
