<%@ page import="java.util.List" %>





<%
	String localBasePath=request.getContextPath();
	List<String>lstGraphList = (List<String>)request.getAttribute("lstGraphList");
	List<String>lstTitleList = (List<String>)request.getAttribute("lstTitleList");
	String netServerId = (String)request.getAttribute("netserverid");
	
%>

<script>
	var defaultId = 0; 
	var timerID = null;
	var timerRunning = false;
	var refreshTime = 30;
	var xmlHttp;
function ajaxFunction()
{  
      try
        {    
            // Firefox, Opera 8.0+, Safari    
            xmlHttp=new XMLHttpRequest();    
        }
        catch (e)
        {    
              // Internet Explorer    
              try
              {
                    xmlHttp=new ActiveXObject("Msxml2.XMLHTTP"); 
              }
              catch (e)
              {      
                  try
                  {
                          xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");        
                  }
                  catch (e)
                  {        
                      return false;        
                  }      
               }    
         }
	  xmlHttp.onreadystatechange=function()
      {
        if(xmlHttp.readyState==4)
        {	
			 refreshTime = document.viewDatasourceSummaryForm.refreshTime.value;
             renderImage();
        }
      }                                                                         
	  xmlHttp.open("POST",'<%=localBasePath%>/viewDatasourceSummary.do?netserverid=<%=netServerId%>&status='+defaultId,true);
      xmlHttp.send(null);  
}

function renderImage()
{
	<% for(int i=0;i<lstGraphList.size();i++) { %>  
			var imageElement = document.getElementById('graphImage<%=i%>');
			if(imageElement != null){
				imageElement.src = "<%=localBasePath%>/servlet/ImageFormationServlet?img=<%=(lstGraphList.get(i)).replace("\\","/\\")%>";
			}
	<% } %>
	timerID = window.setTimeout('ajaxFunction()', refreshTime*1000);
	timerRunning = true;
}

function refreshImage() 
{	
	defaultId = document.viewDatasourceSummaryForm.status.value;   
	if(timerID == null)
	{
		timerID = window.setTimeout('ajaxFunction()',100);
	}else
	{
		timerID = window.clearTimeout(timerID); 
		timerID = window.setTimeout('ajaxFunction()',100);
	}
}
function refreshInstantImage()
{
	defaultId = document.viewDatasourceSummaryForm.status.value;
	if(timerID == null)
	{
		timerID = window.setTimeout('ajaxFunction()',100);
	}else
	{
		timerID = window.clearTimeout(timerID)
		timerID = window.setTimeout('ajaxFunction()',100);
	}
}
	document.onload = ajaxFunction();
</script>

<html:form action="/viewDatasourceSummary">
<table cellpadding="0" cellspacing="0" border="0" width="100%" >    
    <tr>
      <td class="small-gap" >&nbsp;</td>
    </tr>	     
    <tr>    
	  <td valign="top" align="right" > 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
    	     <tr>
    	        <td align="right" class="labeltext" valign="top" width="20%"><bean:message bundle="servermgrResources" key="servermgr.graphchartdetail"/></td>
    	        
		    	<td align="left" valign="top" width="20%">
		    		&nbsp;
			    	<html:select name="viewDatasourceSummaryForm" styleId="status" property="status" onchange="refreshImage()">
						<html:option value="0"><bean:message bundle="servermgrResources" key="servermgr.dailygraphdetails"/></html:option>
			    		<html:option value="1"><bean:message bundle="servermgrResources" key="servermgr.weeklygraphdetails"/></html:option>
			    		<html:option value="2"><bean:message bundle="servermgrResources" key="servermgr.monthlygraphdetails"/></html:option>
			    		<html:option value="3"><bean:message bundle="servermgrResources" key="servermgr.yearlygraphdetails"/></html:option>
			    	</html:select> 
		    	</td>
		    	<td align="right" valign="top" width="20%">
		    		 <input type="button" value="Instant Refresh" onclick="refreshInstantImage();"/>
		    	</td>
		    	<td align="right" class="labeltext" valign="top" width="20%"><bean:message bundle="servermgrResources" key="servermgr.graphrefresh"/></td>
		    	
		    	<td align="left" valign="top" width="20%">
		    		&nbsp;

				 	<html:select name="viewDatasourceSummaryForm" styleId="refreshTime" property="refreshTime" onchange="refreshImage()" value="30">
			    		<% for(int i=1;i<=30;i++) { %>
						<html:option value="<%=""+i%>"><bean:message bundle="servermgrResources" key="<%="servermgr.dailygraphdetails"+i%>" /></html:option>
			    		<%} %>
			    	</html:select>  
			    	
			    	<%-- <select name="refreshTime">
			    		<% for(int i=1;i<=29;i++) { %>
						<option value="<%=""+i%>"><bean:message bundle="servermgrResources" key="<%="servermgr.dailygraphdetails"+i%>" /></option>
			    		<%} %>
						<option value="30" selected="true"><bean:message bundle="servermgrResources" key="servermgr.dailygraphdetails30"/></option>
			    	</select>    --%>
		    	</td>
		    </tr>
		    <tr><td>&nbsp;</td></tr>    
			<% for(int i=0;i<lstGraphList.size();i++) { %>  
	            <tr> 
				  <td class="tblheader-bold" colspan="5" height="20%"><%=lstTitleList.get(i)%>
	            </tr>
	            <tr>
			      <td class="small-gap" colspan="5" >&nbsp;</td>
			    </tr>
	      		<tr>
		      	  <td valign="top" align="center" colspan="5">
						<img name="graphImage<%=i%>" src="<%=localBasePath%>/servlet/ImageFormationServlet?img=<%=lstGraphList.get(i)%>" > 
		      	  </td>
	      		</tr>
	      		<tr>
	      			<td colspan="4">&nbsp;</td>
	      		</tr>
		     <% } %>  
      	</table>
      </td>
    </tr>
</table>
</html:form>
