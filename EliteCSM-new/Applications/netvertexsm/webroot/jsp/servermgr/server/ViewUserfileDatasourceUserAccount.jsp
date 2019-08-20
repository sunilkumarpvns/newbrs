



<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewUserfileDatasourceUserAccountForm" %>
<%
	String localBasePath = request.getContextPath();
	ViewUserfileDatasourceUserAccountForm viewUserfileDatasourceUserAccountForm=(ViewUserfileDatasourceUserAccountForm)request.getAttribute("viewUserfileDatasourceUserAccountForm");
%>
<script type="text/javascript">

function validateUpdate(attrListSize){

	if(attrListSize == 0){
    alert("Select Attribute Parameter."); 
    return false;
  }
   
  for(var i=0;i<attrListSize;i++)
  {
    var field = "fieldValue["+i+"]";
    var attribute = "attributeName["+i+"]";
    var fieldElement = document.getElementById(field);
	var attributeElement = document.getElementById(attribute);
	if(fieldElement!=null){
		if(fieldElement.value==''){
		    alert("Please enter the value for "+attributeElement.value+ ".");
			return false;
		}
		
	}else{
		return false;
	}
  }
  if(isNull(document.forms[0].passwordCheckEnable.value)){
		alert('PasswordCheckEnable must be specified');
		return false;
	}else if(isNull(document.forms[0].password.value)){
		alert('Password must be specified');
		return false;
	}else if(isNull(document.forms[0].encryptionType.value)){
		alert('EncryptionType must be specified');
		return false;
	}else if(isNull(document.forms[0].accountStatus.value)){
		alert('AccountStatus must be specified');
		return false;
	}else if(isNull(document.forms[0].creditLimit.value)){
		alert('CreditLimit must be specified');
		return false;
	}else if(!(document.forms[0].creditLimit.value >=0)){
		alert("credit limit should be in number")
		document.forms[0].creditLimit.focus();
		return false;
	}else if(isNull(document.forms[0].expiryDate.value)){
		alert('ExpiryDate must be specified');
		return false;
	}else if(isNull(document.forms[0].customerType.value)){
		alert('CustomerType must be specified');
		return false;
	}else if(!(document.forms[0].groupBandwidth.value >=0)){
		alert("group Bandwidth should be in number")
		document.forms[0].groupBandwidth.focus();
		return false;
	}
	
	document.forms[0].action.value="update";
	document.forms[0].submit();
	
}
function removeAttribute(attributeId){
	document.forms[0].attributeId.value = attributeId;
	document.forms[0].action.value="removeAttribute";
	document.forms[0].submit();
}
</script>

<html>

