package com.elitecore.elitesm.web.servermgr.eap;

import com.elitecore.elitesm.web.core.base.BaseWebAction;

public class BaseEAPConfigAction extends BaseWebAction{
	
	
	protected String convertCertificatetypeListToLabel(String certificateTypesList) {

		String[] certificateTypesValueArray = certificateTypesList.split(",");
		StringBuffer labelCertificateType =new StringBuffer();

		for(int i=0;i<certificateTypesValueArray.length;i++){

			if(labelCertificateType.length()>0){

				if("2".equals(certificateTypesValueArray[i])){

					labelCertificateType.append(",");
					labelCertificateType.append("DSS");

				}else if("4".equals(certificateTypesValueArray[i])){

					labelCertificateType.append(",");
					labelCertificateType.append("DSS_DH");

				}else if("1".equals(certificateTypesValueArray[i])){

					labelCertificateType.append(",");
					labelCertificateType.append("RSA");

				}else if("3".equals(certificateTypesValueArray[i])){

					labelCertificateType.append(",");
					labelCertificateType.append("RSS_DH");
				}

			}else{

				if("2".equals(certificateTypesValueArray[i])){
					labelCertificateType.append("DSS");
				}else if("4".equals(certificateTypesValueArray[i])){
					labelCertificateType.append("DSS_DH");
				}else if("1".equals(certificateTypesValueArray[i])){
					labelCertificateType.append("RSA");
				}else if("3".equals(certificateTypesValueArray[i])){
					labelCertificateType.append("RSS_DH");
				}
			}

		}
		return labelCertificateType.toString();	
	}

	protected String[] convertcipherSuiteListToLabel(String ciphersuiteList) {
		String[] cipherSuiteValueArray = ciphersuiteList.split(",");
		String[] cipherSuiteLabelArray = new String[cipherSuiteValueArray.length];

		for(int i=0;i<cipherSuiteValueArray.length;i++){

			if("10".equals(cipherSuiteValueArray[i])){
				cipherSuiteLabelArray[i]="TLS_RSA_WITH_3DES_EDE_CBC_SHA";				
			}else if("47".equals(cipherSuiteValueArray[i])){
				cipherSuiteLabelArray[i]="TLS_RSA_WITH_AES_128_CBC_SHA";
			}else if("9".equals(cipherSuiteValueArray[i])){
				cipherSuiteLabelArray[i]="TLS_RSA_WITH_DES_CBC_SHA";
			}
		}

		return cipherSuiteLabelArray;
	}
}
