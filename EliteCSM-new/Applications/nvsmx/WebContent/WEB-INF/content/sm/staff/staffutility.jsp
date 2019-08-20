<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/third-party/cropper.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/third-party/cropper.js" ></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/staff/staffprofilepicture.css" />
<style>
    .removePadding{
        display: inline;
    }
    .wrap-word{
        word-break: break-all;
        width: 20%;
    }
</style>

<script type="text/javascript">
    function loadCropper() {
        $('.img-container > img').cropper({
            aspectRatio: 1 / 1,
            autoCropArea: 0.8,
            preview: '.img-preview',
            guides: true,
            highlight: true,
            dragCrop: true,
            movable: true,
            resizable: true
        });
    }

    var fileSizeExceeded = false;
    var isImage = true;

    function clearImage(element) {
        element.value = null;
    }

    var loadImage = function (event) {
        var URL = window.URL || window.webkitURL,
                blobURL,
                input = event.target;

        var reader = new FileReader();
        reader.onload = function () {
            if (URL) {
                openCropPopup();
                blobURL = URL.createObjectURL(input.files[0]);
                var output = document.getElementById('picture');
                $('.img-container > img').one('built.cropper', function () {
                    URL.revokeObjectURL(blobURL);
                }).cropper('reset', true).cropper('replace', blobURL);
                $('.img-container > img').val('');
            }

        };
        var fileType = input.files[0].type;
        var fileName = input.files[0].name;
        if (isNullOrEmpty(fileType) == false) {
            if (fileType.indexOf("image") > -1) {
                reader.readAsDataURL(input.files[0]);
                isImage = true;
            } else {
                $('#warning').text('<s:text name="staff.profilepicture.warning"/>');
                isImage = false;
                $('#profilePictureFile').val('');
            }
        } else {
            if (isNullOrEmpty(fileName) == false) {
                $('#warning').text('<s:text name="staff.profilepicture.warning"/>');
                isImage = false;
                $('#profilePictureFile').val('');
            }
        }
    };


    function getCroppedData() {
        var croppedData = $('.img-container > img').cropper('getDataURL', 'image/jpeg', 0.8);
        var croppedImage = document.getElementById('croppedImage');
        croppedImage.src = croppedData;
        var profilePicture = document.getElementById('profilePicture');
        profilePicture.value = croppedData;

//checking file Size
        fileSizeExceeded = false;
        var fileSize = croppedData.length;
        if (fileSize > (2 * 1024 * 1024)) {
            fileSizeExceeded = true;
            $('#warning').text('<s:text name="staff.profilepicture.filesize"/>');
            return;
        }
        $('#warning').text('');
    }

    function clearData() {
        $('#croppedImage').val('');
        $('#profilePictureFile').val('');
    }

    function openCropPopup() {
        $("#container").modal('show');
    }


   var i = '<s:property value="staffGroupRoleRelDataList.size"/>';
   function addRoleAccessGroup() {
        i = parseInt(i);
        $("#roleaccessgrouptable tbody").append("<tr>" + $("#tempRoleGrouptable").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#roleaccessgrouptable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'staffGroupRoleRelDataList[' + i + '].roleData.id');
        $("#roleaccessgrouptable").find("tr:last td:nth-child(2)").find("select").attr(NAME, 'staffGroupRoleRelDataList[' + i + '].groupData.id');
        i++;
    }

    function isPasswordsAreSame(){
        var authenticationMode = $("#authenticationMode").val();
        if('<s:property value="@com.elitecore.corenetvertex.sm.acl.AuthenticationMode@LDAP.name()" />' != authenticationMode) {
            var password = $("#staffPassword").val();
            var confirmPassword = $("#confirmNewPassword").val();
            if (isNullOrEmpty(password)) {
                setError('staffPassword','<s:text name="password.required"/>');
                return false;
            }

            if (isNullOrEmpty(confirmPassword)) {
                setError('confirmNewPassword','<s:text name="confirm.password.required"/>');
                return false;

            }

            if(password != confirmPassword){
                setError('confirmNewPassword','<s:text name="password.are.not.same"/>');
                return false;
            }
        }

        return true;
    }

    function isValidStaffGroupRoleRelationMapping(){
        var staffGroupRoleRelationMappings = $("#roleaccessgrouptable tbody tr").length;
        if (staffGroupRoleRelationMappings == 0) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text('<s:text name="role.group.required"/>');
            return false;
        }

        var duplicateFlag = false;

        $("#roleaccessgrouptable tbody .group").each(function () {
            if (isUniqueGroup($(this).val()) == false) {
                $("#generalError").addClass("bg-danger");
                $("#generalError").text('<s:text name="group.duplicate"/>');
                duplicateFlag = true;
                return false;
            }
        });

        if (duplicateFlag) {
            return false;
        }

    }

    function validateGroup(currentElement) {
        clearAllErrors();
        if (isUniqueGroup($(currentElement).val()) == false) {
            setErrorOnElement($(currentElement), '<s:text name="group.validation"/>');
        }
    }

    function isUniqueGroup(value) {
        var count = 0;
        $("#roleaccessgrouptable tbody .group").each(function () {
            if ($(this).val() == value) {
                count++;
            }
        });
        if (count > 1) {
            return false;
        }
        return true;
    }

    function clearAllErrors() {
        $("#generalError").text("");
        $("#generalError").removeAttr("class");
        clearErrorMessages();
    }

</script>



<table id="tempRoleGrouptable" style="display: none;">
    <tr>
        <td><s:select list="roleDataList" listValue="name" listKey="id" cssClass="form-control"
                      elementCssClass="col-xs-12"></s:select></td>
        <td><s:select list="groupDataList" listValue="name" listKey="id" cssClass="form-control group" onblur="validateGroup(this)"
                      elementCssClass="col-xs-12"></s:select></td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span>
                </a>
        </span>
        </td>
    </tr>
</table>