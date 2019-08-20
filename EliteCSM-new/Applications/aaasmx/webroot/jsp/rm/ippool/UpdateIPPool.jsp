<%@page import="com.elitecore.elitesm.Version"%>
<script src="<%=request.getContextPath()%>/js/rm/ippool/ippool.js?rev=<%=Version.getSVNRevision()%>>"></script>
<script>
	setTitle('<bean:message bundle="ippoolResources" key="ippool.header"/>');
	
	$(document).ready(function(){
		/* bind click event of delete image */
		$('img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		$('img.deleteRange').live('click',function() {
			var img = $(this);
			var ipAddressRanges = img.siblings().val();
			var msg = "All the generated IP Address from this range would be deleted. Would you like to continue? ";
	        var agree = confirm(msg);
	        var data = {
	        		ipPoolId : $("#ipPoolId").val(), 
	        		ipAddressRanges : ipAddressRanges
	        } ;
	        if(agree){
	        	$.ajax({
	    			url : "deleteIPPoolDetailByRange.do",
	    			type: "POST",
	    			data: data,
	    			async : false,
	    			success : function (response) {
	    				img.parent().parent().remove();
	    				//$("#deleteIPPoolByRangeDiv").html("IP Addresses of range deleted successfully.");
	    			}
	    		});
	        }
		});
		
		
	});

	
	var isValidName;
	function verifyName() {
		id = "verifyNameDiv";
		$('<div/>', {
			id : id
		}).appendTo($("#name").parent());
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.IPPOOL%>',$("#name").val(),'update',$("#ipPoolId").val(),id);
	}
	
</script>

<div id="ipPoolFormDiv">
<html:form action="updateIPPool" styleId="ipPoolForm" enctype="multipart/form-data">
	<html:hidden property="ipPoolId" styleId="ipPoolId" />
	<html:hidden property="activeStatus" />
	<html:hidden property="createdByStaffId" />
	<html:hidden property="statusChangedDate" />
	<html:hidden property="createDate" />	
	<html:hidden property="auditUId" />					
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		
		<tr>
			<td class="tblheader-bold" colspan="3">
					<bean:message bundle="ippoolResources" key="ippool.basic.details" /> 
				</td>
			</tr>
		<tr><td class="small-gap" colspan="3">&nbsp;</td></tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="25%">
				<bean:message bundle="ippoolResources" key="ippool.name" /> 
				<ec:elitehelp headerBundle="ippoolResources" text="ippool.name" header="ippool.name" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:text styleId="name" property="name" tabindex="1" onblur="verifyName()" size="40" maxlength="40" style="width:250px" /><font color="#FF0000"> *</font> 
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="25%" valign="top">
				<bean:message bundle="ippoolResources" key="ippool.description" />
				<ec:elitehelp headerBundle="ippoolResources" 
				text="ippool.description" header="ippool.description"/>
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:textarea styleId="description" tabindex="2" property="description" cols="30" rows="4" style="width:250px" />
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="25%">
				<bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" /> 
				<ec:elitehelp headerBundle="ippoolResources" text="ippool.nasipaddress" header="ippool.nas.ipaddress" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:text styleId="nasIPAddress" tabindex="3" property="nasIPAddress" size="40" maxlength="40" style="width:250px" />
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="25%">
				<bean:message bundle="ippoolResources" key="ippool.additionalattributes" /> 
				<ec:elitehelp headerBundle="ippoolResources" text="ippool.additionalattributes" header="ippool.additionalattributes" />
	 		</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:textarea styleId="additionalAttributes" tabindex="4" property="additionalAttributes" cols="30" rows="4" style="width:250px" />
			</td>
		</tr>
		
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="ippoolResources" key="ippool.ippooldetails" /> 
			</td>
		</tr>
		
		<tr>
			<td align="left" class="captiontext" valign="top" colspan="2" >
				<input type="button" onclick='addNewRow();' value=" Add " class="light-btn" style="size: 140px" tabindex="5"/>
			</td>
		</tr>
		
		<tr>
			<td width="100%" colspan="3" class="captiontext">
				<table width="50%" cellpadding="0" cellspacing="0" id="ipAddressRangeTable">
					<tr>
						<td align="center" class="tblheader" valign="top" width="20%">
							<bean:message bundle="ippoolResources" key="ippool.ipaddressrangeid" />
							<ec:elitehelp headerBundle="ippoolResources" text="ippool.ipaddressrangeid" header="ippool.ipaddressrangeid" />
						</td>
						<td align="left" class="tblheader" valign="top">
							<bean:message bundle="ippoolResources" key="ippool.ipaddressrange" />
							<ec:elitehelp headerBundle="ippoolResources" text="ippool.ipaddressrange" header="ippool.ipaddressrange" example="ippool.ipaddressrange.example"/>
						</td>
						<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
					</tr>
					<logic:iterate id="ipPoolDetail" name="lstIPPoolDetail" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData" >
				    <logic:notEmpty name="ipPoolDetail" property="ipAddressRangeId">
				    <tr>
						<td class="tblfirstcol"><bean:write name="ipPoolDetail" property="ipAddressRangeId" /></td>
						<td class="tblrows"><bean:write name="ipPoolDetail" property="ipAddressRange" /></td>
						<td class="tblrows" align="center" colspan="3">
							<input type="hidden" name="oldRangeId" class="rangeIdClass" value='<bean:write name="ipPoolDetail" property="ipAddressRangeId" />'/>
							<input type="hidden" name="oldIPAddressRange" class="ipAddressRangeClass" value='<bean:write name="ipPoolDetail" property="ipAddressRange" />'/>
							<img value="top" src="<%=basePath%>/images/minus.jpg"  class="deleteRange" style="padding-right: 5px; padding-top: 5px;" height="14" />
						</td>
					</tr>
					</logic:notEmpty>
					</logic:iterate>
					
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
				<input type="file" name="fileUpload" id="fileUpload" size="30"  class="uploadIPFile" tabindex="7"/>
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
				<html:radio styleId="status" property="status" value="1" tabindex="8"  /> 
				<bean:message bundle="ippoolResources" key="ippool.withinnas" />&nbsp;&nbsp;
				<html:radio styleId="status" property="status" value="2" tabindex="9"/> 
				<bean:message bundle="ippoolResources" key="ippool.withinsystem" />&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" name="c_btnCheck" onclick="checkIPAddList('<%=basePath%>/checkIPPool.do','<bean:message bundle="ippoolResources" key="ippool.addipaddress" />')" value="   Check   " class="light-btn" tabindex="10">
			</td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top">&nbsp;</td>
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td class="small-gap" width="16%">&nbsp;</td>
			<td align="left" class="labeltext" valign="top">
				<input type="button" name="c_btnNext" tabindex="11" onclick="validateUpdate()" value=" Update " class="light-btn" />
				<input type="button" name="c_btnCancel" tabindex="12" onclick="javascript:location.href='<%=basePath%>/searchIPPool.do'" value="   Cancel   " class="light-btn" />
			</td>
			</tr>
	</table>
</html:form>
</div>

<table id="ipRangeTemplateTable" style="display:none">
	<tr>
		<td class="tblfirstcol"><input class="rangeIdClass" type="text" name="rangeId"  maxlength="60" size="28" style="width:100%" tabindex="6" /></td>
		<td class="tblrows"><input class="ipAddressRangeClass" type="text" name="ipAddressRanges"  maxlength="60" size="28" style="width:100%" tabindex="6"/></td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="6"/></td>
	</tr>
</table>
