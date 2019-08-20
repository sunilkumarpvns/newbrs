<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>

<%
 JsonArray usageNotificationData = (JsonArray)request.getAttribute(Attributes.USAGE_NOTIFICATION_DATA);
%>
<style type="text/css">
.qos-th-col{
	font-weight: bold;color: #4679bd; width:100px; text-align:right;
}

.qos-th-col-left{
	font-weight: bold;color: #4679bd; width:100px; text-align:left;
}

.qos-td-cell{
	text-align:right;width:100px;
}

.qos-td-cell-left{
	text-align:left;width:100px;
}

.usage-qouta-th{
	font-weight: bold;
	color: #4679bd; 
	width:100px; 
	text-align:right;
}
.usage-qouta-td{
	text-align:right;
	width:100px;
	word-break: break-word;
}
@media ( max-width :600px) {
	.qos-td-cell{
		width:230px;
	}
	.usage-qouta-td{
		width:230px !important;
	}
}
</style>
 
 	<div class="row">
			<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="pkg.qosprofiles"></s:text></legend>
				<div style="text-align: right;">
					<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<s:if test="%{pkgData.qosProfiles.size > 1}">
					<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/excludeValidation/policydesigner/emergencypkgqos/EmergencyPkgQos/manageOrder?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
						<span class="glyphicon glyphicon-sort" title="Manage Order"></span>
						<s:text name="manage.order" />
					</button>
					</s:if>
					<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
						<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
						<s:text name="pkg.addqosprofiles" />
					</button>
					</s:if>
					<s:else>
					<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  >
						<span class="glyphicon glyphicon-sort" title="Manage Order"></span>
						<s:text name="manage.order" />
					</button>
					<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
						<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
						<s:text name="pkg.addqosprofiles" />
					</button>
					</s:else>
				</div>
		 			<nv:dataTable 
		 				id="qosProfileData" 
		 				actionUrl="/searchTable/policydesigner/util/Nvsmx/execute?pkgId=${pkgData.id}"
						beanType="com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper" 
						rows="5" 
						showPagination="false"
						showInfo="false"
						cssClass="table table-blue" 
						subTableUrl="/subTable/policydesigner/emergencypkgqos/EmergencyPkgQos/viewQosProfileData" width="100%">
						
						
						<nv:dataTableColumn title="#"   	
											beanProperty="dataTable.RowNumber"
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left;width:10px;" tdCssClass="word-break"	/> 	 											
						
						<nv:dataTableColumn title="Name"   	
											beanProperty="name" 	
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" 											
											hrefurl="${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId=id"/>
											
 						<nv:dataTableColumn title="MBR" 	
											beanProperty="mbrdl,mbrul"
											cssClass="qos-th-col"											 											 										
											tdCssClass="qos-td-cell"	/>
																	
						<nv:dataTableColumn title="AAMBR" 	
											beanProperty="aambrdl,aambrul" 
											cssClass="qos-th-col" 
											tdCssClass="qos-td-cell"	/>
											
						<nv:dataTableColumn title="QCI" 	
											beanProperty="qci" 		
											cssClass="qos-th-col" 
											tdCssClass="qos-td-cell"/>
						<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">											
						
							<nv:dataTableColumn title="" 
												icon="<span class='glyphicon glyphicon-pencil'></span>"   
												hrefurl="edit:${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/init?qosProfileId=id&groupIds=${pkgData.groups}"
												style="width:20px;" tdStyle="width:20px;"  />
												
				            <nv:dataTableColumn title="" 
				            					icon="<span class='glyphicon glyphicon-trash'></span>" 	
				            					hrefurl="delete:${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/delete?qosProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"
				            					style="width:20px;" tdStyle="width:20px;"  />
				            					
			            </s:if>
			            <s:else>
				            <nv:dataTableColumn title="" 				            					
												icon="<span disabled='disabled' class='glyphicon glyphicon-pencil'></span>"   												  
												style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
												
				            <nv:dataTableColumn title="" 				            					
				            					icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>" 					            					 	
				            					style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
			            </s:else>
					 </nv:dataTable>					
			</fieldset>
	  </div>
	 