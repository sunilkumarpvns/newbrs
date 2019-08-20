package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class RetriveCipherSuit
 */
public class RetriveCipherSuit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="RetriveCipherSuit";

   
    public RetriveCipherSuit() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<CipherSuites> cipherSuitesList = new ArrayList<CipherSuites>();
		String minTLSVersion = request.getParameter("minTLSVersion");
		String maxTLSVersion = request.getParameter("maxTLSVersion");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			TLSVersion minTLSVer=TLSVersion.fromVersion(minTLSVersion);
			TLSVersion maxTLSVer=TLSVersion.fromVersion(maxTLSVersion);
			
			cipherSuitesList=(ArrayList<CipherSuites>) CipherSuites.getSupportedCipherSuites(minTLSVer, maxTLSVer);
			
			TreeSet<String> treeSetOfCipherSuit=new TreeSet<String>();
			
			for(CipherSuites cipherSuit:cipherSuitesList){
				treeSetOfCipherSuit.add(cipherSuit.name());
			}
			out.println(treeSetOfCipherSuit);
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
