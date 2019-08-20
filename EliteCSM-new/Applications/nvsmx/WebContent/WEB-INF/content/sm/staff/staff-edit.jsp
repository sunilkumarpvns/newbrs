<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="staffutility.jsp"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="staff.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/staff" enctype="multipart/form-data" action="staff" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm();" >
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-xs-6">
                        <s:textfield name="name" key="staff.name" 	id="staffName"		cssClass="form-control focusElement" tabindex="1" maxLength="60" />
                        <s:textfield name="userName" key="staff.username" id="staffUserName"
                                     cssClass="form-control" maxlength="18" tabindex="2" />
                         <s:select list="#{'ACTIVE':'Active','INACTIVE':'InActive'}" name="status" key="staff.status" id="staffStatus" tabindex="3"/>
                        <s:textfield name="authenticationMode" key="staff.authentication.mode" id="authenticationMode" cssClass="form-control" tabindex="4" readonly="true"/>

                        <s:textfield name="emailAddress" key="staff.email" id="staffEmailAddress"
                                        cssClass="form-control" maxlength="50"
                                        tabindex="5"/>
                        <s:textfield name="phone" key="staff.phone.no" id="staffPhoneNo"
                                     cssClass="form-control" maxlength="20"
                                     tabindex="6"/>
                        <s:textfield name="mobile" key="staff.mobile.no" id="staffMobileNo"
                                     cssClass="form-control" maxlength="20"
                                     tabindex="7"/>


                </div>
                <div class="col-xs-5 col-sm-4 col-lg-3">
                    <div class="col-xs-12">
                        <s:url action="../../commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
                            <s:param name="staffId"><s:property value="%{profilePictureId}"/></s:param>
                        </s:url>
                        <img height="150" width="150" src='<s:property value="#profilePic"/>' id="croppedImage"  />
                        <br>
                        <br>
                        <input type="hidden" id="profilePicture" name="profilePicture"/>
                        <s:hidden name="profilePictureId"/>
                        <s:file theme="xhtml" accept="image/*" name="profilePictureFile" onchange="loadImage(event)" onclick="clearImage(this);" tabindex="8"/>
                        <div id="warning" style="color:red;"></div>
                    </div>
                </div>
            </div>
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="staff.assign.role"/>
                    </legend>
                    <div class="row">
                        <div id="roleaccessgroup">
                            <div class="col-xs-12">
                                <table id='roleaccessgrouptable'  class="table table-blue table-bordered">
                                    <caption class="caption-header"><s:text name="staff.add.role.access.group"/>
                                        <div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addRoleAccessGroup('${id}');"
                                                  id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
                                        </div>
                                    </caption>
                                    <thead>
                                    <tr>
                                        <th><s:text name="staff.role"/></th>
                                        <th><s:text name="staff.group"/></th>
                                        <th style="width:35px;">&nbsp;</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <s:iterator value="staffGroupRoleRelDataList" var="staffGroupRoleRel" status="stat" >
                                        <tr>
                                            <td><s:select value="%{#staffGroupRoleRel.roleData.id}"
                                                          list="roleDataList"
                                                          name="staffGroupRoleRelDataList[%{#stat.index}].roleData.id"
                                                          listValue="name" cssClass="form-control" listKey="id"
                                                          elementCssClass="col-xs-12"/>
                                            </td>
                                            <td><s:select value="%{#staffGroupRoleRel.groupData.id}"
                                                          list="groupDataList"
                                                          name="staffGroupRoleRelDataList[%{#stat.index}].groupData.id"
                                                          listValue="name" cssClass="form-control group" listKey="id"
                                                          onblur="validateGroup(this)"
                                                          elementCssClass="col-xs-12"/>
                                            </td>
                                            <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span
                                                    class='delete glyphicon glyphicon-trash' title='delete'></span></a></span>
                                            </td>
                                        </tr>
                                    </s:iterator>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12" id="generalError"></div>
                </fieldset>

            </div>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/staff/staff/${id}" tabindex="8"  ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" tabindex="9" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/staff/staff/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back" /></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<div class="modal fade" id="container" style="display: none;" role="dialog" aria-labelledby="cropLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="cropLabel">Crop </h4>
            </div>
            <div class="modal-body">
                <div class="image-box">
                    <div class="img-container">
                        <img src="${pageContext.request.contextPath}/images/defaultProfilePicture.jpg" alt="Picture" id="picture">
                        <br>
                    </div>
                </div>
                <div class="preview-box">
                    <div class="docs-preview clearfix">
                        <div class="img-preview preview-md"></div>
                        <div class="img-preview preview-md-round"></div>
                        <div class="img-preview preview-xs-round"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="clearData();"><s:text name="staff.cancel"></s:text> </button>
                <button type="button" class="btn btn-primary" name="crop" value="Crop" type="button" data-dismiss="modal" onclick="getCroppedData()">Crop</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function(){
        loadCropper();
    });
    function validateForm() {
        var isValidUserName =   verifyUniquenessOnSubmit('staffUserName','update','<s:text name="id"/>','com.elitecore.corenetvertex.sm.acl.StaffData','','userName');

        return  isValidUserName && isValidStaffGroupRoleRelationMapping();
    }

</script>