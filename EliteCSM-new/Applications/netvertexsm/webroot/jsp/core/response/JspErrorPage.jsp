<LINK REL ="stylesheet" TYPE="text/css" HREF="../../../css/mllnstyles.css" >
<%@ page isErrorPage="true" %>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>

<style type="text/css">
	.space{	margin-bottom:5px}	
	.container {
	    height	: auto;
	    width	: 370px;
	    border	: 0px solid #333;	    
	    overflow: auto;
	}
</style>

<table width="750" border="0" align="left" cellpadding="0" cellspacing="0">
  <tr> 
    <td width="234" >&nbsp;</td>
    <td width="27" >&nbsp;</td>
    <td width="459" >&nbsp;</td>
    <td width="30" >&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="4">&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="4">&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="4">&nbsp;</td>
  </tr>
  <tr> 
    <td colspan="4">&nbsp;</td>
  </tr>  
  <tr>  
    <td rowspan="3" valign="top" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><img src="<%=basePath%>/images/error-top.jpg" width="234" height="24"></td>
        </tr>
        <tr>
          <td><img src="<%=basePath%>/images/error.jpg" width="234" height="38"></td>
        </tr>
        <tr>
          <td><img src="<%=basePath%>/images/error-btm.jpg" width="234" height="56"></td>
        </tr>
      </table></td>
    <td ><img src="<%=basePath%>/images/error-top1.jpg" width="27" height="24"></td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr> 
    <td ><img src="<%=basePath%>/images/error-top-left-curve.jpg" width="27" height="38"></td>
    <td background="<%=basePath%>/images/error-heading-bkgd.jpg" class="textbold">JSP Error</td>
    <td ><img src="<%=basePath%>/images/error-top-right-curve.jpg" width="30" height="38"></td>
  </tr>
  <tr> 
    <td colspan="3"  valign="top" background="<%=basePath%>/images/error-bkgd.jpg" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="100%" valign="top" colspan="2">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">                 
                <tr>  
	                <td class="labeltext" colspan="3" style="padding-left:15px">
	                	<table>
	                		<tr>
	                			<td class="labeltext" width="22%"><b>HTTP Status Code</b></td>
	                			<td>:</td>	 
	                			<td class="labeltext">${pageContext.errorData.statusCode}</td>               				                	
	                		</tr>
	                		<tr style="height:5px;font-size: 1">
	                			<td>&nbsp;</td>
	                		</tr>
	                		<tr>
	                			<td class="labeltext" style="vertical-align: top;" ><b>Requested URL</b></td>
	                			<td style="vertical-align: top;">:</td>
	                			<td class="labeltext">${pageContext.errorData.requestURI}</td>	                				                	
	                		</tr>
	                		<tr style="height:5px;font-size: 1">
	                			<td>&nbsp;</td>
	                		</tr>
	                		 <tr>
	                			<td class="labeltext" style="vertical-align: top;" ><b>Message</b></td>
	                			<td style="vertical-align: top;">:</td>
	                			<td class="labeltext">
	                				<table>
	                					<div class="container" >
	                						<%
	                						String message = pageContext.getException().getMessage();
	                						if(message!=null){
	                							message = message.replaceAll("\\<.*?>","");
	                						}%>
	                						<%=message%>
	                					</div>
	                				</table>
	                			</td>	                				                	
	                		</tr>   
	                		<tr style="height:5px;font-size: 1">
	                			<td>&nbsp;</td>
	                		</tr>
	                	</table>
					</td> 
                </tr>				  
                <tr> 
                 <td class="labeltext">&nbsp;</td>
                  <td align="center" class="labeltext"> 
                    <input type="button" name="Button2" value="Cancel" class="light-btn" onclick="history.go(-1)"> 
                  </td>
                   <td class="labeltext">&nbsp;</td>
                </tr>
            </table></td>
          <td width="6%">&nbsp;</td>
        </tr>
      </table>
      </td>      
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td ><img src="<%=basePath%>/images/error-btm-left-curve.jpg" width="27" height="31"></td>
    <td background="<%=basePath%>/images/error-btm-bkgd.jpg" >&nbsp;</td>
    <td ><img src="<%=basePath%>/images/error-btm-right-curve.jpg" width="30" height="31"></td>
  </tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
