<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.HashMap" />
<jsp:directive.page
	import="com.elitecore.elitesm.util.constants.MBeanNameConstant" />
<jsp:directive.page import="java.util.Date" />
<jsp:directive.page
	import="com.elitecore.elitesm.web.servermgr.server.forms.SearchCDRForm" />

<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>





<% 
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT); 
	List fieldList = (List)request.getSession().getAttribute("fieldList");
	String localBasePath = request.getContextPath(); 
%>

<script>
	var dFormat;
	dFormat = '<%=dateFormat%>';
	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
	}
	
	function show() {
    	var srcElement = document.getElementById("advanceSearch_Id");
    	if(srcElement != null) {
    	
    	if(srcElement.style.display == "block") {
       		srcElement.style.display = 'none';
       		document.getElementById("img_id").src = '<%=localBasePath%>/images/bottom-level.jpg';
    	} else {
       		srcElement.style.display = 'block';
       		document.getElementById("img_id").src = '<%=localBasePath%>/images/top-level.jpg';
    	}
  	  }
    }
    
    function chkForProcess() {
    	var selectVars = document.getElementsByName('select');
    	var flag = false;
    	for(i = 0; i < selectVars.length; i++) {
    		if(selectVars[i].checked == true) {
    			flag = true;
    			break;
    		}	
    	}
    	if(!flag) {
    		alert("At Least Select One Record To Be Process...");
    		return false;
    	}
    	return true;
    }	

  	function addParam() {
  		if(isNull(document.forms[0].value.value)){
  			alert("Field Value Should Not Be Blank..")
  		} else {
  			document.forms[0].checkAction.value = 'Add';
			document.forms[0].submit();
		}
  	}
  	
  	function remove(index) {
		document.forms[0].checkAction.value = 'Remove';
		document.forms[0].itemIndex.value = index;
		document.forms[0].submit();
	}	
	
	function save() {
  		if(isNull(document.forms[0].searchName.value)){
  			alert("Search Name Should Not Be Blank..")
  		} else {
  			document.forms[0].checkAction.value = 'Save';
			document.forms[0].submit();
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
	
	function validateSearch() {
		if(document.forms[0].dateFrom.value != null && document.forms[0].dateFrom.value != "" && document.forms[0].dateTo.value != null && document.forms[0].dateTo.value != "") {
			if (Date.parse(document.forms[0].dateFrom.value) > Date.parse(document.forms[0].dateTo.value)) {
				alert("Invalid Date Range!\nStart Date cannot be after End Date!");
				return false;
			}
		}
		document.forms[0].checkAction.value = 'Search';
		document.forms[0].submit();
  	}
	
	function markForReprocess() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'markForReprocess';
			document.forms[0].submit();
		}	
	}
	
	function update() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'update';
			document.forms[0].submit();
		}	
	}
	
	function checkOutSelected() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'checkOutSelected';
			document.forms[0].submit();
		}	
	}
	
	function checkedOutAll() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'checkedOutAll';
			document.forms[0].submit();
		}	
	}
	
	function popup(mylink, windowname)
	{
		if (! window.focus)return true;
			var href;
					
		if (typeof(mylink) == 'string')
			href=mylink;
		else
			href=mylink.href;

		window.open(href, windowname, 'width=700,height=450,left=150,top=100,scrollbars=yes');
		return false;
	}
	
	function clearData() {
		document.forms[0].searchCriteria.value = "";
		document.forms[0].reason.value = "";
		document.forms[0].deviceId.value = "";
		document.forms[0].fileName.value = "";
		document.forms[0].dateFrom.value = "";
		document.forms[0].dateTo.value = "";
		document.forms[0].status.value = "ALL";
		document.forms[0].distributionStatus.value = "ALL";
	}
	
</script>

