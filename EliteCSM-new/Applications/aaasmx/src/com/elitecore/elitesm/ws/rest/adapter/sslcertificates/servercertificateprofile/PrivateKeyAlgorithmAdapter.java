package com.elitecore.elitesm.ws.rest.adapter.sslcertificates.servercertificateprofile;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
 * 
 * Private Key Algorithm Adapter do conversion of Private Key Algorithm, short form to full form and vice versa. <br>
 * It takes short form Private Key Algorithm as input and give full form Private Key Algorithm as output in unmarshal. <br>
 * It takes full form Private Key Algorithm as input and give short form Private Key Algorithm as output in marshal. <br>
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <private-key-algorithm>DHA</private-key-algorithm>
 * 
 * }
 * 
 * than output is :
 * DiffieHellman
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class PrivateKeyAlgorithmAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String privateKeyAlgorithm) throws Exception {

		String privateKeyAlgorithmName = privateKeyAlgorithm;

		if (Strings.isNullOrBlank(privateKeyAlgorithm) == false) {
			if (privateKeyAlgorithm.equalsIgnoreCase("DHA")) {
				privateKeyAlgorithmName = "DiffieHellman";
			} else if ("DiffieHellman".equalsIgnoreCase(privateKeyAlgorithm.trim())) {
				privateKeyAlgorithmName = "invalid";
			}
		}

		return privateKeyAlgorithmName;
	}

	@Override
	public String marshal(String privateKeyAlgorithmName) throws Exception {

		String privateKeyAlgorithm = privateKeyAlgorithmName;

		if (privateKeyAlgorithmName.equals("DiffieHellman")) {
			privateKeyAlgorithm = "DHA";
		}
		
		return privateKeyAlgorithm;
	}

}
