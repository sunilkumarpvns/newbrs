package com.elitecore.elitesm.web.core.system.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * Servlet implementation class DownloadNetServerFileServlet
 */
public class DownloadNetServerFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = DownloadNetServerFileServlet.class.getSimpleName();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadNetServerFileServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String fileName = request.getParameter("fileName");
		String netServerId = request.getParameter("netServerId");
		ServletOutputStream op = response.getOutputStream();
		IRemoteCommunicationManager remoteCommunicationManager = null;
		try{
			byte data[] = null;
			ServletContext      context  = getServletConfig().getServletContext();
			String              mimetype = context.getMimeType( fileName );
			File file = new File(fileName);
			response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
			response.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName()  + "\"" );
			NetServerBLManager blManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData= blManager.getNetServerInstance(netServerId);
			String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);				
			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
			remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
			int length =0;
			do{
				Object[] params = {fileName,length};
				String[] signs = {"java.lang.String","java.lang.Integer"};
				data = (byte[])remoteCommunicationManager.execute(MBeanConstants.CONFIGURATION,"retriveFileData",params,signs);
				if(data!=null && data.length>0){
					op.write(data);
					length = length + data.length;
				}
				
			}while(data!=null && data.length>0 && data.length == 50000);
			response.setContentLength(length);


		}catch(IOException e){
			throw e;
		}catch(Exception e){
			Logger.logError(MODULE, "Error in downloading file : " + e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				op.close();
			}catch(Exception e){

			}
			try{
				if(remoteCommunicationManager != null)
					remoteCommunicationManager.close();  
			}
			catch (Throwable e) {
				remoteCommunicationManager = null;
			}
		}
	}

}
