package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class FindInvalidCipherSuit
 */
public class FindInvalidCipherSuit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="FindInvalidCipherSuit";

   
    public FindInvalidCipherSuit() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<String> invalidCipherSuitList=new ArrayList<String>();
		String cipherList=request.getParameter("cipherList");
		String [] paramArray=cipherList.split(",");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		try {
			
			Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
			List<String> lstNewString=new ArrayList<String>();
			
			for(CipherSuites str:cipherSuites){
				lstNewString.add(str.name());
			}
		
			for(int i=0;i<paramArray.length;i++){
				boolean flag=false;
				for(CipherSuites cs:cipherSuites){
					if(cs.name().equals(paramArray[i].trim())){
						flag=true;
					}
				}
				if(flag==false){
					invalidCipherSuitList.add(paramArray[i]);
				}
			}
			
			for(CipherSuites cipherSuit:cipherSuites){
				for(String invalidStr:invalidCipherSuitList){
					if(cipherSuit.name().equals(invalidStr)){
						invalidCipherSuitList.remove(invalidStr);
					}
				}
			}
			out.println(invalidCipherSuitList);
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
