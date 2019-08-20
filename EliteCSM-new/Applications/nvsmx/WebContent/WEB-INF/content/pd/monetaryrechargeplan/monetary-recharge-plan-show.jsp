<%--
  Created by IntelliJ IDEA.
  User: arpit
  Date: 27/8/18
  Time: 2:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <s:set var="priceTag">
        <s:property value="getText('plan.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
    </s:set>
    <s:set var="amountTag">
        <s:property value="getText('plan.amount')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
    </s:set>
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
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=<s:property value="name"/>&refererUrl=/pd/monetaryrechargeplan/monetary-recharge-plan/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/${id}?_method=DELETE">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                        <span class="glyphicon glyphicon-trash"></span>
                    </button>
                </span>
            </s:if>
        </div>
    </div>

    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="plan.detail" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="plan.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="plan.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="plan.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="priceTag" value="%{price}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:if test="%{amount==null}"><s:label key="amountTag" id="amount" value="0" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /></s:if>
                            <s:else><s:label key="amountTag" id="amount" value="%{amount}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /></s:else>
                            <s:if test="%{validity==null}"><s:label key="plan.validity" value="0 DAY" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /></s:if>
                            <s:else><s:label key="plan.validity" value="%{validity} %{validityPeriodUnit}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" /></s:else>
                        </div>
                    </div>

                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
                    <div class="row">
                        <div class="col-xs-12 back-to-list" align="center">
                            <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan'">
                                <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            </fieldset>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            setPackageMode();
        })

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
                var updateURL= "${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/"+pkgId+"/updateMode?_method=put";
                $.ajax({
                    async       : true,
                    type 		: "POST",
                    url  		: updateURL,
                    data		: {'pkgId' : pkgId , 'pkgMode' : pkgMode,'groupIds':'<s:property value="groups"/>', 'entityOldGroups' : '<s:property value="groups" />'},
                    success 	: function(json){
                        document.location.href = "${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/"+pkgId;
                    },
                    error: function (json) {
                        document.location.href = "${pageContext.request.contextPath}/pd/monetaryrechargeplan/monetary-recharge-plan/"+pkgId;
                    }
                });
            }
        }

    </script>
