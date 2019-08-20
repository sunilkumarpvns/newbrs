package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class RetriveEAPCipherSuites
 */
public class RetriveEAPCipherSuites extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="RetriveEAPCipherSuites";

   
    public RetriveEAPCipherSuites() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String minTLSVersion = request.getParameter("minTLSVersion");
		String maxTLSVersion = request.getParameter("maxTLSVersion");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			ProtocolVersion minTLSVer=ProtocolVersion.fromVersion(minTLSVersion);
			ProtocolVersion maxTLSVer=ProtocolVersion.fromVersion(maxTLSVersion);
			
			Set<CipherSuites> cipherSuitesSet = new LinkedHashSet<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTLSVer, maxTLSVer));
			out.println(cipherSuitesSet);
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
