package com.elitecore.netvertex.gateway.diameter.mapping;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.base.Preconditions.checkArgument;

public class DiameterValueProvider implements ValueProvider{
	private DiameterRequest diameterRequest;
	private DiameterAnswer diameterAnswer;
	private DiameterGatewayConfiguration configuration;

	public DiameterValueProvider(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, DiameterGatewayConfiguration configuration){
		checkArgument(diameterRequest != null || diameterAnswer != null, "Either diameter request or diameter answer should be provided");
		this.diameterRequest = diameterRequest;
		this.diameterAnswer = diameterAnswer;
		this.configuration = configuration;
	}
	
	@Override
	public String getStringValue(String identifier) {
		IDiameterAVP diameterAVP = null;
		if(diameterRequest != null){
			diameterAVP = diameterRequest.getAVP(identifier);
		}
		
		if(diameterAVP==null){
			if(diameterAnswer != null){
				diameterAVP = diameterAnswer.getAVP(identifier);
			}
		}
		
		if(diameterAVP==null)
			return null;
		else
			return diameterAVP.getStringValue();
	}
	
	@Override
	public long getLongValue(String identifier)throws InvalidTypeCastException,MissingIdentifierException {
		IDiameterAVP diameterAVP = null;
		long value=0;
		if(diameterRequest != null){
			diameterAVP = diameterRequest.getAVP(identifier);
		}
		
		if(diameterAVP==null){
			if(diameterAnswer != null){
				diameterAVP = diameterAnswer.getAVP(identifier);
			}
		}
		
		if (diameterAVP==null) {
			throw new MissingIdentifierException("Configure identifier not found: " + identifier);
		}

		if((value=diameterAVP.getInteger()) == -1)
			throw new NumberFormatException();
		
		return value;
	}

	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<IDiameterAVP> diameterAVPList = null;
		if(diameterRequest != null){
			diameterAVPList = diameterRequest.getAVPList(identifier);
		}
		
		if(diameterAVPList ==null || diameterAVPList.isEmpty()){
			if(diameterAnswer != null){
				diameterAVPList = diameterAnswer.getAVPList(identifier);
			}
		}
		if(diameterAVPList!= null){
			List<String> stringValues=new ArrayList<String>();
			for(IDiameterAVP iDiameterAVP : diameterAVPList){
				stringValues.add(iDiameterAVP.getStringValue());
			}
			return stringValues;
		}else {
			throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}
	}

	@Override
	public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		List<IDiameterAVP> diameterAVPList= null;
		if(diameterRequest != null){
			diameterAVPList = diameterRequest.getAVPList(identifier);
		}
		
		if(diameterAVPList ==null || diameterAVPList.isEmpty()){
			if(diameterAnswer != null){
				diameterAVPList = diameterAnswer.getAVPList(identifier);
			}
		}
		if(diameterAVPList!=null){
			List<Long> longValues=new ArrayList<Long>();
			for(IDiameterAVP iDiameterAVP : diameterAVPList){
				longValues.add(iDiameterAVP.getInteger());
			}
			return longValues;
		}else{
			throw new MissingIdentifierException("Configured identifier not found: "+identifier);
		}
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	public @Nullable
	DiameterRequest getDiameterRequest() {
		return diameterRequest;
	}
	public @Nullable DiameterAnswer getDiameterAnswer() {
		return diameterAnswer;
	}
	
	public DiameterGatewayConfiguration getConfiguration() {
		return configuration;
	}

}
