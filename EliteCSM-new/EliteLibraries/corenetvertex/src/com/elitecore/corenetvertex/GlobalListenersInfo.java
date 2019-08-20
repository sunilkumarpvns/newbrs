package com.elitecore.corenetvertex;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.Date;

public class GlobalListenersInfo implements ToStringable {
    private String listenerName;
    private String listenerAddress;
    private int listenerPort;
    private Date startDate;
    private String status;
    private String remarks;

    public GlobalListenersInfo(String listenerName, String listenerAddress, int listnerPort, Date startDate, String status, String remarks) {
        this.listenerName = listenerName;
        this.listenerAddress = listenerAddress;
        this.listenerPort = listnerPort;
        this.startDate = startDate;
        this.status = status;
        this.remarks = remarks;
    }

	public String getListenerAddress() {
		return listenerAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public String getListenerName() {
        return listenerName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Global Listeners Information -- ");
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Listener Name", listenerName );
        out.append("Listener Address", listenerAddress);
        out.append("Listener Port", listenerPort);
        out.append("Start Date", startDate);
        out.append("Status ", status);
        out.append("Remarks", remarks);
        out.decrementIndentation();
    }
}
