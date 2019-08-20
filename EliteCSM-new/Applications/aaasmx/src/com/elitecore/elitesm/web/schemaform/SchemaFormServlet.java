package com.elitecore.elitesm.web.schemaform;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class SchemaFormServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestDispatcher requestDispatcher;
		if(request.getSession() != null) {
			if(request.getSession().getAttribute("radiusLoginForm") != null){    		
				String userName = (String) request.getSession().getAttribute("systemUserName");

				StaffBLManager staffBLManager = new StaffBLManager();
				IStaffData staffData = null;
				try {
					staffData = staffBLManager.getStaffDataByUserName(userName);
				} catch (DataManagerException e) {
					e.printStackTrace();
				}

				if (staffData != null ) {
					String username = staffData.getUsername();
					String password = staffData.getPassword();
					String decryptedPassword = null;
					
					try {
						decryptedPassword = PasswordEncryption.getInstance().decrypt(password, PasswordEncryption.ELITE_PASSWORD_CRYPT);
					} catch (NoSuchEncryptionException e) {
						e.printStackTrace();
					} catch (DecryptionNotSupportedException e) {
						e.printStackTrace();
					} catch (DecryptionFailedException e) {
						e.printStackTrace();
					}
			
					request.setAttribute("usrname", username);
					request.setAttribute("pwd", decryptedPassword);
				}
			}
		}
		requestDispatcher = request.getRequestDispatcher("/jsp/schemaform/SchemaForm.jsp");
		requestDispatcher.forward(request, response);			
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

