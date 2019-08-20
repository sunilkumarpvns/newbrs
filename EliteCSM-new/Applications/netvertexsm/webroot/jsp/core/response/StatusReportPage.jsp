<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.datamanager.core.util.MessageData"%>
<%@ taglib uri="/WEB-INF/config/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/config/tlds/taglibs-input.tld" prefix="elitecore" %>
<%
	List<MessageData> messages = (List<MessageData>)session.getAttribute("messages");
%>

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="*" background="<%=request.getContextPath()%>/images/popup-bkgd.jpg" valign="top">
						&nbsp;
					</td>
					<td width="20px">
						<img src="<%=request.getContextPath()%>/images/popup-curve.jpg">
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/refresh.jpg"
								name="Image1" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=request.getContextPath()%>/images/refresh-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#" onclick="window.print()"><img
								src="<%=request.getContextPath()%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=request.getContextPath()%>/images/print-hover.jpg',1)"
								border="0"> </a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/aboutus.jpg"
								name="Image3" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=request.getContextPath()%>/images/aboutus-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/help.jpg"
								name="Image4" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=request.getContextPath()%>/images/help-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="20px">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="7">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="38%" class="tblheader-bold" valign="bottom">
										<bean:message  key="general.status.report.title"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" style="padding-left:30px" colspan="7">
						 <table width="100%" cellpadding="0" cellspacing="0">
							<logic:iterate id="messageBean" scope="session" name="messages" type="MessageData">
								<tr>
								 	<td width="10px" class="labeltext" valign="top">
									<logic:equal value="<%=MessageData.INFO%>" name="messageBean" property="messageType">
											<font color="#4682B4"><bean:write name="messageBean" property="messageType"/></font>
									 </logic:equal>
									 <logic:equal value="<%=MessageData.WARN%>" name="messageBean" property="messageType">
											<font color="#FF8C00"><bean:write name="messageBean" property="messageType"/></font>
									 </logic:equal>
									 <logic:equal value="<%=MessageData.ERROR%>" name="messageBean" property="messageType">
											<font color="#B22222"><bean:write name="messageBean" property="messageType"/></font>
									 </logic:equal>
									 </td> 
									 
									 <td class="labeltext" valign="top">
									 	<bean:write name="messageBean" property="message"/>
									 </td>
								 </tr>
								 
							
							</logic:iterate>			 
						 </table>	 
					</td>
				</tr>
				<tr>
					<td bgcolor="#00477F" class="small-gap"  colspan="6">
						&nbsp;
					</td>
					<td class="small-gap" >
						<img src="<%=request.getContextPath()%>/images/pbtm-line-end.jpg">
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
			</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
