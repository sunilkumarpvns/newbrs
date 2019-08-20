<LINK REL ="stylesheet" TYPE="text/css" HREF="../../../css/mllnstyles.css" >
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<script language="javascript">

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

hideMainFrame();

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
	
	String basePath = request.getContextPath();
	
	
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
    <td background="<%=basePath%>/images/error-heading-bkgd.jpg" class="textbold">Error Heading</td>
    <td ><img src="<%=basePath%>/images/error-top-right-curve.jpg" width="30" height="38"></td>
  </tr>
  <tr> 
    <td colspan="3"  valign="top" background="<%=basePath%>/images/error-bkgd.jpg" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="5%">&nbsp;</td>
          <td width="100%" valign="top" colspan="2">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
                <tr> 
                  <td class="labeltext"  colspan="3">&nbsp;</td>
                </tr>
          
                <tr>  
	                <td class="labeltext" colspan="3" align="center">
						<ul id="content-body-messages" >
							<logic:messagesPresent>
							           <html:messages bundle="resultMessageResources" id="errorMsg">
							               <bean:write name="errorMsg" filter="false"/><br>
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
                  	<logic:equal value="true" name="showError">
                    	<input type="button" name="viewDetails" id="viewDetails" value="View Details" class="light-btn" onclick="popup('<%=request.getContextPath()%>/jsp/core/response/ErrorDetailPage.jsp','Erorrs')">
                    	<input type="button" name="Button2" value="Cancel" class="light-btn" onclick="history.go(-1)">
                     </logic:equal>
                     
                     <logic:notEqual value="true" name="showError">
						<input type="button" name="Button2" value="OK" class="light-btn" onclick="history.go(-1)">	                     
                     </logic:notEqual>
                     
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
  <tr height="150px">
	 <td> &nbsp; </td>
	</tr>
</table>
