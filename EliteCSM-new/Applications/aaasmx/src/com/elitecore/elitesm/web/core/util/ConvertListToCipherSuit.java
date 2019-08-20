package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class ConvertListToCipherSuit
 */
public class ConvertListToCipherSuit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="ConvertListToCipherSuit";

   
    public ConvertListToCipherSuit() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cipherList=request.getParameter("listArray");
		String [] paramArray=cipherList.split(",");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			List<String> strcipherSuitArray=new ArrayList<String>();
			Collection<CipherSuites> cipherSuitesMap = Arrays.asList(CipherSuites.values());
			
			for(int i=0;i<paramArray.length;i++){
				if(paramArray[i] != null || paramArray[i] != " "){
					for(CipherSuites cipherSuit:cipherSuitesMap){
						if(paramArray[i].trim().equals(cipherSuit.name())){
							strcipherSuitArray.add(String.valueOf(cipherSuit.code));
						}
					}
				}
			}
		
			out.println(strcipherSuitArray);
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
