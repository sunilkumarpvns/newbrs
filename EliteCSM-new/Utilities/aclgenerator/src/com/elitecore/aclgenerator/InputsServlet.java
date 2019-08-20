package com.elitecore.aclgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * Servlet implementation class InputsServlet
 */
public class InputsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InputsServlet() {
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
	    String configurationKey = null;
	    String serialNo = null;
	  //  String configID = request.getParameter("config_id");
	    int lastModelId = getNumber(request.getParameter("last_model_id"));
	    int lastModuleId =getNumber(request.getParameter("last_module_id"));
	    int lastSubmoduleId = getNumber(request.getParameter("last_submodule_id"));
	    int lastActionId = getNumber(request.getParameter("last_action_id"));
	    
	    String tmpDir = System.getProperty("java.io.tmpdir");
	    File tmpDirFile = new File(tmpDir);
	    DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
	    diskFileItemFactory.setSizeThreshold(61440);
	    diskFileItemFactory.setRepository(tmpDirFile);
	    File xmlFile = null;
	    ServletFileUpload uploadHandler = new ServletFileUpload(diskFileItemFactory);
	    try {
	      List items = uploadHandler.parseRequest(request);
	      Iterator iterator = items.iterator();
	      while (iterator.hasNext()) {
	    	  FileItem fileItem = (FileItem)iterator.next();
	    	  if (fileItem.isFormField()) {
	    		  System.out.println(" parameter name : " + fileItem.getFieldName() + " ; value :" + fileItem.getString());
	    		  
	    		  if (fileItem.getFieldName().equals("last_model_id"))
	    			  lastModelId = getNumber(fileItem.getString());
	    		  else if (fileItem.getFieldName().equals("last_module_id"))
	    			  lastModuleId = getNumber(fileItem.getString());
	    		  else if (fileItem.getFieldName().equals("last_submodule_id"))
	    			  lastSubmoduleId = getNumber(fileItem.getString());
	    		  else if (fileItem.getFieldName().equals("last_action_id"))
	    			  lastActionId = getNumber(fileItem.getString());
	    	
	    	  }
	    	  else
	    	  {
	    		  System.out.println(" Field Name   = " + fileItem.getFieldName());
	    		  System.out.println(" File Name    = " + fileItem.getName());
	    		  System.out.println(" Content type = " + fileItem.getContentType());
	    		  System.out.println(" File Size    = " + fileItem.getSize());

	    		  xmlFile = new File(tmpDirFile, fileItem.getName());
	    		  System.out.println("temp file is generated at : " + xmlFile.getCanonicalPath());
	    		  fileItem.write(xmlFile);
	    	  }

	      }

	      System.out.println("lastModelId      : " + lastModelId);
	      System.out.println("lastModuleId     : " + lastModuleId);
	      System.out.println("lastSubmoduleId  : " + lastSubmoduleId);
	      System.out.println("lastActionId     : " + lastActionId);
	      String msg = "";

	      AclSqlGenerator dbcGenerator = new AclSqlGenerator();
	      File dbcfile = dbcGenerator.generateDbc(xmlFile,lastModelId,lastModuleId,lastSubmoduleId,lastActionId);
	      System.out.println("dbcfile file is generated at : " + dbcfile.getCanonicalPath());
	      if (dbcfile != null) {
	        response.setContentType("text/plain");
	        OutputStream stream = response.getOutputStream();
	        FileInputStream fileInputStream = new FileInputStream(dbcfile);
	        byte[] data = new byte[1024];
	        int length = 0;
	        while ((length = fileInputStream.read(data)) > -1)
	          stream.write(data, 0, length);

	        stream.close(); return;
	      }

	      response.setContentType("text/html");
	      PrintWriter out = response.getWriter();
	      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	      out.println("<HTML>");
	      out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
	      out.println("  <BODY>");
	      out.print("    Failed to generate DBC ");
	      out.println("  </BODY>");
	      out.println("</HTML>");
	      out.flush();
	      out.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }

	}
	private int getNumber(String str)
	{
	  int number = 0;
	  try {
	    number = Integer.parseInt(str);
	  }
	  catch (Exception localException) {
	  }
	  return number;
	}

}

