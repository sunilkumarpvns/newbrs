<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.*"%>
<%@ page
	import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.UpdateConcLoginPolicyAttributeDetailForm"%>





<%
	Map attributeLoginMap = (Map)request.getAttribute("attributeLoginMap");
 %>
<script type="text/javascript">
var counter=0; //counter for hash map value.
jsHashMap = new Array();
function JSHashMap()
{
	var key;
	var value;		
	var counter=0;
}
//the function will be called when loading the page.
function initializeMap()
{
	//initialize JSHashMap with the nasPortTypeLoginMap values. 
	<%
	if(attributeLoginMap!=null){
		Set keySet = attributeLoginMap.keySet();
		Iterator iterator = keySet.iterator();
		while(iterator.hasNext()){
		String key =(String)iterator.next();
		Integer value = (Integer)attributeLoginMap.get(key);
		%>
			put('<%=key%>',<%=value.intValue()%>);
		<%		
		}
	}
	%>
}
function put(keyVal, valueVal){
	for(var i =0;i<jsHashMap.length;i++){
		//if key is already exist then add the login with the existing one.	
		if(jsHashMap[i].key==keyVal){
			jsHashMap[i].value= jsHashMap[i].value + valueVal;
			return;
		}	
	}
	jsHashMap[counter]= new JSHashMap();
	jsHashMap[counter].key = keyVal;
	jsHashMap[counter].value = valueVal;
	counter++;
}
//get() will find the value from custom hashmap based on keyVal.
function get(keyVal){
	for(var i =0;i<jsHashMap.length;i++){
	
		if(jsHashMap[i].key==keyVal){
			return jsHashMap[i].value;
		}
	}
}
function submitAdd()
{
	var regexpNum = /^\d+$/;
	
	
	
	var attributeValueElement = document.getElementById("attributeValue");
	
	
	if(attributeValueElement.type == 'text'){
		
		if(isNull(document.updateConcLoginPolicyAttributeDetailForm.attributeValue.value)){
			alert("Please enter Attribute Value");
			attributeValueElement.focus();
			return false;
		}
	}else{
		
		if(document.updateConcLoginPolicyAttributeDetailForm.attributeValue.value  == ''){
			alert("Please select Attribute Value");
			attributeValueElement.focus();
			return false;
		}
	}
	
	//get attribute value
    var keyName = document.getElementById("attributeValue");

    //check for already added or not 
    var alreadyAdded = get(keyName.value);
    
    if(alreadyAdded!=null){
    	alert("Attribute Value is already added.");
    	return;
    	
    }
    
    
    
	
	if(document.updateConcLoginPolicyAttributeDetailForm.maxLogin[1].checked){
			//alert('unlimited');
			document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.disabled = false;
			document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value = '-1';
			
	}else{
			if(!regexpNum.test(document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value)  ){
				alert('Invalid value of Max. Concurrent Login. It must be zero or positive Integer.');
				return false;
            }
            
    }
	
    
   //get no. of maximum login
    var maximumLoginNum = parseInt(document.updateConcLoginPolicyAttributeDetailForm.login.value);
    var enteredLoginNum = parseInt(document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value);
	
    if(document.updateConcLoginPolicyAttributeDetailForm.login.value != '-1' ){
        if(document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value == '-1'){
            alert('Number of login should be less than(or eq) Max Allowed : '+maximumLoginNum);
            document.addConcurrentLoginPolicyDetailForm.maxNumLogin.disabled = true;
            return false;
        }
        if(enteredLoginNum > maximumLoginNum){
            alert('Number of login should be less than(or eq) Max Allowed : '+maximumLoginNum);
            return false;
        }
    }
    
	

    //add the currently entered login to existing logins.
    var maxCurrentLogin = enteredLoginNum;

    //check for total entered login for particular service should be less or equal maximum login.
    if(maximumLoginNum != -1 && maxCurrentLogin > maximumLoginNum){
    	alert('Number of login should be less than(or eq) Max Allowed : '+maximumLoginNum + '.\nTotal configured logins of '+keyName.value+' is '+totalCurrentLogin+'.');
    	return false;
    }
    
    
	document.forms[0].action.value = 'Add';
	document.forms[0].submit();
	
}

function submitCreate(){
	document.forms[0].action.value = 'Create';
	document.forms[0].submit();

}

