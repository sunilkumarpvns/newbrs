package com.elitecore.elitesm.ws.rest.validator.esi;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

/**
 * Validates IP and port(both Ipv4 and Ipv6)
 * In Ipv6 address must be in format of [ipv6]:port
 * @author chirag.i.prajapati
 *
 */
public class IpAndPortValidator implements ConstraintValidator<ValidateIPPort, String> {

	private static Pattern ipPortPattern;
	
	@Override
	public void initialize(ValidateIPPort arg0) {
		ipPortPattern = Pattern.compile(RestValidationMessages.IPV4_IPV6_REGEX);
	}
	
	@Override
	public boolean isValid(String ipAndPort, ConstraintValidatorContext arg1) {
		
		boolean validIp = false;
		boolean validPort = false;
		
		if(ipAndPort == null){
			return false;
		}
		
		try {

			int ipPortPortion = ipAndPort.split(":").length - 1;

			if (ipPortPortion == 1) {
				String[] smallportion = ipAndPort.split(":");
				String port = smallportion[1];
				String ipAddress = smallportion[0];
				validPort = isValidPort(port);
				validIp = isValidIp(ipAddress);
			} else if (ipPortPortion > 1) {
				int portDivisorPos = ipAndPort.lastIndexOf(":");
				String port = ipAndPort.substring(portDivisorPos + 1);
				String ipAddress = ipAndPort.substring(0, portDivisorPos);

				if (ipAddress.startsWith("[") && ipAddress.endsWith("]")) {
					String finalIpddress = ipAddress.substring(1,ipAddress.length() - 1);
					validPort = isValidPort(port);
					validIp = isValidIp(finalIpddress);
				} else {
					validIp = false;
					validPort = false;
				}
			}

			if (validIp && validPort) {
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	private boolean isValidIp(String ipAddress){
		return ipPortPattern.matcher(ipAddress).matches();
	}
	
	private boolean isValidPort(String port){
		Long portValue;
		try {
			portValue = Long.parseLong(port);
		}catch(NumberFormatException nbe){
			return false;
		}
		if (portValue >= 0 && portValue <= 65535) {
			return true;
		}
		return false;
	}
}
