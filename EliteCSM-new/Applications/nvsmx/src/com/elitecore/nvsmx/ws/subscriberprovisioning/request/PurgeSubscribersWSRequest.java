package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class PurgeSubscribersWSRequest extends SubscriberProvisioningWSRequest implements WebServiceRequest{

	private List<String> subscriberIds;
	private List<String> alternateIds;
	private String webServiceName;
	private String webServiceMethodName;
	

	private static Predicate<String>  FILTER_LIST_PREDICATE = new Predicate<String>() {
		@Override
		public boolean apply(String input) {
			if(Strings.isNullOrBlank(input)==true){
				return false;
			}
			return true;
		}
	};

	public PurgeSubscribersWSRequest(List<String> subscriberIdentities, List<String> alternateIds, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
		super(parameter1, parameter2,webServiceName, webServiceMethodName);
		this.subscriberIds =  filterList(subscriberIdentities);
		this.alternateIds = filterList(alternateIds);
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}


	public List<String> getSubscriberIds() {
		return subscriberIds;
	}

	public void setSubscriberIds(List<String> subscriberIds) {
		this.subscriberIds = subscriberIds;
	}

	public List<String> getAlternateIds() {
		return alternateIds;
	}

	public void setAlternateIds(List<String> alternateIds) {
		this.alternateIds = alternateIds;
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}
	
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Ids", subscriberIds);
		builder.append("Alternate Ids", alternateIds);
		builder.append("Parameter 1", getParameter1());
		builder.append("Paramater 2", getParameter2());
		
		return builder.toString();
	}

	private List<String> filterList(List<String> list){

		if(Collectionz.isNullOrEmpty(list)==true){
			return null;
		}
		Collectionz.filter(list, FILTER_LIST_PREDICATE);
		trimList(list);
		return list;
   }

	@XmlTransient
	@Override
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlTransient
	@Override
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}
}