<html:form action="/searchCDR">
	<html:hidden styleId="checkAction" property="checkAction" />
	<html:hidden styleId="itemIndex" property="itemIndex" />
	<html:hidden styleId="serverId" property="serverId" />
	<table cellSpacing="0" cellPadding="0" width="100%" border="0">
		<tr>
			<td class="tblheader-bold" colspan="5">SEARCH CDR</td>
		</tr>
		<tr>
			<td width="23" class="small-gap">&nbsp;</td>
			<td colspan="2">
				<table width="60%" name="c_tblCrossProductList"
					id="c_tblCrossProductList" align="left" border="0">
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Search
							Criteria</td>
						<td align="left" class="labeltext" valign="top" width="18%"><html:text
								styleId="searchCriteria" property="searchCriteria" size="30" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Reason</td>
						<td align="left" class="labeltext" valign="top" width="32%"><html:text
								styleId="reason" property="reason" size="30" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Device</td>
						<td align="left" class="labeltext" valign="top" width="32%"><html:text
								styleId="deviceId" property="deviceId" size="30" /></td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">File
							Name</td>
						<td align="left" class="labeltext" valign="top" width="32%"><html:text
								styleId="fileName" property="fileName" size="30" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Status</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:select styleId="status" property="status"
								style="width:100;">
								<html:option value="ALL">All</html:option>
								<html:option value="FAILED">Failed</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Distribution
							Status</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:select styleId="distributionStatus"
								property="distributionStatus" style="width:100;">
								<html:option value="ALL">All</html:option>
								<html:option value="PARSED">Parsed</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">Date</td>
						<td align="left" class="labeltext" valign="top" width="16%">
							From:&nbsp;<html:text styleId="dateFrom" property="dateFrom"
								size="8" maxlength="15" readonly="true" /> <a
							href="javascript:void(0)"
							onclick="popUpCalendar(this, document.forms[0].dateFrom)"> <img
								src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
						</a> &nbsp;&nbsp; To:&nbsp;<html:text styleId="dateTo"
								property="dateTo" size="8" maxlength="15" readonly="true" /> <a
							href="javascript:void(0)"
							onclick="popUpCalendar(this, document.forms[0].dateTo)"> <img
								src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
						</a>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="15%">Hide
							Checked Out CDR?</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:radio styleId="hideCheckedOut" property="hideCheckedOut"
								value="Yes" />&nbsp; Yes <html:radio styleId="hideCheckedOut"
								property="hideCheckedOut" value="No" />&nbsp; No
						</td>
					</tr>

				</table> <br>

				<table cellSpacing="0" cellPadding="0" border="1" align="center">
					<tr class="grey-bkgd">
						<td colspan="2" align="left" class="labeltext" valign="top"><strong>Save
								Search</strong></td>
					</tr>

					<tr>
						<td align="center" class="labeltext">&nbsp;&nbsp;&nbsp;Name:&nbsp;
							<html:text styleId="searchName" property="searchName" size="25" />
							<br> <input class="light-btn" type="button" name="Save"
							value="  Save  " onclick="save()" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<table cellSpacing="0" cellPadding="0" width="100%" border="0">
		<tr class="grey-bkgd">
			<td align="left" valign="top" class="labeltext" width="95%"><strong>Advance
					Search</strong></td>
			<td align="center" valign="top" class="labeltext" width="5%"><img
				id="img_id" src="<%=basePath%>/images/bottom-level.jpg" border="0"
				onclick="show()" /></td>
		</tr>
	</table>

	<table cellSpacing="0" cellPadding="0" width="100%" border="0">
		<tr>
			<td width="23" class="small-gap">&nbsp;</td>
			<td>
				<div id="advanceSearch_Id" style="display: none;">
					<table width="100%">
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">Field
								Name</td>
							<td align="left" class="labeltext" valign="top" width="32%">
								<html:select styleId="fieldName" property="fieldName">
									<html:option value="FieldName">Field Name</html:option>
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">Operator</td>
							<td align="left" class="labeltext" valign="top" width="32%">
								<html:select styleId="operator" property="operator">
									<html:option value="Equals">Equals</html:option>
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">Value</td>
							<td align="left" class="labeltext" valign="top" width="32%"><html:text
									styleId="value" property="value" size="30" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">&nbsp;</td>
							<td align="left" class="labeltext" valign="top" width="32%"><input
								type="button" name="add" value="Add Field" onclick="addParam()" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">Logical
								Connector</td>
							<td align="left" class="labeltext" valign="top" width="32%">
								<html:radio styleId="logicalConnector"
									property="logicalConnector" value="And" />&nbsp; And <html:radio
									styleId="logicalConnector" property="logicalConnector"
									value="Or" />&nbsp; Or
							</td>
						</tr>
					</table>
					<% if(fieldList != null && fieldList.size() > 0) { %>
					<table width="100%" border="1" cellSpacing="0" cellPadding="0">
						<tr class="grey-bkgd">
							<td align="left" class="labeltext" valign="top" width="5%">Sr.
								No.</td>
							<td align="left" class="labeltext" valign="top" width="10%">Field
								Name</td>
							<td align="left" class="labeltext" valign="top" width="10%">Operator</td>
							<td align="left" class="labeltext" valign="top" width="10%">Value</td>
							<td align="left" class="labeltext" valign="top" width="5%">Remove</td>
						</tr>

						<% for(int index = 0;index<fieldList.size();index++) {
											
												HashMap fieldMap = (HashMap)fieldList.get(index);
												
												String fieldName = 	(String)fieldMap.get(MBeanNameConstant.FIELD_NAME);	
												String operator = 	(String)fieldMap.get(MBeanNameConstant.OPERATOR);	
												String fieldValue = (String)fieldMap.get(MBeanNameConstant.FIELD_VALUE);	
											 %>

						<tr>
							<td align="left" class="tblfirstcol"><%=(index+1)%></td>
							<td align="left" class="tblrows"><%=fieldName%></td>
							<td align="left" class="tblrows"><%=operator%></td>
							<td align="left" class="tblrows"><%=fieldValue%></td>
							<td align="left" class="tblrows"><img
								src="<%=basePath%>/images/minus.jpg"
								onclick="remove('<%=index%>')" border="0" /></td>
						</tr>

						<% } %>

					</table>
					<% } %>
				</div>
			</td>
			<td width="23" class="small-gap">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3"><hr></td>
		</tr>

		<tr>
			<td width="23" class="small-gap">&nbsp;</td>
			<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" name="search" value=" Search  "
				class="light-btn" onclick="return validateSearch()" /> &nbsp;&nbsp;
				<input type="button" name="reset" value="   Reset   "
				class="light-btn" onclick="clearData()" />
			</td>
			<td width="23" class="small-gap">&nbsp;</td>
		</tr>
	</table>

	<script>
					if(document.forms[0].checkAction.value == 'Remove' || document.forms[0].checkAction.value == 'Add') {
						document.getElementById("advanceSearch_Id").style.display = 'block';
       					document.getElementById("img_id").src = '<%=localBasePath%>/images/top-level.jpg';
					}	
				</script>
	<br>

	<%if(request.getSession().getAttribute("cdrDataList") != null) { %>
	<table width="100%" border="0" cellSpacing="0" cellPadding="0">
		<tr>
			<td align="left" valign="top" width="2%" class="tblheader-bold"><input
				type="checkbox" name="toggleAll" value="checkbox"
				onclick="checkAll()" /></td>
			<td align="left" valign="top" width="5%" class="tblheader-bold">Sr.
				No.</td>
			<td align="left" valign="top" width="15%" class="tblheader-bold">Date</td>
			<td align="left" valign="top" width="10%" class="tblheader-bold">Device</td>
			<td align="left" valign="top" width="10%" class="tblheader-bold">State</td>
			<td align="left" valign="top" width="10%" class="tblheader-bold">Reason</td>
			<td align="left" valign="top" width="5%" class="tblheader-bold">View</td>
		</tr>
		<% List cdrDataList = (List)request.getSession().getAttribute("cdrDataList"); %>

		<% if(cdrDataList != null && cdrDataList.size() > 0) {
					
						for(int index = 0;index < cdrDataList.size();index++) {
							HashMap cdrMap = (HashMap)cdrDataList.get(index);
							
							String cdrId = (String)cdrMap.get(MBeanNameConstant.CDR_ID);
							String date = 	((Date)cdrMap.get(MBeanNameConstant.DATE)).toString();	
							String deviceId = (String)cdrMap.get(MBeanNameConstant.DEVICE_ID);	
							String state = (String)cdrMap.get(MBeanNameConstant.STATE);	
							String reason = (String)cdrMap.get(MBeanNameConstant.REASON);
					 %>
		<tr>
			<td align="left" class="tblfirstcol"><input type="checkbox"
				name="select" value="<%=cdrId%>" /></td>
			<td align="left" class="tblfirstcol"><%=(index+1)%></td>
			<td align="left" class="tblrows"><%=date%></td>
			<td align="left" class="tblrows"><%=deviceId%></td>
			<td align="left" class="tblrows"><%=state%></td>
			<td align="left" class="tblrows"><%=reason%></td>
			<td align="left" class="tblrows" valign="center"><a href=""
				onclick="return popup('<%=basePath%>/viewCDRInfo.do?cdrId=<%=cdrId%>&netserverid=<bean:write name="searchCDRForm" property="serverId"/>','notes')"><img
					src="<%=basePath%>/images/view-info.jpg" alt="View" border="0"></a>
			</td>
		</tr>
		<% } %>
		<tr>
			<td colspan="7" class="tblfirstcol">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="7" align="center" class="tblfirstcol"><input
				type="button" name="reprocess_btn" value="Mark For Reprocess"
				class="light-btn" onclick="markForReprocess()" /> <input
				type="button" name="update_btn" value="Update" class="light-btn"
				onclick="update()" /> <input type="button"
				name="checkOutSelected_btn" value="Check Out" class="light-btn"
				onclick="checkOutSelected()" /> <input type="button"
				name="checkedOutAll_btn" value="Checked Out All" class="light-btn"
				onclick="checkedOutAll()" /></td>
		</tr>
		<tr>
			<td align="right" class="tblfirstcol" colspan="7"><a
				href="searchCDRSummary.do?netserverid=<bean:write name="searchCDRForm" property="serverId"/>">View
					Summary Information</a></td>
		</tr>
		<% } else { %>
		<tr>
			<td colspan="7" align="center" class="tblfirstcol">No Records
				Found...</td>
		</tr>
		<% } %>
	</table>
	<% } %>
</html:form>
