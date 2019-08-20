<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<script type="text/javascript">
jQuery(document).ready(function(){
		$('ul#example').collapsibleCheckboxTree('collapsed');
		$("#expand").addClass("light-btn");
		$("#expand").after("&nbsp;");
		$("#collapse").addClass("light-btn");
});
</script>
<%
   String basePath = request.getContextPath();
   List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<%! 
public void drawTree(JspWriter out,AlertTypeData alertData) throws Exception {
	if(alertData.getType().equalsIgnoreCase("L")) {
		out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=alerts value=" + '"' + alertData.getAlertTypeId() + '"' + '/' + '>' + alertData.getName() +"</li>");
	}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=" + '"' + childAlertData.getName() + "value=" + '"' + childAlertData.getAlertTypeId() + '"' + '/' + '>' + "<b>" +childAlertData.getName() + "</b>" + "<ul>");
			}
			drawTree(out,childAlertData);
			if(childAlertData.getType().equalsIgnoreCase("P")){
			out.write("</li></ul>");
			}
		}
	}
}
%>

<script><!--

function validateCreate()
{
	if(isNull(document.forms[0].address.value)){
		document.forms[0].address.focus();
		alert('Address must be specified');
	}else if(document.forms[0].facility.value == 0){
		document.forms[0].facility.focus();
		alert('Facility must be specified');		
     }else{
    	 var verifyAddress=false;
    	 verifyAddress=validateIP(document.forms[0].address.value);
    	 if(verifyAddress==true){
	     document.forms[0].submit();
    	 }else{
    		 alert("Enter Address is not Valid");
    		 document.forms[0].address.focus();
    }
}	
}	
function validateIP(ipaddress){
	var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
		if(ip.test(ipaddress)){
			   return true;
		   }else{
			   return false;
		   }
}
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}
setTitle('SYSLOG Alert Listener');
</script>

<html:form action="/createAlertListener">
	<html:hidden name="createAlertListenerForm" property="typeId" value="" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header">SYSLog Alert Listener</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.address" /> 
													<ec:elitehelp headerBundle="servermgrResources" 
													text="syslogalertlistener.address" header="servermgr.alert.address"/>
												</td>

												<td align="left" class="labeltext" valign="top" colspan="2"
													width="82%"><html:text styleId="address" tabindex="1"
														property="address" size="25" maxlength="100"
														style="width:200px" /> <font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.facility" /> 
													<ec:elitehelp headerBundle="servermgrResources" 
													text="syslogalertlistener.facility" header="servermgr.alert.facility"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%">
													<html:select property="facility" styleId="facility"
														tabindex="2" style="width:130px">
														<html:option value="0">Select</html:option>
														<html:optionsCollection name="createAlertListenerForm"
															property="sysLogNameValuePoolDataList" label="name"
															value="value" />
													</html:select><font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="18%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction" /> 
													<ec:elitehelp headerBundle="servermgrResources" 
													text="syslogalertlistener.repeatedmessagereduction" header="servermgr.alert.repeatedmessagereduction"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select styleId="repeatedMessageReduction" tabindex="3" property="repeatedMessageReduction" size="1" value="true" style="width:130px">
														<html:option value="true">True</html:option>
														<html:option value="false">False</html:option>
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td class="tblheader-bold" align="left" colspan="7">
													<bean:message bundle="servermgrResources" key="servermgr.alert.alertlist" /> 
													<ec:elitehelp headerBundle="servermgrResources" 
													text="syslogalertlistener.list" header="servermgr.alert.alertlist"/>
												</td>
											</tr>
											<tr>
												<td align="left" colspan="3" class="labeltext"><ul
														id="example">
														<% 
                                  	for(int i=0;i<alertTypeDataList.size();i++) {
                                  		AlertTypeData data = alertTypeDataList.get(i);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  			out.println("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=" + '"' + data.getName() + "value=" + '"' + data.getAlertTypeId() + '"' + '/' + '>'+"<b>"+data.getName()+"</b>"+"<ul>");                                  		
                                  		}
                                  		drawTree(out,data);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  		out.println("</ul></li>");
                                  		}                                  	
                                  	}
                                  %>
													</ul></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="4" name="c_btnCreate"
													onclick="validateCreate()" id="c_btnCreate2" value="Create"
													class="light-btn"> <input type="reset" tabindex="5"
													name="c_btnCancel"
													onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'"
													value="Cancel" class="light-btn"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>

