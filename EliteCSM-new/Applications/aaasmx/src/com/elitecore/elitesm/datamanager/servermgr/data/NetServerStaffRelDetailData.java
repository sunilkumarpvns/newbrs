package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * 
 * @author Tejas.P.Shah
 *
 */
public class NetServerStaffRelDetailData extends BaseData {
    private String id;
    private String name;
    private String staffUser;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(String staffUser) {
		this.staffUser = staffUser;
	}

	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetServiceStaffRelInstanceData-----------------");
        writer.println("id="+id);
        writer.println("name="+name);
        writer.println("Staff User="+staffUser);
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }   

}
