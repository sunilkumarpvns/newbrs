/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitSearchASMForm.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.sessionmanager.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class InitSearchASMForm extends BaseWebForm{

    private long concUserId;
    private String user_Name;
    private String nas_Ip_Address;
    private String framed_Ip_Address;
    private String acct_Session_Id;
    private String nas_Port_Type;
    private String param_Str0;
    private String param_Str1;
    private String param_Str2;
    private String param_Str3;
    private String param_Str4;
    private String param_Str5;
    private String group_Name;
    private String user_Identity;
    private java.sql.Timestamp last_Updated_Time;



    public long getConcUserId(){
        return concUserId;
    }

	public void setConcUserId(long concUserId) {
		this.concUserId = concUserId;
	}


    public String getUser_Name(){
        return user_Name;
    }

	public void setUser_Name(String user_Name) {
		this.user_Name = user_Name;
	}


    public String getNas_Ip_Address(){
        return nas_Ip_Address;
    }

	public void setNas_Ip_Address(String nas_Ip_Address) {
		this.nas_Ip_Address = nas_Ip_Address;
	}


    public String getFramed_Ip_Address(){
        return framed_Ip_Address;
    }

	public void setFramed_Ip_Address(String framed_Ip_Address) {
		this.framed_Ip_Address = framed_Ip_Address;
	}


    public String getAcct_Session_Id(){
        return acct_Session_Id;
    }

	public void setAcct_Session_Id(String acct_Session_Id) {
		this.acct_Session_Id = acct_Session_Id;
	}


    public String getNas_Port_Type(){
        return nas_Port_Type;
    }

	public void setNas_Port_Type(String nas_Port_Type) {
		this.nas_Port_Type = nas_Port_Type;
	}


    public String getParam_Str0(){
        return param_Str0;
    }

	public void setParam_Str0(String param_Str0) {
		this.param_Str0 = param_Str0;
	}


    public String getParam_Str1(){
        return param_Str1;
    }

	public void setParam_Str1(String param_Str1) {
		this.param_Str1 = param_Str1;
	}


    public String getParam_Str2(){
        return param_Str2;
    }

	public void setParam_Str2(String param_Str2) {
		this.param_Str2 = param_Str2;
	}


    public String getParam_Str3(){
        return param_Str3;
    }

	public void setParam_Str3(String param_Str3) {
		this.param_Str3 = param_Str3;
	}


    public String getParam_Str4(){
        return param_Str4;
    }

	public void setParam_Str4(String param_Str4) {
		this.param_Str4 = param_Str4;
	}


    public String getParam_Str5(){
        return param_Str5;
    }

	public void setParam_Str5(String param_Str5) {
		this.param_Str5 = param_Str5;
	}


    public String getGroup_Name(){
        return group_Name;
    }

	public void setGroup_Name(String group_Name) {
		this.group_Name = group_Name;
	}


    public String getUser_Identity(){
        return user_Identity;
    }

	public void setUser_Identity(String user_Identity) {
		this.user_Identity = user_Identity;
	}


    public java.sql.Timestamp getLast_Updated_Time(){
        return last_Updated_Time;
    }

	public void setLast_Updated_Time(java.sql.Timestamp last_Updated_Time) {
		this.last_Updated_Time = last_Updated_Time;
	}


}
