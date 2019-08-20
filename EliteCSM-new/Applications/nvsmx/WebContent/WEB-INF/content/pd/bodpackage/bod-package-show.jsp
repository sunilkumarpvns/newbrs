<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <div class="btn-group btn-group-xs">
                <button class="btn btn-primary" style="display:none"  	id="designMode">Design</button>
                <button class="btn btn-primary" style="display:none"  	id="testMode" 		onclick="changePkgMode('Design to Test','testMode');" >Test it</button>
                <button class="btn btn-primary" style="display:none"  	id="liveMode" 		onclick="changePkgMode('Test to Live','liveMode');">Go Live</button>
                <button class="btn btn-primary" style="display:none"  	id="live2Mode" 		onclick="changePkgMode('Live to Live2','live2Mode');" >Go Live2</button>
            </div>
            &nbsp; <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="<s:text name="audit.history"/>"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=<s:property value="name"/>&refererUrl=/pd/bodpackage/bod-package/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="<s:text name="tooltip.edit"/>"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodpackage/bod-package/${id}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
            <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() || packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/bodpackage/bod-package/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" disabled="disabled" data-toggle="tooltip" data-placement="bottom" title="<s:text name="tooltip.delete"/>" >
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:if>
            <s:else>
             <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/bodpackage/bod-package/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="<s:text name="tooltip.delete"/>" >
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:else>
        </div>
    </div>
    <div class="panel-body">
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="basic.detail"/>
                </legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="bod.package.description" value="%{description}"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4"
                                     elementCssClass="col-sm-7 col-xs-8"/>
                            <s:label key="bod.package.status" value="%{status}" cssClass="control-label light-text"
                                     labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            <s:label key="bod.package.groups" value="%{groupNames}" cssClass="control-label light-text"
                                     labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            <s:if test="%{validityPeriod != null && validityPeriod != 0}">
                                <s:label key="bod.package.validity.period" value="%{validityPeriod}  %{validityPeriodUnit}"
                                         cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="bod.package.validity.period"
                                         cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>
                            </s:else>
                            <s:set var="priceTag">
                                <s:property value="getText('bod.package.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                            </s:set>
                            <s:label key="priceTag" value="%{price}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:if test="%{availabilityStartDate != null}">
                                <s:set var="availStartDate">
                                    <s:date name="%{availabilityStartDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                                </s:set>
                                <s:label key="bod.package.availability.start.date" value="%{availStartDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="bod.package.availability.start.date" 	value="%{availabilityStartDate}" cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:else>

                            <s:if test="%{availabilityEndDate != null}">
                                <s:set var="availEndDate">
                                    <s:date name="%{availabilityEndDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                                </s:set>
                                <s:label key="bod.package.availability.end.date" value="%{availEndDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="bod.package.availability.end.date" 	value="%{availabilityEndDate}" 	cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:else>
                            <s:label key="Param 1" value="%{param1}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            <s:label key="Param 2" value="%{param2}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>

                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>
        <!-- applicable QoS Profiles -->
        <div class="row">
            <fieldset class="fieldSet-line" >
                <legend align="top"><s:text name="bod.package.applicableQosProfiles" ></s:text></legend>
                <div class="row">
                    <s:label key="QoS Profiles" value="%{applicableQosProfiles}" cssClass="control-label light-text word-break" labelCssClass="col-sm-3 col-xs-4 col-lg-2" elementCssClass="col-sm-9 col-xs-8 col-lg-10"  />
                </div>
                <!-- Qos multiplier -->
                <div class="row">
                    <fieldset class="fieldSet-line" >
                        <legend align="top"><s:text name="bod.package.qosmultiplier" /></legend>
                        <s:iterator value="bodQosMultiplierDatas">
                            <fieldset class="fieldSet-line" style="margin-bottom: 0px;">
                                <legend>
                                    <s:if test="%{fupLevel == 0}">
                                        <s:text name="bod.package.hsqIPCANSessionMultiplier" />
                                    </s:if>
                                    <s:elseif test="%{fupLevel == 1}">
                                        <s:text name="bod.package.fup1IPCANSessionMultiplier"></s:text>
                                    </s:elseif>
                                    <s:elseif test="%{fupLevel == 2}">
                                        <s:text name="bod.package.fup2IPCANSessionMultiplier"></s:text>
                                    </s:elseif>
                                </legend>
                                <div align="right" class="nv-btn-group">
                                    <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()
                                    && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                                        <div class="btn-group btn-group-xs" role="group">
                                            <button type="button" class="btn btn-default" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodqosmultiplier/bod-qos-multiplier/${id}/edit'">
                                                <span class="glyphicon glyphicon-pencil"></span>
                                            </button>
                                        </div>
                                        <s:if test="fupLevel != 0 && fupLevel == bodQosMultiplierDatas.size()-1 ">
                                            <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/bodqosmultiplier/bod-qos-multiplier/${id}?_method=DELETE&bodPackageId=${bodData.id}">
                                                <button type="button" class="btn btn-default" >
                                                    <span class="glyphicon glyphicon-trash"></span>
                                                </button>
                                            </div>
                                        </s:if>
                                    </s:if>
                                    <s:else>
                                        <div class="btn-group btn-group-xs" role="group">
                                            <button type="button" disabled="disabled" class="btn btn-default"  >
                                                <span class="glyphicon glyphicon-pencil"></span>
                                            </button>
                                        </div>
                                        <s:if test="fupLevel != 0 && fupLevel == bodQosMultiplierDatas.size()-1 ">
                                            <div class="btn-group btn-group-xs" role="group" data-toggle="confirmation-singleton"   >
                                                <button type="button" class="btn btn-default" disabled="disabled" >
                                                    <span class="glyphicon glyphicon-trash"></span>
                                                </button>
                                            </div>
                                        </s:if>
                                    </s:else>
                                </div>
                                <div class="row">
                                    <s:label key="bod.package.multiplier" value="%{multiplier}" cssClass="control-label light-text word-break" labelCssClass="col-xs-3 col-sm-3 col-lg-2" elementCssClass="col-xs-9 col-sm-9 col-lg-10" />
                                </div>
                                <table class="table table-blue table-bordered">
                                    <caption class="caption-header"><s:text name="bod.package.servicemultiplier" /></caption>
                                    <thead>
                                    <th><s:text name="bod.package.servicetype" /></th>
                                    <th><s:text name="bod.package.servicemultiplier" /></th>
                                    </thead>
                                    <tbody>
                                    <s:if test="bodServiceMultiplierDatas.size != 0">
                                        <s:iterator  value="bodServiceMultiplierDatas">
                                            <tr>
                                                <td style="width: 50%"><s:property value="serviceTypeData.name"/> </td>
                                                <td style="width: 50%"><s:property value="multiplier"/> </td>
                                            </tr>
                                        </s:iterator>
                                    </s:if>
                                    <s:else>
                                        <tr><td colspan="2"><s:text name="empty.table.message"/> </td></tr>
                                    </s:else>
                                    </tbody>
                                </table>
                            </fieldset>
                        </s:iterator>
                    </fieldset>
                    <s:if test="%{bodQosMultiplierDatas.size() == 0}">
                        <div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
                            <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                <button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodqosmultiplier/bod-qos-multiplier/new?bodPackageId=${id}&fupLevel=0'">
                                    <span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
                                    <s:text name="bod.package.hsqIPCANSessionMultiplier" />
                                </button>
                            </s:if>
                            <s:else>
                                <button type="button" disabled="disabled" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" >
                                    <span class="glyphicon glyphicon-plus-sign" style="top: 0px"></span>
                                    <s:text name="bod.package.hsqIPCANSessionMultiplier" />
                                </button>
                            </s:else>
                        </div>
                    </s:if>
                    <s:elseif test="%{bodQosMultiplierDatas.size() == 1}">
                        <div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
                            <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                <button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodqosmultiplier/bod-qos-multiplier/new?bodPackageId=${id}&fupLevel=1'">
                                    <span class="glyphicon glyphicon-plus-sign"></span>
                                    <s:text name="bod.package.fup1IPCANSessionMultiplier" />
                                </button>
                            </s:if>
                            <s:else>
                                <button type="button" disabled="disabled" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" >
                                    <span class="glyphicon glyphicon-plus-sign"></span>
                                    <s:text name="bod.package.fup1IPCANSessionMultiplier" />
                                </button>
                            </s:else>
                        </div>
                    </s:elseif>
                    <s:elseif test="%{bodQosMultiplierDatas.size() == 2}">
                        <div class="col-xs-12" style="margin-bottom: 10px; margin-left: 0px;" id="fupbutton">
                            <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                <button type="button" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodqosmultiplier/bod-qos-multiplier/new?bodPackageId=${id}&fupLevel=2'">
                                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                    <s:text name="bod.package.fup2IPCANSessionMultiplier" />
                                </button>
                            </s:if>
                            <s:else>
                                <button type="button" disabled="disabled" class="btn btn-xs btn-primary" id="btnAddFup" data-target="fup"  >
                                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                    <s:text name="bod.package.fup2IPCANSessionMultiplier" />
                                </button>
                            </s:else>
                        </div>
                    </s:elseif>
                </div>
            </fieldset>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/bodpackage/bod-package'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                        name="button.list"/></button>
            </div>
        </div>
    </div>
