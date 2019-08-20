<%@page import="com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.Version"%>
<%
    String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
    String nasIPAddress=(String)request.getParameter("nasIPAddress");
%>
<script src="<%=request.getContextPath()%>/js/rm/ippool/ippool.js?rev=1<%=Version.getSVNRevision()%>>"></script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message bundle="ippoolResources" key="ippool.header"/>');
	
	$(document).ready(function(){
		/* bind click event of delete image */
		$('img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		var chkBoxVal='<%=statusVal%>';
		if(chkBoxVal=='Hide'){
			document.getElementById("activeStatus").checked=false;
		}else{
			document.getElementById("activeStatus").checked=true;
		}
		
		if (($('#name').is(':empty'))){
			verifyName();
		}
	});
	
	var isValidName;
	var count = 0;
	
	function verifyName() {
		if(count == 0 ){
			count++;
			return false;
		}
		id = "verifyNameDiv";
		$('<div/>', {
			id : id
		}).appendTo($("#name").parent());
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.IPPOOL%>',$("#name").val(),'create','',id);
	}
	
	
	
</script>

<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<html:form action="createIPPool" styleId="ipPoolForm" enctype="multipart/form-data">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="3">
									<bean:message bundle="ippoolResources" key="ippool.createippool" />
								</td>
							</tr>
							
							<tr>
								<td class="tblheader-bold" colspan="3">
										<bean:message bundle="ippoolResources" key="ippool.basic.details" /> 
									</td>
								</tr>
							<tr><td class="small-gap" colspan="3">&nbsp;</td></tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%">
									<bean:message bundle="ippoolResources" key="ippool.name" /> 
									<ec:elitehelp headerBundle="ippoolResources" text="ippool.name" 
									header="ippool.name"/>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="2">
									<html:text styleId="name" property="name" tabindex="1" onblur="verifyName()" size="40" maxlength="40" style="width:250px" /><font color="#FF0000"> *</font> 
									<html:checkbox styleId="activeStatus" tabindex="2" property="activeStatus" value="1"  />&nbsp;Show
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" valign="top">
									<bean:message bundle="ippoolResources" key="ippool.description" />
									<ec:elitehelp headerBundle="ippoolResources" 
									text="ippool.description" header="ippool.description"/>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="2">
									<html:textarea styleId="description" tabindex="3" property="description" cols="30" rows="4" style="width:250px" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%">
									<bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" /> 
									<ec:elitehelp headerBundle="ippoolResources" text="ippool.name" header="ippool.nas.ipaddress"/>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="2">
									<html:text styleId="nasIPAddress" tabindex="4" property="nasIPAddress" size="40" maxlength="40" style="width:250px" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%">
									<bean:message bundle="ippoolResources" key="ippool.additionalattributes" /> 
									<ec:elitehelp headerBundle="ippoolResources" text="ippool.additionalattributes" 
									header="ippool.additionalattributes" />
								</td>
								<td align="left" class="labeltext" valign="top" colspan="2">
									<html:textarea styleId="additionalAttributes" tabindex="5" property="additionalAttributes" cols="30" rows="4" style="width:250px" />
								</td>
							</tr>
							
							<tr>
								<td class="tblheader-bold" colspan="3">
									<bean:message bundle="ippoolResources" key="ippool.ippooldetails" /> 
								</td>
							</tr>
							
							<tr>
								<td align="left" class="captiontext" valign="top" colspan="2">
									<input type="button" onclick='addNewRow();' value=" Add " tabindex="6" class="light-btn" style="size: 140px" tabindex="10"/>
								</td>
							</tr>
							
							<tr>
								<td width="100%" colspan="3" class="captiontext">
									<table width="50%" cellpadding="0" cellspacing="0" id="ipAddressRangeTable">
										<tr>
											<td align="center" class="tblheader" valign="top" width="20%">
												<bean:message bundle="ippoolResources" key="ippool.ipaddressrangeid" />
												<ec:elitehelp headerBundle="ippoolResources" text="ippool.ipaddressrangeid" 
												header="ippool.ipaddressrangeid"/>
											</td>
											<td align="left" class="tblheader" valign="top">
												<bean:message bundle="ippoolResources" key="ippool.ipaddressrange" />
												<ec:elitehelp headerBundle="ippoolResources" text="ippool.ipaddressrange" 
												header="ippool.ipaddressrange" example="ippool.ipaddressrange.example"/>
											</td>
											<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
										</tr>
									    <tr>
											<td class="tblfirstcol"><input class="rangeIdClass" type="text" name="rangeId"  maxlength="60" size="28" style="width:100%" tabindex="7" /></td>
											<td class="tblrows"><input class="ipAddressRangeClass" type="text" name="ipAddressRanges"  maxlength="64" size="28" style="width:100%" tabindex="7" /></td>
											<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="7"/></td>
										</tr>
										<!-- Case of Exception set details -->
									</table>
							</tr>
							
							<tr>
								<td class="tblheader-bold" colspan="3">
									<bean:message bundle="ippoolResources" key="ippool.uploadipaddresslist" /> 
								</td>
							</tr>
							
							<tr>
								<td class="captiontext" width="20%">&nbsp;</td>
								<td class="labeltext">
									<input type="file" name="fileUpload" id="fileUpload" size="30"  class="uploadIPFile" tabindex="8"/>
									<img src="<%=basePath%>/images/view.jpg" name="Image6" onclick="window.open('<%=basePath%>/ipPoolCSVFormatFile.do','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')" border="0" title='<bean:message bundle="ippoolResources" key="ippool.csvformat" />' >
									<img src="<%=basePath%>/images/cross.jpg" name="Image6" onclick="javascript:document.getElementById('fileUpload').value=''" border="0" title='Remove File' >
								</td>
							</tr>
							
							<tr>
								<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="ippoolResources" key="ippool.checkforuniqueip" />
								</td>
							</tr>
											
							<tr>
								<td align="left" class="labeltext" valign="top" colspan="2">
									<html:radio styleId="status" property="status" value="1" tabindex="9"  /> 
									<bean:message bundle="ippoolResources" key="ippool.withinnas" />&nbsp;&nbsp;
									<html:radio styleId="status" property="status" value="2" tabindex="10"/> 
									<bean:message bundle="ippoolResources" key="ippool.withinsystem" />&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="button" name="c_btnCheck" onclick="checkIPAddList('<%=basePath%>/checkIPPool.do','<bean:message bundle="ippoolResources" key="ippool.addipaddress" />')" value="   Check   " class="light-btn" tabindex="11">
								</td>
							</tr>
							
							<tr>
								<td align="left" class="labeltext" valign="top">&nbsp;</td>
								<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td class="small-gap" width="16%">&nbsp;</td>
								<td align="left" class="labeltext" valign="top">
									<input type="button" name="c_btnNext" tabindex="12" onclick="validateCreate();" value=" Create " class="light-btn"  />
									<input type="button" name="c_btnCancel" tabindex="13" onclick="javascript:location.href='<%=basePath%>/searchIPPool.do'" value="   Cancel   " class="light-btn" />
								</td>
								</tr>
						</table>
						</html:form>
						<table id="ipRangeTemplateTable" style="display:none">
							<tr>
								<td class="tblfirstcol"><input class="rangeIdClass" type="text" name="rangeId"  maxlength="60" size="28" style="width:100%" tabindex="7" /></td>
								<td class="tblrows"><input class="ipAddressRangeClass" type="text" name="ipAddressRanges"  maxlength="60" size="28" style="width:100%" tabindex="7" /></td>
								<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="7"/></td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>							




