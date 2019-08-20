<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%
	String basePath = request.getContextPath();	
	SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	if(currentUser == null){
		currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
	}
	String selectedLink = (String)request.getSession().getAttribute(SessionAttributeKeyConstant.SELECTED_LINK);	
    	request.getSession().removeAttribute(SessionAttributeKeyConstant.SELECTED_LINK);
    	String childIndex=(String)request.getSession().getAttribute("childIndex");
%>

<html>
<script src="<%=request.getContextPath()%>/jquery/development/jquery-1.4.2.js"></script>
<title>Enterprise</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/common.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menu.css" />
<style>
	a:hover {text-decoration:none;}
</style>
	<script type="text/javascript">    	
		$(document).ready(function(){
			setSelectionLink();			
		});				    	
    	function setSelectionLink(){
    		var acctInfo=document.getElementById("acctinfo");
    		var usageInfo=document.getElementById("usageinfo");
    		var parentalControl=document.getElementById("parentalcontrol");
    		var prmOffer=document.getElementById("prmoffer");
    		var quotatrans=document.getElementById("quotatrans");
    		var quotaAdvance=document.getElementById("quotaAdvance");
    		var serviceActivation = document.getElementById("serviceactivation");
    		if(acctInfo.id == '<%=selectedLink%>'){    			   					
    			acctInfo.className="active";
    			usageInfo.className="line";
    			parentalControl.className="line";
    			prmOffer.className="line";
    			quotatrans.className="line";
    			quotaAdvance.className="line";
    		}else if(usageInfo.id == '<%=selectedLink%>'){    			
    			acctInfo.className="line";
    			usageInfo.className="active";
    			parentalControl.className="line";
    			prmOffer.className="line";
    			quotatrans.className="line";
    			quotaAdvance.className="line";
    		}else if(parentalControl.id == '<%=selectedLink%>'){    			
    			acctInfo.className="line";
    			usageInfo.className="line";
    			parentalControl.className="active";
    			prmOffer.className="line";
    			quotatrans.className="line";
    			quotaAdvance.className="line";
    		}else if(prmOffer.id == '<%=selectedLink%>'){
    			acctInfo.className="line";
    			usageInfo.className="line";
    			parentalControl.className="line";
    			prmOffer.className="active";
    			quotatrans.className="line";
    			quotaAdvance.className="line";
    		}else if(serviceActivation.id == '<%=selectedLink%>'){
    			acctInfo.className="line";
    			usageInfo.className="line";
    			parentalControl.className="line";
    			prmOffer.className="line";
    			serviceActivation.className="active";
    			quotatrans.className="line";
    			quotaAdvance.className="line";
    		}else if(quotatrans.id == '<%=selectedLink%>'){
    			acctInfo.className="line";
    			usageInfo.className="line";
    			parentalControl.className="line";
    			prmOffer.className="line";
    			serviceActivation.className="line";
    			quotatrans.className="active";
    			quotaAdvance.className="line";
    		}else if(quotaAdvance.id == '<%=selectedLink%>'){
    			acctInfo.className="line";
    			usageInfo.className="line";
    			parentalControl.className="line";
    			prmOffer.className="line";
    			serviceActivation.className="line";
    			quotatrans.className="line";
    			quotaAdvance.className="active";
    		}   
    		
    	}
    	
    	function createNewForm(id, action) {
    		var newform = document.createElement('form');
    		if(id != undefined){
    			newform.setAttribute("id",id);
    		}
    		if(action != undefined){
    			newform.setAttribute("action",action);
    		}
    		document.body.appendChild(newform);
    		$(newform).attr("method","post");
    		return newform;
    	}

    	/**
    	 * Function Navigate Page
    	 * @param action : navigation page action 
    	 * @param appendAttrbId : attribute append to Form
    	 */
    	function navigatePage(action) {
    		createNewForm("newFormData",action);
    		//var name = $("#"+appendAttrbId).attr("name");
    		//var val = $("#"+appendAttrbId).val();
    		$("input:checkbox[name=select]:checked").each(function(){
    			$("#newFormData").append("<input type='hidden' name='select' value='"+$(this).val()+"'>");    			
    		});
    		$("#newFormData").append("<input type='hidden' name='childIndex' value='<%=childIndex%>'>");
    		
    		if(action == "enterpriseMemberAcctInfo.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='acctinfo'>");	
    		}else if(action == "childAccountUsageInfo.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='usageinfo'>");
    		}else if(action == "accessControl.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='parentalcontrol'>");
    		}else if(action == "bod.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='serviceactivation'>");
    			$("#newFormData").append("<input type='hidden' name='selectedPortal' value='enterprise'>");    			
    		}else if(action == "promotional.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='prmoffer'>");
    			$("#newFormData").append("<input type='hidden' name='selectedPortal' value='enterprise'>");    			
    		}else if(action == "quotatransfer.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='quotatrans'>");
    			$("#newFormData").append("<input type='hidden' name='selectedPortal' value='enterprise'>");    			
    		}else if(action == "quotaAdvance.do"){
    			$("#newFormData").append("<input type='hidden' name='<%=SessionAttributeKeyConstant.SELECTED_LINK%>' value='quotaAdvancePage'>");
    			$("#newFormData").append("<input type='hidden' name='selectedPortal' value='enterprise'>");    			
    		}
    		
    		$("#newFormData").submit();    		
    	}	
    	
    </script>
   
