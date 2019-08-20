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
	
function selectedAllOptions(){
	selObj = document.forms[0].selected;
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

var fileSizeExceeded = false;

function clearImage(element){
   element.value=null;
}


var loadImage = function(event) {
	$('#warning').css('display', 'none');
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
	 if(isNull(fileType) == false){
		 if(fileType.indexOf("image") > -1){
			 reader.readAsDataURL(input.files[0]);
		 }else{
			 $('#warning').css('display', 'block');
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
	    }
	
}	
	
function openCropPopup(){	
	$.fx.speeds._default = 1000;
	document.getElementById("container").style.display = "block";
	$( "#container" ).dialog({
		modal: true,
		autoOpen: false,		
		minHeight:400,
		minWidth:400,
		position:["top",100],
		hide: {effect: "fadeOut", duration: 400},
		buttons: {
			'Crop': function() {
				getCroppedData();
				$(this).dialog('close');
			},
			'Cancel': function() {
				$('#croppedImage').val('');
				$('#profilePictureFile').val('');
				$(this).dialog('close');
			}
		},
		close: function() {
			$('#profilePictureFile').val('');
    		$("#profilePictureFile").focus();
    	}
	});
	$( "#container" ).dialog("open");	
}

