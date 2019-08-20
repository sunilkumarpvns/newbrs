package com.elitecore.elitesm.web.dashboard;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.dashboard.db.DBConnectionManager;
import com.elitecore.elitesm.ws.logger.Logger;



public class DashboardPluginAction extends BaseDispatchAction{
	private static final String MODULE = DashboardPluginAction.class.getSimpleName();;
	private static final String VIEW_DASHBOARD="viewDashboard";
	private static final String VIEWELITEAAA="viewEliteAAA";
	private static final String VIEWTABCONTAINER="viewTabContainer";
	private static final String VIEWELITEDSC="viewEliteDSC";
	private static final String VIEWGRAPHS="viewGraphs";
	private static final String VIEWLIVEUSAGE="viewLiveUsage";
	private static final String VIEWSESSIONDETAILS="viewSessionDetails";
	private static final String VIEWCPUUSAGE="viewCPUUsage";
	private static String queryForUserWidgetRelation=null;
	private static String queryForUpdateWidgetOrder=null;
	private static String queryForServerInstanceList=null;
	private static String queryForActiveSessionList=null;
	private static String queryForPromotionalWiseSubscribers=null;
	private static String queryForBWList=null;
	private static String queryForAddOnWiseSubscribers=null;
	private static String queryForPolicyGroupWiseSubscribers=null;
	private static String queryForRemovingWidget=null;
	private static String queryForInsertNewWidgets=null;
	private static String queryForHighestOrder=null;
	
	
	static{
		queryForUserWidgetRelation="SELECT WIDGETID,USERID,ORDERNO,WIDGETTYPE,CONFIGPARAMS FROM TBLMWIDGETUSERRELATION  WHERE USERID=? ORDER BY ORDERNO";           
		queryForUpdateWidgetOrder="UPDATE TBLMWIDGETUSERRELATION SET ORDERNO=? WHERE WIDGETID=? AND USERID=?";            
		queryForServerInstanceList="SELECT NETSERVERID,NAME,DESCRIPTION FROM TBLMNETSERVERINSTANCE";           
		queryForActiveSessionList="SELECT USER_IDENTITY, ACCT_SESSION_ID, NAS_IP_ADDRESS, USER_NAME FROM TBLMCONCURRENTUSERS";          
		queryForPromotionalWiseSubscribers="SELECT COUNT(CONCUSERID),GROUPNAME  FROM TBLMCONCURRENTUSERS GROUP by groupname";   
		queryForBWList="SELECT COUNT(BWID),TYPENAME FROM tblbwlist GROUP by TYPENAME";
		queryForAddOnWiseSubscribers="SELECT COUNT(CONCUSERID),GROUPNAME  FROM TBLMCONCURRENTUSERS GROUP by groupname";         
		queryForPolicyGroupWiseSubscribers="SELECT COUNT(T1.SUBSCRIBERPACKAGE) AS TOTAL, T2.NAME AS PACKAGENAME FROM TBLNETVERTEXCUSTOMER T1, TBLMPOLICYGROUP T2 WHERE T1.SUBSCRIBERPACKAGE=T2.NAME GROUP BY T1.SUBSCRIBERPACKAGE,T2.NAME";
		queryForRemovingWidget="DELETE FROM TBLMWIDGETUSERRELATION WHERE WIDGETID=? AND USERID=?";
		queryForInsertNewWidgets="INSERT INTO TBLMWIDGETUSERRELATION(WIDGETID,USERID,ORDERNO,WIDGETTYPE,CONFIGPARAMS ) VALUES(SEQ_MWIDGETUSERRELATION.nextval,?,?,?,?)";
		queryForHighestOrder="SELECT MAX(ORDERNO) AS ORDERNO FROM TBLMWIDGETUSERRELATION WHERE USERID=?";
}
	
