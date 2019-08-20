<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<script language="JavaScript">
    window.focus();
    $(document).ready(function(){
		$("#c_btnOK").focus();
	});
</script>
<% 
	String successMessage="The operation was successfullly completed";
	String strResponseUrl=null;
	if(request.getAttribute("successMessage") != null){
		successMessage = (String)request.getAttribute("successMessage");
	}
	
	if(request.getAttribute("responseUrl") != null){
		strResponseUrl =(String)request.getAttribute("responseUrl");		
		//strResponseUrl	= basePath+strResponseUrl;
		strResponseUrl = strResponseUrl.trim();
//		strResponseUrl = strResponseUrl.replaceAll("//","/");
	}else{
		//System.out.println("responseUrl");
	}
//	System.out.println(strResponseUrl);

	Long netServerId = (Long)request.getAttribute("netServerId");
	Boolean flag = (Boolean)request.getAttribute("showButton");
%>
<script>
	$(document).ready(function() {
		$("#c_btnOK").focus();
	});
	function responseUrl(value){
		if(value!=null){
			document.forms[0].action=value;
			document.forms[0].submit();
		}else{
			history.back();
		}
	}
	
	function redirectToViewPolicyStatus(){
		document.forms[0].action="viewPolicyStatusAction.do?netServerId="+<%=netServerId%>;
		document.forms[0].submit();
	}
</script>
<table cellpadding="0" cellspacing="0" border="0" align="center" width="750" >
	<tr > 
	    <td width="234">&nbsp;</td>
	    <td width="27">&nbsp;</td>
	    <td width="459">&nbsp;</td>
	    <td width="30">&nbsp;</td>
	</tr>
	
	<tr> 
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr> 
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr> 
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr> 
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr> 
		<td rowspan="3" valign="top" >
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td><img  src="<%=basePath%>/images/error-top.jpg"  border="0" ></td>
				</tr>
				
				<tr>
					<td><img  src="<%=basePath%>/images/success.jpg"  border="0" ></td>
				</tr>
				
				<tr>
					<td><img  src="<%=basePath%>/images/success-btm.jpg"  border="0" ></td>
				</tr>
			</table>
		</td>
		<td><img src="<%=basePath%>/images/error-top1.jpg" width="27" height="24"></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr> 
		<td><img  src="<%=basePath%>/images/error-top-left-curve.jpg"  border="0"></td>
    	<td background="<%=basePath%>/images/error-heading-bkgd.jpg" class="textbold"> &lt;&lt;&lt; Success &gt;&gt;&gt; </td>
    	<td><img  src="<%=basePath%>/images/error-top-right-curve.jpg"  border="0"></td>
	</tr>
	
	<tr> 
		<td colspan="3" valign="top" background="<%=basePath%>/images/error-bkgd.jpg">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr> 
					<td width="5%">&nbsp;</td>
					<td width="89%" valign="top">
						<table type="tbl-list"  width="450" >
                                <tr> 
                                    <td align="left" class="labeltext" valign="top">&nbsp;</td>
                                </tr>
                        
								<tr> 							
					                <td class="labeltext">	
										<ul id="content-body-messages" style="word-break:break-all;">								
											<logic:messagesPresent message="true" >
											           <html:messages bundle="resultMessageResources" id="msg" message="true">
											               <bean:write name="msg" filter="false"/><br>
											           </html:messages>
											</logic:messagesPresent>
										</ul>
									</td>
								</tr>								

								<tr> 
									<td align="center" class="labeltext" valign="top" align="center" width="100%"> 
									<html:form action="<%=strResponseUrl%>">
									 	<%
											
									 		if(flag!=null && netServerId!=null){	
									 	%>
												<html:button styleId="c_btnViewPolicyDetails" property="c_btnViewPolicyStatus" value=" View Policy Status " styleClass="light-btn" onclick="redirectToViewPolicyStatus();"/>
									 		<%}%>  
											<html:button styleId="c_btnOK" property="c_btnOK" value=" OK " styleClass="light-btn" onclick="document.forms[0].submit();"/>
										 
									</html:form>
									</td>
								</tr>
								
								<tr> 
									<td align="left" class="labeltext" valign="top" align="center" valign="top"> 
										<div id="errorDetails" style="display:'none'"> 
											<table cellpadding="0" cellspacing="0" border="0" width="450">
												<tr> 
													<td align="left" class="labeltext" valign="top" >&nbsp;</td>
												</tr>
												
												<tr> 
												  <td align="left" class="labeltext" valign="top"><p align="justify">&nbsp;</p></td>
												</tr>
                        
												<tr>
												  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
						</table>
					</td>
					<td width="6%">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr> 
		<td >&nbsp;</td>
    	<td ><img  src="<%=basePath%>/images/error-btm-left-curve.jpg"  border="0" ></td>
    	<td background="<%=basePath%>/images/error-btm-bkgd.jpg" >&nbsp;</td>
    	<td ><img  src="<%=basePath%>/images/error-btm-right-curve.jpg"  border="0" ></td>
	</tr>
</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>



