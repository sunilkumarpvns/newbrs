<%@ page import="java.util.Arrays"%>
<%@ page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountManageForm"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.PromotionalData"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.ParentalPolicyData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<head>
   <title>Service Selection Portal</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script src="<%=request.getContextPath()%>/jquery/development/jquery-1.4.2.js"></script>	
</head>
<%
	String basePath = request.getContextPath();
	String subscriberName=(String)request.getSession().getAttribute("subscriberName");	
	ChildAccountManageForm childAccountManageForm = (ChildAccountManageForm) request.getAttribute("childAccountManageForm");
	PromotionalData[] promotionalData = null;
	ParentalPolicyData[] parentalPolicyData=null;	
	String[] daysName=null;
	String[] colorsName=null;
  	if( childAccountManageForm != null ){
  		promotionalData = childAccountManageForm.getPromotionalData();  		
  		parentalPolicyData = childAccountManageForm.getParentalPolicyData();
  		daysName=childAccountManageForm.getDaysName();
  		colorsName=childAccountManageForm.getColorsName();  		
  	}
%>
<!-- SIMPLE COMPONENT TABLE CODE BEGIN -->
<script>
		var days=new Array("Sun","Mon","Tue","Wed","Thur","Fri","Sat");
		$(document).ready(function(){
			$('#add').click(function(e) {				
				$('#noRecordRow').remove();				
				var lastRowNo=document.getElementById("lastRowNo");
				var options = document.getElementById('prData0').options;				
				var srNoChainElement=document.getElementById("srNoChain");
				var srNoChainValue=srNoChainElement.value;	
				var values = srNoChainValue.split(",");
				if(values.length>10){
					alert("Maximum 10 policies can be added.");
					return;
				}
				srNoChainElement.value+=lastRowNo.value+",";								
				var i=0;								
				$('#addons').append(						
				'<tr id="'+(lastRowNo.value)+'"><td align="left" width="5%" class="tblfirstcol">'+(lastRowNo.value)+'</td>'	
					+'<td align="left" width="15%" class="tblrows">'
						+'<select name="prData'+(lastRowNo.value)+'" id="prData'+(lastRowNo.value)+'" >'							
							+'<%for(int j=0; j<promotionalData.length; j++){%>'
							+'<option value="'+options[i].value+'">'+							
								options[i++].text								
							+'</option>'
							+'<%}%>'+
						+'</select>'						
					+'</td>'
					+'<td align="left" width="45%" class="tblrows">'
						+'<table width="100%">'					
							+'<tr>'								
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="1" />Sun</td>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="2" />Mon</td>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="3" />Tue</td>'								
							+'</tr>'
							+'<tr>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="4" />Wed</td>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="5" />Thu</td>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="6" />Fri</td>'
								+'<td width="22%"><input type="checkbox" name="weekDay'+(lastRowNo.value)+'" id="weekDay'+(lastRowNo.value)+'" value="7" />Sat</td>'								
							+'</tr>'							
						+'</table>'
					+'</td>'
					+'<td align="left" width="25%" class="tblrows">'
						+'<input type="text" name="time'+(lastRowNo.value)+'" id="time'+(lastRowNo.value)+'" value=""  onblur="" style="width: 95%" />'
					+'</td>'
					+'<td align="center" width="10%" class="tblrows"><input type="button" name="Delete"    onclick="deleteRow('+(lastRowNo.value++)+')" value="Delete">'
					+'</td>'
				+'</tr>');
			});
		});
	
 
	function validateAllTimeFields(rowsSrNo){		
		if(rowsSrNo!=null && rowsSrNo.length>1){		 
			 for(var i = 0 ; i < rowsSrNo.length-1 ; i++) {				 
				 var id="time"+rowsSrNo[i];
				 var timeField=document.getElementById(id);
				 validateTime(timeField);
			 }
		}
	}
	
	function validateWeekDays(rowsSrNo){
		var globalDayflag=1;
		var msg="";
		if(rowsSrNo!=null && rowsSrNo.length>1){			
			 for(var i = 0 ; i < rowsSrNo.length-1 ; i++) {				 
				 var weekDays="weekDay"+rowsSrNo[i];				 
				 flag=0;
				 var rowDayArr=document.getElementsByName(weekDays);				 
				 for(var j=0; j<rowDayArr.length; j++){
					if(rowDayArr[j].checked){
						flag=1;
					}				 
				 }
				 if(flag==0){
					 globalDayflag=0;
					 msg+=rowsSrNo[i]+",";				 	 
				 }
			 }			
		}		
		if(globalDayflag==0){
			alert("Atleast one 'Day of week' must be specified for Policy: "+msg.substr(0,msg.length-1));
		}
		return globalDayflag;
	}
	
	var regex=/^(([0-9]|[0-1][0-9]|[2][0-3])[-]([0-9]|[0-1][0-9]|[2][0-3]))$/;
	var timeValidationFlag=1;
	function validateTime(timeField){
		timeValidationFlag=1;
		var timeValue=timeField.value.trim();		
		var timeVal=timeValue.split(",");
		var str="";
		for(var i=0; i<timeVal.length; i++){
			if(timeVal[i]!=null && timeVal[i].trim().length>0){
				str+=timeVal[i]+",";
				var indexOfDash=timeVal[i].indexOf("-");
				var startHr=timeVal[i].substr(0,indexOfDash);
				var endHr=timeVal[i].substr(indexOfDash+1,timeVal[i].length);			
				var flag=regex.test(timeVal[i]);
				if(!flag || parseInt(startHr)>parseInt(endHr)){
					alert("Invalid Time !\n"+timeVal[i]);
					timeValidationFlag=0;
					//timeField.value=startHr+"-"+startHr;
					timeField.focus();
					timeField.select();
					break;
				}		
			}
		}
		if(timeValidationFlag==1){			
			timeField.value=str;
		}
	}
	
	function deleteRow(rowID){
		var srNoChainElement=document.getElementById("srNoChain");
		var srNoChainValue=srNoChainElement.value;	
		var values = srNoChainValue.split(",");
		 for(var i = 0 ; i < values.length ; i++) {
		    if(values[i] == rowID) {
		      values.splice(i, 1);		      
		    }
		}
		srNoChainElement.value=values.join(",");		
		$('#' + rowID).remove();
		var lastRowNo=document.getElementById("lastRowNo");			
	}
	
	function sendDataToActionClass(){		
		var noRecord=document.getElementById("noRecord").value;		
		if(noRecord!=null && noRecord=="true"){
			alert("Add atleast one policy.");
			return false;
		}else{			
			var srNoChainElement=document.getElementById("srNoChain");
			var srNoChainValue=srNoChainElement.value;		
			var values = srNoChainValue.split(",");		
			var daysFlag=validateWeekDays(values);			
			if(daysFlag!=0){
				document.forms[0].simpleDataTable.value="true";	 
				return true;
			}else{
				return false;
			}
		}
	}	
