
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails"%>




<%@ page import="org.apache.struts.util.MessageResources"%>

<%
            String localBasePath = request.getContextPath();
            EliteNetServerDetails liveDetailsBean = (EliteNetServerDetails) request.getAttribute("eliteLiveServerDetails");
            Long netServerId = (Long) request.getAttribute("netServerId");
            String errorCode = (String) request.getAttribute("errorCode");
%>
<bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" />
<bean:define id="netStartStopForm" name="netServerStartStopForm" scope="request" type="com.elitecore.netvertexsm.web.servermgr.server.form.NetServerStartStopForm" />

	<script>
		      <logic:notEqual name="netStartStopForm" property="netSeverStatus" value="false">
			     <logic:equal name="netStartStopForm" property="netServerRestartStatus" value="true">
  					ajaxreRefreshServerState();
		   		  </logic:equal>
			  </logic:notEqual>
	
	function ajaxreRefreshServerState()
	{
	window.setTimeout('monitorRestartServer()',5000);
	
	}
	
    function startServer(){    
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.managenetserverlivedetails.startservre"/>';        
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=request.getContextPath()%>/startNetServerInstance.do?netServerId=<%=netServerId%>';  
        }
    }
    
    function restartServer(){
       var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.server.restart"/>';        
        var agree = confirm(msg);
        if(agree){
           // javascript:location.href='<%=request.getContextPath()%>+/restartNetServerInstance.do?netServerId=<%=netServerId%>';
           
           var url = '<%=request.getContextPath()%>/ajaxRestartNetServerInstance.do?netServerId=<%=netServerId%>';
           makePostRequest(url);
        }
    }
    
    function monitorRestartServer(){
   			var url = '<%=request.getContextPath()%>/ajaxMonitorRestartNetServerInstance.do?netServerId=<%=netServerId%>';
            makePostRequestForRefreshServerState(url);
    }
    
     function stopServer(){    
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.viewnetserverlivedetailsjsp.stopserver"/>';        
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=request.getContextPath()%>/signalServerShutDown.do?netServerId=<%=netServerId%>';
        }
    }
    
    
/**
 * getXMLHttpRequestOb() for getting 
 * XMLHttpRequest object for using ajax
 *  
 */
 
 var  xmlHttp1 = false;
  var  xmlHttp2 = false;
 
 function getXMLHttpRequestOb() {
 	
  try
    {
    // Firefox, Opera 8.0+, Safari
     return  new XMLHttpRequest();
    }
  catch (e)
    {
    // Internet Explorer
    try
      {
       return new ActiveXObject("Msxml2.XMLHTTP");
      }
    catch (e)
      {
      try
        {
         return new ActiveXObject("Microsoft.XMLHTTP");
        }
      catch (e)
        {
        alert("Your browser does not support AJAX!");
        return false;
        }
      }
    }
  }
 	
 

 