<html:form action="/viewUserfileDatasourceUserAccount" >
<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">

	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="id" property="id" />
	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="netServerId" property="netServerId" />
	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="fileName" property="fileName" />
	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="action" property="action" />
	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="attributeId" property="attributeId" />
	<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="attributeIdName" property="attributeIdName" />
	
	<%
    	List listAttributeId=viewUserfileDatasourceUserAccountForm.getListAttributeId();
        for(int i=0;i<listAttributeId.size();i++){
        	String strfieldId="fieldId["+i+"]";
    %>
		<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="<%=strfieldId%>" property="<%=strfieldId%>" />
	<% }%>
	
	<tr> 
    <td>
        	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
        		<tr>
					<td colspan="9" class="labeltext">Select Attribute Parameter&nbsp;&nbsp;
			        	<a  href="javascript:void(0)"  onclick="window.open('addAttributeDetail.do?','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')" ><img src="<%=localBasePath%>/images/lookup.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a>
					</td>
				</tr>	
          		<tr>
            		<td >&nbsp;</td>
            	</tr>
            	<tr>
               		<td valign="top" align="right" colspan="5">
               		
                		<table cellpadding="0" cellspacing="0" border="0" width="100%" >
                  			<tr>
                    			<td class="small-gap" colspan="5">&nbsp;</td>
                  			</tr>
                  			<tr>
                    			<td colspan="6">
			                    	<table width="100%" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" height="15%" >
     								   <tr>
						                    <td align="left" class="tblheader-bold" valign="top" colspan="6">
					                          &nbsp; &nbsp; &nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.useraccount" /> 
					                          &nbsp; &nbsp; &nbsp;[ &nbsp; 
					                          <%= "Account"+viewUserfileDatasourceUserAccountForm.getId() %>
					                          &nbsp; ]&nbsp; &nbsp; &nbsp;
 			        		                </td>
 			        		           </tr>
 			        		            <tr>
											
						                    <td align="left" class="tblheader-bold" valign="top" colspan="6">
					                           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Attribute 
 			        		                </td>
 			        		           </tr>
 			        		           <% List listAttrbuteValue=viewUserfileDatasourceUserAccountForm.getListAttrbuteValue();
			                  				if(listAttrbuteValue.size()>0){ 
			                  				
												for(int i=0;i<listAttrbuteValue.size();i++){
									        		String strfieldValue="fieldValue["+i+"]";
									        		String strattributeName="attributeName["+i+"]";
									   %>	
			                	 	   <tr>
			                	 	   		<html:hidden name="viewUserfileDatasourceUserAccountForm" styleId="<%=strattributeName%>" property="<%=strattributeName%>" />
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
			                	 	   		 <td class="topHLine" height="20%" colspan="3" align="left" width="40%">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="viewUserfileDatasourceUserAccountForm" property="<%=strattributeName%>" />
 			        		                </td>
 			        		                <td class="topHLine" height="20%" colspan="2" align="left">
					                        	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="<%=strfieldValue%>" property="<%=strfieldValue%>" /><font color="#FF0000"> *</font>
					                        	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:removeAttribute('<%=i%>')"><img src="<%=localBasePath%>/images/minus.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a>
 			        		                </td>
			                	 	   </tr>
			                	 	   <% }%>
			                	 	   
									   <%} %>	   
			                	 	   <tr>
 			        		             	<td align="center"  class="tblheader-bold" valign="top"  width="2%"> 
												&nbsp;					
											</td>   
 			        		                <td align="left" class="tblheader-bold" valign="top" colspan="6" >
					                            &nbsp; &nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.userpassword" />
 			        		                </td>
			                	 	   </tr>
			                	 	   <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left" width="40%">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.passwordcheckenable" /> 
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="passwordCheckEnable" property="passwordCheckEnable" /><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	   </tr>  
			                	 	   
			                	 	   <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.password" />   
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:password name="viewUserfileDatasourceUserAccountForm" styleId="password" property="password" /><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	   </tr>
			                	 	   <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.encryptiontype" />   
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="encryptionType" property="encryptionType" /><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	   </tr>
			                	 	   <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <bean:message bundle="servermgrResources" key="servermgr.userfile.accountstatus" />   
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="accountStatus" property="accountStatus" /><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	   </tr>
			                	 	   <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.creditlimit" /> 
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="creditLimit" property="creditLimit"/><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <bean:message bundle="servermgrResources" key="servermgr.userfile.expirydate" /> 
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="expiryDate" property="expiryDate"/><font color="#FF0000"> *</font>
 			        		                	&nbsp; <font color="#FF0000">[mm/dd/yyyy]</font>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <bean:message bundle="servermgrResources" key="servermgr.userfile.customerservice" /> 
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="customerService" property="customerService"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.customertype" /> 
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="customerType" property="customerType"/><font color="#FF0000"> *</font>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.servicetype" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="serviceType" property="serviceType"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.callingastationid" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="callingStationId" property="callingStationId"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.calledstationid" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="calledStationId" property="calledStationId"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.emailid" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="emailId" property="emailId"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.usergroup" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="userGroup" property="userGroup"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.usercheckitem" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="userCheckItem" property="userCheckItem"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.userrejectitem" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="userRejectItem" property="userRejectItem"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.userreplyitem" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="userReplyItem" property="userReplyItem"/>
 			        		                </td>
			                	 	    </tr>
			                	 	    <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.accesspolicy" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="accessPolicy" property="accessPolicy"/>
 			        		                </td>
			                	 	     </tr>
			                	 	     <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.radiuspolicy" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="radiusPolicy" property="radiusPolicy"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.concurrentloginpolicy" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="concurrentLoginPolicy" property="concurrentLoginPolicy"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.ippoolname" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="ipPoolName" property="ipPoolName"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.cui" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="cui" property="cui"/>
 			        		                </td>
			                	 	      </tr>

			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.hotlinepolicy" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="hotlinePolicy" property="hotlinePolicy"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.param1" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="param1" property="param1"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.param2" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="param2" property="param2"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.param3" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="param3" property="param3"/>
 			        		                </td>
			                	 	      </tr>
			                	 	     <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.graceperiod" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="gracePeriod" property="gracePeriod"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.callbackid" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="callbackId" property="callbackId"/>
 			        		                </td>
			                	 	      </tr>
			                	 	       <tr>
			                	 	   		<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.profileusername" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="profileUserName" property="profileUserName"/>
 			        		                </td>
			                	 	      </tr>
			                	 	      
			                	 	      <tr>
											<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.macvalidation" />
					                        </td>    
 			        		                <td class="tblfirstcol" width="50%" height="20%" colspan="2" align="left">
					    						<html:select name="viewUserfileDatasourceUserAccountForm" styleId="macValidation" property="macValidation" size="1"  style="width :100px">
						   						<html:option value="true" >True</html:option>
						   						<html:option value="false" >False</html:option> 
												</html:select>	      
											</td>
			                	 	      </tr>
			                	 	      
			                	 	      <tr>
											<td class="topHLine">
			                	 	   			&nbsp;
			                	 	   		</td>
 			        		                <td class="topHLine" width="20%" height="20%" colspan="3" align="left">
					                           	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources" key="servermgr.userfile.groupbandwidth" />
					                        </td>    
 			        		                <td class="topHLine" width="50%" height="20%" colspan="2" align="left">
 			        		                	<html:text name="viewUserfileDatasourceUserAccountForm" styleId="groupBandwidth" property="groupBandwidth"/>
 			        		                </td>
			                	 	      </tr>
			                      </table>
			                    </td>
			                  
			                    <tr>
 			        		                <td width="50%" height="20%" colspan="2" align="right">
	 			        		                <input name="c_btnAdd" type="button" class="light-btn"  onclick="validateUpdate('<%=listAttrbuteValue.size()%>')" id="c_btnAdd" value="   Update  ">
 			        		                </td>    
 			        		                <td width="50%" colspan="1">
 			        		                	&nbsp;&nbsp;&nbsp;
 			        		                	 <input name="c_btnAdd" type="button" class="light-btn"  onclick="javascript:location.href='<%=localBasePath%>/listUserfileAccountInformation.do?netserverid=<%=viewUserfileDatasourceUserAccountForm.getNetServerId()%>&selectedFileName=<%=viewUserfileDatasourceUserAccountForm.getFileName()%>'" id="c_btnAdd" value="   Cancel  ">
 			        		                </td>
			                	 	    </tr>  
            		        	</tr>
		                </table>
					</td>			                
             </tr>
		</table>             
      </td>
    </tr>
</table> 
</html:form>
</html>
