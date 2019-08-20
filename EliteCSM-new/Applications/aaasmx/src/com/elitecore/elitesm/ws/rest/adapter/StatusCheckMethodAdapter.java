package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;

/**
 * Parse status check method like ICMP_REQUEST,RADIUS_PACKET,
 * and PACKET_BYTES to Long id. if value does not match from above status check-
 * methods we will return -1L and give validation message to user.
 * if status check method is null than we will set default ICMP_REQUEST.
 * @author chirag.i.prajapati
 *
 */
public class StatusCheckMethodAdapter extends XmlAdapter<String, Long>{

	@Override
	public Long unmarshal(String statusCheckMethod){
		Long statusCheckMethodId;
		try {
			if(Strings.isNullOrBlank(statusCheckMethod)){
				return 1L;
			}
			StatusCheckMethod checkMethod = StatusCheckMethod.valueOf(statusCheckMethod);
			if (checkMethod == null){
				return -1L;
			}
			statusCheckMethodId = (long)checkMethod.id;
		} catch(Exception e){
			return -1L;
		}
		return statusCheckMethodId;
	}

	@Override
	public String marshal(Long statusCheckMethodId) throws Exception {
		StatusCheckMethod checkMethod = StatusCheckMethod.fromStatusCheckMethods(statusCheckMethodId.intValue());
		return checkMethod.name();
	}

}
