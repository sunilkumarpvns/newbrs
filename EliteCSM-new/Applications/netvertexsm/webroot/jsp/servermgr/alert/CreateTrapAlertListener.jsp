<%@page import="com.elitecore.netvertexsm.util.constants.AlertListenerConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.core.serverx.alert.TrapVersion"%>
<%@page import="com.elitecore.core.serverx.alert.SnmpRequestType"%>
<link rel="stylesheet" href="jquery/css/jquery.treetable.css" />
<link rel="stylesheet" href="jquery/css/jquery.treetable.theme.default.css" />
<script src="jquery/libs/jquery/jquery.treetable.js"></script>

<script type="text/javascript">
jQuery(document).ready(function(){
		showHideRequestType(document.getElementById("trapVersion").value);
		setTitle('<bean:message bundle="servermgrResources" key="trap.alert.listener" />');
});
</script>
<%
   List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
%>

<%! 
public void drawTree(JspWriter out,AlertTypeData alertData) throws Exception {
	if(alertData.getType().equalsIgnoreCase("L")) {
		out.write("<tr data-tt-id="+alertData.getAlertTypeId()+" data-tt-parent-id="+alertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'>"+alertData.getName()+"</td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+alertData.getAlertTypeId()+"></td><td align='center' class='tblrows'>");
		if(alertData.getFloodControl().equalsIgnoreCase(AlertListenerConstant.FLOODCONTROL_ENABLE)){
			out.println("<input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" checked>");
		}else {
			out.println("<input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" >");
		}
		out.println("</td></tr>");
	}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				 out.write("<tr data-tt-id="+childAlertData.getAlertTypeId()+" data-tt-parent-id="+childAlertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'><b>"+childAlertData.getName()+"</b></td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+childAlertData.getAlertTypeId()+"></td><td align='center' class='tblrows'>");
				 if(childAlertData.getFloodControl().equalsIgnoreCase(AlertListenerConstant.FLOODCONTROL_ENABLE)){
				 	out.println("<input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+" checked >");
				 }else{
					 out.println("<input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+">");
				 }
				 out.println("</td></tr>"); 
			}
			  drawTree(out,childAlertData);
			}
	}
}
%>

<script>

function validateCreate()
{
	if(isNull(document.forms[0].trapServer.value)){
		document.forms[0].trapServer.focus();
		alert('Trap Server must be specified');
	}else if(isNull(document.forms[0].trapVersion.value)){
		document.forms[0].trapVersion.focus();
		alert('Trap Version must be specified');
	}else if(document.forms[0].snmpRequestType.value==2 && isNull(document.forms[0].timeout.value)){
		document.forms[0].timeout.focus();
		alert('Timeout must be specified');
	}else if(document.forms[0].snmpRequestType.value==2 && !isNumber(document.forms[0].timeout.value)){		
		document.forms[0].timeout.focus();
		alert('Invalid Timeout');		
	}else if(document.forms[0].snmpRequestType.value==2 && isNull(document.forms[0].retryCount.value)){
		document.forms[0].retryCount.focus();
		alert('Retry Count must be specified');				
	}else if(document.forms[0].snmpRequestType.value==2 && !isNumber(document.forms[0].retryCount.value)){		
		document.forms[0].retryCount.focus();
		alert('Invalid Retry Count');						
	}else if(isNull(document.forms[0].community.value)){
		document.forms[0].community.focus();
		alert('Community must be specified');		
     }else if(isAlertConfigured()==false){
         alert('Atleast one alert must be selected');
   	} else{
    	 document.forms[0].retryCount.value = jQuery.trim(document.forms[0].retryCount.value);
    	 document.forms[0].timeout.value = jQuery.trim(document.forms[0].timeout.value);    	 
	     document.forms[0].submit();
    }
}	

function isAlertConfigured(){
	var isAlertConfigured=false;
	$("input[name=alertEnable]").each(function(){
                 if(this.checked){
                	 isAlertConfigured=true;
                     return false;
                 }
 		});
		return isAlertConfigured;
}

function isNumber(val){
	nre= /^\d+$/;
	val = jQuery.trim(val);
 
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}
//jQuery(document).ready(function(){
	
		
	function showHideRequestType(val){
	 	if(parseInt(val)==0){		
			//$("#snmpRequestTypeRow").hide();
			document.getElementById("snmpRequestType").value=1;						
			$('#snmpRequestType').prop('disabled','disabled');
			$("#timeoutRow").hide();
			$("#retryCountRow").hide();		 			 				
		}else{
			//$("#snmpRequestTypeRow").show();
			//document.getElementById("snmpRequestType").value="1";
			$('#snmpRequestType').removeAttr('disabled');
			showTimeOutAndRetryCount(document.getElementById("snmpRequestType").value);
		}
	}
	
	function showTimeOutAndRetryCount(val){
		if(parseInt(val)==1){		
			$("#timeoutRow").hide();
			$("#retryCountRow").hide();		 			 		
		}else{		
			$("#timeoutRow").show();
			$("#retryCountRow").show();		 			 				
		}
	}
	
//});
</script>

<html:form action="/createAlertListener">
<html:hidden name="createAlertListenerForm" property="typeId" value=""/>
<table cellpadding="0" cellspacing="0" border="0" width="100%">	
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">
				&nbsp;
			</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5">
							<bean:message bundle="servermgrResources" key="trap.alert.listener" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
														
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">										
										<bean:message bundle="servermgrResources" key="trap.server" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="traplistener.server"/>','Trap Server')"/>										
						
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="trapServer" property="trapServer" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">										
										<bean:message bundle="servermgrResources" key="trap.version" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="traplistener.version"/>','Trap Version')"/>																
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">										
										<html:select  styleId="trapVersion" property="trapVersion" value="0" onchange="showHideRequestType(this.value)" style="width:65px"  >
										   <%for(TrapVersion trapVersion:TrapVersion.values()){%>	
										   		<html:option value="<%=trapVersion.getVal().toString()%>" ><%=trapVersion.getName()%></html:option>
										   <%}%>
					   					</html:select><font color="#FF0000"> *</font>										
									</td>
								</tr>
								<tr id="snmpRequestTypeRow" >
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" key="trap.snmp.request.type" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:select  styleId="snmpRequestType" property="snmpRequestType" value="0" size="1" onchange="showTimeOutAndRetryCount(this.value)" style="width:65px"  >
											<%for(SnmpRequestType snmpRequestType:SnmpRequestType.values()){%>
										   	<html:option value="<%=String.valueOf(snmpRequestType.getId())%>" ><%=snmpRequestType.getType()%></html:option>
										   <%}%>
					   					</html:select><font color="#FF0000"> *</font>
									</td>
								</tr>
						
	 							<tr id="timeoutRow" style="display:none;" >
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" key="trap.timeout" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="timeout" property="timeout" size="10" maxlength="8" />
										<font color="#FF0000"> *</font>
									</td>
								</tr> 
										
								<tr id="retryCountRow" style="display:none;" >
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" key="trap.retrycount" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="retryCount" property="retryCount" size="10" maxlength="2" />
										<font color="#FF0000"> *</font>
									</td>
								</tr> 								
										
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">										
										<bean:message bundle="servermgrResources" key="trap.community" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="traplistener.community"/>','Community')"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="community" property="community" size="25" maxlength="50" />
										<font color="#FF0000"> *</font>
										
									</td>
								</tr>
								
							<tr>
								<td align="left" class="labeltext" valign="top" width="18%">																
									<bean:message bundle="servermgrResources" key="trap.advanced.trap" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="traplistener.advancetrap"/>','Advance Trap')"/>
								</td>
								<td align="left" class="labeltext" valign="top" width="70%">								
								<html:select property="advanceTrap" styleId="advanceTrap" value="true">								
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>								
								</html:select>	
								</td>							
							</tr>
								
   							 <tr>
				   		            <td align="left" class="labeltext" valign="top" >&nbsp;</td>
						           <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
				        	   </tr>
		    			   <tr> 
							  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
							  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
						   </tr>
							<tr>
								<td class="tblheader-bold" align="left" colspan="7">								
									<bean:message bundle="servermgrResources" key="trap.alerts.list" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="traplistener.list"/>','Alert List')"/>										
								</td>
							</tr>
						  <tr>
						     <td align="left" colspan="3" class="labeltext">
						     <table width="50%" cellspacing="0" cellpadding="0" border="0" id="alerttable">
						     <thead>
								<tr>
									<td align="center" class="tblheader-bold" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alert.alertname"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.enable"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.floodcontrol"/></td>
								</tr>
						     </thead>
						     <tbody>
                                <%
                                  	for(int i=0;i<alertTypeDataList.size();i++) {
                                		AlertTypeData data = alertTypeDataList.get(i);
                                         		if(data.getType().equalsIgnoreCase("P")){
                                         			out.println("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+data.getAlertTypeId()+"></td><td align='center' class='tblrows'>&nbsp;");
                                         			if(data.getFloodControl().equalsIgnoreCase(AlertListenerConstant.FLOODCONTROL_ENABLE)){		
                                         				out.println("<input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+" checked>");
                                         			}else{
                                         				out.println("<input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+">");
                                         			}
                                         			 out.println("</td></tr>");                                  		
                                         		}
                                         	    drawTree(out,data);
                                     	}
                                  %>
                             </tbody>  
                            </table>
						    </td>
						   </tr>
								<tr>
									<td class="btns-td" valign="middle">
										&nbsp;
									</td>
									<td class="btns-td" valign="middle" colspan="2">
										<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value="Create" class="light-btn">
									 	<input type="reset" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'" value="Cancel" class="light-btn"> 
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>		
</html:form>
<script>
$("#alerttable").treetable({
	expandable : true,
	clickableNodeNames:true,
	indent:15
});
$("tr.dataRow").hover(function() {
	$(this).css("background", "#D9E6F6");
}, function() {
	$(this).css("background", "");
});
$("input[name=alertEnable]").click(function(){
          selectchildcheckbox($(this),this.checked,1);
          //selectparentcheckbox($(this),this.checked,1);
          if(!this.checked){
            	$(this).parent().parent().children("td").eq(2).children().eq(0).prop('checked',false);
            	selectchildcheckbox($(this),false,2);
            }
 });

$("input[name=floodcontrolenable]").click(function(){
    selectchildcheckbox($(this),this.checked,2);
        if(this.checked){
    $(this).parent().parent().children("td").eq(1).children().eq(0).prop('checked',true);
    selectchildcheckbox($(this),true,1);
        }
});     
function selectchildcheckbox(currentSelectedBox,isChecked,columnno){
	 var parentrowid=currentSelectedBox.val();
	 var childcheckbox;
      currentSelectedBox.closest("tr").siblings("tr[data-tt-parent-id="+parentrowid+"]").each(function(){
    	  childcheckbox=$(this).children("td").eq(columnno).children().eq(0);
                if(isChecked){        
                  childcheckbox.prop('checked',true);
                 }else{
                  childcheckbox.prop('checked',false);
                 }
                selectchildcheckbox(childcheckbox,childcheckbox.prop('checked'),columnno);
    });
}

function selectparentcheckbox(currentrow,isChecked,columnno){
	var currentrowid=currentrow.parent().parent().prop('data-tt-parent-id');
	var parentCheckBox;
	currentrow.parent().parent().siblings("tr[data-tt-id="+currentrowid+"]").each(function(){
		      parentCheckBox=$(this).children("td").eq(columnno).children().eq(0);
	          if(isChecked){
	             parentCheckBox.prop('checked',true);
	           }/* else{
	        	   parentCheckBox.prop('checked',false);
	           } */
	          selectparentcheckbox(parentCheckBox,parentCheckBox.prop('checked'),columnno);
	});
}

	
</script>
