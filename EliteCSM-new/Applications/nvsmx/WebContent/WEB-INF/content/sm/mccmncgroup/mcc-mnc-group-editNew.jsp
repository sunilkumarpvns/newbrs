<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<style>
    #Add_MCC_MNC_CODE {
        margin-bottom: 10px;
    }

    #Network-MCC-MNC-Model {
        height: 450px;
        overflow-x: overlay;
    }
</style>
<script>
    $(document).ready(function () {
        $("#networkMccMncListingTableBody").html("");
    });

    function validate() {
        return verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData', '', '');
    }

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="mccmncgroup.add"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/mccmncgroup" id="mccmncgroupCreateForm" action="mcc-mnc-group" method="post"
                cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                validator="validate()" elementCssClass="col-xs-12 col-sm-8 col-lg-9">

            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <s:textfield name="name" key="mccmncgroup.groupname" cssClass="form-control" id="name" type="text"
                                 maxlength="100" tabindex="1"/>
                    <s:textarea name="description" key="mccmncgroup.description" cssClass="form-control"
                                id="mccmncgroupDescription" type="text" maxlength="200" tabindex="2" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"/>
                    <s:select id="selectBrandName" name="brandData.id" onChange="selectBrand();" key="mccmncgroup.brand"
                              cssClass="form-control select2" list="%{brandList}" listKey="id" listValue="name"
                              cssStyle="width:100%" tabindex="3"/>
                </div>
            </div>
            <!-- <button id="Add_MCC_MNC_CODE" class="btn btn-sm btn-primary" data-toggle="modal" data-target="#addNetworkMCCMNC" onClick="addingMCCMNCCode();" type="button" role="button" tabindex="5">Add MCC MNC Code</button> -->
            <table name="networkDatas" cellpadding="0" cellspacing="0" border="0" id="networkMccMncListingTable"
                   class="table table-blue">
                <caption class="caption-header"><s:text name="mccmncgroup.fieldMapping"/>
                    <div align="right" class="display-btn">
                        <span class="btn btn-group btn-group-xs defaultBtn" data-toggle="modal"
                              data-target="#addNetworkMCCMNC" onClick="addingMCCMNCCode();" id="addRow"> <span
                                class="glyphicon glyphicon-plus"> </span></span>
                    </div>
                </caption>
                <thead id="networkMccMncListingTableHead">
                <tr>
                    <!-- <th>No.</th>
                    <th></th> -->
                    <th><s:text name="mccmncgroup.operator"/></th>
                    <th><s:text name="mccmncgroup.network"/></th>
                    <th><s:text name="mccmncgroup.mcc"/></th>
                    <th><s:text name="mccmncgroup.mnc"/></th>
                    <th><s:text name="mccmncgroup.remove"/></th>
                </tr>
                </thead>
                <tbody id="networkMccMncListingTableBody">

                </tbody>
            </table>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="4"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="5"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<div class="modal fade" id="addNetworkMCCMNC" tabindex="-1" role="dialog" aria-labelledby="addNetworkMCCMNC"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="clearDialog();">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="addSubscriptionLabel"><s:text name="mccmncgroup.addingCode"/></h4>
            </div>
            <div class="modal-body" id="Network-MCC-MNC-Model">

            </div>
            <div class="col-xs-12">
                <div class="col-xs-12 generalError"></div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" onClick="addNetworkMccMncData();" name="Add" value="Add" type="button">
                    <s:text name="button.add"/>
                </button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearDialog();">Close
                </button>
            </div>
        </div>
    </div>
</div>
<%@include file="mcc-mnc-group-utility.jsp"%>