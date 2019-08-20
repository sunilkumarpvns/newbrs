<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.web.servermgr.server.forms.LiveDictionaryData"%>
<%@page import="java.util.Map"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData"%>




<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.ManageNetServerDictionaryForm"%>
<% 
	String localBasePath = request.getContextPath();
	INetServerInstanceData localNetServerInstanceData = (INetServerInstanceData)request.getAttribute("netServerInstanceData");
%>

<%
	ManageNetServerDictionaryForm manageNetServerDictionaryForm = (ManageNetServerDictionaryForm)request.getAttribute("manageNetServerDictionaryForm");
	Map dictionaryMap = manageNetServerDictionaryForm.getMapDictionary();
	int iIndex = 0;
%>

<script>
function checkDictionarySelection(){

	var flag = false;
	try{
		var formElem = document.forms[0];
		
		 for (i=0; i<formElem.elements.length; i++){
		   		if(formElem.elements[i].type=="checkbox" && formElem.elements[i].checked){
					flag = true;
					break;
				}
	     }
	     
	}catch(err){
		alert("Error description: " + err.description);
	}
	
	if(flag==false)
		alert("Dictionary Needs To Be Selected To Perform Delete Operation.")
	else{
		var r=confirm("This Operation Will Delete the Selected Dictionaries. Do you Want To Continue?");
			if (r==true)
  			{
  				document.forms[0].submit();		
  			}
	}

}
function validateCreate(){

		document.forms[0].submit();
	
}
function popup(mylink, windowname)
{
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	windowname= windowname.substring(0,windowname.length - 4);					
	var winDic = window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
	winDic.focus();
	
	return false;
}
</script>

<html:form action="/manageRadiusDictionary">
	<html:hidden name="manageNetServerDictionaryForm" styleId=""
		property="action" value="delete" />
	<html:hidden name="manageNetServerDictionaryForm" styleId=""
		property="netServerId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<logic:equal name="manageNetServerDictionaryForm" property="errorCode"
			value="0">

			<tr>
				<td>&nbsp;</td>
			</tr>



			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						height="15%">


						<tr>
							<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="2%" class="tblheader-bold" colspan="3"><bean:message
												bundle="servermgrResources"
												key="servermgr.view.dictionarylist" /></td>
									</tr>
									<%
	    				if(dictionaryMap != null && dictionaryMap.size() > 0){
			         	  Iterator it = dictionaryMap.keySet().iterator();
						  
			         	  while(it.hasNext()) {
			         	    String key = (String ) it.next();
			         	    List<LiveDictionaryData> dictionaryList = (List<LiveDictionaryData>)dictionaryMap.get(key);
			         	    %>
									<tr>
										<td align="left" class="tblfirstcol" valign="top" colspan="3">
											<b><%=key%></b>
										</td>
									</tr>
									<tr>
										<td align="left" class="tblheader" valign="top" width="1%"><bean:message
												bundle="servermgrResources" key="servermgr.select" /></td>
										<td align="left" class="tblheader" valign="top" width="1%"><bean:message
												bundle="servermgrResources" key="servermgr.serialnumber" /></td>
										<td align="left" class="tblheader" valign="top" width="90%"><bean:message
												bundle="servermgrResources" key="servermgr.name" /></td>
									</tr>
									<%		if(dictionaryList != null && dictionaryList.size() > 0){
			         	    		int size= dictionaryList.size();
				         	%>
									<logic:iterate id="liveDictionaryData"
										collection="<%=(List)dictionaryMap.get(key)%>"
										length="<%=Integer.toString(dictionaryList.size())%>"
										type="com.elitecore.elitesm.web.servermgr.server.forms.LiveDictionaryData">

										<tr>
											<td align="left" class="tblfirstcol" valign="top"><input
												type="checkbox" name="<%=key%>" id="<%=(iIndex+1) %>"
												value="<%=liveDictionaryData.getName()%>" /></td>
											<td align="left" class="tblrows" valign="top"><%=(iIndex+1) %></td>
											<td align="left" class="tblcol" valign="top"><a
												href='<%=localBasePath%>/initShowNetServerDictionary.do?fileName=<%=liveDictionaryData.getName()%>&fileGroup=<%=key%>&netServerId=<%=localNetServerInstanceData.getNetServerId()%>'
												onClick="return popup(this, '<%=liveDictionaryData.getName()%>');"><bean:write
														name="liveDictionaryData" property="name" /></a></td>
										</tr>
										<%iIndex+=1; %>
									</logic:iterate>
									<%	    			
						}else {
						%>
									<tr>
										<td align="center" class="tblfirstcol" colspan="3"><bean:message
												bundle="servermgrResources" key="servermgr.norecordsfound" /></td>
									</tr>
									<%    	   }
							   	}
							 }
						%>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>

								</table>
							</td>
						</tr>
						<tr>
							<td valign="middle" align="left"><input type="button"
								name="c_btnDelete" id="c_btnDelete" value=" Delete "
								onclick="checkDictionarySelection()" class="light-btn">
								<input type="button" name="c_btnUpload" id="c_btnUpload"
								onclick="javascript:location.href='<%=localBasePath%>/uploadRadiusDictionary.do?netServerId=<bean:write name="manageNetServerDictionaryForm" property="netServerId"/>'"
								value="Upload Dictionary" class="light-btn"></td>
						</tr>
					</table>
				</td>
			</tr>
		</logic:equal>
		<logic:notEqual name="manageNetServerDictionaryForm"
			property="errorCode" value="0">
			<tr>
				<td>&nbsp;</td>
			</tr>

			<tr>
				<td valign="top" align="right">
					<table width="97%" border="0" cellspacing="0" cellpadding="0"
						height="15%">
						<tr>
							<td class="blue-text-bold"><bean:message
									bundle="servermgrResources" key="servermgr.connectionfailure" />
								<br> <bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceip" /> : <bean:write
									name="netServerInstanceData" property="adminHost" /> <br>
								<bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceport" /> : <bean:write
									name="netServerInstanceData" property="adminPort" /> &nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td>&nbsp;</td>
			</tr>
		</logic:notEqual>

	</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
