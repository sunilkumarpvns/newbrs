package com.elitecore.elitesm.ws.rest.security;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.elitecore.elitesm.util.constants.ConfigConstant;

/**
 * <p>Additional details are required for authorizing a user/staff for that <b>AuthenticationDetails</b> is extended.
 * The HTTP request is available inside its constructor, just get value from request and initialize data members
 * of the class.</p>
 * 
 * <p>The object of this class will be available inside Spring custom authentication provider to access those addition 
 * request parameters for authorization.</p>
 * @author animesh christie
 */

@SuppressWarnings("serial")
public class AuthenticationDetails extends WebAuthenticationDetails{

	private String restACLParameter;
	private boolean diameter;
	private boolean crestelCharging;
	private boolean crestelRating;
	private String contextPath;
	private String localAddress;
	private int localPort;
	public AuthenticationDetails(HttpServletRequest request) {
		super(request);
		
		String iswadl= request.getParameter("_wadl"); 
		
		this.contextPath = request.getContextPath();
		this.localAddress = request.getLocalAddr();
		this.localPort = request.getLocalPort();
		
		if(iswadl != null){
			this.restACLParameter = "wadl";
		} else {
			String URL = request.getRequestURL().toString();
			
			String method = request.getMethod();
			boolean isValidURL = false; 
			String selectedVersion = null;

			for(String version : ConfigConstant.restVersions){										
			
				if(URL.contains("/diameter/")){
					diameter =true;
				} else if(URL.contains("/crestelcharging")) {
					crestelCharging = true;
				} else if(URL.contains("/crestelrating")) {
					crestelRating = true;
				} 				
				if( URL.contains("/"+version+"/")) {
					isValidURL = true;
					selectedVersion = version+"/";
					break;
				}
			}

			if (isValidURL == false) {
				this.restACLParameter = "invalidversion";
			} else {
				boolean isValidOperation = false, isModule = false;
				String postAction = request.getParameter("operation");
				
				if(postAction != null) {
					for(ModuleOperation module: ModuleOperation.values()){
						/*It checks for operation that it is defined in specific module or not */
						if(URL.contains(module.name())){
							isModule = true;
							List<String> operation = Arrays.asList(module.getModuleOperation());
							/*Below conditioned is specified to check weather specific module has a none or specific operation */
							if(operation.contains("NONE")== false && operation.contains(postAction)) {
								isValidOperation = true;
							}
						}
					}
					
					if(isModule && isValidOperation == false) {
						this.restACLParameter = "invalidoperation";	
					}else {
						if ("post".equalsIgnoreCase(method)) {
							if ("create".equalsIgnoreCase(postAction)) {
								method = "POST";
							} else if ("update".equalsIgnoreCase(postAction)) {
								method = "PUT";
							} else if ("search".equalsIgnoreCase(postAction)) {
								method = "GET";
							} else if ("delete".equalsIgnoreCase(postAction)) {
								method = "DELETE";
							} else {
								// TODO Intentionally kept blank
							}
						}
					}
				}
				
				int startIndex = URL.indexOf(selectedVersion)+selectedVersion.length();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(method).append("_");

				int endSlashIndex = URL.indexOf("/", startIndex);
				int endDotIndex = URL.indexOf(".", startIndex);
				int urlEndIndex = URL.length();
				int endIndex;
				
				if (endSlashIndex != -1 && endSlashIndex < urlEndIndex) {
					endIndex = endSlashIndex;
				} else if (endDotIndex != -1 && endDotIndex < urlEndIndex) {
					endIndex = endDotIndex;
				} else {
					endIndex = urlEndIndex;
				}
				stringBuilder.append(URL.substring(startIndex, endIndex));
				this.restACLParameter = stringBuilder.toString().toUpperCase();
			}
		}
		
	}

	public String getRestACLParameter() {
		return restACLParameter;
	}

	public void setRestACLParameter(String restACLParameter) {
		this.restACLParameter = restACLParameter;
	}

	public boolean isDiameter() {
		return diameter;
	}

	public void setDiameter(boolean diameter) {
		this.diameter = diameter;
	}

	public boolean isCrestelCharging() {
		return crestelCharging;
	}

	public void setCrestelCharging(boolean crestelCharging) {
		this.crestelCharging = crestelCharging;
	}

	public boolean isCrestelRating() {
		return crestelRating;
	}

	public void setCrestelRating(boolean crestelRating) {
		this.crestelRating = crestelRating;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}
}