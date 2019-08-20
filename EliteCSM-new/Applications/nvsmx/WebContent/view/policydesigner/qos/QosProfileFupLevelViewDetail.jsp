<s:iterator value="qosProfile.qosProfileDetailDataList">
    <fieldset class="fieldSet-line">
        <s:if test="fupLevel == 0">
            <legend align="top"><s:text name="qosprofile.hs" /> <s:text name="qosprofile"/></legend>
        </s:if>
        <s:else>
            <legend align="top"><s:text name="qosprofile.fup" />-<s:property value="fupLevel"/> <s:text name="qosprofile"/></legend>
        </s:else>
        <div align="right" class="nv-btn-group">
            <s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != qosProfile.pkgData.type}">
                    <div class="btn-group btn-group-xs" role="group">
                        <button type="button" class="btn btn-default" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/initUpdateHSQDetail?qosProfileDetailId=${id}&groupIds=${qosProfile.pkgData.groups}'">
                            <span class="glyphicon glyphicon-pencil"></span>
                        </button>
                    </div>
                    <%--//TODO provide fix for deleting qos profile detail  currently removing condition so that qos profile detail delete allowed  ---%>

                    <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton"
                         onmousedown="deleteConfirm()"
                         data-href="${pageContext.request.contextPath}/policydesigner/qos/QosProfile/deleteFupLevels?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&groupIds=${qosProfile.pkgData.groups}">
                        <button type="button" class="btn btn-default">
                            <span class="glyphicon glyphicon-trash"></span>
                        </button>
                    </div>

                </s:if>
                <s:else>
                    <div class="btn-group btn-group-xs" role="group">
                        <button type="button" class="btn btn-default" onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/initUpdateHSQDetail?qosProfileDetailId=${id}&groupIds=${qosProfile.pkgData.groups}'">
                            <span class="glyphicon glyphicon-pencil"></span>
                        </button>
                    </div>
                    <s:if test="fupLevel != 0 && fupLevel == qosProfile.qosProfileDetailDataList.size()-1 ">
                        <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/promotional/policydesigner/qos/QosProfile/PromotionalQosProfile/deleteFupLevels?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&groupIds=${qosProfile.pkgData.groups}" >
                            <button type="button" class="btn btn-default" >
                                <span class="glyphicon glyphicon-trash"></span>
                            </button>
                        </div>
                    </s:if>
                </s:else>
            </s:if>
            <s:else>
                <div class="btn-group btn-group-xs" role="group">
                    <button type="button" disabled="disabled" class="btn btn-default"  >
                        <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </div>
                <s:if test="fupLevel != 0 && fupLevel == qosProfile.qosProfileDetailDataList.size()-1 ">
                    <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton"   >
                        <button type="button" class="btn btn-default" disabled="disabled" >
                            <span class="glyphicon glyphicon-trash"></span>
                        </button>
                    </div>
                </s:if>
            </s:else>
        </div>
        <s:if test="action == 0">
            <div class="row">
                <div class="col-sm-4 col-xs-12">
                    <div class="row">
                        <s:label key="getText('qosprofile.action')" value="%{@com.elitecore.corenetvertex.constants.QoSProfileAction@fromValue(action).getName()}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
                            <%--<s:if test="action != 0">
                                <s:label key="getText('qosprofile.rejectcause')" value="%{rejectCause}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                                </div></div>
                                <s:label key="getText('qosprofile.redirecturl')" value="%{redirectUrl}"  cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2 col-lg-1" elementCssClass="col-xs-8 col-sm-10 col-lg-11 word-break"  />
                                </div>
                            </s:if>--%>
                        <s:label key="getText('qosprofile.detail.qci')" value="%{@com.elitecore.corenetvertex.constants.QCI@fromId(qci).getDisplayValue()}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
                    </div>
                </div>
                <div class="col-sm-4 col-xs-12 leftVerticalLine">
                    <div class="row">
                        <s:if test="mbrdl != null">
                            <div class="col-sm-12 row">
                                <div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px"><s:text name="qosprofile.detail.mbr" /> </div>
                                <div class="col-xs-8 col-sm-5">
                                    <s:property value="mbrdl"/> <s:property value="mbrdlUnit"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
                                </div>
                            </div>
                        </s:if>
                        <div class="col-sm-12 row">
                            <div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px">
                                <s:if test="mbrdl == null">
                                    <s:text name="qosprofile.detail.mbr" />
                                </s:if>
                            </div>
                            <div class="col-xs-8 col-sm-5" style="margin-bottom: 5px">
                                <s:if test="mbrul != null">
                                    <s:property value="mbrul"/> <s:property value="mbrulUnit"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
                                </s:if>
                            </div>
                        </div>
                        <s:if test="aambrdl != null">
                            <div class="col-sm-12 row">
                                <div class="col-xs-4 col-sm-4"style="font-weight: 700;margin-bottom: 5px"><s:text name="qosprofile.detail.aambr" /> </div>
                                <div class="col-xs-8 col-sm-8">
                                    <s:property value="aambrdl"/> <s:property value="aambrdlUnit"/> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>

                                </div>
                            </div>
                        </s:if>
                        <div class="col-sm-12 row">
                            <div class="col-xs-4 col-sm-4" style="font-weight: 700;margin-bottom: 5px">
                                <s:if test="aambrdl == null">
                                    <s:text name="qosprofile.detail.aambr" />
                                </s:if>
                            </div>
                            <div class="col-xs-8 col-sm-8" style="margin-bottom: 5px">
                                <s:if test="aambrul != null">
                                    <s:property value="aambrul"/> <s:property value="aambrulUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
                                </s:if>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4 col-xs-12 leftVerticalLine">
                    <div class="row">
                        <s:label key="getText('qosprofile.detail.precapability')" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(preCapability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
                        <s:label key="getText('qosprofile.detail.prevulnerability')" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromValue(preVulnerability).getStringName()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
                        <s:label key="getText('qosprofile.detail.prioritylevel')" value="%{priorityLevel}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-7" elementCssClass="col-xs-8 col-sm-5" />
                    </div>
                </div>
            </div>

            <div class="row">
                <s:label key="getText('qosprofile.redirecturl')" value="%{redirectUrl}"  cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2 col-lg-1" elementCssClass="col-xs-8 col-sm-10 col-lg-11 word-break"  />
            </div>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="qosProfile.detail.usagemonitoringinformation" /> </legend>
                <div class="row">
                    <div class="col-sm-4 col-xs-12">
                        <div class="row">
                            <div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"><s:text name="qosProfile.detail.usagemonitoring" /> </div>
                            <div class="col-xs-8 col-sm-7" >
                                <s:property value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(usageMonitoring).getStringName()}"/>
                            </div>
                        </div>
                    </div>
                    <s:if test="usageMonitoring == false">
                    <div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
                        </s:if>
                        <s:else>
                        <div class="col-sm-4 col-xs-12 leftVerticalLine">
                            </s:else>
                            <div class="row">
                                <div class="col-xs-4 col-sm-5" style="margin-bottom: 5px"><s:label value="Slice Quota" /></div>
                                <div class="col-xs-8 col-sm-7">
                                    <s:if test="sliceTotal != null">
                                        <s:property value="sliceTotal"/> <s:property value="sliceTotalUnit"/>
                                    </s:if>
                                </div>
                                <div class="col-xs-4 col-sm-5" ></div>
                                <div class="col-xs-8 col-sm-7" >
                                    <s:if test="sliceDownload != null">
                                        <s:property value="sliceDownload" /> <s:property value="sliceDownloadUnit" /> <span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
                                    </s:if>
                                </div>
                                <div class="col-xs-4 col-sm-5"></div>
                                <div class="col-xs-8 col-sm-7">
                                    <s:if test="sliceUpload != null">
                                        <s:property value="sliceUpload"/> <s:property value="sliceUploadUnit"/> <span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
                                    </s:if>
                                </div>
                            </div>
                        </div>
                        <s:if test="usageMonitoring == false">
                        <div class="col-sm-4 col-xs-12 leftVerticalLine" style="color:grey">
                            </s:if>
                            <s:else>
                            <div class="col-sm-4 col-xs-12 leftVerticalLine">
                                </s:else>
                                <div class="row">
                                    <div class="col-xs-4 col-sm-5" style="font-weight: 700;margin-bottom: 5px"> <s:text name="Slice Time" /> </div>
                                    <div class="col-xs-8 col-sm-7">
                                        <s:if test="sliceTime != null">
                                            <span><s:property value="sliceTime"/></span>
                                            <span><s:property value="sliceTimeUnit"/></span>
                                        </s:if>
                                    </div>
                                </div>
                            </div>
                        </div>
            </fieldset>
            <div style="text-align: right;">
                <s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">

                    <s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() == qosProfile.pkgData.type}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  onclick="javascript:location.href='${pageContext.request.contextPath}/promotional/policydesigner/pccrule/PccRule/PromotionalPCCRule/init?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&pkgId=${qosProfile.pkgData.id }&groupIds=${qosProfile.pkgData.groups}'" >
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.addpccrule" />
                        </button>
                    </s:if>
                    <s:else>
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/init?qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&pkgId=${qosProfile.pkgData.id }&groupIds=${qosProfile.pkgData.groups}'" >
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.addpccrule" />
                        </button>

                    </s:else>
                    <s:if test = "%{qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" data-toggle="modal" onclick="wrapTable('PCCRuleDataGlobalSearch-${id}',${fupLevel},'${id}');">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.addpccrule.global" />
                        </button>
                    </s:if>
                    <s:if test = "%{(qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()) || (qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@ADDON.name() && qosProfile.pkgData.exclusiveAddOn == true)}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  data-toggle="modal" data-target="#addChargingRuleBaseName-${id}" onclick="wrapCRBNTable('ChargingRuleBaseNameSearch-${id}',${fupLevel});">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.chargingrulebasename" />
                        </button>
                    </s:if>

                </s:if>
                <s:else>
                    <button class="btn btn-primary btn-xs" disabled="disabled" style="padding-top: 3px; padding-bottom: 3px" >
                        <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                        <s:text name="qosprofile.addpccrule" />
                    </button>
                    <s:if test = "%{qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
                        <button class="btn btn-primary btn-xs" disabled="disabled" style="padding-top: 3px; padding-bottom: 3px">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.addpccrule.global" />
                        </button>
                    </s:if>

                    <s:if test = "%{qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()
                                    || qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@ADDON.name() && qosProfile.pkgData.exclusiveAddOn == true}">
                        <button class="btn btn-primary btn-xs" disabled="disabled" style="padding-top: 3px; padding-bottom: 3px">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="qosprofile.chargingrulebasename" />
                        </button>
                    </s:if>


                </s:else>
            </div>

            <s:if test = "%{qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
                <%@include file="AddGlobalPCCRule.jsp"%>
            </s:if>

            <%@include file="AddChargingRuleBaseName.jsp" %>

            <nv:dataTable
                    id="qosProfileDetail-${fupLevel}"
                    actionUrl="/searchTable/policydesigner/util/Nvsmx/execute?qosProfileDetailId=${id}"
                    beanType="com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper"
                    width="100%"
                    showPagination="false"
                    showInfo="false"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="Rule Name" 		 beanProperty="name" tdCssClass="text-left text-middle word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/view?pccRuleId=id&type=type&requestFromQosView=true&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}" tdStyle="width:200px" />
                <nv:dataTableColumn title="Type" 		     beanProperty="type" tdCssClass="text-left text-middle" tdStyle="width:100px"/>
                <nv:dataTableColumn title="Service" 		 beanProperty="serviceName" tdCssClass="text-left text-middle" tdStyle="width:100px"/>
                <nv:dataTableColumn title="Monitoring Key"   beanProperty="monitoringKey" tdCssClass="text-left text-middle word-break" tdStyle="width:200px"/>
                <nv:dataTableColumn title="MBR" 			 beanProperty="mbrdl,mbrul" tdCssClass="text-right" cssClass="text-right" tdStyle="width:180px"/>
                <nv:dataTableColumn title="GBR" 		 	 beanProperty="gbrdl,gbrul" tdCssClass="text-right" cssClass="text-right" tdStyle="width:180px"/>
                <s:if test="%{qosProfile.pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                    <s:if test = "%{qosProfile.pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()}">
                        <nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>"  disableWhen="scope==GLOBAL" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" hrefurl="edit:${pageContext.request.contextPath}/promotional/policydesigner/pccrule/PccRule/PromotionalPCCRule/init?pccRuleId=id&qosProfileId=${qosProfile.id}&requestFromQosProfileView=true&groupIds=${qosProfile.pkgData.groups}"/>
                        <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/pccrule/PccRule/PromotionalPCCRule/delete?pccRuleId=id&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&scope=scope&fromQoSProfileView=true&groupIds=${qosProfile.pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" />
                    </s:if>
                    <s:else>
                        <nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>"  disableWhen="scope==GLOBAL" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" hrefurl="edit:${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/init?pccRuleId=id&type=type&qosProfileId=${qosProfile.id}&requestFromQosProfileView=true&groupIds=${qosProfile.pkgData.groups}"/>
                        <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/delete?pccRuleId=id&type=type&qosProfileDetailId=${id}&qosProfileId=${qosProfile.id}&fromQoSProfileView=true&groupIds=${qosProfile.pkgData.groups}" style="width:20px;" tdStyle="width:20px;"  tdCssClass="text-middle" />
                    </s:else>
                </s:if>
                <s:else>
                    <nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.50;"  tdCssClass="text-middle"  />
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"  style="width:20px;" tdStyle="width:20px;opacity:0.50;"  tdCssClass="text-middle" />
                </s:else>
            </nv:dataTable>

        </s:if>
        <s:if test="action != 0">
            <div class="row">
                <div class="col-sm-4 col-xs-12">
                    <div class="row">
                        <s:label key="getText('qosprofile.action')" value="%{@com.elitecore.corenetvertex.constants.QoSProfileAction@fromValue(action).getName()}"  cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"  />
                        <s:label key="getText('qosprofile.rejectcause')" value="%{rejectCause}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                    </div>
                </div>
                <s:label key="getText('qosprofile.redirecturl')" value="%{redirectUrl}"  cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-2 col-lg-1" elementCssClass="col-xs-8 col-sm-10 col-lg-11 word-break"  />
            </div>
        </s:if>
    </fieldset>
</s:iterator>