<body>
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td bgcolor="#0074c5">
    	<table width="80%" border="0" align="right" cellpadding="0" cellspacing="0">
          <tr>
            <td width="88%"></td>
            <td width="7%" bgcolor="#000000" height="5"></td>
              <td width="5%"></td>
          </tr>
          <tr>
            <td height="25">&nbsp;</td>
            <td class="wlc-txt"><div align="center"><a href="<%=basePath%>/logout.do">Log out</a></div></td>
            <td>&nbsp;</td>
          </tr>        
        </table>
       </td>
  </tr>
  <tr>
    <td>
    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
      		<tr>
        		<td width="3%" bgcolor="#0074c5">&nbsp;</td>
        		<td width="20%" align="center"><img src="<%=request.getContextPath()%>/images/logo.jpg" alt="" /></td>
        		<td width="77%" valign="top">
        			<table width="100%" border="0" cellspacing="0" cellpadding="0">
	          			<tr>
	            			<td bgcolor="#0074c5" height="30">&nbsp;</td>
	          			</tr>
	          			<tr>
	            			<td bgcolor="#0074c5">
	            				<table width="80%" border="0" align="right" cellpadding="0" cellspacing="0">
	              					<tr>
	                					<td width="9%">&nbsp;</td>
	                					<td width="84%" height="20"  class="wlc-txt"><div align="right">Welcome <%=currentUser.getUserName()%></div></td>
	                					<td width="7%">&nbsp;</td>	                
	              					</tr>
	            				</table>
	            			</td>
	          			</tr>
	          			<tr>
	            			<td height="31" class="menu_bg">
	            					<table width="100%" border="0" cellspacing="0" cellpadding="0" height="31">
	              						<tr>
	         								<td width="100%">
	         									<div class="menu">
		                    						<ul>
		                    							<li id="simple">&nbsp;&nbsp;</li>
		                      							<li class="line" id="acctinfo" ><a  href="<%=basePath%>/enterpriseMemberAcctInfo.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo"    >Account Info</a></li>
		                      							
		                      							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="usageinfo" ><a href="<%=basePath%>/childAccountUsageInfo.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=usageinfo"   >Usage Info</a></li>		                      									                      							
		                       							
		                       							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="parentalcontrol"><a  href="<%=basePath%>/accessControl.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=parentalcontrol"   >Enterprise Control</a></li>
		                       							
		                       							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="serviceactivation"><a  href="<%=basePath%>/bod.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=serviceactivation&selectedPortal=enterprise"    >Service Activation</a></li>
		                       							
		                       							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="prmoffer"><a  href="<%=basePath%>/promotional.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=prmoffer&selectedPortal=enterprise"    >Promotional Offer</a></li>
		                      							
		                      							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="quotatrans"><a  href="<%=basePath%>/quotatransfer.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=quotatrans&selectedPortal=enterprise"    >Quota Transfer</a></li>
		                      							
		                      							<li class="line">&nbsp;| &nbsp;</li>
		                      							<li class="line" id="quotaAdvance"><a  href="<%=basePath%>/quotaAdvance.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=quotaAdvancePage&selectedPortal=enterprise"    >Quota Advance</a></li>
		                    						</ul>
	                  							</div>
	                 						</td>
	                						<td width="16%" align="left" >&nbsp;</td>
	              						</tr>
	            					</table>
	            				</td>
	          				</tr>
        			</table>
        		</td>
      		</tr>
    	</table>
    </td>
  </tr>
</table>
</body>
</html>
