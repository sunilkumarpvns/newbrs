package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AvpAccumalatorTestSupport implements AvpAccumalator {

	private List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
	private List<IDiameterAVP> diameterInfoAVPs = new ArrayList<IDiameterAVP>();
	
	@Override
	public boolean isEmpty() {
		return diameterAVPs.isEmpty();
	}

	@Override
	public void addAvp(String id, String value) {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
		diameterAVP.setStringValue(value);
		add(diameterAVP);
	}

	@Override
	public void addAvp(String id, int value) {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
		diameterAVP.setInteger(value);
		add(diameterAVP);
	}

	@Override
	public void addAvp(String id, long value) {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
		diameterAVP.setInteger(value);
		add(diameterAVP);
	}

	@Override
	public void addAvp(String id, Date value) {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
		diameterAVP.setTime(value);
		add(diameterAVP);
	}

	@Override
	public void add(List<IDiameterAVP> diameterAVP) {
		diameterAVPs.addAll(diameterAVP);
	}
	
	@Override
	public void add(IDiameterAVP diameterAVP) {
		diameterAVPs.add(diameterAVP);
	}
	
	public List<IDiameterAVP> getAll() {
		return diameterAVPs;
	}

	public List<IDiameterAVP> getAllInfoAVPs() {
		return diameterInfoAVPs;
	}
	
	@Override
	public void addInfoAVP(IDiameterAVP diameterAVP) {
		diameterInfoAVPs.add(diameterAVP);
	}

	@Override
	public void addInfoAVP(String id, String value) {
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
		diameterAVP.setStringValue(value);
		addInfoAVP(diameterAVP);
	}

	public List<IDiameterAVP> getDiameterAVPs(String id) {
		return diameterAVPs.stream().filter(avp -> avp.getAVPId().equals(id)).collect(Collectors.toList());
	}
}
