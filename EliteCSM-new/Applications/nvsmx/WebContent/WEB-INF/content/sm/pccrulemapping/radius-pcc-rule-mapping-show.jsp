<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<script src="${pageContext.request.contextPath}/js/third-party/jquery.edittreetable.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/treetable.css"/>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <%--<span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ratinggroup/RatingGroup/export?ids=${ratingGroupData.id}&${ratingGroupData.id}=${ratingGroupData.groups}'">
                    <span class="glyphicon glyphicon-export" ></span>
                </button>
            </span>--%>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/pccrulemapping/radius-pcc-rule-mapping/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping/${id}?_method=DELETE">
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
                            <s:label key="pccrule.mapping.description" value="%{description}" cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <%--<s:label key="pccrule.mapping.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />--%>
                            <s:label key="pccrule.mapping.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                    <div id="staticAttributeMapping">
                        <s:iterator value="staticAttributeMappings.mappings" status="i" var="mapping">
                            <s:if test="%{#mapping != null}">
                                <s:hidden name="staticAttributeMappings.mappings[%{#i.count - 1}]"
                                          value="%{#mapping}"/>
                            </s:if>
                        </s:iterator>
                    </div>

                    <div id="dynamicAttributeMapping">
                        <s:iterator value="dynamicAttributeMappings.mappings" status="j" var="mapping">
                            <s:if test="%{#mapping != null}">
                                <s:hidden name="dynamicAttributeMappings.mappings[%{#j.count - 1}]"
                                          value="%{#mapping}"/>
                            </s:if>
                        </s:iterator>
                    </div>
                </div>
            </fieldset>
            <div class="col-xs-12 col-sm-12">
                <div class="treetable" id="staticMappings">

                </div>
            </div>

            <div class="col-xs-12 col-sm-12">
                <div class="treetable" id="dynamicMappings">

                </div>
            </div>

        </div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" style="margin-right:10px;"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccrulemapping/radius-pcc-rule-mapping'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                </button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var staticList = new Object();
    var dynamicList = new Object();
    $(function(){
        var mappingData = [];
        var mappings = $("#staticAttributeMapping").find("input");
        for (var i = 0; i < mappings.length; i++) {
            var inputElement = mappings[i];
            if (isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                mappingData.push(jsonData);
                staticList[jsonData.id] = JSON.stringify(jsonData);
            }
        }

        $("#staticMappings").bstreetable({
                data: mappingData,
                maintitle: "Attribute",
                maxlevel: 10,
                view: true,
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@STATIC.name()" />',
                title: "Static Attribute Mappings",
                extfield: [
                    {
                        title: "Policy Key",
                        key: "policykey",
                        type: "input",
                        cssClass: "form-control input-sm policyKeySuggestions",
                        id: "policyKey-"
                    },
                    {
                        title: "Default Value",
                        key: "defaultvalue",
                        type: "input",
                        cssClass: "form-control input-sm",
                        id: "defaultValue-"
                    },
                    {
                        title: "Value Mapping",
                        key: "valuemapping",
                        type: "input",
                        cssClass: "form-control input-sm",
                        id: "valueMapping-"
                    }
                ]

            }
        );

        var dynamicMappingData = [];
        var dynamicMappings = $("#dynamicAttributeMapping").find("input");
        for(var i=0;i< dynamicMappings.length;i++){
            var inputElement = dynamicMappings[i];
            if(isNullOrEmpty($(inputElement).val()) == false) {
                var jsonData = JSON.parse($(inputElement).val());
                dynamicMappingData.push(jsonData);
                dynamicList[jsonData.id] = JSON.stringify(jsonData);
            }
        }

        $("#dynamicMappings").bstreetable({
                data : dynamicMappingData,
                maintitle:"Attribute",
                maxlevel:10,
                view:true,
                type:'<s:property value="@com.elitecore.corenetvertex.constants.PacketMappingConstants@DYNAMIC.name()" />',
                title:"Dynamic Attribute Mappings",
                extfield:[
                    {title:"Policy Key",key:"policykey",type:"input",cssClass:"form-control input-sm policyKeySuggestions",id:"policyKey-"},
                    {title:"Default Value",key:"defaultvalue",type:"input",cssClass:"form-control input-sm",id:"defaultValue-"},
                    {title:"Value Mapping",key:"valuemapping",type:"input",cssClass:"form-control input-sm",id:"valueMapping-"}
                ]

            }
        );

    });

</script>
