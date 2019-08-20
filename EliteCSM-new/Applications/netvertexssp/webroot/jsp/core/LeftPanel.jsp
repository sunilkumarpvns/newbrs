<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.PromotionalData"%>
<%@page import="java.util.List"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>

<%
PromotionalData[] promotionalDatas = (PromotionalData[])session.getAttribute(SessionAttributeKeyConstant.PROMOTIONAL_OFFERS);
%>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
     	<tr>
			<td valign="top">
			<table width="219" cellspacing="0" cellpadding="0" border="0">
                  <tbody><tr>
                    <td height="30" background="<%=request.getContextPath()%>/images/btbkgd.jpg" class="lightbluepaneltitle">
                    Do More ,Do it instantly
                    </td>
                    </tr>
                  <tr>
                    <td height="120" background="<%=request.getContextPath()%>/images/bbkgd.jpg"><table width="90%" cellspacing="0" cellpadding="0" border="0" align="center">
                      <tbody><tr>
                        <td width="20" valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top">Watch Movies Without Buffering</td>
                      </tr>
                      <tr>
                        <td valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top">Upload images/Pictures Instantly</td>
                      </tr>
                      <tr>
                        <td valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top">Enjoy Uninterrupted Online Gaming</td>
                      </tr>
                      <tr>
                        <td valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top">Watch Video On Demand</td>
                      </tr>
                      <tr>
                        <td valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top">Enjoy TV on IPTV</td>
                      </tr>
                    </tbody></table></td>
                    </tr>
                  <tr>
                    <td><img width="219" height="10" src="<%=request.getContextPath()%>/images/bbtm.jpg"></td>
                    </tr>
                </tbody></table></td>
           </tr>
           <tr>
           	   <td height="10" bgcolor="#F2F5FA"></td>
           </tr>
           
           <%if(promotionalDatas!=null && promotionalDatas.length>0){ %>
           <tr>
                <td ><table width="100%" cellspacing="0" cellpadding="0" border="0">
                      <tbody><tr>
                    <td height="30" background="<%=request.getContextPath()%>/images/btbkgd.jpg" class="lightbluepaneltitle">
                    	Promotional Offers
                    </td>
                    </tr>
                  <tr>
                    <td valign="top" height="120" background="<%=request.getContextPath()%>/images/bbkgd.jpg"><table width="90%" cellspacing="0" cellpadding="0" border="0" align="center">
                      <tbody>
                      
                      <logic:iterate id="promotionalBean" name="promotionalDatas" type="PromotionalData">
                      <tr>
                        <td width="20" valign="top"><img width="12" height="12" src="<%=request.getContextPath()%>/images/bluearrow_btn.png"></td>
                        <td class="blacktext" valign="top"><a href="<%=request.getContextPath()%>/promotional.do?offerId=<%=promotionalBean.getAddOnPackageId()%>"> <bean:write name="promotionalBean" property="name" /></a></td>
                      </tr>
                      </logic:iterate>

                    </tbody></table></td>
                    </tr>
                  <tr>
                    <td><img width="219" height="10" src="<%=request.getContextPath()%>/images/bbtm.jpg"></td>
                    </tr>
                </tbody></table></td>
              </tr>
             <%} %>
           
           <tr>
           	   <td height="10" bgcolor="#F2F5FA"></td>
           </tr>
           
    </table>
