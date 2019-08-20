package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;

public abstract class RadResponseListnerImpl implements RadResponseListener {
	RadServiceRequest request;
	RadServiceResponse response;
	public RadResponseListnerImpl(RadServiceRequest request, RadServiceResponse response) {
		this.request = request;
		this.response = response;
	}
}
