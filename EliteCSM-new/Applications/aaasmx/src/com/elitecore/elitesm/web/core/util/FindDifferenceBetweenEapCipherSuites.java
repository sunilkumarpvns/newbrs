package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;

public class FindDifferenceBetweenEapCipherSuites extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FindDifferenceBetweenEapCipherSuites";

	public FindDifferenceBetweenEapCipherSuites(){
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String preserveValuesArray = request.getParameter("preserveValuesArray");
		String [] paramArray=preserveValuesArray.split(",");
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			ProtocolVersion minTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1);
			ProtocolVersion maxTLSVer=ProtocolVersion.fromVersion(EAPConfigConstant.TLSv_1_2);
			
			Set<CipherSuites> cipherSuiteSet = new LinkedHashSet<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTLSVer,maxTLSVer));
			TreeSet<String> treeSetOfCipherSuit=new TreeSet<String>();
			ArrayList<String> cipherArrayList=new ArrayList<String>();
			
			for(String str:paramArray){
				cipherArrayList.add(str);
			}
			
			if(Collectionz.isNullOrEmpty(cipherSuiteSet) == false){
				for(CipherSuites cipherSuites:cipherSuiteSet){
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