/**
 * sendPostRequest
 * @param {url} url for request
 */
 
	function makePostRequestForRefreshServerState(url) {
		
     	xmlHttp2 = getXMLHttpRequestOb();
        xmlHttp2.open('POST',url, false);
        xmlHttp2.onreadystatechange = xmlHttp2Results;
     	xmlHttp2.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      	xmlHttp2.setRequestHeader("Connection","close");
        xmlHttp2.send(null);
   } 
  
 function makePostRequest(url) {
		
     	xmlHttp1 = getXMLHttpRequestOb();
        xmlHttp1.open('POST',url, false);
        xmlHttp1.onreadystatechange = xmlHttp1Results;
     	xmlHttp1.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      	xmlHttp1.setRequestHeader("Connection","close");
        xmlHttp1.send(null);
        
   }
 
 function xmlHttp1Results() {
      if (xmlHttp1.readyState == 4){
   	   if (xmlHttp1.status == 200) {
            handleXmlResponse();
            } else {
            alert('There was a problem with the request.');
            document.getElementById('notediv').innerHTML = 'Note :  Unable to Connect ServerManager. Please Verify ServerManager logs.....';
      		document.getElementById('c_btnRestart').disabled = false;
      		document.getElementById('c_btnShutdown').disabled = false;
            return;
          }
      }
}
function xmlHttp2Results() {
 	
      if (xmlHttp2.readyState == 4){
   	   if (xmlHttp2.status == 200) {
            handleXmlResponseForRefreshServerState();
            } else {
            alert('There was a problem with the request.');
            document.getElementById('notediv').innerHTML = 'Note :  Unable to Connect ServerManager. Please Verify ServerManager logs.....';
      		document.getElementById('c_btnRestart').disabled = false;
      		document.getElementById('c_btnShutdown').disabled = false;
            return;
         }
      }
}



		function handleXmlResponseForRefreshServerState() {
		
		 		
		 		 var response1 = xmlHttp2.responseXML.getElementsByTagName('monitorresponse');
      		     var exid1 = response1[0].childNodes[0].nodeValue;
      		   
      		     if(exid1 == 3)
      		     {
      		      document.getElementById('notediv').innerHTML ='Note : Server Restart Process is Running.....&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=request.getContextPath()%>/images/loading.gif"></img>';
      		      ajaxreRefreshServerState();
      		     }
      		     else if(exid1 == 0)
      		     {
      		     document.getElementById('notediv').innerHTML = 'Note :  Server Restart operation failed.....  Please Verify Server logs.....';
    		     document.getElementById('c_btnRestart').disabled = false;
      		     document.getElementById('c_btnShutdown').disabled = false;
      		     }
      		     else if(exid1 == 1)
      		     {
      		     document.getElementById('notediv').innerHTML = 'Note :  Server Restarted Successfully..... Please Verify Server logs.....';
      		     document.getElementById('c_btnRestart').disabled =  false;
      		     document.getElementById('c_btnShutdown').disabled = false;
      		     
      			 }
      		     else if(exid1 == -1)
      		     {
      		     document.getElementById('notediv').innerHTML = 'Note :  Unable to find Server infomation. Please Verify Server logs.....';
      		     document.getElementById('c_btnRestart').disabled = false;
      		     document.getElementById('c_btnShutdown').disabled = false;
      		    
      		     }
      		     else
      		     {
      		      document.getElementById('notediv').innerHTML = 'Note :  Unable to find Server infomation. Please Verify Server logs.....';
    		      document.getElementById('c_btnRestart').disabled = false;
      		      document.getElementById('c_btnShutdown').disabled = false;
      		   
       		     }
 		}
 

			function handleXmlResponse() {
	 			 var processingmsg ='Note : Server Restart Process is Running.....&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=request.getContextPath()%>/images/loading.gif">';				
      		 	 document.getElementById('notediv').innerHTML =processingmsg;
      		 	 document.getElementById('c_btnRestart').disabled = true;
      		     document.getElementById('c_btnShutdown').disabled = true;
      		     
      		     var response = xmlHttp1.responseXML.getElementsByTagName('response');
      		     var exid = response[0].childNodes[0].nodeValue;
      		     if(exid == -1)
      		     {
      		     document.getElementById('notediv').innerHTML =  'Note :  Unable to Start Restart Process. Please Verify Server Connection Details for Telnet Connection.';
      		      document.getElementById('c_btnRestart').disabled = false;
      		      document.getElementById('c_btnShutdown').disabled = false;
      		     }else if(exid == -2)
      		     {
      		     document.getElementById('notediv').innerHTML =  'Note :  Server Restart operation Already Running...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=request.getContextPath()%>/images/loading.gif"></img>';
      		     ajaxreRefreshServerState();
      		     return;
      		     }
      		     else if(exid == 3)
      		     {
      		     document.getElementById('notediv').innerHTML = 'Note :  Server Restart operation Successfully Started...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=request.getContextPath()%>/images/loading.gif"></img>';
      		     ajaxreRefreshServerState();
      		     return;
      		     }else if(exid == -3)
      		     {
      		     document.getElementById('notediv').innerHTML = 'Server Restart operation failed..... Please Verify Server logs.....';
      		     }
      		     else
      		     {
      		     document.getElementById('notediv').innerHTML = 'Note :  Unable to find Server infomation. Please Verify logs.....';
      		     }
      		     
 }
    
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" align="right">
			<table width="97%" border="0" cellspacing="0" cellpadding="0"
				height="15%">
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>

					<logic:equal name="netServerStartStopForm" property="netSeverStatus" value="false">
						<td class="tblheader-bold" colspan="2">
							<bean:message bundle="servermgrResources" key="servermgr.startserverdetails" />
						</td>
					</logic:equal>

					<logic:notEqual name="netStartStopForm" property="netSeverStatus"
						value="false">

						<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="false">	
						   <td class="tblheader-bold" colspan="2">
								<bean:message bundle="servermgrResources" key="servermgr.stopserverdetails" />
							</td>
						</logic:equal>

						<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="true">
							<td class="tblheader-bold" colspan="2">
								<bean:message bundle="servermgrResources" key="servermgr.Restartserverstatus" />
							</td>
						</logic:equal>
					</logic:notEqual>

				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="2">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="servermgrResources" key="servermgr.admininterfaceip" />
					</td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<bean:write name="netServerInstanceBean" property="adminHost" />
					</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="servermgrResources" key="servermgr.admininterfaceport" />
					</td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<bean:write name="netServerInstanceBean" property="adminPort" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>


	<tr>
		<td valign="top" align="right">
			<table width="97%" border="0" cellspacing="0" cellpadding="0"height="15%" align="right">
				<tr>
				 <logic:equal name="netStartStopForm" property="configInSyncState" value="false">
							<td id="notes" class="small-text-grey" height="25%">
								<div id="syncnotediv" style="hight: 25%">
									<font color="#FF1F00">Note: - Server Configurations Found Unsynchronized. To Reflect These Changes on Server, Please Synchronize Server Configurations.</font>
								<div>
							</td>
					</logic:equal>
				</tr>
				<tr class="small-gap"><td>&nbsp;</td></tr>
				
				<tr>
				<logic:equal name="netServerStartStopForm" property="netSeverStatus" value="false">
						<td id="notes" class="small-text-grey" height="25%">
							<div id="notediv" style="hight: 25%">
								Note:- This operation will start the Server using Connection Details supplied in Admin Interface.
						    <div>
						</td>
					</logic:equal>

					<logic:notEqual name="netStartStopForm" property="netSeverStatus" value="false">
						<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="false">
							<td id="notes" class="small-text-grey" height="25%">
								<div id="notediv" style="hight: 25%">
									Note:- Restart Operation will Stop Sever, and Start Server using Connection Details supplied in Admin Interface.
								<div>
							</td>
						</logic:equal>
						<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="true">
							<td id="notes" class="small-text-grey" height="25%">
								<div id="notediv" style="hight: 25%">
									Note:- Server Restart Process is Running.....&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=request.getContextPath()%>/images/loading.gif"></img>
								<div>
							</td>
						</logic:equal>
					</logic:notEqual>
					
					

				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<td class="btns-td" valign="middle">

			<logic:equal name="netServerStartStopForm" property="netSeverStatus"
				value="false">
				<input type="button" name="c_btnStartup" onclick="startServer()" id="c_btnStart" value="Start Server" class="light-btn" />
			</logic:equal>

			<logic:notEqual name="netStartStopForm" property="netSeverStatus"
				value="false">

				<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="false">
					<input type="button" name="c_btnShutdown" onclick="stopServer()" id="c_btnShutdown" value="Stop Server" class="light-btn">
					<input type="button" name="c_btnRestart" onclick="restartServer()" id="c_btnRestart" value="Restart Server" class="light-btn">

				</logic:equal>

				<logic:equal name="netStartStopForm" property="netServerRestartStatus" value="true">
					<input type="button" name="c_btnShutdown" onclick="stopServer()" id="c_btnShutdown" value="Stop Server" class="light-btn" disabled="disabled">
					<input type="button" name="c_btnRestart" onclick="restartServer()" id="c_btnRestart" value="Restart Server" class="light-btn" disabled="disabled">
				</logic:equal>

			</logic:notEqual>
	<input type="reset" name="c_btnDeletePolicy" value="Cancel"	class="light-btn" onclick="javascript:location.href='<%=request.getContextPath()%>/viewNetServerInstance.do?netserverid=<%=netServerId%>'">
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
