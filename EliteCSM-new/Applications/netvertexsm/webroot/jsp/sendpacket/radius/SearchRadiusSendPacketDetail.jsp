<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.corenetvertex.test.radius.jaxb.RadiusPacketConfigurationData" %>
<%@page import=" com.elitecore.netvertexsm.web.sendpacket.form.SendRadiusPacketForm" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="java.util.*"%>
<%@page import="java.io.File"%>

<%
	SendRadiusPacketForm sendRadiusPacketForm = (SendRadiusPacketForm)request.getAttribute("sendRadiusPacketForm");
	String action = sendRadiusPacketForm.getAction();	
	List sendPacketConfigList = sendRadiusPacketForm.getLstSendPacketData();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	String status;
	
	long pageNo = sendRadiusPacketForm.getPageNumber();
	long totalPages = sendRadiusPacketForm.getTotalPages();
	long totalRecord = sendRadiusPacketForm.getTotalRecords();
	int count = 1;
	
	String strPageNumber = String.valueOf(pageNo);     
	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
	String strName=sendRadiusPacketForm.getName() != null ? sendRadiusPacketForm.getName() :"";
	String paramString="name="+strName;
	
%>
<script>
function validateSearch(){
	document.forms[0].action = "searchRadiusSendPacket.do?netserverid=<%=request.getSession().getAttribute("netserverid") %>";
	document.forms[0].submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}

function removeData(){
    var selectVar = false;
    
    for (i=0; i < document.forms[0].elements.length; i++){
        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[0].elements[i].checked == true){
            	   selectVar = true;
            }
             
        }
    }
    if(selectVar == false){
        alert('At least One Sent Packet Configuration should be selected for removal');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.sendpacket.radius.delete.query"/>';              
        var agree = confirm(msg);
        if(agree){
        	document.forms[0].action = "searchRadiusSendPacket.do?netserverid=<%=request.getSession().getAttribute("netserverid") %>";
       	    document.forms[0].action.value = 'delete';
        	document.forms[0].submit();
        }
    }
}
function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.forms[0].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
}
$(document).ready(function(){
setTitle('<bean:message bundle="servermgrResources" key="sendpacket.header"/>');
});
</script>
<html:form action="/searchRadiusSendPacket" >
<html:hidden name="sendRadiusPacketForm" styleId="action"
		property="action" />
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="5"><bean:message bundle="servermgrResources" key="servermgr.sendpacket.radius.search"/></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" key="servermgr.sendpacket.packetname" />
												
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="name" property="name" size="30"
											maxlength="60" style="width:250px" />
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="middle" width="18%">&nbsp;</td>
									<td class="btns-td" valign="middle" >     
										<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateSearch()">
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<%if(action.equals("list")) {%>
								<tr>
									<td align="left" class="labeltext" colspan="5" valign="top">
										<table cellSpacing="0" cellPadding="0" width="99%" border="0">
											<tr>
												<td class="table-header" width="50%">Sent Radius Packet Configuration List</td>
												<td align="right" class="blue-text" valign="middle" width="50%">
													<% if(totalRecord == 0) { %> <% }else if(pageNo == totalPages+1) { %>
													[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
													<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
													of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
													of <%= totalRecord %> <% } %>

												</td>
											</tr>
											<tr>
												<td></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp; 
													<input type="button" name="Create" value="   Create   "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateRadiusSendPacket.do?action=create'"/>
													<input type="button" name="Delete" onclick="removeData()" value="   Delete   " class="light-btn">
												</td>
											</tr>
											<tr height="2">
												<td></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle" colspan="2">
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0" id="listTable">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="1%"><input type="checkbox" name="toggleAll"
																value="checkbox" onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Sr. No.</td>
															<td align="left" class="tblheader" valign="top"
																width="30%"><bean:message bundle="servermgrResources" key="servermgr.sendpacket.filename"/></td>
														</tr>
														<tr>
															<%
																if(Collectionz.isNullOrEmpty(sendPacketConfigList) == false){
															%>
															<logic:iterate id="obj" name="sendRadiusPacketForm"
																property="lstSendPacketData" type="java.io.File">
																<tr>
																	<td align="center" class="tblfirstcol"><input
																		type="checkbox" name="select"
																		value=<%=obj.getName() %> /></td>
																	<td align="center" class="tblrows" width="5%"><%=((pageNo-1)*pageSize)+count%></td>
																	<td align="left" class="tblrows"><a href="<%=basePath%>/initEditRadiusSendPacket.do?packetName=<%=obj.getName()%>">
																			<%=obj.getName().substring(0, obj.getName().lastIndexOf('.')) %>
																	</a></td>
																</tr>
																<%count ++ ;%>
															</logic:iterate>
															<%}else{ %>
														
														<tr>
															<td align="center" class="tblfirstcol" colspan="8">No
																Records Found.</td>
														</tr>
														<%	}%>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;
												<input type="button" name="Create" value="   Create   "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateRadiusSendPacket.do?action=create'"/>
												 <html:button property="c_btnDelete" onclick="removeData()"
														value="   Delete   " styleClass="light-btn" />
												</td>
												
											</tr>
										</table>
									</td>
								</tr>
								<%} %>
							</table>
						</td>
					</tr>
				</table>
</html:form>
