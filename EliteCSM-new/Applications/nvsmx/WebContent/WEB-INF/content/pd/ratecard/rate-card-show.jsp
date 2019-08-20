<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
    .label-bold{
    	font-weight: bold;
    	margin-bottom: 0;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" id="auditButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
				
				onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/ratecard/rate-card/${id}'">
                              
                  <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" id="editButton" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/ratecard/rate-card/${id}/edit?rncPackageId=${rncPackageData.id}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" id="deleteButton" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/ratecard/rate-card/${id}?_method=DELETE&rncPackageId=${rncPackageData.id}">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="ratecard.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                           <%--  <s:label key="ratecard.currency" value="%{currency}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.accounteffect" value="%{accountEffect}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /> --%>
                           <%--  <s:label key="ratecard.rateUploadFormat" value="%{rateFileFormatData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /> --%>
                        	<s:label key="ratecard.rateUom" value="%{rateUom}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.pulseUom" value="%{pulseUom}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                   
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                        	<s:label key="ratecard.labelOne" value="%{labelKey1}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="ratecard.labelTwo" value="%{labelKey2}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{createdDate != null}">
                                <s:set var="createdByDate">
                                    <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                            <s:label key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                        </div>
                    </div>
                  </fieldset>
                  
                  
                       	
               <div id="versionHeaderDiv">
                    <div class="col-xs-12 col-sm-12">
                        <table id='ratecardVersions'  class="table table-blue table-bordered">
                            <caption class="caption-header">
                                <s:text name="ratecard.version" />
                                <div align="right" class="display-btn">
	                              <span class="btn btn-group btn-group-xs defaultBtn" onclick="addVersionData();" id="addRow"></span>
                                </div>
                            </caption>
                            <tbody>
								<tr>
									<td>
										<div id="versionDivDetails">
											 <table id='versionDetailTbl' class="table">
	                            <s:iterator value="rateCardVersionRelation" status="i" var="rateCardVersionRelation">
	                        	<tr name='versionRow'>	
	                        		<td style="border: 0px !important;">
	                        			<div class="col-xs-12 col-sm-12">
											<table class="table table-blue table-bordered version-detail" style="margin-bottom: 0px !important;">
												<caption class="caption-header">
													<span class="version-text" style="width: 97%; float: left;">
														<h4 style="width: 20%; float: left;">
															<s:label value="%{#rateCardVersionRelation.versionName}"></s:label>
															<s:textfield type="hidden" name="rateCardVersionRelation[%{#i.count - 1}].versionName" cssClass="form-control focusElement version" maxlength="50"/>
														</h4> 
													</span>
												</caption>
												<thead>
													<th><s:label value="%{labelKey1}" cssClass="label-bold labelKeyOne"/></th>
                      								<th><s:label value="%{labelKey2}" cssClass="label-bold labelKeyTwo"/></th>
													<th><s:text name="ratecard.version.slab" /></th>
													<th><s:text name="ratecard.version.pulse" /></th>
													<th><s:text name="ratecard.version.rate" /></th>
													<th><s:text name="ratecard.version.tier.rate.type" /></th>
													<th><s:text name="ratecard.version.fromdate" /></th>
												</thead>
												<tbody>
													<s:iterator value="#rateCardVersionRelation.rateCardVersionDetail" status="j" var="rateCardVersionDetail">
													<tr>
														<td class="col-xs-12 col-sm-2"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text labelOne"   value="%{#rateCardVersionDetail.label1}" maxlength="50"/></td>
														<td class="col-xs-12 col-sm-2"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text labelTwo"   value="%{#rateCardVersionDetail.label2}" maxlength="50"/></td>
														<td>
															<table>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text slabOne"  value="%{#rateCardVersionDetail.slab1}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text slabTwo"   value="%{#rateCardVersionDetail.slab2}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text slabThree"   value="%{#rateCardVersionDetail.slab3}" maxlength="50"/></td>
																</tr>
															</table>
														</td>
														<td>
															<table>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12"  cssClass="control-label light-text slabTwo"   value="%{#rateCardVersionDetail.pulse1}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12"  cssClass="control-label light-text pulseTwo"    value="%{#rateCardVersionDetail.pulse2}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12"  cssClass="control-label light-text rateTwo"    value="%{#rateCardVersionDetail.pulse3}" maxlength="50"/></td>
																</tr>
															</table>
														</td>
														<td>
															<table>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text slabThree"   value="%{#rateCardVersionDetail.rate1}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text pulseThree"   value="%{#rateCardVersionDetail.rate2}" maxlength="50"/></td>
																</tr>
																<tr>
																	<td style="border: 0px !important;"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text rateThree"  value="%{#rateCardVersionDetail.rate3}" maxlength="50"/></td>
																</tr>
															</table>
														</td>
														<td class="col-xs-12 col-sm-2"><s:label elementCssClass="col-xs-12" cssClass="control-label light-text rateCardTierRateType" list="@com.elitecore.corenetvertex.constants.TierRateType@values()"  value="%{#rateCardVersionDetail.tierRateType}" id="rateCardTierRateType" tabindex="19" /></td>
											            <td class="col-xs-12 col-sm-2"><s:label elementCssClass="col-xs-12" parentTheme="bootstrap" changeMonth="true" changeYear="true"  cssClass="control-label light-text availabilityStartDate"  value="%{#rateCardVersionDetail.fromDate}" tabindex="20"/></td>
													</tr>
													</s:iterator>
												</tbody>
											</table>
										</div>
	                        		</td>
	                        	</tr>
	                        	</s:iterator>
	                        </table>
										</div>
									</td>
								</tr>
							</tbody>
                        </table>
                    </div>
                </div>
            	 <div class="row" >
                    	<div class="col-xs-12 back-to-list" align="center">
                        	<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${rncPackageData.id}'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.rncPackage"/></button>
                    	</div>
                </div>
        </div>
    </div>
</div>

<script>
  var availabilityStartDateDates = $(".availabilityStartDate");
  for(var index = 0 ; index < availabilityStartDateDates.length ; index++){
	  var availabilityStartDateDom = availabilityStartDateDates[index];
	  var availabilityStartDateDomText = availabilityStartDateDom.textContent;
	  availabilityStartDateDom.textContent = availabilityStartDateDomText.split(" ")[0];
  }
</script>