	public ActionForward getSessionDetails(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWSESSIONDETAILS);
	}
	
	public ActionForward getTest(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward("viewtest");
	}
	
	public ActionForward getTest2(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward("viewtest2");
	}
	
	
	public ActionForward getLiveUsage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWLIVEUSAGE);
	}
	
	public ActionForward getCPUUsage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWCPUUSAGE);
	}
	
	
	public ActionForward getAAAData(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWELITEAAA);
	}
	
	public ActionForward getContainer(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			//   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", null);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWTABCONTAINER);
	}
	
	
	public ActionForward getEliteDSCData(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWELITEDSC);
	}
	
	public ActionForward getGraph(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEWGRAPHS);
	}
	
	
	
	
	public ActionForward initDashboard(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called initDashboard");
	      Connection connection=null;
	      try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   JSONArray userWidgetArray=getUserWidgets(userId,connection); 
			   request.getSession().setAttribute("userWidgetList", userWidgetArray);
		   }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
	      catch (Exception e){
			   Logger.logError(MODULE, "Error while getting user widgets information. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE,e);
		   }
		   finally{
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return mapping.findForward(VIEW_DASHBOARD);
	}
	
	public ActionForward updateWidgetOrder(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called updateWidgetOrder");
	      Connection connection=null;
	      PreparedStatement pstmt=null;
	      String tempWidgetIds=request.getParameter("sortedWidgetDefinitions");
	      Long[] widgetIds=null;
	      if(tempWidgetIds!=null&&tempWidgetIds.trim().length()>=0){
	    	 widgetIds=convertStringIdsToLong(tempWidgetIds.split(","));
	      }
	   	  try{ 
	   	       String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			   connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			   pstmt=connection.prepareStatement(queryForUpdateWidgetOrder);
			   int i=0;
			   for(Long widgetId:widgetIds){
				   pstmt.setInt(1,++i);
				   pstmt.setLong(2,widgetId);
				   pstmt.setString(3, userId);
				   pstmt.execute();
			   }
			   connection.commit();
			   Logger.logDebug(MODULE, "successfully update the widget order");
		      }catch (DatabaseConnectionException e){
			   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE, e);
		   }catch(SQLException e){
			   Logger.logError(MODULE, "SQL Error while updating user widget order. Reason: " + e.getMessage());
		   }catch(Exception e){
			   Logger.logError(MODULE, "Error while updating user widget order. Reason: " + e.getMessage());
			   Logger.logTrace(MODULE, e);
		   }finally{
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
	     return null;
	}

		
	

	public ActionForward removeWidget(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called remove widget method");
	     Connection connection=null;
	      PreparedStatement pstmt=null;
	      String tempWidgetId=request.getParameter("widgetId");
	      long widgetId=0;
	      if(tempWidgetId!=null&&tempWidgetId.trim().length()>=0)
	       widgetId=Long.parseLong(tempWidgetId);
	      try{ 
	    	  String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
	    	  connection =DBConnectionManager.getInstance().getSMDatabaseConection();
	    	  pstmt=connection.prepareStatement(queryForRemovingWidget);
	    	  pstmt.setLong(1,widgetId);
	    	  pstmt.setString(2, userId);
	    	  pstmt.execute();
	    	  connection.commit();
	    	  Logger.logDebug(MODULE, "successfully delete the widget");
	      }catch (DatabaseConnectionException e){
	    	  Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
	      }catch(SQLException e){
	    	  Logger.logError(MODULE, "SQL Error while deleting  widget. Reason: " + e.getMessage());
	      }catch(Exception e){
	    	  Logger.logError(MODULE, "Error while deleting  widget. Reason: " + e.getMessage());
	    	  Logger.logTrace(MODULE, e);
	      }finally{
	    	  if(pstmt != null){
	    		  try{pstmt.close();}catch(SQLException e){
	    			  Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
	    		  }
	    	  }
	    	  if(connection!=null){
	    		  try {connection.close();} catch (SQLException e) {
	    			  Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
	    		  }
	    	  }
	      }
	      return null;
	}
	
	public ActionForward insertNewWidgets(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	     Logger.logDebug(MODULE, "Called insert new widgets");
	     response.setContentType("application/json; charset=utf-8");
	     PrintWriter out = response.getWriter();
	      Connection connection=null;
	      PreparedStatement pstmt=null;
	      PreparedStatement pstmtForselectOrder=null;
	      ResultSet rstForHighestOrder=null;
	      String tempWidgetTypeArray=request.getParameter("widgetTypeArray");
	      String[] widgetTypeArray=null;
	      if(tempWidgetTypeArray!=null&&tempWidgetTypeArray.trim().length()>=0){
	    	 widgetTypeArray=tempWidgetTypeArray.split(",");
	      }
	      try{ 
	    	  String userId = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
	    	  connection =DBConnectionManager.getInstance().getSMDatabaseConection();
	    	  pstmtForselectOrder=connection.prepareStatement(queryForHighestOrder);
	    	  pstmtForselectOrder.setString(1, userId);
	    	  rstForHighestOrder=pstmtForselectOrder.executeQuery();
	    	  int orderNo=0;
	    	  if(rstForHighestOrder.next()){
	    		  orderNo=rstForHighestOrder.getInt(1);
	    	  }
	    	  pstmt=connection.prepareStatement(queryForInsertNewWidgets);
	    	  for(int i=0;i<widgetTypeArray.length;i++){
	    		  pstmt.setString(1, userId);
	    		  pstmt.setInt(2,++orderNo);
	    		  pstmt.setString(3,widgetTypeArray[i]);
	    		  pstmt.setString(4, null);
	    		  pstmt.execute();
	    	  }
	    	  JSONArray userWidgetArray=getUserWidgets(userId,connection); 
	    	  request.getSession().setAttribute("userWidgetList", userWidgetArray);
	    	  connection.commit();
	    	  Logger.logDebug(MODULE,"successfully added new widgets");
	      }catch (DatabaseConnectionException e){
	    	  Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
	    	  Logger.logTrace(MODULE, e);
	      }catch(SQLException e){
	    	  Logger.logError(MODULE, "SQL Error while adding new widgets. Reason: " + e.getMessage());
	      }catch(Exception e){
	    	  Logger.logError(MODULE, "Error while adding new widgets. Reason: " + e.getMessage());
	    	  Logger.logTrace(MODULE, e);
	      }finally{
	    	  if(rstForHighestOrder != null){
	    		  try{rstForHighestOrder.close();}catch(SQLException e){
	    			  Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
	    		  }
	    	  }
	    	  if(pstmt != null){
	    		  try{pstmt.close();}catch(SQLException e){
	    			  Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
	    		  }
	    	  }if(pstmtForselectOrder!=null){
	    		  try{pstmtForselectOrder.close();}catch(SQLException e){
	    			  Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
	    		  }
	    	  }
	    	  if(connection!=null){
	    		  try {connection.close();} catch (SQLException e) {
	    			  Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
	    		  }
	    	  }
	      }
	      return  null;
	}

	
	
	
	
	public ActionForward  getPromotionalWiseSubscribers(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching promotional offer wise subscribers");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray promotionalWiseSubscribers =null;
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForPromotionalWiseSubscribers);
			resultSet = pstmt.executeQuery();
			promotionalWiseSubscribers=new JSONArray();
			while(resultSet.next()){
				int  data;
				
			/*	PieChartWidData prm=new PieChartWidData();
				data=resultSet.getInt("COUNT(CONCUSERID)");
				prm.setLabel(resultSet.getString("GROUPNAME"));
				prm.setData(data);
				promotionalWiseSubscribers.add(prm);*/
			}
			out.print(promotionalWiseSubscribers);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting promotional offer wise subscribers. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	public ActionForward  getBWList(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Black List White List Data....");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray promotionalWiseSubscribers =null;
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForBWList);
			resultSet = pstmt.executeQuery();
			promotionalWiseSubscribers=new JSONArray();
			while(resultSet.next()){
				int[][] data=new int [1][2];
				data[0][0]=0;
				/*PieChartWidgetData prm=new PieChartWidgetData();
				data[0][1]=resultSet.getInt("COUNT(BWID)");
				prm.setLabel(resultSet.getString("TYPENAME"));
				prm.setData(data);
				promotionalWiseSubscribers.add(prm);*/
			}
			out.print(promotionalWiseSubscribers);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting promotional offer wise subscribers. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	public ActionForward  getBWList2(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Black List White List Data....");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray promotionalWiseSubscribers =null;
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForBWList);
			resultSet = pstmt.executeQuery();
			promotionalWiseSubscribers=new JSONArray();
			int i=1;
			while(resultSet.next()){
				int[][] data=new int [1][2];
				data[0][0]=i;
				i+=2;
				/*PieChartWidgetData prm=new PieChartWidgetData();
				data[0][1]=resultSet.getInt("COUNT(BWID)");
				prm.setLabel(resultSet.getString("TYPENAME"));
				prm.setData(data);
				promotionalWiseSubscribers.add(prm);*/
			}
			out.print(promotionalWiseSubscribers);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting promotional offer wise subscribers. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	
 
	public ActionForward  getPolicyGroupWiseSubscribers(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Policy Group wise subscribers");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		JSONArray policyGroupwiseSubscribers=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForPolicyGroupWiseSubscribers);
			resultSet = pstmt.executeQuery();
			policyGroupwiseSubscribers=new JSONArray();
			while(resultSet.next()){
				int[][] data=new int [1][2];
				/*PieChartWidgetData temp=new PieChartWidgetData();
				data[0][1]=resultSet.getInt("TOTAL");
				temp.setData(data);
				temp.setLabel(resultSet.getString("PACKAGENAME"));
				policyGroupwiseSubscribers.add(temp);*/
			}
			out.print(policyGroupwiseSubscribers);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting Policy group wise subscribers. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	public ActionForward  getActiveSessions(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Active Session Data");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForActiveSessionList);
			resultSet = pstmt.executeQuery();
			JSONArray activeSessions=new JSONArray();
			while(resultSet.next()){
				JSONArray activSessionData=new JSONArray();
				activSessionData.add(resultSet.getString("USER_IDENTITY"));
				activSessionData.add(resultSet.getString("ACCT_SESSION_ID"));
				activSessionData.add(resultSet.getString("NAS_IP_ADDRESS"));
				activSessionData.add(resultSet.getString("USER_NAME"));
				activeSessions.add(activSessionData);
			}
			out.print(activeSessions);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting Active Sessions. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);	
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	public ActionForward  getServerIntance(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Server instances");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForServerInstanceList);
			resultSet = pstmt.executeQuery();
			JSONArray serverInstances=new JSONArray();
			while(resultSet.next()){
				/*ServerInstanceData serverInstanceData=new ServerInstanceData();
				serverInstanceData.setInstanceId(resultSet.getInt("NETSERVERID"));
				serverInstanceData.setInstanceName(resultSet.getString("NAME"));
				serverInstanceData.setInstanceDetail(resultSet.getString("DESCRIPTION"));
				serverInstances.add(serverInstanceData);*/
			}
			out.print(serverInstances);
			System.out.println("Server Instance Details.."+serverInstances);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting Netvertex Server instances. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}	
	
	public ActionForward  getAddOnWiseSubscribers(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "fetching AddOn wise subscribers");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection connection =null;
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		JSONArray addOnWiseSubscribers=new JSONArray();
		try {
			connection =DBConnectionManager.getInstance().getSMDatabaseConection();
			pstmt = connection.prepareStatement(queryForAddOnWiseSubscribers);
			resultSet = pstmt.executeQuery();
			while(resultSet.next()){
				int[][] data=new int [1][2];
			/*	PieChartWidgetData temp=new PieChartWidgetData();
				data[0][1]=resultSet.getInt("COUNT(CONCUSERID)");
				temp.setData(data);
				temp.setLabel(resultSet.getString("GROUPNAME"));
				addOnWiseSubscribers.add(temp);*/
			}
			out.print(addOnWiseSubscribers);
			out.flush();
			out.close();
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting AddOn wise subscribers. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}finally{
			   if(resultSet != null){
				   try{resultSet.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
				   }
			   }
			   if(pstmt != null){
				   try{pstmt.close();}catch(SQLException e){
					   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
				   }
			   }
			   if(connection!=null){
				   try {connection.close();} catch (SQLException e) {
					   Logger.logDebug(MODULE, "Error while closing Connection object. Reason: " + e.getMessage());
				   }
			   }
		   }
		return null;
}
	public ActionForward  getMemoryUsage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "fetching Memory Usage");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray memoryUsageData=new JSONArray();
		try {
			/*for(ServerInformationData temp:RetreiveServerInformationTask.getServerDataQueue()){
				JSONArray tempArray=new JSONArray();
				if(temp.getTime()>=System.currentTimeMillis()-(10*60*1000)){
					tempArray.add(temp.getTime());
					tempArray.add(temp.getMemoryUsage());
					memoryUsageData.add(tempArray);
				}
			}*/
			out.print(memoryUsageData);
			out.flush();
			out.close();
			
		}catch(Exception e){
			Logger.logError(MODULE, "Error in getting memory usage. Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
	}
	
private JSONArray getUserWidgets(String userId,Connection connection){
	Logger.logDebug(MODULE, "Called user widgets method");
    PreparedStatement pstmt=null;
    ResultSet resultSet=null;
   // List<WidgetUserRelData> userWidgetList=null;
    JSONArray userWidgetArray=null;
 	     try{ 
 	       connection =DBConnectionManager.getInstance().getSMDatabaseConection();
		   pstmt=connection.prepareStatement(queryForUserWidgetRelation);
		   pstmt.setString(1,userId); 
		   resultSet=pstmt.executeQuery();
		 /*  userWidgetList=new ArrayList<WidgetUserRelData>();
		   while(resultSet.next()){
			   	WidgetUserRelData widgetUserRelData=new WidgetUserRelData();
			    widgetUserRelData.setWidgetId(resultSet.getInt("WIDGETID"));
			    widgetUserRelData.setWidgetType(resultSet.getString("WIDGETTYPE"));
			    widgetUserRelData.setOrderNo(resultSet.getInt("ORDERNO"));
			    userWidgetList.add(widgetUserRelData);
		   }*/
		//   userWidgetArray=JSONArray.fromObject(userWidgetList); 
		  
	   }catch (DatabaseConnectionException e){
		   Logger.logError(MODULE, "Error while getting Database connection. Reason: " + e.getMessage());
		   Logger.logTrace(MODULE,e);
	   }catch(SQLException e){
		   Logger.logError(MODULE, "Error while getting user widget information. Reason: " + e.getMessage());
		   Logger.logTrace(MODULE,e);
	   }
	   finally{
		   if(resultSet != null){
			   try{resultSet.close();}catch(SQLException e){
				   Logger.logDebug(MODULE, "Error while closing ResultSet object. Reason: " + e.getMessage());
			   }
		   }
		   if(pstmt != null){
			   try{pstmt.close();}catch(SQLException e){
				   Logger.logDebug(MODULE, "Error while closing Prepared Statement object. Reason: " + e.getMessage());
			   }
		   }
	   }
 	  return userWidgetArray;   
}
	
/*	public ActionForward  getCpuUsage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
	    Logger.logDebug(MODULE, "Fetching Cpu Usage");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONArray cpuUsageData=new JSONArray();
		try {
			 for(com.elitecore.netvertexsm.datamanager.dashboard.ServerInformationData temp:RetreiveServerInformationTask.getServerDataQueue()){
				 JSONArray tempArray=new JSONArray();
				 if(temp.getTime()>=System.currentTimeMillis()-(10*60*1000)){
				 tempArray.add(temp.getTime());
				 tempArray.add(temp.getCpuUsage());
				 cpuUsageData.add(tempArray);
				 }
			 }
			 out.print(cpuUsageData);
		     out.flush();
		     out.close();
			
      	}catch(Exception e){
      		Logger.logError(MODULE, "Error in getting Cpu usage Reason: "+e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return null;
}*/

}

 
