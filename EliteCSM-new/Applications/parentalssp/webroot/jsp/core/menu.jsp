<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@	page import="com.elitecore.ssp.web.home.forms.HomeForm"%>
<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.parental.SubscriberProfile"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Menu</title>
</head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/common.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menu.css" />
<script src="<%=request.getContextPath()%>/jquery/development/jquery-1.4.2.js"></script>
</head>
<%
	String childIndex =(String)request.getSession().getAttribute("childIndex");
	String basePath = request.getContextPath();
	HomeForm homeForm = (HomeForm) request.getSession().getAttribute(SessionAttributeKeyConstant.HOME_FORM);
	SubscriberProfile currentUser = null;
	SubscriberProfile[] childAccounts = null;  
  	if( homeForm != null ){
  		currentUser = homeForm.getCurrentUser();
  		childAccounts = homeForm.getChildAccounts();  		
  	}   	
%>
<style>
	.gray{
		background-color: #e7e7e7;
	}
	.white{
		background-color: white;
	}
</style>
<script>	
	$(document).ready(function(){
		setColor();
	});
  	function setColor(){  
  		
  		var current=document.getElementById("currentUserTd");  		  		  		  	
  		var child=document.getElementById("childDiv"+'<%=childIndex%>');  		
  		if(child!=null){   			
  			child.className="white";
  			current.className="gray";  			
  		}else{  	  			   		  		
  			current.className="white";  		
  		}
 		
  		if(document.getElementById("totalChild")!=null){
			var value=document.getElementById("totalChild").value;
			if(value>0){
		  		for(var i=1; i<=value; i++){
		  			if(i!=<%=childIndex%>){
		  				child=document.getElementById("childDiv"+i);  			  
		  				child.className="gray";
		  			}
				}
			}
  		}
  	}  	
</script>
<body>
<div style="width:100%">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="100%" valign="top" align="right"><table width="90%" align="right" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td style="background-image:url(<%=request.getContextPath()%>/images/leftpanel_bkgd.jpg)"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="left-header">Family Members</td>
                  </tr>
                  <tr>
                    <td height="10" bgcolor="#CCCCCC"></td>
                  </tr>
                  <%if(currentUser!=null){%>
                  <tr>
                    <td id="currentUserTd" onclick="setColor()" class="" >  
	                    <a href="<%=basePath%>/childAccountInfo.do?subscriberIdentity=<%=currentUser.getSubscriberID()%>&<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo">
	                  	<div  class="white" style="background-color:#e7e7e7;cursor: pointer;" >
							<div style="width: 100%;float:left;text-decoration: none" >
								<div style="width: 23%;float:left" >
									<div align="center" ><img  class="small-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></div>
								</div>
								<div class="member-name">
									<%=currentUser.getUserName()%>
								</div>
							</div>
	                    </div>
	                    </a>                  	                    	
                    </td>
                  </tr>
                  <tr>
                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
                  </tr>
                  <%}%>
                  <%if(childAccounts!=null && childAccounts.length>0){    
                	  int index=0;
                  %>
                  <logic:iterate id="childAccountObj" name="HOME_FORM" property="childAccounts" type="com.elitecore.netvertexsm.ws.cxfws.ssp.parental.SubscriberProfile">
                  <tr>
                  	<td  id="childDiv<%=++index%>">
	                  	<a href="<%=basePath%>/childAccountInfo.do?subscriberIdentity=<bean:write name="childAccountObj" property="subscriberID"/>&childIndex=<%=index%>&<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo">
	                  	<div class="gray"   style="background-color:#e7e7e7;cursor: pointer;" >
							<div style="width: 100%;float:left;text-decoration: none;" >
								<div style="width: 23%;float:left" >
									<div align="center"><img class="small-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></div>
								</div>
								<div class="member-name">
									<%=childAccountObj.getUserName()%>
									<%-- <%childAccountObj.setSelectedPortel(RequestAttributeKeyConstant.PARENTAL_PORTAL);%> --%>									
								</div>
							</div>
	                    </div>
	                    </a>
                   	</td>
                  </tr>
                  <tr>
                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
                  </tr>
                  </logic:iterate>
                  	<input type="hidden" name="totalChild" value="<%=index%>" id="totalChild"/>
                  	<%homeForm.setChildAccounts(childAccounts);%>
                  <%}%>                                
                </table></td>
              </tr>
              <tr>
                <td height="10"></td>
              </tr>
            </table></td>
            
          </tr>
        </table></td>
  </tr>
</table>
</div>
</body>
</html>
