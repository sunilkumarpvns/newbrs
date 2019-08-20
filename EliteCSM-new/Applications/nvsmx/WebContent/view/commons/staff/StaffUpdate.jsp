<%@taglib uri="/struts-tags/ec" prefix="s"%>
<link href="${pageContext.request.contextPath}/css/third-party/cropper.css" rel="stylesheet"></link>
<script src="${pageContext.request.contextPath}/js/third-party/cropper.js"></script>
<style>

.img-container,
.img-preview {
  background-color: #f7f7f7;
  overflow: hidden;
  width: 100%;
  text-align: center;
}

.img-container {
  margin-bottom: 20px;
}

.img-container > img {
  max-width: 100%;
}

.preview-box{
	width: 100%;
	border: 2px solid #D9E6F6;
	float: left;
	padding-top: 2%;
}
.docs-preview {
  margin-bottom: 10px;
}

.img-preview {
  float: left;
  margin-bottom: 10px;
}

.img-preview > img {
  max-width: 100%;
}

.preview-md {
  float: left;
  width: 100px;
  height: 100px;
  margin-left: 10px;
  margin-right: 10px;
}

.preview-md-round{
  position: relative;
  float: left;
  height: 90px;
  margin-right: 10px;
  border-radius: 50%;
}

.preview-xs-round {
  float: left;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
}

</style>


<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="staff.update" />
		</h3>
	</div>

	<div class="panel-body">
		<div class="row">
		
		<s:form action="commons/staff/Staff/update" id="updateStaffForm" validate="true" cssClass="form-horizontal" labelCssClass="col-sm-4" elementCssClass="col-sm-8" validator="validateForm()">
			<fieldset class="fieldSet-line">
					<legend>
						<s:text name="staff.personal"/>
					</legend>
					<div class="row" id="personalDetailContent">
					<div class="col-xs-5 col-sm-4 col-lg-3">
						<s:url action="commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
              				<s:param name="staffId">${loggedInStaffProfilePictureId}</s:param>
              			</s:url>
              			<div class="col-xs-12">
              				<img height="150" width="150" src='<s:property value="#profilePic"/>' id="croppedImage"  />
              				<br>
              				<br>
              				 <input type="hidden" id="profilePicture" name="profilePicture"/> 
              				<s:file theme="xhtml" accept="image/*" onchange="loadImage(event)" onclick="clearImage(this);" tabindex="1"/>
              				<div id="warning" style="color:red;"></div>
              			</div>
					</div>
					<div class="col-xs-7 col-sm-8 col-lg-7">
						<s:textfield key="staff.name" name="staff.name" id="name" maxlength="60" cssClass="form-control" tabindex="2"/>
						<s:textfield key="staff.email" name="staff.emailAddress" id="emailAddress" maxlength="50" cssClass="form-control" tabindex="3"/>
						<s:textfield key="staff.phone" name="staff.phone" id="phone" maxlength="20" onkeypress="return isNaturalInteger(event);" cssClass="form-control" tabindex="4"/>
						<s:textfield key="staff.mobile" name="staff.mobile" id="mobile" maxlength="20" onkeypress="return isNaturalInteger(event);" cssClass="form-control" tabindex="5"/>
					</div>
						
					</div>
					
			</fieldset>
			
			<div class="col-xs-12" align="center">
			<s:submit cssClass="btn btn-primary" tabindex="6" value="Update" type="button"></s:submit>
			<button type="button" id="btnCancel" class="btn btn-primary" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/commons/staff/Staff/view'">Back</button>
			
			</div>
		</s:form>	
		
				
		</div>
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

function loadCropper(){	
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

function clearImage(element){
   element.value=null;
}

var loadImage = function(event) {
	var URL = window.URL || window.webkitURL,
	blobURL,
	input = event.target;
	    
	var reader = new FileReader();
	reader.onload = function(){
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
	 if(isNullOrEmpty(fileType) == false){
		 if(fileType.indexOf("image") > -1){
			 reader.readAsDataURL(input.files[0]);
			 isImage=true;
		 }else{
			 $('#warning').text('<s:text name="staff.profilepicture.warning"/>');
			 isImage = false;
			 $('#profilePictureFile').val('');
		 }
	 }else{
		 if(isNullOrEmpty(fileName) == false){
			 $('#warning').text('<s:text name="staff.profilepicture.warning"/>');
			 isImage = false;
			 $('#profilePictureFile').val('');
		 }
	 }
	};
	

function getCroppedData(){
	var croppedData = $('.img-container > img').cropper('getDataURL', 'image/jpeg', 0.8);
	var croppedImage =  document.getElementById('croppedImage');
	croppedImage.src = croppedData; 
	var profilePicture =  document.getElementById('profilePicture');
	profilePicture.value = croppedData;
	
	//checking file Size
	fileSizeExceeded = false;
	var fileSize = croppedData.length;
	if(fileSize > (2*1024*1024)){
	    	fileSizeExceeded = true;
	 		$('#warning').text('<s:text name="staff.profilepicture.filesize"/>');
		    return ;
	}
	$('#warning').text('');
}

function clearData(){
	$('#croppedImage').val('');
	$('#profilePictureFile').val('');
}
	
function openCropPopup(){
	$("#container").modal('show');
}
 
function validateForm(){
	if(fileSizeExceeded == true){
		return false;
	}else if(isImage == false){
		return false;
	}
} 
</script>