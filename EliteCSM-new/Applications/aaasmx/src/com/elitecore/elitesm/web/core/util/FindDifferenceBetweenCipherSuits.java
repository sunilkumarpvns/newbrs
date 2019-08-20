package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class FindDifferenceBetweenCipherSuits
 */
public class FindDifferenceBetweenCipherSuits extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FindDifferenceBetweenCipherSuits";
	
    public FindDifferenceBetweenCipherSuits() {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String preserveValuesArray = request.getParameter("preserveValuesArray");
		String minTLSVersion = request.getParameter("minTLSVersion");
		String maxTLSVersion = request.getParameter("maxTLSVersion");
		String [] paramArray=preserveValuesArray.split(",");
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			ArrayList<CipherSuites> cipherSuitesList = new ArrayList<CipherSuites>();
			
			TLSVersion minTLSVer=TLSVersion.fromVersion(minTLSVersion);
			TLSVersion maxTLSVer=TLSVersion.fromVersion(maxTLSVersion);
			
			cipherSuitesList=(ArrayList<CipherSuites>) CipherSuites.getSupportedCipherSuites(minTLSVer, maxTLSVer);
			
			TreeSet<String> treeSetOfCipherSuit=new TreeSet<String>();
			ArrayList<String> cipherArrayList=new ArrayList<String>();
			
			for(String str:paramArray){
				cipherArrayList.add(str);
			}
			
			if(cipherSuitesList !=null && cipherSuitesList.size() > 0){
				for(CipherSuites cipherSuites:cipherSuitesList){
					treeSetOfCipherSuit.add(cipherSuites.name());
					cipherArrayList.add(cipherSuites.name());
				}
				
			}
			 Set set = new HashSet<String>();
			 List newList = new ArrayList<String>();
			 for (Iterator iter = cipherArrayList.iterator();    iter.hasNext(); )
			 {
				 Object element = iter.next();
				 if (set.add(element))
					 newList.add(element);
			 }
			 cipherArrayList.clear();
			 cipherArrayList.addAll(newList);
			out.println(cipherArrayList);  
			
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
	
}
