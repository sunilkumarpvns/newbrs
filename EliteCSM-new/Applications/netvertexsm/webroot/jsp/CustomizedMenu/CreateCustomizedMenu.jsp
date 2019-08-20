<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.customizedmenu.CustomizedMenuForm"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.customizedmenu.TitleData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<script src="<%=basePath%>/js/commonfunctions.js"></script>
<%
	CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)request.getAttribute("customizedMenuForm");
	List<CustomizedMenuData> menuList = customizedMenuForm.getCustomizedMenuList();
%>

<script>
var isValidName;
$(document).ready(function(){
	document.getElementById("isContainer").checked=true;
	document.getElementById("openMethod").checked=true;
	document.getElementById("openMethodRow").style.display ='none';
	document.getElementById("urlRow").style.display ='none';
	document.getElementById("parameterRow").style.display ='none';
});
 
function validate(){ 		
	var isContainerArr = document.getElementsByName("isContainer");			
	if(isNull(document.forms[0].title.value)){
		alert("Menu Title must be specified.");
		document.forms[0].title.focus();
	}else if(!isValidName){
			alert("Enter valid name of menu");
	}else if( isContainerArr[1].checked && isNull(document.forms[0].url.value)){
		alert("Menu URL must be specified.");
		document.forms[0].url.focus();
	}else{
		document.forms[0].submit();		
	}		
}
function verifyFormat (){
	var searchName = document.getElementById("title").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.CUSTOMIZED_MENU%>',isMenuCall:'yes',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName(){
	var searchName = document.getElementById("title").value;
	searchName = searchName.trim();
	isValidName = verifyCustomizedMenuName({instanceType:'<%=InstanceTypeConstants.CUSTOMIZED_MENU%>',isMenuCall:'yes',searchName:searchName,mode:'create',id:''},'verifyNameDiv');	
}
$(document).ready(function(){
	setTitle('<bean:message  bundle="customizedMenuResources" key="csmmenumgmt.title"/>');
	$("#title").focus();
});



function displayUrlFields(){
	
	document.getElementById("openMethodRow").style.display ='';
	document.getElementById("urlRow").style.display ='';
	document.getElementById("parameterRow").style.display ='';
			
}

function hideUrlFields(){

	document.getElementById("openMethodRow").style.display ='none';
	document.getElementById("urlRow").style.display ='none';
	document.getElementById("parameterRow").style.display ='none';
			
}
</script>

<html:form action="/customizedMenumgmt.do?method=create"> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message  bundle="customizedMenuResources" key="csmmenumgmt.create.title"/>
			</td>
		  </tr>
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%"  align="left" border="0" > 
			  				   	 	
			   	 <tr> 
						<td align="left" class="captiontext" valign="top" width="30%" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.titlename" />')"/>
						</td> 
															
						<sm:nvNameField id="title" name="title" maxLength="255" size="25" value="${customizedMenuForm.title}"/>
				   </tr>
				   
				   <tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parentID" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parentID"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parentID" />')"/>
						</td> 
						<td align="left" valign="top" width="70%" >
							<html:select name="customizedMenuForm" styleId="parentID" tabindex="3" property="parentID" size="1" style="width: 180px;" >
								<html:option value="0">--Select--</html:option>
								<logic:iterate id="customizedMenuDataBean"  name="customizedMenuForm" property="parentIDList" type="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData">
									<%
										String parentID = ""+customizedMenuDataBean.getParentID();
										String customizedMenuId = ""+customizedMenuDataBean.getCustomizedMenuId();
										if(customizedMenuDataBean.getIsContainer().equalsIgnoreCase("Yes"))
										{
									%>
											<html:option value="<%=customizedMenuId%>"><%=customizedMenuDataBean.getTitle()%></html:option>
									<%
										}
									%>									
								</logic:iterate>
							</html:select>	      
						</td>
				   </tr>
				   
				   <tr> 
						<td align="left" class="captiontext" valign="top" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.order" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.order"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.order" />')"/>
						</td> 
						<td align="left"  valign="top" > 
						<html:select name="customizedMenuForm" styleId="order" tabindex="3" property="order" size="1" style="width: 40px;" >
							<html:option value="1" >1</html:option>
							<html:option value="2" >2</html:option>
							<html:option value="3" >3</html:option>
							<html:option value="4" >4</html:option>
							<html:option value="5" >5</html:option>
							<html:option value="6" >6</html:option>
							<html:option value="7" >7</html:option>
							<html:option value="8" >8</html:option>
							<html:option value="9" >9</html:option>
							<html:option value="10" >10</html:option>
						</html:select>
						</td> 
				   </tr>
				   
				   <tr> 
						<td align="left" class="captiontext" valign="top" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.isContainer" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.isContainer"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.isContainer" />')"/>
						</td> 
						<td align="left" class="labeltext" valign="top" >
						<div> 
							<html:radio property="isContainer" name="customizedMenuForm" styleId="isContainer" value="Yes" onclick="hideUrlFields()" tabindex="4"/>Yes
							<html:radio property="isContainer" name="customizedMenuForm" styleId="isContainer" value="No" onclick="displayUrlFields()" tabindex="4" />No
						</div>							 
						</td> 
				   </tr>
				   
				   <tr id="urlRow"  > 
						<td align="left"   class="captiontext" valign="top" width="30%" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.url" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.url"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.url" />')"/>
						</td> 
															
						<td align="left"   valign="top" width="70%"> 
							<html:text name="customizedMenuForm" tabindex="5" property="url" maxlength="255"  size="32" styleId="url"/><font color="#FF0000" > *</font> 	      
						</td>
				   </tr>		
			   	  
		   	  <tr id="parameterRow" > 
						<td align="left"  class="captiontext" valign="top" width="30%" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parameters" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parameters"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.parameters" />')"/>
						</td> 
						<td align="left"    valign="top" > 
							<html:text name="customizedMenuForm" property="parameters" maxlength="255"  size="32" styleId="parameters"  tabindex="6"/> 
						</td> 							
				  </tr>				   				   			  				   		   	 							 
				
			   	  <tr id="openMethodRow"  > 
						<td align="left"  class="captiontext" valign="top" >
							<bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod"/>','<bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod" />')"/>
						</td> 
						<td align="left"  class="labeltext" valign="top" >
						<div> 
						<html:radio property="openMethod" name="customizedMenuForm" styleId="openMethod" value="self" tabindex="7"/><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.samewindow" />
						<html:radio property="openMethod" name="customizedMenuForm" styleId="openMethod" value="blank" tabindex="7"/><bean:message bundle="customizedMenuResources" key="csmmenumgmt.openmethod.newwindow" />
						</div>							
						</td> 
				   </tr>
			   		  			  	
				 <tr>
				  		<td align="left" valign="top" ></td>
						<td ><input type="button" onclick="validate();" value="  Create  " class="light-btn" tabindex="8"/>
						<input type="button" value=" Cancel " tabindex="9" class="light-btn" onclick="javascript:location.href='<%=basePath%>/customizedMenumgmt.do?method=initSearch'"/></td>
				 </tr>
				 
			   </table>  
			</td> 
		</tr>
		
		<tr><td>&nbsp;</td></tr>		
		</table> 
	  </td> 
	</tr>			
	  <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>

