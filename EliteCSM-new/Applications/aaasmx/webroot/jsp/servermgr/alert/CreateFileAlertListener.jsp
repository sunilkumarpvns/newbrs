<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>

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
				out.write("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=" + '"' + childAlertData.getName() + "value=" + '"' + childAlertData.getAlertTypeId() + '"' + '/' + '>' + "<b>" + childAlertData.getName() + "</b>" + "<ul>");
			}
			drawTree(out,childAlertData);
			if(childAlertData.getType().equalsIgnoreCase("P")){
			out.write("</li></ul>");
			}
		}
	}
}
%>



<script>
function validateCreate()
{	
	 if(isNull(document.forms[0].fileName.value)){
		document.forms[0].fileName.focus();
		alert('File name must be specified');
     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].rollingUnitSizedBased.value)){
  		 alert('Rolling Unit must be Numeric.');
         
     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].maxRollingUnitSizedBased.value)){

    	 alert('Max Rolled Unit must be Numeric.');
         
     }else{

         if(document.forms[0].rollingType[0].checked){

             document.forms[0].rollingUnit.value=document.forms[0].rollingUnitTimeBased.value;
             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitTimeBased.value;  
         
         }else if(document.forms[0].rollingType[1].checked){
             
        	 document.forms[0].rollingUnit.value=document.forms[0].rollingUnitSizedBased.value;
             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitSizedBased.value;

         }         
	     document.forms[0].submit();
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

function initialized(){

	   document.getElementById('sizedBasedRollingUnit').style.display='none';
	   document.getElementById('sizedBasedMaxRolledUnit').style.display='none';
	   document.getElementById('timeBasedRollingUnit').style.display='';
	   document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	   document.forms[0].rollingType[0].checked=true;	   

	   }
function setTimeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='none';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='none';
	document.getElementById('timeBasedRollingUnit').style.display='';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}

function setSizeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='';
	document.getElementById('timeBasedRollingUnit').style.display='none';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}
setTitle('File Alert Listener');
</script>

<html:form action="/createAlertListener">
	<html:hidden name="createAlertListenerForm" property="typeId" value="" />
	<html:hidden name="createAlertListenerForm" property="rollingUnit" />
	<html:hidden name="createAlertListenerForm" property="maxRollingUnit" />

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
									<td class="table-header">File Alert Listener</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">

											<tr>
												<td align="left" class="captiontext" valign="top"
													width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.filename" />
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.filename" header="servermgr.alert.filename"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2"
													width="82%"><html:text styleId="fileName" tabindex="1"
														property="fileName" size="25" maxlength="100"
														style="width:250px" /> <font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"
													width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype" />
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.rolling" header="servermgr.alert.rollingtype"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:radio
														styleId="rollingType" tabindex="2" property="rollingType"
														onclick="setTimeBased();" value="1" />Time-Based <html:radio
														styleId="rollingType" tabindex="3" property="rollingType"
														onclick="setSizeBased();" value="2" />Size-Based</td>
											</tr>

											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>



											<!-- Size Based selected ,display below portion -->
											<tr id="sizedBasedRollingUnit">
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs" />
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.rollingunit" header="servermgr.alert.rollingunitkbs"/> 
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="rollingUnitSizedBased" tabindex="4"
														property="rollingUnitSizedBased" size="25" maxlength="100" />
													<font color="#FF0000"> *</font></td>
											</tr>
											
											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>
											<tr id="sizedBasedMaxRolledUnit">
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit" />
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.maxrollunit" header="servermgr.alert.maxrollingunit"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="maxRollingUnitSizedBased" tabindex="5"
														property="maxRollingUnitSizedBased" size="25"
														maxlength="100" /> <font color="#FF0000"> *</font></td>
											</tr>
											<!-- end -->
											<!-- Time Based selected ,display below portion -->
											<tr id="timeBasedRollingUnit">
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit"/>
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.rollingunit" header="servermgr.alert.rollingunit"/>
												</td>

												<td align="left" class="labeltext" valign="top"><html:select
														styleId="rollingUnitTimeBased" tabindex="6"
														property="rollingUnitTimeBased" value="5" size="1"
														style="width:130px">
														<html:option value="3">Minute</html:option>
														<html:option value="4">Hour</html:option>
														<html:option value="5">Daily</html:option>
													</html:select><font color="#FF0000"> *</font></td>
											</tr>
											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>
											<tr id="timeBasedMaxRolledUnit">
												<td align="left" class="captiontext" valign="top"
													width="25%">Max Rolled Unit</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="maxRollingUnitTimeBased" tabindex="7"
														property="maxRollingUnitTimeBased" size="25"
														maxlength="100" value="0" /> <font color="#FF0000">
														*</font></td>
											</tr>


											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit"/>
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.compressrollunit" header="servermgr.alert.comprolledunit"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:select
														styleId="compRollingUnit" tabindex="8"
														property="compRollingUnit" size="1" value="false"
														style="width:130px">
														<html:option value="true">True</html:option>
														<html:option value="false">False</html:option>
													</html:select><font color="#FF0000"> *</font></td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction" /> 
													<ec:elitehelp headerBundle="servermgrResources" 
													text="filealertlistener.repeatedmessagereduction" header="servermgr.alert.repeatedmessagereduction"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select styleId="repeatedMessageReduction" tabindex="9" property="repeatedMessageReduction" size="1" value="true" style="width:130px">
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
													text="filealertlistener.alertlist" header="servermgr.alert.alertlist"/>
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
													type="button" name="c_btnCreate" tabindex="10"
													onclick="validateCreate()" id="c_btnCreate2" value="Create"
													class="light-btn"> <input type="reset"
													name="c_btnCancel" tabindex="11"
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
<script>
initialized();
</script>


