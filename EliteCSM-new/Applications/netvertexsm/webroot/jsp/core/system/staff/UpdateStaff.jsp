<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>

<%
    String contextPath = request.getContextPath();
%>

<link href = "<%=contextPath%>/css/staff.css" rel="stylesheet"></link>
<script type="text/javascript" src="<%=contextPath%>/js/staff.js"></script>
<link href = "<%=contextPath%>/css/cropper.css" rel="stylesheet"></link>
<script src="<%=contextPath%>/js/cropper.js"></script>


<script>

   function validateUpdate(){
	   document.forms[0].action.value='update';
	   	if(fileSizeExceeded == true){
	   		alert('File size must not exceed 2 MB');
	    	document.forms[0].profilePicture.focus();	
	   	}else if(isNull(document.forms[0].name.value)){
	   		alert('Name must be specified');
	   		document.forms[0].name.focus();
	   	}else if(!validatePhoneNumber(document.forms[0].phone)){
	   		alert('Enter valid Phone Number.');
	   		document.forms[0].phone.focus();
	   	}else if(document.forms[0].mobile.value.length > 0 && !validateMobileNumber(document.forms[0].mobile)){
	   		alert('Enter valid Mobile Number.');
	   		document.forms[0].mobile.focus();
	   	}else if(isNull(document.forms[0].emailAddress.value)){
			alert('Email address must be specified');
			document.forms[0].emailAddress.focus();
		}else if(validateEmailAddress(document.forms[0].emailAddress) == false){
			alert("Invalid E-mail ID");
			document.forms[0].emailAddress.focus();
		}else{
	   		document.forms[0].submit();
	   	}
	   }	
	    
$(document).ready(function(){
	$("#name").focus();
	loadCropper();
});
</script>
<html:form action="/updateStaff" enctype="multipart/form-data">
	<html:hidden name="updateStaffBasicDetailForm" styleId="action" property="action" />
	<html:hidden name="updateStaffBasicDetailForm" styleId="staffId" property="staffId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="97%" height="15%" class="box">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message key="staff.staffdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message key="staff.name" />
						</td>
						<td align="left" align="top" width="82%">
							<html:text	styleId="name" property="name" tabindex="1" maxlength="30" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message key="staff.username" />
						</td>
						<td align="left" class="labeltext" valign="top" width="30%">
							<bean:write	name="updateStaffBasicDetailForm" property="userName" />&nbsp;
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%%">
							<bean:message key="staff.profilepicture" />
						</td>
						<td align="left" valign="top">
							<html:img styleId="croppedImage" action="profilePicture?staffId=${staffId}" height="100" width="100"/>
							<input type="hidden" id="profilePicture" name="profilePicture"/>
							<br> 
							<html:file property="profilePictureFile" accept="image/*" styleId="profilePictureFile" onchange="loadImage(event)" onclick="clearImage(this);" ></html:file> 
							<label class="small-text-grey"><bean:message key="staff.profilepicture.note" /> </label>
							<label class="small-text-grey" id="warning" style="display: none; color: #FF0000;"> <bean:message key="staff.profilepicture.warning" /></label>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message key="staff.contactdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message key="staff.phonenumber" />
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="phone" property="phone" size="20" maxlength="20" tabindex="11" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message key="staff.mobilenumber" />
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="mobile" property="mobile" size="20" maxlength="20" tabindex="12" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message key="staff.emailaddress" />
						</td>
						<td align="left" valign="top">
							<html:text	styleId="emailAddress" property="emailAddress" size="30" tabindex="10" maxlength="50" /> 
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle">
							<input type="button" name="c_btnCreate" onclick="validateUpdate()" value="Update" tabindex="13" class="light-btn"> 
							<input type="reset" name="c_btnDeletePolicy" tabindex="14" onclick="javascript:location.href='<%=contextPath%>/searchStaff.do?/>'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
	</table>

<div class="container" id="container" style="display: none;">
	<div class="image-box">
 	<div class="img-container">
		<img src="<%=contextPath%>/images/defaultProfilePicture.jpg" alt="Picture" id="picture">
		<br>
	</div>
    </div>
    <div class="preview-box">
    	 <div class="docs-preview clearfix">
    	   	<div class="img-preview preview-md"></div>
          	<div class="img-preview preview-md-round"></div>
          	<div class="img-preview preview-xs-round"></div>
        </div>
			
    </div>
</div>
		
</html:form>

