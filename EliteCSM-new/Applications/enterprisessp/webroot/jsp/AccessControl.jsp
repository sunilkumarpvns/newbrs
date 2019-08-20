
<%@ page import="com.elitecore.ssp.util.ChildAccountUtility"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="com.elitecore.ssp.web.parentalcontrol.forms.AccessControlForm"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>

<%@include file="/jsp/core/commonheader.jsp" %>

<head>
   <title>Service Selection Portal</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">	
</head>
<%	
	SubscriberProfile currentUserObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	boolean isChild;
	if(currentUserObj.getParentID()==null || currentUserObj.getParentID()==currentUserObj.getSubscriberID()){
		isChild=false;
	}else{
		isChild=true;
	}
	
	AccessControlForm accessControlForm = (AccessControlForm) request.getAttribute("accessControlForm");
	AddOnPackage[] promotionalData = null;
	List<ParentalPolicy> parentalPolicyData=null;
	long usageControlAddOnId=0;
	String[] colorsName=null;
	
  	if( accessControlForm != null ){
  		promotionalData = accessControlForm.getPromotionalData();  
  		parentalPolicyData = accessControlForm.getParentalPolicyList();  	  		
  		colorsName=ChildAccountUtility.accessControlPoliciesColor;
  		usageControlAddOnId=accessControlForm.getUsageControlAddOn();
  	}  	  	 
  	
  	AddOnPackage[] quotaControlPolicies = ChildAccountUtility.getQuotaControlPolicies(promotionalData);
  	AddOnPackage[] accessControlPolicies = ChildAccountUtility.getAccessControlPolicies(promotionalData);
  	