</script>
					
<html:form action="/childAccountManage" method="post"  >
<table width="100%" cellpadding="0"> 	
	<%if(promotionalData !=null && promotionalData.length>0){ %>	
	<select style="display:none"  name="prData0" id="prData0" >									
			<% for(PromotionalData obj:promotionalData){ %>
			<option value="<%=obj.getAddOnPackageId()%>" ><%=obj.getName()%></option>
			<%} %>
	</select>
   	<tr>
   		<td  height="20" class="titlebold" colspan="3" >
   		  &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="parentalcontrol.child.account"/> : <%=(subscriberName!=null)?subscriberName:""%>
   		</td>   		
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
	   	<td  height="20" class="titlebold" colspan="3" ><bean:message key="parentalcontrol.addons"/></td>
	</tr>
	<tr>
		<td align="left" valign="top" >		
		<table width="100%" cellpadding="0" cellspacing="0" border="0" >			
			<tr>
				<td align="left" class="tableheader">&nbsp;<bean:message key="general.serialno"/></td>				
				<td align="center" class="tableheader"><bean:message key="childaccount.name"/></td>
				<td align="center" class="tableheader"><bean:message key="childaccount.description"/></td>				
			</tr>	
			<%  int index=1;
				int rowCounter=0;
				String daysOfWeekStr="";
				int dayCount=0;
				String chekBoxVal="";	
				String srNoChain="";
			%>					 	
			<logic:iterate id="promotionalDataObj" name="childAccountManageForm" property="promotionalData" type="PromotionalData">
			<tr>
				<td align="left" class="tblfirstcol"><%=index%></td>					
				<td align="left" class="tblrows">&nbsp;&nbsp;<bean:write name="promotionalDataObj" property="name" /></td>
				<td align="left" class="tblrows">&nbsp;&nbsp;<bean:write name="promotionalDataObj" property="description" /></td>		
			</tr>
			<%index++;%>
			</logic:iterate>			 
			<tr>
				<td  align="center" colspan="3">&nbsp;</td>
			</tr>			
			<tr>
				<td  align="left" colspan="3">
					<input type="button" name="add" id="add"  onclick="" value="   Add   " class="sspbutton" style="margin-bottom:5px" />
				</td>
			</tr>			
		</table>
		<table width="100%" cellpadding="0" cellspacing="0" border="0" id="addons" >			
			<tr>
				<td align="left" class="tableheader" width="5%">&nbsp;<bean:message key="general.serialno"/></td>
			    <td align="center" class="tableheader" width="15%">&nbsp;<bean:message key="childaccount.addon"/></td>
				<td align="center" class="tableheader" width="45%">&nbsp;<bean:message key="childaccount.daysofweek"/></td>				
				<td align="center" class="tableheader" width="25%">&nbsp;<bean:message key="childaccount.time"/>&nbsp;&nbsp;<img title="Time must be in 0-23 hour format : (i.e. 0-5,4-8,..) " src="<%=basePath%>/images/help_hover.png"  width="15" style=" cursor: pointer;vertical-align: bottom; margin-bottom: 2px"/></td>
				<td align="center" class="tableheader" width="10%">&nbsp;<bean:message key="childaccount.delete"/></td>				
			</tr>									
			<%  index=1; %>
			<%if(parentalPolicyData!=null){ %>
			<logic:iterate id="parentalPolicyDataObj" name="childAccountManageForm" property="parentalPolicyData" type="ParentalPolicyData">
				<%if(parentalPolicyDataObj!=null){ %>				
				<%srNoChain+=index+",";%>
				<tr id="<%=index%>">				
					<td align="left" class="tblfirstcol"><%=++rowCounter%></td>
					<td align="left" class="tblrows">							
							<select name="prData<%=index%>" id="prData<%=index%>" >								
								
								<% 	long paId=parentalPolicyData[index-1].getAddOnPackageId();
									 
										for(PromotionalData obj:promotionalData){
										long prId=obj.getAddOnPackageId();
								%>
									<option value="<%=obj.getAddOnPackageId()%>" <%=(paId==prId)?"selected":""%> ><%=obj.getName()%></option>
								<%} %>
							</select>																	
					</td>
					<td align="left" class="tblrows">
							<% daysOfWeekStr=parentalPolicyData[index-1].getDaysOfTheWeek(); %>
							<table width="100%">
							<%
							for(String day:daysName){								
								dayCount++;
								if(daysOfWeekStr!=null && daysOfWeekStr.trim().length()>0 && daysOfWeekStr.contains(""+dayCount)){
									chekBoxVal="checked";
								} 							
							%>			
							<% if(dayCount%4==0){ %>										
							<tr> 
							<%}%>
							<td width="22%"> <input type="checkbox"  name="weekDay<%=index%>" id="weekDay<%=index%>" <%=chekBoxVal%>  value="<%=dayCount%>"/> <%=day%> </td>
							<% if(dayCount%4==3){ %>
							</tr>
							<%}%>									
					<%	chekBoxVal=""; 
						} 		%>						
						</table>
						<%
						dayCount=0;				
					%>
					</td>
					<td align="left" class="tblrows"><input type="text" name="time<%=index%>" id="time<%=index%>" value="<bean:write name="parentalPolicyDataObj" property="timePeriod" />" onblur="" style="width: 95%" /> </td>										
					<td align="center" class="tblrows"><input type="button" name="Delete"   onclick="deleteRow('<%=index%>')" value="Delete" > </td>
				</tr>				
			 	<%}%>
			 	<%index++;%>			 	
			</logic:iterate>
			<%}else{%>				
				<tr id="noRecordRow">
					<input type="hidden" name="noRecord" id="noRecord" value="true" />
					<td align="center" class="tblfirstcol" colspan="5"><bean:message key="parentalcontrol.norecords"/></td>				
				</tr>
			<%}%>				
			<input type="hidden" name="lastRowNo" id="lastRowNo" value="<%=index%>" />
			<input type="hidden" name="srNoChain" id="srNoChain" value="<%=srNoChain%>" />
			<input type="hidden" name="noRecord" id="noRecord" value="false" />									
		</table>	
		</td>
		</tr>
		<tr><td  align="center" colspan="3">&nbsp;</td></tr>
		<tr>
			<td  align="center" colspan="5">		
				<html:hidden property="simpleDataTable" value="false" styleId="simpleDataTable"/>		
				<input type="submit" name="save1" id="save1"  onclick="return sendDataToActionClass();" value="   Save   " class="sspbutton"  />				
			</td>	
		</tr>
		<tr> <td  align="center" colspan="3">&nbsp;</td></tr>	
	 <%}else{%>	    
		<tr>
   			<td  height="20" class="titlebold" width="100%">
   		  		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="parentalcontrol.child.account"/>
   			</td>
		</tr>
	    <tr>
			<td align="center" class="tblfirstcol" colspan="5"><bean:message key="parentalcontrol.norecords"/></td>
		</tr>
	<%}%>	
</table>
</html:form> 
<!-- SIMPLE COMPONENT TABLE CODE END -->
