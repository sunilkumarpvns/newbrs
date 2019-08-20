<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData"%>
<%@page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyForm"%>
<%@ page
	import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyDetailForm"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	Map attributeLoginMap = (Map)request.getAttribute("attributeLoginMap");
 
	String basePath = request.getContextPath();
	
	AddConcurrentLoginPolicyForm addConcurrentLoginPolicyForm      = (AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm");

	Map<String,String> supportedValueMap = (Map<String,String>)request.getSession().getAttribute("supportedValueMap");
%>
 
 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
 
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
		
		if(isNull(document.addConcurrentLoginPolicyDetailForm.attributeValue.value)){
			alert("Please enter Attribute Value");
			attributeValueElement.focus();
			return false;
		}
	}else{
		if(document.addConcurrentLoginPolicyDetailForm.attributeValue.value  == ""){
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
    
	if(document.addConcurrentLoginPolicyDetailForm.maxLogin[1].checked){
			//alert('unlimited');
			document.addConcurrentLoginPolicyDetailForm.maxNumLogin.disabled = false;
			document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value = '-1';
			
	}else{
			if(!regexpNum.test(document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value)  ){
				alert('Invalid value of Max. Concurrent Login. It must be zero or positive Integer.');
				return false;
            }
            
    }
	//get no. of maximum login
    var maximumLoginNum = parseInt(document.addConcurrentLoginPolicyDetailForm.login.value);
    var enteredLoginNum = parseInt(document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value);
	
    if(document.addConcurrentLoginPolicyDetailForm.login.value != '-1' ){
        if(document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value == '-1'){
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


function deleteSubmit(index)
{
	document.forms[0].action.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

function submitCreate(){
	document.forms[0].action.value = 'Create';
	document.forms[0].submit();
	
}

function submitPrevious(){
	document.forms[0].action.value = 'Previous';
	document.forms[0].submit();
}
function setLoginLimit(value) {
	if (value=="Limited") {
		document.addConcurrentLoginPolicyDetailForm.maxNumLogin.disabled = false;
		document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value="";
	} else if (value=="Unlimited") {
		document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value="-1";
		document.addConcurrentLoginPolicyDetailForm.maxNumLogin.disabled = true;
	} else {
		document.addConcurrentLoginPolicyDetailForm.maxNumLogin.value="";
	}
}
initializeMap();
setTitle('<bean:message bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicy"/>');
</script>


<html:form action="/addConcurrentLoginPolicyDetail">
	<html:hidden name="addConcurrentLoginPolicyDetailForm" styleId="action"
		property="action" />
	<html:hidden name="addConcurrentLoginPolicyDetailForm"
		styleId="itemIndex" property="itemIndex" />
	<html:hidden name="addConcurrentLoginPolicyForm" styleId="login"
		property="login" />
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
									<td class="table-header"><bean:message
											bundle="radiusResources"
											key="concurrentloginpolicy.attributedetails" /></td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3" align="right">
										<table width="100%" align="right" cellpadding="0"
											cellspacing="0" border="0">

											<tr>
												<td align="left" class="captiontext" valign="top"
														width="20%"><bean:message bundle="radiusResources"
														key="concurrentloginpolicy.attributevalue" />
														<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.attributevalue" 
														header="concurrentloginpolicy.attributevalue"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="40%">
													<%if(supportedValueMap!=null && !supportedValueMap.isEmpty()){ %>
													<html:select name="addConcurrentLoginPolicyDetailForm"
														styleId="attributeValue" property="attributeValue"
														tabindex="1">
														<html:option value="">
															<bean:message key="general.select" />
														</html:option>
														<html:options collection="supportedValueMap"
															property="key" labelProperty="value" />
													</html:select> <%} else { %> <html:text
														name="addConcurrentLoginPolicyDetailForm" tabindex="2"
														styleId="attributeValue" property="attributeValue"
														size="30" maxlength="255" /> <br /> <font
													class="small-text-grey">For the rest of other values
														enter star (*) only.</font> <%} %>
												</td>

											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="12%"><bean:message bundle="radiusResources"
														key="concurrentloginpolicy.maximumconcurrentlogin" />
														<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.maxlogin" 
														header="concurrentloginpolicy.maximumconcurrentlogin"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0">
														<tr>
															<td align="left" class="labeltext" valign="top"
																width="5%">&nbsp;&nbsp;<html:radio
																	name="addConcurrentLoginPolicyDetailForm" tabindex="3"
																	styleId="maxLogin" property="maxLogin" value="Limited"
																	onclick="setLoginLimit('Limited')" />Limited
																&nbsp;&nbsp;&nbsp;&nbsp;<html:text
																	name="addConcurrentLoginPolicyDetailForm"
																	styleId="maxNumLogin" property="maxNumLogin" size="30"
																	styleClass="flatfields" maxlength="30" /><font
																color="#FF0000"> *</font></br> &nbsp;&nbsp;<html:radio
																	name="addConcurrentLoginPolicyDetailForm" tabindex="4"
																	styleId="maxLogin" property="maxLogin"
																	value="Unlimited" onclick="setLoginLimit('Unlimited')" />Unlimited
															</td>
														</tr>
													</table>
												</td>
											</tr>


											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;
												</td>
												<td align="left" class="labeltext" valign="top"><input
													type="button" value="    Add    " tabindex="5"
													property="ADD" onclick="submitAdd()" class="light-btn" /></td>
												<td width="10%">&nbsp;</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="table-header"><bean:message
											bundle="radiusResources"
											key="concurrentloginpolicy.concurrentloginpolicy" /></td>
								</tr>

								<%/*        List       */%>
								<tr>
									<td colspan="3">
										<table class="captiontext" cellpadding="0" cellspacing="0"
											border="0" width="98%">
											<%
							int index = 0;
						%>
											<tr>
												<td align="left" class="tblheader" valign="top" width="10%"><bean:message
														bundle="radiusResources"
														key="concurrentloginpolicy.serialnumber" /></td>
												<td align="left" class="tblheader" valign="top" width="40%"><bean:message
														bundle="radiusResources"
														key="concurrentloginpolicy.attributevalue" /></td>
												<td align="left" class="tblheader" valign="top" width="40%"><bean:message
														bundle="radiusResources"
														key="concurrentloginpolicy.maxlogin" /></td>
												<td align="left" class="tblheader" valign="top" width="10%"><bean:message
														bundle="radiusResources"
														key="concurrentloginpolicy.remove" /></td>
											</tr>

											<logic:iterate id="loginPolicyDetail"
												name="addConcurrentLoginPolicyForm"
												property="concurrentLoginPolicyDetail"
												type="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyDetailForm"
												scope="session">
												<tr>
													<td align="left" class="tblfirstcol"><%=(index+1)%></td>

													<td align="left" class="tblrows">
														<%if(supportedValueMap!=null && !supportedValueMap.isEmpty()){ 
								  String label = supportedValueMap.get(loginPolicyDetail.getAttributeValue());
								  %> <%=label%> <%}else{%> <logic:equal value="*"
															name="loginPolicyDetail" property="attributeValue">
										others  
									</logic:equal> <logic:notEqual value="*" name="loginPolicyDetail"
															property="attributeValue">
															<bean:write name="loginPolicyDetail"
																property="attributeValue" />
														</logic:notEqual> <%}%>
													</td>

													<logic:equal name="loginPolicyDetail"
														property="maxNumLogin" value="-1">
														<td align="left" class="tblrows"><bean:message
																bundle="radiusResources"
																key="concurrentloginpolicy.unlimited" /></td>
													</logic:equal>

													<logic:notEqual name="loginPolicyDetail"
														property="maxNumLogin" value="-1">
														<td align="left" class="tblrows"><bean:write
																name="loginPolicyDetail" property="maxNumLogin" /></td>
													</logic:notEqual>

													<td align="center" class="tblrows"><img
														src="<%=basePath%>/images/minus.jpg"
														onclick="deleteSubmit('<%=index%>')" border="0" /></td>
												</tr>
												<% index++; %>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<%/*        List     End  */%>

								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>

								<tr>
									<td class="btns-td" valign="middle" align="left"><input
										type="button" value="Previous" tabindex="6"
										onClick="submitPrevious()" class="light-btn" />&nbsp;&nbsp; <input
										type="button" value="Create" tabindex="7"
										onClick="submitCreate()" class="light-btn" />&nbsp;&nbsp; <input
										type="reset" name="c_btnDeletePolicy" tabindex="8"
										onclick="javascript:location.href='<%=basePath%>/initSearchConcurrentLoginPolicy.do?isMenuCall=yes'"
										value="Cancel" class="light-btn"></td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
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