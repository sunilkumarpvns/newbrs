<%@page import="java.util.Iterator"%>
<%@page
	import="com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerLogsForm"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.io.File"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>

<% 
	String localBasePath = request.getContextPath();
	String netServerId = (String) request.getAttribute("netServerId");
	ListNetServerLogsForm listNetServerLogsForm = (ListNetServerLogsForm) request.getAttribute("listNetServerLogsForm");
%>
<script>
jQuery(document).ready(function(){
		var collapsedval ="<%=request.getContextPath()%>/images/closeddirectory.png";
		var expandval ="<%=request.getContextPath()%>/images/opendirectory.png";
		<%if(listNetServerLogsForm.getServerLogFileMap()!=null){%>
		$('ul#serverlogs').collapsibleCheckboxTree('collapsed',null,collapsedval,expandval);
		<%}%>
		<%if(listNetServerLogsForm.getServerLogFileMap()!=null){%>
		$('ul#servicelogs').collapsibleCheckboxTree('collapsed',null,collapsedval,expandval);
		<%}%>
		<%if(listNetServerLogsForm.getServerLogFileMap()!=null){%>
		$('ul#cdrs').collapsibleCheckboxTree('collapsed',null,collapsedval,expandval);
		<%}%>
		
});

</script>


<%! 
public void drawTree(JspWriter out,Object obj,String netServerId, String filePath, HttpServletRequest request) throws Exception{
	
	if(obj instanceof String) {
		File file = new File((String)obj);
		out.write("<li><img src='"+request.getContextPath()+"/images/log.png' width=16px height=16px/><a href='"+request.getContextPath()+"/DownloadNetServerFileServlet?fileName="+filePath+"&netServerId="+netServerId+"'>"+file.getName()+"</a></li>");			
		
	}else if (obj instanceof Map){
		Map map = (Map) obj;
		Iterator<String> childIterator = map.keySet().iterator();
		while(childIterator.hasNext()) {
			String key = childIterator.next();
			Object childObject = map.get(key);
			File file = new File(key);
			if(childObject instanceof Map){
				out.write("<li><b>" + file.getName() + "</b>" + "<ul>");
			}
			drawTree(out,childObject,netServerId,key,request);
			if(childObject instanceof Map){
			out.write("</li></ul>");
			}
		}
	}
}
public void initTree(JspWriter out,Map<String,Object> fileMap,String netServerId,String filePath, HttpServletRequest request) throws Exception{
	Iterator<String> childIterator = fileMap.keySet().iterator();
	while(childIterator.hasNext()) {
		String key = childIterator.next();
		Object childObject = fileMap.get(key);
		File file = new File(key);
		if(childObject instanceof Map){
			out.write("<li><b>" + file.getName() + "</b>" + "<ul>");
		}
		drawTree(out,childObject,netServerId, key,request);
		if(childObject instanceof Map){
			out.write("</ul></li>");
		}
	}
}
%>
<script>
    
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" align="right">
			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				height="15%">

				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
				<logic:notEmpty name="listNetServerLogsForm"
					property="serverLogFileMap">

					<tr>
						<td class="tblheader-bold" colspan="5" height="20%"><bean:message
								bundle="servermgrResources" key="servermgr.download.serverlogs" />
						</td>
					</tr>
					<tr>
						<td class="lableltext" colspan="5" height="20%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="left" colspan="3" class="labeltext"><ul
											id="serverlogs">

											<% 
                                  		initTree(out,listNetServerLogsForm.getServerLogFileMap(),netServerId,null,request);
                                   %>
										</ul></td>
								</tr>

							</table>
						</td>
					</tr>
				</logic:notEmpty>



				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
				<logic:notEmpty name="listNetServerLogsForm"
					property="serviceLogFileMap">
					<tr>
						<td class="tblheader-bold" colspan="5" height="20%"><bean:message
								bundle="servermgrResources" key="servermgr.download.servicelogs" />
						</td>
					</tr>
					<tr>
						<td class="lableltext" colspan="5" height="20%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td align="left" colspan="3" class="labeltext"><ul
											id="servicelogs">
											<% 
                               
                            		initTree(out,listNetServerLogsForm.getServiceLogFileMap(),netServerId,null,request);			                              	

                                  %>
										</ul></td>
								</tr>

							</table>
						</td>
					</tr>


					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</logic:notEmpty>
				<logic:notEmpty name="listNetServerLogsForm" property="cdrFileMap">
					<tr>
						<td class="tblheader-bold" colspan="5" height="20%"><bean:message
								bundle="servermgrResources" key="servermgr.download.cdr" /></td>
					</tr>
					<tr>
						<td class="lableltext" colspan="5" height="20%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td align="left" colspan="3" class="labeltext"><ul
											id="cdrs">
											<% 
                                  initTree(out,listNetServerLogsForm.getCdrFileMap(),netServerId,null,request);		
                                  %>
										</ul></td>
								</tr>

							</table>
						</td>
					</tr>


					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</logic:notEmpty>

				<logic:equal name="listNetServerLogsForm" scope="request"
					property="errorCode" value="-1">
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>
						<td class="blue-text-bold" colspan="3"><bean:message
								bundle="servermgrResources" key="servermgr.connectionfailure" />
							<br> <bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceip" /> :<bean:write
								name="netServerInstanceData" property="adminHost" /> <br>
							<bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceport" /> : <bean:write
								name="netServerInstanceData" property="adminPort" /> &nbsp;</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</logic:equal>

			</table>
		</td>
	</tr>
</table>