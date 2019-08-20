<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/12/17
  Time: 12:43 PM
  To change this template use File | Settings | File Templates.
--%>

<s:form  id="licenseForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="license"/>
        </h3>

    </div>
    <div class="panel-body" >
        <div class="row">
            <div class="container-fluid">
                <ul class="nav nav-tabs tab-headings" id="tabs">
                    <li class="active" id="tab1">
                        <a data-toggle="tab" href="#section1"><s:text name="license.detail"/> </a>
                    </li>
                    <li id="tab2">
                        <a data-toggle="tab" href="#section2"><s:text name="licence.instance"/> </a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="tab-content">
            <div id="section1" class="tab-pane fade in active">
                <s:form id="licenseInformation" method="get" enctype="multipart/form-data" cssClass="form-vertical">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-xs-4">
                                <button type="button" class="btn btn-primary btn-sm " onclick="javascript:location.href='${pageContext.request.contextPath}/sm/license/license/${id}/downloadPublicKey'">
                                    <span class="glyphicon glyphicon-download-alt"></span> <s:text name="license.public.key"/>
                                </button>
                                <button type="button" class="btn btn-primary btn-sm " data-toggle="collapse" data-target="#license" aria-expanded="false" aria-controls="license" data-placement="right" title="license" role="button" >
                                    <span class="glyphicon glyphicon-upload"></span> <s:text name="license" />
                                </button>
                            </div>
                            <div class="col-xs-8 collapse" id="license">
                                <div class="input-append form-group">
                                    <div class="col-xs-10" >
                                        <div class="input-group">
                                            <input id="uploadedFile" class="form-control" type="text">
                                            <a class="input-group-addon" onclick="$('input[id=licenseFile]').click();" style="text-decoration:none;cursor : pointer;"><s:text name="license.upload.browse" /></a>
                                        </div>
                                    </div>
                                    <div class="col-xs-2">
                                        <button type="button" class="btn btn-sm btn-primary" role="button" onclick="return uploadLicense();"><s:text name="license.upload"/> </button>
                                    </div>
                                </div>
                                <s:file id="licenseFile" name="licenseFile" type="file" cssStyle="display:none;" />
                            </div>
                        </div>
                        <br>
                        Uploaded on <s:property value="%{lastUpdatedTime}"/>
                        <nv:dataTable
                                id="LicenseDataListAsJson"
                                list="${licenseDataListAsJson}"
                                rows="50"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="License Name" beanProperty="key" width="40%" />
                            <nv:dataTableColumn title="Version" beanProperty="version" width="20%"/>
                            <nv:dataTableColumn title="Validity" beanProperty="value" width="40%"/>
                        </nv:dataTable>
                    </div>
                </s:form>
            </div>
            <div id="section2" class="tab-pane fade in">
                <s:form id="instanceInformation" method="get" namespace="/sm/license" action="license" enctype="multipart/form-data" cssClass="form-vertical">
                    <div class="panel-body">
                        <nv:dataTable
                                id="InstancesListAsJson"
                                list="${instancesListAsJson}"
                                rows="50"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="Instances" beanProperty="instanceName"  />
                            <nv:dataTableColumn title="Last Updated Time" beanProperty="lastUpdateTime"  />
                            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-list-alt'></span>" hrefurl="edit:javascript:loadLicense()"	 style="width:20px;"  />
                            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/license/license/$<id>/deregisterLicense?_method=DELETE" 	 style="width:20px;"  />
                        </nv:dataTable>
                    </div>
                </s:form>
            </div>
        </div>
    </div>
</div>

</s:form>
<script>

    function uploadLicense(){
        if($("#uploadedFile").val().length == 0){
            addWarning(".popup","At least configure file");
            return false;
        };
        document.forms["licenseForm"].action = "${id}/uploadLicense";
        document.forms["licenseForm"].submit();

    }


    $(function(){
        $('input[id=licenseFile]').change(function() {
            $('#uploadedFile').val($(this).val());
        });

    });


    $(document).ready(function(){

        $('#tabs a').click(function (e) {
            e.preventDefault();
            $(this).tab('show');
        });

        // store the currently selected tab in the hash value
        $("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
            var id = $(e.target).attr("href").substr(1);
            window.location.hash = id;
        });

        // on load of the page: switch to the currently selected tab
        var hash = window.location.hash;
        $('#tabs a[href="' + hash + '"]').tab('show');



    });

    function loadLicense() {
        $("#licenseViewDialog").modal('show');
    }
</script>
<%@include file="license-dialog-show.jsp"%>