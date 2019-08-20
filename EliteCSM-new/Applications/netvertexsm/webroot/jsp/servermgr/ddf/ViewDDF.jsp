<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.hibernate.servermgr.spr.ddf.HDDFDataManager"%>
<%@page import="com.elitecore.corenetvertex.spr.ddf.DDFTableData"%>
<%@page import="com.elitecore.corenetvertex.spr.ddf.DDFEntryData"%>
<%@page import="com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData"%>
<%

	DDFTableData ddfTableData = (DDFTableData)request.getAttribute("ddfTableData");
	SubscriberRepositoryData defaultSPR = ddfTableData.getDefaultSPR();

%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="ddfBean" name="ddfTableData" scope="request" type="DDFTableData" />



	<tr>
		<td valign="top" align="right">
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
				<tr>
					<td class="tblheader-bold" valign="top" colspan="3"><bean:message
							bundle="ddfResources" key="ddf.basicdetail" /></td>
				</tr>
				<tr>
					<td align="left" class="tbllabelcol" valign="top" width="30%">
						<bean:message bundle="ddfResources" key="ddf.defaultSPR" />
					</td>
					<td align="left" class="tblrows" valign="top" colspan="2">
						<%
							if (defaultSPR != null) {
						%> <%=defaultSPR.getName()%>&nbsp;
					</td>
					<%
						}
					%>
				</tr>

				<tr>
					<td align="left" class="tbllabelcol" valign="top"><bean:message
							bundle="ddfResources" key="ddf.stripPrefixes" /></td>
					<td align="left" class="tblrows" valign="top" colspan="2"><bean:write
							name="ddfBean" property="stripPrefixes" />&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2">
						<table cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<tr>
								<td class="tblheader-bold" height="20%" colspan="3">
									<bean:message bundle="ddfResources" key="ddf.table.title" />
								</td>
							</tr>
							<tr>
								<td class="tblheader-bold" height="20%"><bean:message
										bundle="ddfResources" key="ddf.table.identityPatternHeading" /></td>
								<td class="tblheader-bold" height="20%"><bean:message
										bundle="ddfResources" key="ddf.table.sprHeading" /></td>
							</tr>

							<%if(Collectionz.isNullOrEmpty(ddfTableData.getDdfEntries()) == false) { %>
							<logic:iterate id="ddfEntryBean" name="ddfBean"
								property="ddfEntries" scope="page" type="DDFEntryData">
								<tr>
									<td class="tblrows" height="20%"><bean:write
											name="ddfEntryBean" property="identityPattern" />&nbsp;</td>
									<td class="tblcol" height="20%"><bean:write
											name="ddfEntryBean" property="subscriberRepositoryData.name" />&nbsp;</td>
								</tr>
							</logic:iterate>
							<%} else {%>
							<tr>
								<td class="tblfirstcol" height="20%" colspan="2">No Record
									Found</td>
							</tr>
							<%}%>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
