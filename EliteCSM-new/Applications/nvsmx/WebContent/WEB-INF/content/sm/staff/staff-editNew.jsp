<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@include file="staffutility.jsp"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="staff.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/sm/staff" action="staff" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm();">
            <s:token/>
            <div class="row">
                <div class="col-xs-7">
                    <s:textfield name="name" key="staff.name" id="staffName"
                                 cssClass="form-control focusElement" tabindex="1" maxlength="60"/>

                    <s:textfield name="userName" key="staff.username" id="staffUserName"
                                 cssClass="form-control" maxlength="18" tabindex="2"/>
                    <s:select list="@com.elitecore.corenetvertex.sm.acl.AuthenticationMode@values()" name="authenticationMode" key="staff.authentication.mode" id="authenticationMode"
                              cssClass="form-control" tabindex="3"/>
                    <s:password name="password" key="staff.password" id="staffPassword"
                                 cssClass="form-control" maxlength="50"
                                 tabindex="4"/>
                    <s:password name="confirmNewPassword" key="staff.confirm.password" id="confirmNewPassword"
                                cssClass="form-control" maxlength="50"
                                tabindex="5"/>
                    <s:textfield name="emailAddress" key="staff.email" id="staffEmailAddress"
                                 cssClass="form-control" maxlength="50"
                                 tabindex="6"/>
                    <s:textfield name="phone" key="staff.phone.no" id="staffPhoneNo"
                                 cssClass="form-control" maxlength="20"
                                 tabindex="7"/>
                    <s:textfield name="mobile" key="staff.mobile.no" id="staffMobileNo"
                                 cssClass="form-control" maxlength="20"
                                 tabindex="8"/>
                </div>
                <div class="col-xs-5">
                    <div class="col-xs-12">
                        <img height="150" width="150" src='${pageContext.request.contextPath}/images/defaultProfilePicture.jpg' id="croppedImage"  />
                        <br>
                        <br>
                        <input type="hidden" id="profilePicture" name="profilePicture"/>

                        <s:file theme="xhtml" id="profilePictureFile" accept="image/*" onchange="loadImage(event)" onclick="clearImage(this);" tabindex="9"/>
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
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addRoleAccessGroup();"
                                                  id="addRow"> <span class="glyphicon glyphicon-plus" tabindex="10"></span>
											</span>
                                          </div>
                                      </caption>
                                      <thead>
                                      <tr>
                                          <th><s:text name="staff.role"/></th>
                                          <th><s:text name="staff.group"/></th>
                                          <th style="width: 35px;">&nbsp;</th>
                                      </tr>
                                      </thead>
                                      <tbody></tbody>
                                  </table>
                              </div>
                          </div>
                      </div>
                      <div class="col-xs-12" id="generalError"></div>
                  </fieldset>
              </div>


            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="12" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" class="btn btn-primary btn-sm" tabindex="13"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/staff/staff'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
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
    $(document).ready(function () {
        loadCropper();
        addRequired(document.getElementById("staffPassword"));
        addRequired(document.getElementById("confirmNewPassword"));
    });

    function validateForm() {
        var isValidUserName = verifyUniquenessOnSubmit('staffUserName', 'create', '', 'com.elitecore.corenetvertex.sm.acl.StaffData', '', 'userName');
        return isValidUserName && isValidUserName && isPasswordsAreSame() && isValidStaffGroupRoleRelationMapping();
    }


</script>
