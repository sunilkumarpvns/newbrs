<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@	page import="com.elitecore.ssp.web.home.forms.HomeForm"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>

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
	SubscriberProfile tempCurrentUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
	SubscriberProfile[] childAccounts = null;  
	SubscriberProfile currentUser=null;
  	if( homeForm != null ){
  		currentUser=homeForm.getCurrentUser();
  		childAccounts = homeForm.getChildAccounts();
  	}  
  	 String[] selectedSubscriber = (String[])request.getAttribute(RequestAttributeKeyConstant.SELECTED_SUBSCRIBER); 
%>
<style>
	.gray{
		background-color: #e7e7e7;
	}
	.white{
		background-color: white;
	}
	.align-check-box{
		width: 23%;
		float:left;
		margin-top:5px;
	}
	.sub-div{
		width: 100%;
		float:left;
		text-decoration: none;
	}
	.base-div{
	}
</style>
<script>	
	$(document).ready(function(){
		setColor();
		<%
		if(selectedSubscriber != null){
			for(int i=0; i<selectedSubscriber.length; i++){				
		%>		
			$('input:checkbox[name=select]').each(function () {
		           if(this.value == '<%=selectedSubscriber[i]%>'){
		        	   this.checked = true;
		           }
			});	  
	    <%}}%>  
	    
	    var selectedCheckBoxCount = $("input:checked").length + 1;
	    var totalCheckBoxCount = $('input:checkbox').length;
	    
	    if(selectedCheckBoxCount == totalCheckBoxCount){
	    	$('input[name=toggleAll]').attr('checked', true);
 	    }
	});
	
  	function setColor(){  
  		
  		var current=document.getElementById("currentUserTd");  		  		  		  	
  		var child=document.getElementById("childDiv"+'<%=childIndex%>');  		
  		
  		if(current != null){
	  		if(child!=null){   			
	  			child.className="white";
	  			current.className="gray";  			
	  		}else{  	  			   		  		
	  			current.className="white";  		
	  		}
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
  	
  	function clearFilter(){
  		$("#actionValue").val("clearfilter");
		$("#searchsubscriberMenu").submit();
	}
	function addMember(){
		$("#actionValue").val("");
		$("#searchsubscriberMenu").submit();
	}
	
	function  checkAll(){	
		
	 	if( document.getElementById("toggleAll").checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (var i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.getElementById("toggleAll").checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (var i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
	}
	
	function selectMembers(department){
		var ID = "deptCheckBox"+department;
  		if($("#"+ID).is(':checked')){
  			$('input:checkbox[class='+department+']').each(function () {	           
 	       	   this.checked = true;	          
 			});			
		}else{
			$('input:checkbox[class='+department+']').each(function () {	           
		       	   this.checked = false;	          
			});
		}
 	}

</script>
<body>
<%
	Map<String,Set<SubscriberProfile>> deptWiseSubscriberProfileMap =  (Map<String,Set<SubscriberProfile>>)request.getSession().getAttribute(SessionAttributeKeyConstant.DEPT_WISE_SUBSCRIBERS);
%>
<html:form action="/initSearchSubscriber" method="post" styleId="searchsubscriberMenu">
<html:hidden property="action" styleId="actionValue" value="search"/>
<div style="width:100%">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="100%" valign="top" align="right"><table width="95%" align="right" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td style="background-image:url(<%=request.getContextPath()%>/images/leftpanel_bkgd.jpg)"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                  <tr>
                    <td class="left-header">Employees</td>
                  </tr>
                  <%if(tempCurrentUser != null && tempCurrentUser.getParentID() == null){%>
	                  <tr>
	                	<td>	                		
	                		<div style="width: 18%;float:left;text-align: center;" class="member-name" >
	                			<input type="checkbox" id="toggleAll" name="toggleAll" onclick='checkAll();' value=""/>
	                		</div>
	                		<div class="member-name" style="vertical-align: top">
	                  			<input type="button" onclick="clearFilter();" value="Clear List" class="orange-btn"/>
								<input type="button" onclick="addMember();" value="Add Member" class="orange-btn" style="width:90px"/>
							</div>							
	                  	</td>
	                  </tr>
                  <%}%>
                  <tr>
                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
                  </tr>

                  <%
              	int index=0;	
                  if(currentUser!=null){                	  
                  %>
                  	  
	                  <tr>
	                    <td class="dept-header" height="30px" style="padding-right:50px" >	                     
		                  <div  class="white" style="background-color:#e7e7e7;cursor: pointer;" >
							<div class="sub-div" >
			                    <div class="align-check-box" >
									<div align="center"><input type="checkbox" name="deptCheckBox"  id="deptCheckBox<%=currentUser.getDepartment()%>"  onclick="selectMembers('<%=currentUser.getDepartment()%>')" value='<%=currentUser.getSubscriberID()%>' /></div>
								</div>					
								<a href="<%=basePath%>/deptWiseUsageInfo.do?departmentName=<%=(currentUser.getDepartment()!=null?currentUser.getDepartment():SessionAttributeKeyConstant.DEPARTMENT_OTHERS)%>">					                    
								<div class="member-name">
									<% if(currentUser.getDepartment()!=null && currentUser.getDepartment().trim().length()>0){%>
										<%=currentUser.getDepartment()%>
									<%}else{%>
										Others
									<%}%>
								</div>		
								</a>								                   
		                   	</div>
		                  </div>
		                </a>                  	                    	
	                    </td>
	                  </tr>
	                  <tr>
	                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
	                  </tr>	                  
                  
	                  <tr>
	                    <td id="currentUserTd" onclick="setColor()" class="" height="30px" style="padding-right:50px" >	                     
		                  <div  class="white" style="background-color:#e7e7e7;cursor: pointer;" >
							<div class="sub-div" >
			                    <div class="align-check-box" >
									<div align="center"><input type="checkbox" name="select" class="<%=currentUser.getDepartment()%>" value='<%=currentUser.getSubscriberID()%>' /></div>
								</div>
								<a  href="<%=basePath%>/childAccountInfo.do?subscriberIdentity=<%=currentUser.getSubscriberID()%>&<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo">		                    	
								<div class="member-name">
										<%=currentUser.getUserName()%>
								</div>
								</a>		                    
		                   	</div>
		                  </div>
		                </a>                  	                    	
	                    </td>
	                  </tr>
	                  <tr>
	                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
	                  </tr>
	                  <%
	                  	Set<SubscriberProfile> parentDeptWiseChildProfiles = null;
	                  	if(currentUser.getDepartment()==null){
	                  		parentDeptWiseChildProfiles = deptWiseSubscriberProfileMap.get("Others");	                  		
	                  	}else{
	                  		parentDeptWiseChildProfiles = deptWiseSubscriberProfileMap.get(currentUser.getDepartment());	                  		
	                  	}
	                  	if(parentDeptWiseChildProfiles!=null){
	                    for(SubscriberProfile childAccountObj:parentDeptWiseChildProfiles){
	                  %>
	                  
	                  <tr>
						<td  id="childDiv<%=++index%>" height="30px" style="padding-right:50px" >			                  	
		                  		<div class="gray"   style="background-color:#e7e7e7;" >
		                  			<div class="sub-div" >
				                  		<div class="align-check-box" >
											<div align="center"><input type="checkbox" name="select" class="<%=childAccountObj.getDepartment()%>" value='<%=childAccountObj.getSubscriberID()%>' /></div>
										</div>
										<a href="<%=basePath%>/childAccountInfo.do?subscriberIdentity=<%=childAccountObj.getSubscriberID()%>&childIndex=<%=index%>&<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo">				                  	
										<div class="member-name" style="cursor: pointer" >																					
											<%=childAccountObj.getUserName()%>																															
					                    </div>
					                    </a>
				                    </div>				                    
			                    </div>			                    
		                   	</td>
		               </tr>
		               <tr>
		                  <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
		               </tr>
	                  <%}}%>
	                  
                  <%}%>
                  
                  <%
                  	if(childAccounts!=null && childAccounts.length>0){                   	  
                	  
                	  boolean isDepartmentChanged = false;
                	  
                	  
                	  Set<String> departmentSet =  deptWiseSubscriberProfileMap.keySet();                	  
                	  /* for( SubscriberProfile childProfile:childAccounts){                		  
                		  		departmentSet.add(childProfile.getDepartment());                		  
                	  } */
                	  
                  if(departmentSet.size()>0){
                  for(String departmentName : departmentSet){                	                  	  
                	  if(currentUser!=null){
	                	  if(currentUser.getDepartment()==null || departmentName.equalsIgnoreCase(currentUser.getDepartment())){	                		  
	                		 continue; 
	                	  }
                	  }
                	  Set<SubscriberProfile> deptWiseChildProfiles = deptWiseSubscriberProfileMap.get(departmentName);                
                  if(departmentName!=null && departmentName.trim().length()>0){
               	  %>
                  <tr>
	                    <td class="dept-header" height="30px" style="padding-right:50px" >	                     
		                  <div  class="white" style="background-color:#e7e7e7;cursor: pointer;" >
							<div class="sub-div" >
			                    <div class="align-check-box" >
									<div align="center"><input type="checkbox" name="deptCheckBox" id="deptCheckBox<%=departmentName%>" onclick="selectMembers('<%=departmentName%>')" value='<%=(currentUser!=null) ? currentUser.getSubscriberID():""%>' /></div>
								</div>		
								<a href="<%=basePath%>/deptWiseUsageInfo.do?departmentName=<%=(departmentName!=null?departmentName:SessionAttributeKeyConstant.DEPARTMENT_OTHERS)%>">								                    
								<div class="member-name">										 
										<%=departmentName%>										 								
								</div>
								</a>										                   
		                   	</div>
		                   </div>		                                 	                    
	                    </td>
	              </tr>
	              <tr>
	                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
	              </tr>
                  <%} 
                  for(SubscriberProfile childAccountObj:deptWiseChildProfiles){                                    	                  	                  	
	                  	if(currentUser == null || !(currentUser.getSubscriberID().equalsIgnoreCase(childAccountObj.getSubscriberID()))){	                  	
	                  	application.setAttribute("selectedPortal", RequestAttributeKeyConstant.ENTERPRISE_PORTAL);
	              %>
	                  
			 		<tr>
						<td  id="childDiv<%=++index%>" height="30px" style="padding-right:50px" >			                  	
		                  		<div class="gray"   style="background-color:#e7e7e7;" >
		                  			<div class="sub-div" >
				                  		<div class="align-check-box" >
											<div align="center"><input type="checkbox" name="select" class="<%=(childAccountObj.getDepartment()==null)?"Others":childAccountObj.getDepartment()%>" value='<%=childAccountObj.getSubscriberID()%>' /></div>
										</div>
										<a href="<%=basePath%>/childAccountInfo.do?subscriberIdentity=<%=childAccountObj.getSubscriberID()%>&childIndex=<%=index%>&<%=SessionAttributeKeyConstant.SELECTED_LINK%>=acctinfo">				                  	
										<div class="member-name" style="cursor: pointer" >																					
											<%=childAccountObj.getUserName()%>																															
					                    </div>
					                    </a>
				                    </div>				                    
			                    </div>			                    
		                   	</td>
		                  </tr>
		                  <tr>
		                    <td bgcolor="#e7e7e7"><img src="<%=request.getContextPath()%>/images/hr-line.jpg" width="100%" height="6"></td>
		                  </tr>
	                 <%}%>   	                                                  
                  <%}%>
                  <%}%>
                  		<input type="hidden" name="totalChild" value="<%=index%>" id="totalChild"/>
                  		<%homeForm.setChildAccounts(childAccounts);%>
                  <%}}%>                                
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
</html:form>
</body>
</html>
