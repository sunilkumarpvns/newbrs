package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * Contains this code for default value of <b>Password Decrypt</b> of <b>LDAP Auth Driver</b>
 * while request came by REST API
 * @author Tejas.P.Shah
 *
 */
public class PasswordDecryptAdapter extends XmlAdapter<String, Long>
{
	@Override
	public Long unmarshal(String passwordDecrypt) throws Exception {
		if(passwordDecrypt.equalsIgnoreCase("0")){
			return 0L;
		}else{
			return -1L;
		}
	}

	@Override
	public String marshal(Long value) throws Exception {
		return String.valueOf(value);
	}
}