function deleteSubmit(index)
{
	document.forms[0].action.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

function customValidate()
{
	document.updateConcLoginPolicyAttributeDetailForm.action.value = 'update';
	var retVar;
	return retVar;
}

function setLoginLimit(value) {
	if (value=="Limited") {
		document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.disabled = false;
		document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value="";
	} else if (value=="Unlimited") {
		document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value="-1";
		document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.disabled = true;
	} else {
		document.updateConcLoginPolicyAttributeDetailForm.maxNumLogin.value="";
	}
}
initializeMap();
</script>

<%
   // String basePath = request.getContextPath();
%>
<%
	UpdateConcLoginPolicyAttributeDetailForm updateConcLoginPolicyAttributeDetailForm      = (UpdateConcLoginPolicyAttributeDetailForm)request.getAttribute("updateConcLoginPolicyAttributeDetailForm");
	
	Map<String,String> supportedValueMap = (Map<String,String>)request.getSession().getAttribute("supportedValueMap");
%>
<html:form action="/updateConcLoginPolicyAttributeDetail">
	<html:hidden name="updateConcLoginPolicyAttributeDetailForm"
		styleId="action" property="action" value='update' />
	<html:hidden name="updateConcLoginPolicyAttributeDetailForm"
		styleId="itemIndex" property="itemIndex" />
	<html:hidden name="updateConcLoginPolicyAttributeDetailForm"
		styleId="concurrentLoginId" property="concurrentLoginId" />
	<html:hidden name="concurrentLoginPolicyData" property="login" />
	<html:hidden name="updateConcLoginPolicyAttributeDetailForm"
		styleId="auditUId" property="auditUId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message
					bundle="radiusResources"
					key="concurrentloginpolicy.attributedetails" /></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources"
								key="concurrentloginpolicy.attributevalue" /> 
							<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.attributevalue" 
							header="concurrentloginpolicy.attributevalue"/>	
						</td>
						<td align="left" class="labeltext" valign="top" width="40%">
							<%if(supportedValueMap!=null && !supportedValueMap.isEmpty()){ %> <html:select
								name="updateConcLoginPolicyAttributeDetailForm" tabindex="1"
								styleId="attributeValue" property="attributeValue">
								<html:option value="">
									<bean:message key="general.select" />
								</html:option>
								<html:options collection="supportedValueMap" property="key"
									labelProperty="value" />
							</html:select> <%} else { %> <html:text
								name="updateConcLoginPolicyAttributeDetailForm" tabindex="2"
								styleId="attributeValue" property="attributeValue" size="30"
								maxlength="255" /> <br /> <font class="small-text-grey">For
								the rest of other values enter star (*) only.</font> <%} %>
						</td>

					</tr>
					<tr>
						<td align="left" class="captiontext" " valign="top" width="12%">
							<bean:message bundle="radiusResources"
								key="concurrentloginpolicy.maximumconcurrentlogin" /> 
								<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.maxlogin" 
								header="concurrentloginpolicy.maximumconcurrentlogin" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="left" class="labeltext" valign="top" width="5%">
										&nbsp;&nbsp;<html:radio
											name="updateConcLoginPolicyAttributeDetailForm" tabindex="3"
											styleId="maxLogin" property="maxLogin" value="Limited"
											onclick="setLoginLimit('Limited')" />Limited
										&nbsp;&nbsp;&nbsp;&nbsp;<html:text
											name="updateConcLoginPolicyAttributeDetailForm"
											styleId="maxNumLogin" property="maxNumLogin" size="30"
											tabindex="5" styleClass="flatfields" maxlength="30" /><font
										color="#FF0000"> *</font></br> &nbsp;&nbsp;<html:radio
											name="updateConcLoginPolicyAttributeDetailForm" tabindex="4"
											styleId="maxLogin" property="maxLogin" value="Unlimited"
											onclick="setLoginLimit('Unlimited')" />Unlimited
									</td>
								</tr>
							</table>
						</td>
					</tr>


					<tr>

						<td align="left" class="captiontext" valign="top"><input
							type="button" value="    Add    " tabindex="6" property="ADD"
							onclick="submitAdd()" class="light-btn" /></td>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td width="10%">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
				<table width="98%" class="captiontext" border="0" cellpadding="0"
					cellspacing="0">
					<%
							int index = 0;
						%>
					<tr>
						<td align="left" class="tblheader" valign="top" width="5%"><bean:message
								bundle="radiusResources"
								key="concurrentloginpolicy.serialnumber" /></td>
						<td align="left" class="tblheader" valign="top" width="20%"><bean:message
								bundle="radiusResources"
								key="concurrentloginpolicy.attributevalue" /></td>
						<td align="left" class="tblheader" valign="top" width="20%"><bean:message
								bundle="radiusResources" key="concurrentloginpolicy.maxlogin" /></td>
						<td align="left" class="tblheader" valign="top" width="20%"><bean:message
								bundle="radiusResources" key="concurrentloginpolicy.remove" /></td>
					</tr>

					<logic:iterate id="loginPolicyDetailBean" name="loginPolicyDetail"
						type="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyDetailData"
						scope="session">
						<tr>
							<td align="left" class="tblfirstcol"><%=(index+1)%></td>
							<%--  <bean:write name="loginPolicyDetailBean" property="serialNumber" /> --%>
							<td align="left" class="tblrows">
								<%if(supportedValueMap!=null && !supportedValueMap.isEmpty()){ 
								  String label = supportedValueMap.get(loginPolicyDetailBean.getAttributeValue());
								  %> <%=label%> <%}else{%> <logic:equal value="*"
									name="loginPolicyDetailBean" property="attributeValue">
									others  
								</logic:equal> <logic:notEqual value="*" name="loginPolicyDetailBean"
									property="attributeValue">
									<bean:write name="loginPolicyDetailBean"
										property="attributeValue" />
								</logic:notEqual> <%}%>
							</td>
							<td align="left" class="tblrows"><bean:write
									name="loginPolicyDetailBean" property="login" /></td>
							<td align="center" class="tblrows"><img
								src="<%=basePath%>/images/minus.jpg"
								onclick="deleteSubmit('<%=index%>')" border="0" /></td>
						</tr>
						<% index++; %>
					</logic:iterate>
				</table>
			</td>
		</tr>

		<tr>
			<td colspan="3" height="20%" class="labeltext">&nbsp;</td>
		</tr>

		<tr>
			<td colspan="3" class="captiontext" align="left"><input
				type="submit" tabindex="7" name="c_btnCreate"
				onclick="return customValidate();" value="   Save   "
				class="light-btn">&nbsp;&nbsp; <input type="reset"
				tabindex="8" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewConcurrentLoginPolicy.do?concurrentLoginPolicyId=<bean:write name="concurrentLoginPolicyBean" property="concurrentLoginId"/>'"
				value=" Cancel " class="light-btn"></td>
		</tr>

		<tr>
			<td colspan="3" height="20%" class="labeltext">&nbsp;</td>
		</tr>
	</table>
</html:form>


