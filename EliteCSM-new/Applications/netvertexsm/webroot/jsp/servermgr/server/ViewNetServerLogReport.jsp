<%@ page language="java" pageEncoding="ISO-8859-1"%>





<% Long netServerId = (Long) request.getAttribute("netServerId"); %>

<script>
	var choice;
	var windowname;
	function validateCreate()
	{
	
		var elementList = document.forms[0].radioChoice; 
		for(i=0;i<elementList.length;i++)
		{
			
			if(elementList[i].checked){
				choice = elementList[i].value;
				windowname	= '<%=netServerId%>' + choice;
			}
		}
	 //	var urlDetailReport = '/viewLogReportDetail.do?netServerId=<%=netServerId%>&triBandnNumber='+document.forms[0].strTriBandNumber.value+'&date='+document.forms[0].strDate.value;
	 //	var urlSummaryReport = '/viewLogReportSummary.do?netServerId=<%=netServerId%>&triBandnNumber='+document.forms[0].strTriBandNumber.value+'&date='+document.forms[0].strDate.value;
		
	    var urlDetailReport = '<%=request.getContextPath()%>/viewLogReportDetail.do?netServerId=<%=netServerId%>&triBandnNumber='+document.forms[0].strTriBandNumber.value;
		var urlSummaryReport = '<%=request.getContextPath()%>/viewLogReportSummary.do?netServerId=<%=netServerId%>&triBandnNumber='+document.forms[0].strTriBandNumber.value;
	
		if( choice == 'summary' ){
			popupReport(urlSummaryReport,windowname);
		 }else{
			popupReport(urlDetailReport,windowname);
		 }
		
	
	
	function popupReport(url,winName){
	var win1 = window.open(url,winName,'top=100, left=100,resizable=1, height=450, width=800, scrollbars=yes, status');
	win1.focus();
}
	
	
}
</script>	

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html:form action="/viewLogReport" method="post" focus="strTriBandNumber">
<html:hidden styleId="netServerId" property="netServerId"/>   
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%">
       
	        <tr>
			  	<td colspan="3">&nbsp;</td>
			</tr>
			
			<tr>
				<td class="tblheader-bold" colspan="5" height="20%">
					<bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport" />
				</td>
			</tr>
      		 <tr> 
				<td align="left" class="labeltext" valign="top" width="18%" >User Name</td>
				<td align="left" class="labeltext" valign="top" width="66%"  colspan="2"> 
 			           <html:text styleId="strTriBandNumber" property="strTriBandNumber" size="20" maxlength="60"/>
 			          &nbsp;&nbsp;&nbsp;&nbsp;<font class="grey-text" ><bean:message bundle="servermgrResources"  key="servermgr.server.viewLogReport.tribandnumber.note"/></font> 
 			           
		  	    </td>
    		 </tr>
    		 
    	<%-- 	
	    	 <tr> 
			    <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.date"/></td>
			    <td align="left" class="labeltext" valign="top" colspan="2" >  
			           <html:text property="strDate" size="15" maxlength="18"/><font color="#FF0000">*&nbsp;&nbsp;</font>
			           <a  href="javascript:void(0)"  onclick="popUpCalendar(this,document.forms[0].strDate)" >
						  <img  src="<%=request.getContextPath()%>/images/calendar.jpg" border="0" tabindex="2">
					   </a>
	           	</td>
	         </tr>
	   --%>
	         
	         <tr>
	            <td class="labeltext"><bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.tribandnumber.format"/></td>
			    <td align="left" class="labeltext" valign="top" width="18%" >
			    	<html:radio styleId="radioChoice" property="radioChoice" value="summary"></html:radio>
			    	<bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.tribandnumber.summary"/>
			    </td>
			    <td align="left" class="labeltext" valign="top">  
			    	
			    	<html:radio styleId="radioChoice" property="radioChoice"  value="detail"></html:radio>
			         <bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.tribandnumber.detail"/> 
	           	</td>
	         </tr>
	         <tr > 
				<td class="btns-td" valign="middle" >&nbsp;</td>
				<td class="btns-td" valign="middle" align="left" colspan="2">
				   	<input type="button" name="c_btnView"  onclick="validateCreate()"  id="c_btnView" value=" View "  class="light-btn">                   
				   	<input type="reset" name="c_btncancle" onclick="javascript:location.href='<%=request.getContextPath()%>/viewNetServerInstance.do?netserverid=<%=netServerId%>'"   value="Cancel" class="light-btn">
  			</td>
            </tr>
	   	</table>
	  </td>
    </tr>
	</table>      
</html:form>
