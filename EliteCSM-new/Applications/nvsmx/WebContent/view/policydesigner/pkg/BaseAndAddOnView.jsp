<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>

<%
 JsonArray usageNotificationData = (JsonArray)request.getAttribute(Attributes.USAGE_NOTIFICATION_DATA);
 JsonArray quotaNotificationData = (JsonArray)request.getAttribute(Attributes.QUOTA_NOTIFICATION_DATA);
 JsonArray rncProfileData = (JsonArray)request.getAttribute(Attributes.RNC_PROFILE_DATA);
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
	<s:hidden name="groupIds" value="%{pkgData.groups}" />
			<fieldset class="fieldSet-line" >
				<legend align="top"><s:text name="pkg.quotaprofiles"></s:text></legend>
					<s:if test="%{pkgData.quotaProfileType.toString() == @com.elitecore.corenetvertex.constants.QuotaProfileType@USAGE_METERING_BASED.name()}">
						<div style="text-align: right;">
						 <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
						 	
							 	<s:if test="%{pkgData.quotaProfiles.size == 3}">
								 	<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="pkg.addquotaprofile" />
									</button>
							 	</s:if>
							 	<s:else>

									<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type }">

									<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="pkg.addquotaprofile" />
									</button>
									</s:if>
									<s:else>

										<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/quota/QuotaProfile/PromotionalQuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
											<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
											<s:text name="pkg.addquotaprofile" />
										</button>
									</s:else>
								</s:else>
							 
						</s:if>
						<s:else>
							<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
								<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
								<s:text name="pkg.addquotaprofile" />
							</button>
						</s:else>
						</div>
				 		<nv:dataTable 
				 				id="quotaProfileData"
				 				list="${usageMeteringQuotaProfilesAsJsonString}"
								showPagination="false"
								showInfo="false"
								cssClass="table table-blue" 
								subTableUrl="/subTable/policydesigner/quota/QuotaProfile/viewQuotaProfileDetail" width="100%">
								<nv:dataTableColumn title="Name"   	beanProperty="name" 	style="font-weight: bold;color: #4679bd; " tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/view?quotaProfileId=id" sortable="true"/>
								<nv:dataTableColumn title="Total" 	beanProperty="total"  	tdCssClass="usage-qouta-td" cssClass="usage-qouta-th" />						
								<nv:dataTableColumn title="Daily" 	beanProperty="daily" 	tdCssClass="usage-qouta-td" cssClass="usage-qouta-th" />						
								<nv:dataTableColumn title="Weekly" 	beanProperty="weekly" 	tdCssClass="usage-qouta-td" cssClass="usage-qouta-th" />					
								<nv:dataTableColumn title="Custom" 	beanProperty="custom" 	tdCssClass="usage-qouta-td" cssClass="usage-qouta-th" />
							    <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
									<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
										<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/init?quotaProfileId=id&groupIds=${pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  />
									</s:if>
									<s:else>
										<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/promotional/policydesigner/quota/QuotaProfile/PromotionalQuotaProfile/init?quotaProfileId=id&groupIds=${pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  />
									</s:else>
								    <s:if test="%{(pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name()) || (pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name())}">
										<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
											<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"		style="width:20px;" tdStyle="width:20px;"  hiddenElement="pkgData.id" />
										</s:if>
										<s:else>
											<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/quota/QuotaProfile/PromotionalQuotaProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"		style="width:20px;" tdStyle="width:20px;"  hiddenElement="pkgData.id" />
										</s:else>
						            </s:if>
						            <s:else>
						             <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash' disabled='disabled'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
						            </s:else>
					            </s:if>
					            <s:else>
					            	<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon Rglyphicon-pencil' disabled='disabled' ></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"   />
					            	<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash' disabled='disabled'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
					            </s:else>
					 	</nv:dataTable>	
				 	</s:if>
				 	<s:elseif test="%{pkgData.quotaProfileType.toString() == @com.elitecore.corenetvertex.constants.QuotaProfileType@SY_COUNTER_BASED.name()}">
				 		<div style="text-align: right;">
				 		<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
							 <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
							 	<s:if test="%{pkgData.syQuotaProfileDatas.size == 3}">
								 	<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/syquota/SyQuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="pkg.addquotaprofile" />
									</button>
							 	</s:if>
							 	<s:else>
									<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() == pkgData.type}">
										<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/syquota/SyQuotaProfile/PromotionalSyQuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
											<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
											<s:text name="pkg.addquotaprofile" />
										</button>
									</s:if>
									<s:else>
										<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/syquota/SyQuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
											<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
											<s:text name="pkg.addquotaprofile" />
										</button>
									</s:else>
								</s:else>
							</s:if>
							<s:else>
								<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/syquota/SyQuotaProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
									<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
									<s:text name="pkg.addquotaprofile" />
								</button>
							</s:else>
						</s:if>
						<s:else>
							<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" >
								<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
								<s:text name="pkg.addquotaprofile" />
							</button>
						</s:else>
						</div>
						<nv:dataTable 
				 				id="syQuotaProfileDatas"
				 				list="${syQuotaProfilesAsJsonString}" 
								showPagination="false"
								showInfo="false"
								cssClass="table table-blue" 
								subTableUrl="/searchTable/policydesigner/syquota/SyQuotaProfile/syQuotaProfileView"
								width="100%"> 
								<nv:dataTableColumn title="Name" 		 beanProperty="name" tdCssClass="word-break" tdStyle="min-width:50px;"></nv:dataTableColumn>
								<nv:dataTableColumn title="Description"  beanProperty="description" style="width:400px" tdStyle="word-wrap:break-word;"></nv:dataTableColumn>
								<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
									<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() == pkgData.type}">
										<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/promotional/policydesigner/syquota/SyQuotaProfile/PromotionalSyQuotaProfile/init?quotaProfileId=id&groupIds=${pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  />
										<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/syquota/SyQuotaProfile/PromotionalSyQuotaProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}" style="width:20px;" tdStyle="width:20px;"/>
									</s:if>
									<s:else>
										<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/policydesigner/syquota/SyQuotaProfile/init?quotaProfileId=id&groupIds=${pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  />
										<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/syquota/SyQuotaProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}" style="width:20px;" tdStyle="width:20px;"/>
									</s:else>
								</s:if>
								<s:else>
									<nv:dataTableColumn title="" sortable="false"   icon="<span disabled='disabled'  class='glyphicon glyphicon-pencil'></span>"  style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
									<nv:dataTableColumn title="" icon="<span disabled='disabled'  class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"/>			           
								</s:else>
					 	</nv:dataTable>
				 	</s:elseif>
					<s:elseif test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED.name() == pkgData.quotaProfileType.toString()}">
						<div style="text-align: right;">
							<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">

								<s:if test="%{pkgData.rncProfileDatas.size == 3}">
									<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" >
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="pkg.addquotaprofile" />
									</button>
								</s:if>
								<s:else>
										<%--RnC Base Quota Profile can be configured for Base, Addon and promotional--%>
										<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="addRncProfile()">
											<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
											<s:text name="pkg.addquotaprofile" />
										</button>
								</s:else>

							</s:if>
							<s:else>
								<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px">
									<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
									<s:text name="pkg.addquotaprofile" />
								</button>
							</s:else>
						</div>
						<%@include file="/view/policydesigner/rnc/RncProfile.jsp"%>

						<nv:dataTable
								id="rncProfileData"
								list="${rncProfileAsJsonString}"
								showPagination="false"
								showInfo="false"
								cssClass="table table-blue"
								subTableUrl="/subTable/policydesigner/rnc/RncProfile/rncConfigurationSubView"
								width="100%">
							<nv:dataTableColumn title="Name"   	beanProperty="name" 	style="font-weight: bold;color: #4679bd; " tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/view?quotaProfileId=id" sortable="true"/>
							<nv:dataTableColumn title="Description"   	beanProperty="description" 	tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" />

							<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
								<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
									<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:javascript:updateRncProfile(id)" style="width:20px;" tdStyle="width:20px;"  />
								</s:if>
								<s:else>
									<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:javascript:updateRncProfile(id)" style="width:20px;" tdStyle="width:20px;"  />
								</s:else>
								<s:if test="%{(pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name()) || (pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name())}">
									<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
										<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"		style="width:20px;" tdStyle="width:20px;"  hiddenElement="pkgData.id" />
									</s:if>
									<s:else>
										<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/rnc/RncProfile/PromotionalQuotaProfile/delete?quotaProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"		style="width:20px;" tdStyle="width:20px;"  hiddenElement="pkgData.id" />
									</s:else>
								</s:if>
								<s:else>
									<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash' disabled='disabled'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
								</s:else>
							</s:if>
							<s:else>
								<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon Rglyphicon-pencil' disabled='disabled' ></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"   />
								<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash' disabled='disabled'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  />
							</s:else>
						</nv:dataTable>
					</s:elseif>
			</fieldset>
	</div>

   <s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@BASE.name() == pkgData.type}">
	<s:if test="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED.name() == pkgData.quotaProfileType.toString()}">
		<%@include file="DataRateCardConfiguration.jsp"%>
	</s:if>
   </s:if>
		
 	<div class="row">
			<fieldset class="fieldSet-line">
				<legend align="top"><s:text name="pkg.qosprofiles"></s:text></legend>
				<div style="text-align: right;">
					<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
					<s:if test="%{pkgData.qosProfiles.size > 1}">
					<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/excludeValidation/policydesigner/qos/QosProfile/manageOrder?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
						<span class="glyphicon glyphicon-sort" title="Add"></span>
						<s:text name="manage.order" />
					</button>
					</s:if>
						<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
							<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
								<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
								<s:text name="pkg.addqosprofiles" />
							</button>
						</s:if>
						<s:else>
							<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
								<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
								<s:text name="pkg.addqosprofiles" />
							</button>
						</s:else>
					</s:if>
					<s:else>
					<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  >
						<span class="glyphicon glyphicon-sort" title="Add"></span>
						<s:text name="manage.order" />
					</button>
					<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}">
						<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
						<s:text name="pkg.addqosprofiles" />
					</button>
					</s:else>
				</div>
		 			<nv:dataTable 
		 				id="qosProfileData" 
		 				list="${qosProfilesAsJsonString}"
						showPagination="false"
						showInfo="false"
						cssClass="table table-blue" 
						subTableUrl="/subTable/policydesigner/qos/QosProfile/viewQosProfileData" width="100%">
						
						
						<nv:dataTableColumn title="#"   	
											beanProperty="dataTable.RowNumber"
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left;width:10px;" tdCssClass="word-break"	/> 	 											
						
						<nv:dataTableColumn title="Name"   	
											beanProperty="name" 	
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" 											
											hrefurl="${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=id"/>
											
	                	<nv:dataTableColumn title="Quota Profile" 	
											beanProperty="quotaProfileName"
											cssClass="qos-th-col-left"
											tdStyle="text-align:left;min-width:50px;" tdCssClass="word-break" />

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

						<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
							<nv:dataTableColumn title="" 
												icon="<span class='glyphicon glyphicon-pencil'></span>"   
												hrefurl="edit:${pageContext.request.contextPath}/policydesigner/qos/QosProfile/init?qosProfileId=id&groupIds=${pkgData.groups}"
												style="width:20px;" tdStyle="width:20px;"  />
												
				            <nv:dataTableColumn title="" 
				            					icon="<span class='glyphicon glyphicon-trash'></span>" 	
				            					hrefurl="delete:${pageContext.request.contextPath}/policydesigner/qos/QosProfile/delete?qosProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"
				            					style="width:20px;" tdStyle="width:20px;"  />
						</s:if>
							<s:else>
								<nv:dataTableColumn title=""
													icon="<span class='glyphicon glyphicon-pencil'></span>"
													hrefurl="edit:${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/init?qosProfileId=id&groupIds=${pkgData.groups}"
													style="width:20px;" tdStyle="width:20px;"  />

								<nv:dataTableColumn title=""
													icon="<span class='glyphicon glyphicon-trash'></span>"
													hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/delete?qosProfileId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"
													style="width:20px;" tdStyle="width:20px;"  />
							</s:else>

				            					
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
	 
	 <s:if test="%{pkgData.quotaProfileType.toString() != @com.elitecore.corenetvertex.constants.QuotaProfileType@SY_COUNTER_BASED.name()}">
		 <s:if test="%{pkgData.quotaProfileType == @com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED}">
			 <%@include file="QuotaNotificationView.jsp" %>
		 </s:if>
		 <s:elseif test="%{pkgData.quotaProfileType == @com.elitecore.corenetvertex.constants.QuotaProfileType@USAGE_METERING_BASED}">
		    <%@include file="UsageNotificationView.jsp"%>
		 </s:elseif>
	  </s:if>
