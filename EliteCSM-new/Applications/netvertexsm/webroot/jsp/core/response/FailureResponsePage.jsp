<LINK REL ="stylesheet" TYPE="text/css" HREF="../../../css/mllnstyles.css" >
<script language="javascript">
<%@ taglib uri="/WEB-INF/config/tlds/struts-logic.tld" prefix="logic" %>
function MM_preloadImages() 
{ //v3.0
  var d=document; 
  if(d.images)
  { 
  	if(!d.MM_p) 
  		d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; 
    for(i=0; i<a.length; i++)
    	if (a[i].indexOf("#")!=0)
    	{ 
    		d.MM_p[j]=new Image; 
    		d.MM_p[j++].src=a[i];
    	}
   }
}
function collapseAllRows()
{
	 var rows = document.getElementsByTagName("TR");
	 for (var j = 0; j < rows.length; j++) 
	 {
	   var r = rows[j];
	   if (r.id.indexOf("-") >= 0) 
	   {
		 r.style.display = "none";    
	   }
	 }
}
function popup(mylink, windowname)
{
	if (!window.focus) return true;
	var href;
	if (typeof(mylink) == 'string')
		href=mylink;
	else
		href=mylink.href;

	window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
	
	return false;
}   
function closeWindow(){	
	    window.close();	
}
</script>
<style>
<!--
.error-detail {  font-family: Arial;font-weight:plain; font-size: 12px;background-color: #FFFFFF; font-color: black; color: black; border: #CCCCCC; }
.left-right-lines {
background-image: images/error-bkgd.jpg;
background-position: center;
background-attachment: fixed;
background-repeat: no-repeat;
}
-->
</style>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%
	//clear session
	/* dt. 09-03-06 dhaval
	session=request.getSession();
	User user=(User)session.getAttribute("UserBean");
	Enumeration en=session.getAttributeNames();
	while(en.hasMoreElements())
	{
	    String name=(String)en.nextElement();
	    session.removeAttribute(name.trim());
	}
	session.setAttribute("UserBean",user);
	//clear request 
	Enumeration e=request.getAttributeNames();
	while(e.hasMoreElements())
	{
	    String name=(String)e.nextElement();
	    session.removeAttribute(name.trim());
	}
	*/
	
	session.removeAttribute("errorDetails");
	Object elements[] =  (Object[])request.getAttribute("errorDetails");
	session.setAttribute("errorDetails",elements);
	
	//String basePath = request.getContextPath();
	
	
//	User user=(User)session.getAttribute("UserBean");
// 	session.setAttribute("UserBean",user);
	//clear request 
/*	
	Enumeration e=request.getAttributeNames();
    while(e.hasMoreElements())
	{
	    String name=(String)e.nextElement();
	    session.removeAttribute(name.trim());
	}
*/	
%>

<table width="750" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td width="234" >&nbsp;</td>
    <td width="27" >&nbsp;</td>
    <td width="459" >&nbsp;</td>
    <td width="30" >&nbsp;</td>
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
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
    <td background="<%=basePath%>/images/error-heading-bkgd.jpg" class="textbold">
       	<% boolean flag=true; %>
    	<logic:messagesPresent message="true">
		          <html:messages bundle="resultMessageResources" id="errorHeading" name="errorHeading" message="true" >		          	
		             	<bean:write name="errorHeading"/><br>
		             	<%flag=false;%>		             
		          </html:messages>
		</logic:messagesPresent>
		<% if(flag){%> Error Heading <%}%>
    </td>
    <td ><img src="<%=basePath%>/images/error-top-right-curve.jpg" width="30" height="38"></td>
  </tr>
  <tr> 
    <td colspan="3"  valign="top" background="<%=basePath%>/images/error-bkgd.jpg" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          
          <td width="100%" valign="top" colspan="2">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr> 
                  <td class="labeltext"  colspan="3">&nbsp;</td>
                </tr>
                <tr>  
	                <td class="labeltext" colspan="3" align="left">
						<ul id="content-body-messages" >
							<logic:messagesPresent>
							           <html:messages bundle="resultMessageResources" id="errorMsg">							               
							               <%if(errorMsg.contains("Reason")){
							            	   	String message = errorMsg.split("Reason")[1]; 
							               %>
							               	<span style="color:red">Reason </span><b><%=message%></b><br>							                							               		
							               <%}else{%>
							               	<bean:write name="errorMsg" /><br>
							               <%} %>
							           </html:messages>
							           <html:messages bundle="resultMessageResources" id="warn_key" name="warn_key">
							               <bean:write name="warn_key" /><br>
							           </html:messages>
							</logic:messagesPresent>
						</ul>
					</td> 
                </tr>				  
                <tr> 
                 <td class="labeltext">&nbsp;</td>
                  <td align="center" class="labeltext"> 
                  	<input type="button" name="viewDetails" id="viewDetails" value="View Details" class="light-btn" onclick="popup('<%=request.getContextPath()%>/jsp/core/response/ErrorDetailPage.jsp','Erorrs')">
                  	<%
                  	 String closeBttn = (String)request.getAttribute("closeBttn");
                  	 if(closeBttn!=null && closeBttn.trim().equalsIgnoreCase("yes")){
                  	%> 
                    	<input type="button" name="Button2" value="Close" class="light-btn" onclick="closeWindow();"> 
					<%}else{%>                    
						<input type="button" name="Button2" value="Cancel" class="light-btn" onclick="history.go(-1)">
					<%}%>
                  </td>
                   <td class="labeltext">&nbsp;</td>
                </tr>
                <tr> 
                 <td class="labeltext">&nbsp;</td>
                  <td align="center" valign="top" class="labeltext"> 
                     <table width="100%" border="0" cellpadding="0" cellspacing="0">
                        <tr> 
                          <td class="labeltext">&nbsp;</td>
                        </tr>
                        <tr>
                          <td class="labeltext">&nbsp;</td>
                        </tr>
                      </table>
  				  </td>
  				   <td class="labeltext">&nbsp;</td>
                </tr>
            </table></td>
          <td width="6%">&nbsp;</td>
        </tr>
      </table></td>
      
  </tr>
  <tr> 
    <td >&nbsp;</td>
    <td ><img src="<%=basePath%>/images/error-btm-left-curve.jpg" width="27" height="31"></td>
    <td background="<%=basePath%>/images/error-btm-bkgd.jpg" >&nbsp;</td>
    <td ><img src="<%=basePath%>/images/error-btm-right-curve.jpg" width="30" height="31"></td>
  </tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
