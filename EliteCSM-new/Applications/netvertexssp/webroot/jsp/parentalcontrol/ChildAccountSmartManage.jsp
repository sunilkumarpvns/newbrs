
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
	System.out.println("data retrieved....1");
	ChildAccountManageForm childAccountManageForm = (ChildAccountmanageForm) request.getAttribute("childAccountManageForm");
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

<!-- COLORS TABLE CODE BEGIN -->
<% String daysLabels[]={"Day/Time","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};%>
<script>

	var boxColor;
 	var startStop=0; 	
 	var days=new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
 	var colors=new Array("black","yellow","green");
 	var day="Monday";
 	var row,col,hour,day;
 	var dataAr=new Array(7);
 	for(var i=0; i<7; i++){
 		dataAr[i]=new Array(24);
 	} 	
 	
	function setPolicies(){		 
		
		'<%	String time="";
			String days="";
			String boxId="";
			long addOnId;
			long addOnIdTemp;
			String str="";
		 	String[] colors={"black","yellow","green"};
		 	long[] addOnPackageIds={101L,121L,141L};
		 	int adOnIndex=0;		 	
			for(ParentalPolicyData obj:parentalPolicyData){	
				if(obj==null)
					continue;
				
				time=obj.getTimePeriod();
				days=obj.getDaysOfTheWeek();
				addOnId=obj.getAddOnPackageId();
				
				String daysAr[]={""};
				if(days!=null && days.length()>0)
					daysAr=days.split(",");
				
				String timeAr[]={""};
				if(time!=null && time.length()>0)
					timeAr=time.split("-");
				
				for(int i=0; i<promotionalData.length; i++){					
					addOnIdTemp=promotionalData[i].getAddOnPackageId();									 
					if(addOnIdTemp==addOnId){
						adOnIndex=i;						
					}
				}				
				str+=","+adOnIndex+":"+addOnId;
				if(daysAr!=null && daysAr.length>0){										
				for(int i=0; i<daysAr.length; i++){
										
						if(timeAr!=null && timeAr.length>=1){
						if(timeAr[0]!=null && timeAr[0].trim().length()>0 && timeAr[1]!=null && timeAr[1].trim().length()>0){
												
						for(int j=Integer.parseInt(timeAr[0]); j<=Integer.parseInt(timeAr[1]); j++){							
							int d1=Integer.parseInt(daysAr[i]);
							int row=d1-1;
							boxId=daysLabels[d1]+j;												
					%>';		
					dataAr['<%=d1-1%>']['<%=j%>']='<%=colors[adOnIndex]%>';
					$('#'+'<%=boxId%>').css('background-color', '<%=colors[adOnIndex]%>');
					'<% 					
					}}}}}
		 }%>';			 
	}
 	 	
	
	function setBoxColor(field){
		id=field.id;
		index=id.indexOf('y');
		length=id.length;	
		day=id.substr(0,index+1);
		hour=id.substr(index+1,length);		
		row=days.indexOf(day);
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
		
		document.forms[0].boxesNameArray.value=dataAr.join(",");
		document.forms[0].submit();		
	}
	
	
	function setSchemeColor(color){
		boxColor=color;
		startStop=0;
	}
</script>

<body onload="setPolicies()">
<html:form action="/childAccount" method="post">
<!-- SELECT POLICY TABLE BEGIN -->
<table width="100%" cellpadding="0" cellspacing="0" border="0" style="padding-bottom:20px;" >
	<tr>
		<td colspan="5" class="titlebold" >&nbsp;&nbsp;&nbsp;Select Policy</td>
	</tr>
	<tr class="tableheader">
		<%for(PromotionalData obj:promotionalData){ %>
			<td >&nbsp;<%=obj.getName()%>&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox"/> </td>
		<% } %>
	</tr>
	<tr>
		<%for(PromotionalData obj:promotionalData){ %>
			<td class="tblrows" >&nbsp;<%=obj.getDescription()%></td>
		<% } %>
	</tr>	
</table>	
<!-- SELECT POLICY TABLE END -->

<html:hidden property="boxesNameArray" value="" styleId="boxesNameArray"/>
<table width="100%" cellpadding="0" cellspacing="0" border="0" >
	
	<%for(String label:daysLabels){%>	
	<tr>
		<td width="15%" class="tableheader">&nbsp;&nbsp;&nbsp;<%=label%></td>
		<% for(int hr=0; hr<24; hr++){ %>
			<td onmousemove="<%=(label.contains("/")?"":"setBoxColor(this)")%>" id="<%=(label.contains("/")?"":label+""+hr)%>" class="<%=(label.contains("/")?"tableheader":"tblrows")%>"><%=(label.contains("/")?(hr<10?"0"+hr:hr):"")%></td>
		<%}%>
	</tr>			
	<%}%>
	<tr>
		<td class="tblrows" colspan="25" style="border-right: none; border-left: none">&nbsp;</td>
	</tr>	
	<tr>
		<% int colorIndex=0;
			for(PromotionalData obj:promotionalData){ %>			
			<td class="tblrows" colspan="6" align="center"><input type="button" name="<%=colorsName[colorIndex]%>" id="<%=colorsName[colorIndex]%>"  	onclick="setSchemeColor('<%=colorsName[colorIndex]%>')" 		value="" class="" 	style="background-color: <%=colorsName[colorIndex]%>;width:35px" />&nbsp;&nbsp; <%=(obj!=null)?obj.getName():""%></td>
		<%colorIndex++;}%>
	<td class="tblrows" colspan="7" align="center"><input type="button" name="white" id="white"  onclick="setSchemeColor('white')" 			value="" class=""  	style="background-color: white;width:35px" />&nbsp;&nbsp; Clear</td>
	</tr>
	<tr>
		<td  align="left" colspan="3">
			<input type="button" name="save" id="save"  onclick="submitDataToActionClass()" value="   Save   " class="sspbutton"  />
		</td>
	</tr>		
</table>	
</html:form>
</body>
<!-- COLORS TABLE CODE END -->

 