%>
<!-- COLORS TABLE CODE BEGIN -->
<%! String daysLabels[]={"Day/Time","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};%>
<script>
	var isSubmitable=false;	
	var boxColor="white";
 	var startStop=0; 	
 	var days=new Array("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"); 	
 	var day="Monday";
 	var row,col,hour,day;
 	var dataAr=new Array(7);
 	for(var i=0; i<7; i++){
 		dataAr[i]=new Array(24);
 	} 	
	$(document).ready(function(){
		arrangePolicy();			
	});	
	function arrangePolicy(){		
		'<%	String time="";
			String days="";
			String boxId="";
			long addOnId;
			long addOnIdTemp;
			String str="";
		 	String[] colors=ChildAccountUtility.accessControlPoliciesColor;
		 	int adOnIndex=0;
		 	if(parentalPolicyData.size()>0){		 		
			for(ParentalPolicy obj:parentalPolicyData){				
				boolean isPolicyDisplayable=false;
				if(accessControlPolicies != null){
					for(AddOnPackage pdObj:accessControlPolicies){
						if(pdObj.getAddOnPackageID().equals(obj.getAddOnPackageID())){														
							isPolicyDisplayable=true;							
							break;
						}
					}
				} 
				
				if(isPolicyDisplayable==false){
					continue;
				}
				
				if(obj==null)
					continue;
				
				time=obj.getTimePeriod();
				days=obj.getDaysOfTheWeek();
				addOnId=obj.getAddOnPackageID();	
				System.out.println("Time : "+time);
				System.out.println("days : "+days);
				System.out.println("addOnId : "+addOnId);
				String daysAr[]={""};
				if(days!=null && days.length()>0)
					daysAr=days.split(",");
				
				String timeAr[]={""};
				if(time!=null && time.length()>0)
					timeAr=time.split("-");
				
				if(accessControlPolicies != null){
					 
					for(int i=0; i<accessControlPolicies.length; i++){					 
						addOnIdTemp=accessControlPolicies[i].getAddOnPackageID();									 
						if(addOnIdTemp==addOnId){
							adOnIndex=i;						 
							break;
						}
						 
					}				
				}
				str+=","+adOnIndex+":"+addOnId;
				if(daysAr!=null && daysAr.length>0){										
				for(int i=0; i<daysAr.length; i++){
										
						if(timeAr!=null && timeAr.length>=1){
						if(timeAr[0]!=null && timeAr[0].trim().length()>0 && timeAr[1]!=null && timeAr[1].trim().length()>0){
							int end = 0;
							if(timeAr[1].contains(":")){
								timeAr[1] = timeAr[1].substring(0, timeAr[1].indexOf(":"));
								end = Integer.parseInt(timeAr[1]);								 
							}else{
								end = Integer.parseInt(timeAr[1]);
							}
							 
						for(int j=Integer.parseInt(timeAr[0]); j<=end; j++){							
							int d1=Integer.parseInt(daysAr[i]);
							int row=d1-1;
							boxId=daysLabels[d1]+j;												
					%>';		
					dataAr['<%=d1-1%>']['<%=j%>']='<%=colors[adOnIndex]%>';					
					$('#'+'<%=boxId%>').css('background-color', '<%=colors[adOnIndex]%>');					
					'<% 					
					}}}}}}			
		 }%>';			 
		 <%
		 	String  areConfiguredPoliciesOverloaded = (String)request.getAttribute("areConfiguredPoliciesOverloaded");		 	
		 	if(areConfiguredPoliciesOverloaded.equalsIgnoreCase("true")){
		 %>
		 		alert(" Too many policies configured ! please reduce. ");
		 <%
	}
		 %>
	}
 	 		
	function setBoxColor(field){				
		id=field.id;
		index=id.indexOf('y');
		var length=id.length;	
		var day=id.substr(0,index+1);
		hour=id.substr(index+1,length);		
		var row;//=days.indexOf(day);
		for(var x=0;x<days.length;x++){
			if(days[x]==day){
				row=x;
			}
		}
		col=hour;		
		
		if(startStop==0){			
			$('#'+id).click(function() {
				startStop=1;
		    	$('#'+id).css('background-color', boxColor);
		    	dataAr[row][col]=boxColor;		    	
			});						
		}else{					
		    $('#'+id).css('background-color', boxColor);		    
			$('#'+id).click(function() {
				startStop=0;
		    	$('#'+id).css('background-color', boxColor);		    	
			});
			dataAr[row][col]=boxColor;
		}				
	}

	function submitDataToActionClass(){		
		var confirmation = confirm("Are you sure to Apply Enterprise Control ?");
		if(confirmation){
			document.getElementById("boxesNameArray").value=dataAr.join(",");
			$("input:checkbox[name=select]:checked").each(function(){
	   			$("#accessControlForm").append("<input type='hidden' name='select' value='"+$(this).val()+"'>");
	    	});
			document.accessControlForm.submit();
		}
	}		
	
	function setSchemeColor(color){
		boxColor=color;
		startStop=0;		
	}
	function showData(){	
		var id=document.getElementById("usageControlAddOnId").value;
		var description="";		
		 
		<%	
			AddOnPackage promData=null;	
			String data="";				
			if(quotaControlPolicies != null){									
					for(AddOnPackage obj:quotaControlPolicies){						 
			%>
						if(<%=obj.getAddOnPackageID()%> == id){								
						description="<%=obj.getDescription()%>";						
								}
			<%		}
				}	
			%>			 
 				
			
		if(description.length>0){
			document.getElementById("policyData").innerHTML=description;			
		}else{
			document.getElementById("policyData").innerHTML="No data found.";
		}			
		document.getElementById("policyData").style.display = "block";			
		
		$( "#policyData" ).dialog({				
			autoOpen: false,
			show: {
				 effect: "blind",
				 duration: 500
			},
			hide: {
				 effect: "explode",
				 duration: 500
			},			
			height: "auto",
			width: 450,
			title: "Usage Profile"				
		});				
		$( "#policyData" ).dialog("open");
		$("#policyData").closest('.ui-dialog').children('.ui-dialog-titlebar').addClass("dialog-title");
	}
	
	
	function clearAccessControlPolicies(){
		var confirmation = confirm("Are you sure to Clear Policies ?");
		if(confirmation){
			for(var weekDay=0; weekDay<7; weekDay++){
				for(var hour=0; hour<24; hour++){
					var id=days[weekDay]+hour;
					$('#'+id).css('background-color', 'white');
					dataAr[weekDay][hour]='white';
				}			
			}			
		}
	}
</script>
<body>
<html:form action="/accessControl" method="post" styleId="accessControlForm">
<div class="border">
<div style="">
<table style="margin-right:5px;margin-left: 5px;margin-top: 10px;margin-bottom: 10px" width="97%" cellpadding="0" cellspacing="0" border="0"  >
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>	
	<tr> 		  		   	
   		<td colspan="2" align="left" valign="top">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name"><%=childObj.getUserName()%> </td></tr>
   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
   			</table>
   		</td>   		
	</tr>	 
	<tr>   		
   		<td class="smallgap">&nbsp;</td>
	</tr>
	<tr>
	<td>
	<table style="border:1px solid gray" width="100%"> <!-- ffd7cf -->
	<tr class="table-org" >   		
   		<td class="table-org-column" align="left" colspan="2"><bean:message key="parentalcontrol.usagecontrol"/></td>    		
	</tr>
	<tr>   		
   		<td class="smallgap">&nbsp;</td>
	</tr>	
	<tr> 		  		   	
   		<td align="left" valign="top">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
				<tr height="20px">   			   				
	   				<td class="table-org-column" style="padding-left:0px" width="150px" align="center"><bean:message key="parentalcontrol.setusagelimit"/></td>
	   				<td width="10px" align="center">&nbsp;</td>   		
	   				<td class="table-org-column" style="padding-left:0px" width="150px" align="center"><bean:message key="parentalcontrol.setspendinglimit"/></td>   		
				</tr> 
				<tr align="center">   		 
	   				<td align="center"  height="40px">		   				   
	   					<div style="margin-left:18px">	   						 						   				
	   						<div style="float:left">
	   						<%String tempAddon="";%>
			   					<select name="usageControlAddOnId" id="usageControlAddOnId" class="dropdown" style="width:100px" >
			   							<option value="0" >None</option>
			   							<%if(quotaControlPolicies != null){%>
					   						<%for(AddOnPackage obj:quotaControlPolicies){%>									
					   							<option <%=usageControlAddOnId==obj.getAddOnPackageID()?"selected":""%> value="<%=obj.getAddOnPackageID()%>"  ><%=obj.getAddOnPackageName()%></option>					   							
					   						<% }
					   					}%>
			   					</select>			   					
		   					</div>
		   					<div class="view-icon">
		   						<img class="view-icon-img" title="View Usafe Profile" src="<%=request.getContextPath()%>/images/view-icon.png" onclick="showData()" />
		   					</div>		   						   				
	   					</div>	   					 
	   				</td>
	   				<td width="10px" align="center"></td>   		
	   					<td align="center"  height="40px">		   				   
	   						<div style="margin-left:18px">	   						 						   				
	   							<div style="float:left">	   				
	   								<select class="dropdown" style="width:100px">
	   									<option value="" selected >None</option>
	   								</select>
	   							</div>
		   					<div class="view-icon">
		   						<img class="view-icon-img"  src="<%=request.getContextPath()%>/images/view-icon.png" />
		   					</div>	   						   				
	   					</div>	   					 
	   				</td>	   						   				   		
				</tr>				
			</table>
		</td>
	</tr>
	</table>
	</td>
	</tr>
	<tr>   		
   		<td class="smallgap">&nbsp;</td>
	</tr> 	
	<tr>
	<td>
	<table style="border:1px solid gray" width="100%">	 <!-- ffd7cf -->
	<tr class="table-org" >   		
   		<td class="table-org-column" align="left" colspan="2"><bean:message key="parentalcontrol.timeandaccesscontrol"/></td>    		
	</tr>
	<tr>   		
   		<td class="smallgap">&nbsp;</td>   		
	</tr>
	<!-- SELECT POLICY TABLE BEGIN -->
	<tr> 		  		   	
   		<td align="left" valign="top">   			
   			<table width="100%" cellpadding="0" cellspacing="0" border="0" >	 		
			<tr height="20px">
				<td colspan="3" class="table-org-column"><bean:message key="parentalcontrol.accesscontrolpolocy"/></td>
			</tr>
				<%int colorIndex=0;
				int addOnsCounter=0;
				if(accessControlPolicies != null){
					for(AddOnPackage obj:accessControlPolicies){												
							addOnsCounter++;
							if(addOnsCounter==1 || addOnsCounter-1%3==0){
						%>					
									<tr class="light-blue-border">
								<%
							}
				%>
													
						<td class="light-blue-border" width="33%" >
							<table>
								<tr>
									<td al class="bold-policy-text">
											<%if(!isChild){%>
											<input type="radio" onclick="setSchemeColor('<%=colorsName[colorIndex]%>')"  name="colorOption" id="colorOption" />
											<%}%>								
									</td>
									<td class="bold-policy-text"><input disabled="disabled" type="button" style="background-color: <%=colorsName[colorIndex]%>;" class="policy-clr-btn" /></td>
									<td class="bold-policy-text"><%=obj.getAddOnPackageName()!=null?obj.getAddOnPackageName():""%></td>
								</tr>
								<tr>
									<td class="policy-text"></td>							
									<td class="policy-text"></td>
									<td class="policy-text"><%=(obj.getDescription()!=null ? obj.getDescription():"&nbsp;")%></td>
								</tr>							
							</table>						
						</td>
				<% 
								colorIndex++;
						if(addOnsCounter%3==0){
				%>
							</tr>
				<%
						}				 
					}
				}
				%>
				<%if(addOnsCounter%3==0){%>
						<tr class="light-blue-border">
				<%}%>
				<%	
					// begin : code to make the access-control addon layout into three columns
					int colspan=0;
					if(addOnsCounter<3){
						colspan=3-addOnsCounter;
					}else if(addOnsCounter==3){
						colspan=(addOnsCounter);
					}else{
						colspan=(3-(addOnsCounter%3));						
					}
					// end : code to make the access-control addon layout into three columns
				%>
					<td class="light-blue-border" width="140px" colspan="<%=colspan%>">
						<table>
							<tr>
								<td class="bold-policy-text">
									<%if(!isChild){%>
									<input type="radio" checked="checked" onclick="setSchemeColor('white')"  name="colorOption" id="colorOption" />
									<%}%>
								</td>								
								<td class="bold-policy-text" ><input type="button" class="policy-clr-btn" style="background-color:white;border: 1px solid gray"/></td>
								<td class="bold-policy-text"><bean:message key="parentalcontrol.nopolicy"/></td>							
							</tr>
							<tr>
								<td class="policy-text">&nbsp;</td>							
								<td class="policy-text">&nbsp;</td>
								<td class="policy-text">&nbsp;</td>
							</tr>							
						</table>						
					</td>
			</tr>						
	 	</table>
	 	</td>
 	</tr>

 	<tr>   		
   		<td class="smallgap">&nbsp;</td>
	</tr>
	<!-- SELECT POLICY TABLE END -->	
	 <tr>
 		<td colspan="2" align="left" valign="top">
	 		<!-- COLORS TABLE CODE BEGIN -->
		 	<table> 				 
				<%for(String label:daysLabels){%>	
				<tr height="20px">
					<td width="75px" class=<%=(label.contains("/")?"multi-col-table":"table-org-column")%> ><%=label%></td>
					<% for(int hr=0; hr<24; hr++){ %>
					<td style="text-align:center" width="20px"  onmousemove="<%=(label.contains("/")?"":(isChild)?"":"setBoxColor(this)")%>" id="<%=(label.contains("/")?"":label+""+hr)%>" class="<%=(label.contains("/")?"table-org-column-no-padding":"table-td")%>"><%=(label.contains("/")?(hr<10?"0"+hr:hr):"&nbsp;")%></td>
					<%}%>
				</tr>			
				<%}%>
				<tr>
					<td>&nbsp;</td>
				</tr>						
			</table>
			<!-- COLORS TABLE CODE END -->
		</td>
	</tr>
	 </table>
 	</td>
 	</tr>
</table>
<table  width="100%"> <!-- ffd7cf -->
	<tr>
		<td  align="center" colspan="3">
			<% if(!isChild){%>
			<input type="button"  name="save" id="save"  onclick="submitDataToActionClass()" value="   <bean:message key="enterprisecontrol.applyenterprisecontrol.button"/>   " class="orange-btn"  />
			&nbsp;
			<input type="button"  name="clearPolicies" id="clearPolicies"  onclick="clearAccessControlPolicies()" value="   <bean:message key="parentalcontrol.reset.button"/>   " class="orange-btn"  />
			<%}%>
		</td>
	</tr>
</table>
</div> 
<html:hidden property="boxesNameArray" value="" styleId="boxesNameArray"/>
<html:hidden property="SELECTED_LINK" value="parentalcontrol" styleId="SELECTED_LINK"/>
<div id="policyData" style="display:none">		 					
</div>
</html:form>
</body>