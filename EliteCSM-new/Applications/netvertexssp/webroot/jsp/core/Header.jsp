<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%
String basePath = request.getContextPath();
SubscriberProfileData subscriberProfileData=(SubscriberProfileData)session.getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
%>


<%@page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
   



<table width="100%">
		<tr>
		<td valign="top"><table width="100%" cellspacing="0" cellpadding="0" border="0">
          <tbody><tr>
            <td width="33" background="<%=basePath %>/images/marquee_bkgd.jpg"><img width="33" height="51" src="<%=basePath %>/images/leftmcurve.jpg"></td>
            <td width="143" background="<%=basePath %>/images/marquee_bkgd.jpg" align="center"><img width="107" height="39" alt="Airtel" src="<%=basePath %>/images/elitecore.jpg"></td>
            <td width="*" align="right" style="background-image: url(&quot;<%=basePath %>/images/marquee_bkgd.jpg&quot;); background-repeat: repeat-x; border-left: 1px solid rgb(195, 195, 195);" class="toplinks">
            <a href="<%=basePath %>/logout.do">Logout</a>
            </td>
            <td width="20"><img width="18" height="51" src="<%=basePath %>/images/topright_curve.jpg"></td>
          </tr>
        </tbody></table></td>
        </tr>
        
      		<tr>
        		<td height="40" style="background-image:url('<%=basePath %>/images//grey_bkgd.jpg'); background-repeat:repeat-x;">
	        		<table border="0" width="100%" cellpadding="0" cellspacing="0">
	          			<tr>
	          				<td align="left" class="blacktext"></td>
	            			<td><img src="<%=basePath %>/images/welcome_icon.jpg" width="38" height="38" align="right"/></td>
	            			<td width="170">
	            				<table width="100%" border="0" cellspacing="0" cellpadding="0">
	              					<tr>
	              						<td height="26" align="center" class="blacktext" nowrap="nowrap" style="background-color:#FFFFFF; border-bottom:#9fc0e1 solid 1px; border-top:#9fc0e1 solid 1px; border-right:#9fc0e1 solid 1px;">
	                					<strong>&nbsp;&nbsp; 
	                					Welcome
	                					 </strong>&nbsp;&nbsp;&nbsp;<%=subscriberProfileData.getUserName()%>&nbsp;&nbsp;&nbsp;</td>
	              					</tr>
	            				</table>
	            			</td>
	            			<td width="20">&nbsp;</td>
	          			</tr>
	        		</table>
	        	</td>
      		</tr>
      		  <tr>
        		<td height="1" class="transparent"></td>
      		</tr> 
      
      		<tr>
		        <td height="35" align="right" class="whitetext" style="border:#172734 solid 1px; background-image:url('<%=basePath %>/images/menu_bkgd1.jpg');">
					<a href="<%=basePath %>/home.do">Home</a>&nbsp;&nbsp;|&nbsp;&nbsp;
					<a href="<%=basePath%>/parentalControl.do">Parental Control</a>&nbsp;&nbsp;|&nbsp;&nbsp;
					<a href="<%=basePath%>/bod.do">BoD</a>&nbsp;&nbsp;|&nbsp;&nbsp;
					<a href="<%=basePath%>/history.do" >Subscription History</a>&nbsp;&nbsp;|&nbsp;&nbsp;
					<a href="#">Contact Us</a>&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
      		</tr>
      	</table>