</div>
<script>
    var selectValuesOnCancel = null;
    $(document).ready(function () {
        setPackageMode();
        selectValuesOnCancel = $("#selectValuesOnCancel").html();
        $(".select2").select2();
    });
    function setPackageMode() {
        var pkgMode = '${packageMode}';
        console.log("pkgMode: "+pkgMode);
        $('#designMode').hide();
        $('#testMode').hide();
        $('#liveMode').hide();
        $('#live2Mode').hide();

        if (pkgMode == 'DESIGN') {
            $('#designMode').show();
            $('#designMode').html("Design");
            $('#testMode').show();
            $('#testMode').attr("class", "btn btn-default");
            $('#testMode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'TEST') {
            $('#testMode').show();
            $('#testMode').html("Test");
            $('#liveMode').show();
            $('#liveMode').attr("class", "btn btn-default");
            $('#liveMode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'LIVE') {
            $('#liveMode').show();
            $('#liveMode').html("Live");
            $('#live2Mode').show();
            $('#live2Mode').attr("class", "btn btn-default");
            $('#live2Mode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'LIVE2') {
            $('#live2Mode').show();
            $('#live2Mode').html("Live2");
        }
    }


    function changePkgMode(fromTo,refId) {
        var classVal = $("#"+refId).attr("class");
        if(classVal.toLowerCase().indexOf("primary") >=0){
            return;
        }

        if(confirm("Change package mode from "+fromTo+" ?")){
            var pkgId = '${id}';
            var pkgMode = '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@getMode(packageMode).getNextMode()" />';
            var updateURL= "${pageContext.request.contextPath}/pd/bodpackage/bod-package/"+pkgId+"/updateMode?_method=put";
            $.ajax({
                async       : true,
                type 		: "POST",
                url  		: updateURL,
                data		: {'pkgId' : pkgId , 'pkgMode' : pkgMode,'groupIds':'<s:property value="groups"/>', 'entityOldGroups' : '<s:property value="groups" />'},
                success 	: function(json){
                    document.location.href = "${pageContext.request.contextPath}/pd/bodpackage/bod-package/"+pkgId;
                },
                error: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/bodpackage/bod-package/"+pkgId;
                }
            });
        }
    }


    function clearDialog() {
        clearErrorMessages(addApplicableQosForm);
    }

    /**
     * set the selected values on click of update ApplicableQosProfiles
     */
    function setSelectValuesOnCancel(){
        //it requires object thats why array is passed in 	.
        $(".select2").select2(['destroy']);
        $("#applicableQosProfiles").html(selectValuesOnCancel);
        $(".select2").select2();
    }
</script>