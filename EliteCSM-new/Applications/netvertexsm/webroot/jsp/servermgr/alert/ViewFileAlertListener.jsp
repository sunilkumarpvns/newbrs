<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %> 
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.netvertexsm.web.servermgr.alert.forms.ViewAlertListenerForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%
	ViewAlertListenerForm viewAlertListenerForm = (ViewAlertListenerForm) request.getAttribute("viewAlertListenerForm");
    //List<String> selectedFloodControl=viewAlertListenerForm.getSelectedFloodControl();
	List<AlertTypeData> alertTypedataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
	 
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
   
    <tr> 
      <td valign="top" align="right" height="15%" > 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.viewalertlistener" /></td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="alertListenerData" property="name"/></td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.listenerType" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="alertListenertypeData" property="typeName"/></td>
          </tr>
           
          <tr> 
            <td class="tbllabelcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.filename" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="fileName"/>&nbsp;</td>
          </tr>
     
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype" /></td>
            <td class="tblcol" width="70%" height="20%">
          
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
            <bean:message bundle="servermgrResources" key="servermgr.alert.timebased" />
            </logic:equal>
          
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:message bundle="servermgrResources" key="servermgr.alert.sizebased" />
            </logic:equal>
          
            &nbsp;</td>
          </tr>
          
          <tr> 
            
            <td class="tbllabelcol" width="30%" height="20%">
             
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
            <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit" />
            </logic:equal>
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs" />
            </logic:equal>
            
            </td>
            
            <td class="tblcol" width="70%" height="20%">
            
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
              
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="3">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.minute" />
                </logic:equal>
                
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="4">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.hour" />
                </logic:equal>
                
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="5">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.daily" />
                </logic:equal>
                
            </logic:equal>
            
            
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:write name="alertFileListenerData" property="rollingUnit" />
            </logic:equal>
            
            &nbsp;</td>
          </tr>
          
          <logic:equal name="alertFileListenerData" property="rollingType" value="2">
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="maxRollingUnit" />&nbsp;</td>
          </tr>
          
          
          </logic:equal>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="compRollingUnit" />&nbsp;</td>
          </tr>
            <tr>
          	<td class="tbllabelcol" width="30%" height="20%" valign="top"><bean:message key="general.createddate"/></td>
          	<td class="tblcol" width="70%" height="20%"><%=EliteUtility.dateToString(alertListenerData.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
          </tr>
          <tr>
          	<td class="tbllabelcol" width="30%" height="20%" valign="top"><bean:message key="general.lastmodifieddate"/></td>
          	<td class="tblcol" width="70%" height="20%"><%=EliteUtility.dateToString(alertListenerData.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
          </tr>
		   <tr>
			<td align="left" class="labeltext" valign="top">&nbsp;</td>
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
		   </tr>
		 		<%if(viewAlertListenerForm!=null){
		 		    List<String> selectAlertTypeList=viewAlertListenerForm.getSelectedAlertsTypeList();
		 		    List<String> selectFloodControl=viewAlertListenerForm.getSelectedFloodControl();
		 			%>	
		  			<tr>
					<td class="tblheader-bold" align="left" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.alert.alertslist"/>&nbsp;</td>
				    </tr>
		
		 			<tr>
						  <td align="left" colspan="3" class="labeltext">
				          <table width="100%" cellspacing="0" cellpadding="0" border="0" id="filealerttable">
						     <thead>
								<tr>
									<td align="center" class="tblheader-bold" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alert.alertname"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.enable"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.floodcontrol"/></td>
								</tr>
						     </thead>
						     <tbody>
                                  <% 
                                  	for(int i=0;i<alertTypedataList.size();i++) {
                                  		AlertTypeData data = alertTypedataList.get(i);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  		  if(selectAlertTypeList.contains(data.getAlertTypeId())){
                       				 	        out.write("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'>"
                       					                   +"<td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td>"
                       					                   +"<td align='center' class='tblrows'><input type='checkbox' name='alertenable' value="+data.getAlertTypeId()+" checked></td>");
                       					    if(selectFloodControl.contains(data.getAlertTypeId())){
                       						    out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+" checked></td></tr>"); 
                       					    }else{
                       					    	 out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+"></td></tr>");	 
                       					    } 
                       					  
                       				}else{
                       				        out.write("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'>"
                       				                  +"<td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td>"
                       				                  +"<td align='center' class='tblrows'><input type='checkbox' name='alertenable' value="+data.getAlertTypeId()+"></td>"
                       				                  +"<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+"></td></tr>"); 
                       				 }	
                                   }
                                  		drawTree(out,data,selectAlertTypeList,selectFloodControl);
                                 }
                                %>
                             </tbody>   
                             </table>
                         </td>
 						</tr>	
 						<%}%> 
			</table>
		</td>
	</tr>
</table>
<script>
$("#filealerttable").treetable({
	expandable : true,
	clickableNodeNames:true,
	indent:15
});
$("tr.dataRow").hover(function() {
	$(this).css("background", "#D9E6F6");
}, function() {
	$(this).css("background", "");
});
$("#filealerttable input[type=checkbox]").attr('disabled',true);

</script>