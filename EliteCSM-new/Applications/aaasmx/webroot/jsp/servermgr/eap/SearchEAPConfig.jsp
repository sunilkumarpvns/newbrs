<%@page import="com.elitecore.elitesm.util.eapconfig.EAPConfigUtils"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%>
<%@page import="com.elitecore.elitesm.web.servermgr.eap.forms.SearchEAPConfigForm"%><style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<script>

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}

 function navigatePageWithStatus(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")

	var name = $("#md5").attr("name");
	if ($('#md5').is(":checked"))
	{
		var chkvalue=$('#md5').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#GTC").attr("name");
	if ($('#GTC').is(":checked"))
	{
		var chkvalue=$('#GTC').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#tls").attr("name");
	if ($('#tls').is(":checked"))
	{
		var chkvalue=$('#tls').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#sim").attr("name");
	if ($('#sim').is(":checked"))
	{
		var chkvalue=$('#sim').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#ttls").attr("name");
	if ($('#ttls').is(":checked"))
	{
		var chkvalue=$('#ttls').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#peap").attr("name");
	if ($('#peap').is(":checked"))
	{
		var chkvalue=$('#peap').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#aka").attr("name");
	if ($('#aka').is(":checked"))
	{
		var chkvalue=$('#aka').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#mschapv2").attr("name");
	if ($('#mschapv2').is(":checked"))
	{
		var chkvalue=$('#mschapv2').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	var name = $("#akaPrime").attr("name");
	if ($('#akaPrime').is(":checked"))
	{
		var chkvalue=$('#akaPrime').val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+chkvalue+"'>")
	}
	
	
	$("#newFormData").submit();
} 
function removeData(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one EAP Configuration for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searcheapconfigjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
            
       	    document.miscEAPConfigForm.action.value = 'delete';
        	document.miscEAPConfigForm.submit();
        }
    }
}
function  checkAll(){
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
}

setTitle('<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header"/>');
</script>
<%																							 	
     SearchEAPConfigForm searchEAPConfigForm = (SearchEAPConfigForm)request.getAttribute("searchEAPConfigForm");
	 List<EAPConfigData> lstEAPConfig = searchEAPConfigForm.getEapConfigList();
     
     
     long pageNo = searchEAPConfigForm.getPageNumber();
     long totalPages = searchEAPConfigForm.getTotalPages();
     long totalRecord = searchEAPConfigForm.getTotalRecords();
	 int count=1;
     
	 String strName = searchEAPConfigForm.getName();

	 String strTls = searchEAPConfigForm.getTls();
	 String strTtls = searchEAPConfigForm.getTtls();
	 String strPeap = searchEAPConfigForm.getPeap();
	 String strSim = searchEAPConfigForm.getSim();
	 String strAka = searchEAPConfigForm.getAka();
	 String strMd5=  searchEAPConfigForm.getMd5();
	 String strMschapv2 =searchEAPConfigForm.getMschapv2();
	 String strGtc=searchEAPConfigForm.getGtc();
	 String strAkaPrime = searchEAPConfigForm.getAkaPrime();
	 
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     
    
     
     
%>

  <table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
				<tr>
					<td class="table-header" colspan="5">SEARCH EAP CONFIGURATION</td> 
				</tr>
		   <tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
			<tr>
				<td colspan="3">
					<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" >
						<html:form action="/searchEAPConfig">
							<input type="hidden" name="c_strActionMode" id="c_strActionMode" value="102231105" />
							<html:hidden name="searchEAPConfigForm" styleId="action" 	property="action" />
							<html:hidden name="searchEAPConfigForm" styleId="pageNumber" property="pageNumber"/>
							<html:hidden name="searchEAPConfigForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
							<html:hidden name="searchEAPConfigForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%">
								<bean:message bundle="servermgrResources" 
									key="servermgr.eapconfig.name" />
										<ec:elitehelp headerBundle="servermgrResources" 
											text="eapconfig.name" 
												header="servermgr.eapconfig.name"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="32%" >
								<html:text styleId="name" property="name" size="30" maxlength="30"/>
							</td>
						</tr>
						<tr>
						<td align="left" class="labeltext" valign="top">
							<bean:message bundle="servermgrResources" 
								key="servermgr.eapconfig.enabledauthmethod" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="eapconfig.authmethods" 
											header="servermgr.eapconfig.enabledauthmethod"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" > 
						 
						 <html:checkbox property="md5" value="004" styleId="md5" ></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.md5"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        
                         <html:checkbox property="gtc" value="006" styleId="GTC"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gtc"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                       
                         <html:checkbox property="tls" value="013" styleId="tls"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.tls"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					  
                         <html:checkbox property="ttls" value="021" styleId="ttls"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.ttls"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="peap" value="025" styleId="peap"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.peap"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
					     <html:checkbox property="sim" value="018" styleId="sim"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.sim"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="aka" value="023" styleId="aka"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.aka"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                          <html:checkbox property="akaPrime" value="050" styleId="akaPrime"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaprime"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
                         <html:checkbox property="mschapv2" value="026" styleId="mschapv2"></html:checkbox>
                         <bean:message bundle="servermgrResources" key="servermgr.eapconfig.mschapv2"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                         
					  </td>
						
						</tr>
						<tr>
							<td>&nbsp;</td>
					    </tr>
						<tr>
								<td class="btns-td" valign="middle">&nbsp;</td>  
							<td align="left" class="labeltext" valign="top" width="5%">
							    <input type="button" name="Search" width="5%" name="EAPConfigName" Onclick="validateSearch()" value="   Search   " class="light-btn" />       
								<!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
								<input type="button" name="Create"    value="   Create   " onclick="javascript:navigatePageWithStatus('initCreateEAPConfig.do','name');" class="light-btn" >  
							</td>
						</tr>
						</html:form>
			 		</table>  
			 	</td>
			</tr>	 				
		<%
		    //System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
			if(searchEAPConfigForm.getAction()!=null && searchEAPConfigForm.getAction().equalsIgnoreCase("list")){
	       	%>	
			<html:form action="/miscEAPConfig">
				<html:hidden name="miscEAPConfigForm" styleId="action" 		property="action" />
				<html:hidden name="miscEAPConfigForm" styleId="name" 		property="name" value="<%=strName%>"/>	
				
				<html:hidden name="miscEAPConfigForm" styleId="tls" 		property="tls" value="<%=strTls%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="ttls" 		property="ttls" value="<%=strTtls%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="ttls" 		property="peap" value="<%=strPeap%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="sim" 		property="sim" value="<%=strSim%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="aka" 		property="aka" value="<%=strAka%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="akaPrime" 		property="akaPrime" value="<%=strAkaPrime%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="md5" 		property="md5" value="<%=strMd5%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="gtc" 		property="gtc" value="<%=strGtc%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="mschapv2" 	property="mschapv2" value="<%=strMschapv2%>"/>
						
				<html:hidden name="miscEAPConfigForm" styleId="pageNumber" 	property="pageNumber" value="<%=strPageNumber%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="totalPages" 	property="totalPages" value="<%=strTotalPages%>"/>
				<html:hidden name="miscEAPConfigForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
	       	
	<tr>
		<td colspan="3">
	 	  	<table cellSpacing="0" cellPadding="0" width="99%" border="0" >
				<tr> 
	   				<td class="table-header" width="50%"  >EAP Configuration List</td>  
	   	        	
					<td align="right" class="blue-text" valign="middle" width="50%">
					    <% if(totalRecord == 0) { %>
						<% }else if(pageNo == totalPages+1) { %>
						    [<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
						<% } else if(pageNo == 1) { %>
						    [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %>
						<% } else { %>
						    [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %>
						<% } %>
						
					</td>
				</tr>
				<tr>				
			    	<td></td>       
				</tr>
			<tr>
				<td class="btns-td" valign="middle">&nbsp;
					<html:button property="c_btnDelete" onclick="removeData()" value="   Delete   " styleClass="light-btn" />													
				</td>
				<td class="btns-td" align="right" >
					  	<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
								<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%>
								<%  if(pageNo-1 == 1){ %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else if(pageNo == totalPages){ %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else { %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							<% } %>
						<% if(pageNo == totalPages+1) { %>
							<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						<% } %>
				  <% } %>
				</td>
	    	</tr>	
			<tr height="2">        
				<td></td>                   
			</tr>
			<tr>
				<td class="btns-td" valign="middle" colspan="2" > 
	   				<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable"  > 
						<tr>
							<td align="center" class="tblheader" valign="top" width="5%">
	  								<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()"/>
							</td>
					        <td align="center" class="tblheader" valign="top" width="40px"><bean:message key="general.serialnumber" /></td>
						    <td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.name"/></td>
                            <td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.defaultnegiotationmethod"/></td>
                            <td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.enabledauthmethod"/></td>
                           	<td align="center" class="tblheader" valign="top" width="40px"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.edit"/></td>
						</tr>
				<%	if(lstEAPConfig!=null && lstEAPConfig.size()>0){%>
					<logic:iterate id="eapConfigBean" name="searchEAPConfigForm" property="eapConfigList" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData">
					   <%
					   //DefaultNegotiation method..
					   
					   String strdefaultNegotiationMethod="";
					   Long defaultNegotiationMethod = eapConfigBean.getDefaultNegiotationMethod(); 
					   strdefaultNegotiationMethod = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(defaultNegotiationMethod);
					   
					   //Enabled Auth Methods..
					   String strEnabledAuthMethods = eapConfigBean.getEnabledAuthMethods();
					   String strLabelEnabledAuthMethods=EAPConfigUtils.convertEnableAuthMethodToLabelString(strEnabledAuthMethods);
					   System.out.println("Enabled Auth Method :"+strLabelEnabledAuthMethods.toString());
					   
					   %>
							<tr >
							<td align="center" class="tblfirstcol" > 
								<input type="checkbox" name="select" value="<bean:write name="eapConfigBean" property="eapId"/>"/>
							</td>
		 					<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
							<td align="left" class="tblrows" ><a href="<%=basePath%>/viewEAPConfig.do?viewType=basic&eapId=<bean:write name="eapConfigBean" property="eapId"/>"><bean:write name="eapConfigBean" property="name"/></a></td> 
				            <td align="left" class="tblrows" ><%=strdefaultNegotiationMethod%>&nbsp;&nbsp;</td> 
				            <td align="left" class="tblrows" ><%=strLabelEnabledAuthMethods%>&nbsp;&nbsp;</td>   
							<td align="center" class="tblrows" >
						
							<a  href="<%=basePath%>/updateEAPBasicDetails.do?eapId=<bean:write name="eapConfigBean" property="eapId"/>" ><img  src="<%=basePath%>/images/edit.jpg"  alt="Edit"  border="0" ></a>
							
							</td>							
	 	   				</tr> 
	 	  				<% count=count+1; %>
	 	  				<% iIndex += 1; %>
					</logic:iterate>
				<%}else{%>
	                				<tr>
	                  					<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
	                				</tr>
				<%}%>
					</table>
				</td>
				</tr>
				<tr>
				<td class="btns-td" valign="middle"> &nbsp;
					<html:button property="c_btnDelete" onclick="removeData()" value="   Delete   " styleClass="light-btn" />													
				</td>
				<td class="btns-td" align="right" >
					  	<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
								<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%>
								<%  if(pageNo-1 == 1){ %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else if(pageNo == totalPages){ %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } else { %>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							<% } %>
						<% if(pageNo == totalPages+1) { %>
							<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
							<a  href="searchEAPConfig.do?name=<%=strName%>&md5=<%=strMd5%>&gtc=<%=strGtc%>&mschapv2=<%=strMschapv2%>&tls=<%=strTls%>&ttls=<%=strTtls%>&sim=<%=strSim%>&aka=<%=strAka%>&akaPrime=<%=strAkaPrime%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
						<% } %>
				  <% } %>
				</td>
	    				</tr>	
					  	</table>
					   </td>	
				</tr>	
				</html:form>							
			<%}%>		
	    </table>
		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
</table>
</td>
</tr>
</table>		