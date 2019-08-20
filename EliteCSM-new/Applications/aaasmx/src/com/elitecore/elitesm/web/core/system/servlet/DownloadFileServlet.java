package com.elitecore.elitesm.web.core.system.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.util.logger.Logger;



public class DownloadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "DOWNLOAD FILE SERVLET";

	public void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0,arg1);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileType = request.getParameter("filetype");
		String fileName = request.getParameter("filename");
		ServletOutputStream op = response.getOutputStream();
		try{
			byte data[] = null;
			File file = null;
			if(fileType.toLowerCase().equals("license")){
                String licensePath = this.getServletContext().getRealPath("license/notes");
                file = new File(licensePath,fileName);
                data = readFile(file);
			}else if(fileType.toLowerCase().equals("help")){
				String userManualPath = this.getServletContext().getRealPath("docs");
	            file = new File(userManualPath,fileName);
			    data = readFile(file);
			}

			ServletContext      context  = getServletConfig().getServletContext();
			String              mimetype = context.getMimeType( file.getName() );
			response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
			response.setContentLength( (int)file.length() );
			response.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName()  + "\"" );
			op.write(data);
			
		}catch(ServletException e){
			throw e;
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
		}

	}
	public byte[] readFile(File file)  throws Exception{
    	
    	InputStream fileInputStream = null;
    	DataInputStream dataInputStream = null;
    	byte[] dateLicenceFiledata = null;
    	try{
    		fileInputStream = new FileInputStream(file);
    		dataInputStream = new DataInputStream(fileInputStream);
    		dateLicenceFiledata = new byte[(int)file.length()];
    		dataInputStream.readFully(dateLicenceFiledata);
    	}catch(FileNotFoundException e) {
    		Logger.logError(MODULE, "File Not Found : " + e.getMessage());
    		throw e;
    	} catch(Exception e){
    		Logger.logError(MODULE, "Problem while reading file: " + e.getMessage());
    		throw e;
    	}finally{
    		try{
    			if(fileInputStream!=null)
    				fileInputStream.close();
    			if(dataInputStream!=null)
    				dataInputStream.close();
    		}catch(IOException ioExc){
    			Logger.logError(MODULE,"Problem while Reading LicenseKey "+ioExc.getMessage());
    		}
    	}
    	return dateLicenceFiledata;
    }
}
