/* This function will initialize file uploader and Render file uploader*/
function getReadyFiler(){
	
	 $('#groovyFilePlugin').filer({
         limit: null,
         maxSize: null,
         extensions: null,
         changeInput: '<div class="jFiler-input-dragDrop"><div class="jFiler-input-inner"><div class="jFiler-input-icon"><i class="icon-jfi-cloud-up-o"></i></div><div class="jFiler-input-text"><h3>Drag & Drop files here</h3> <span style="display:inline-block; margin: 15px 0">or</span></div><a class="jFiler-input-choose-btn blue">Browse Files</a></div></div>',
         showThumbs: true,
         appendTo: null,
         theme: "dragdropbox",
         templates: {
             box: '<table class="jFiler-item-list box groovy-table-css" cellspacing="0" cellpadding="0" border="0" width="80%"><tr><th align="left" class="tbl-header-bold" width="60%">Groovy File Name</th><th align="center" class="tbl-header-bold" width="20%">Last Modified Time</th><th align="center" class="tbl-header-bold" width="10%">Size</th><th align="center" class="tbl-header-bold" width="10%">Remove</th></tr></table>',
             item: '<tr class="jFiler-item">\
                        <td  class="labeltext tblfirstcol groovyName"> <span class="groovy-label-text" onClick="viewGroovyFile1(this);">{{fi-name}}</span></td>\
            	 		<td  class="labeltext tblrows" align="center"> {{fi-lastModifiedDate}}</td>\
                        <td  class="labeltext tblrows" align="center"> {{fi-size2}}</td>\
                        <td  class="labeltext tblrows" align="center"><span class="delete remove-proxy-server jFiler-item-trash-action" onclick="deleteMe(this);"></span>&nbsp;</td>\
                    </tr>',
             itemAppend: '<tr class="jFiler-item">\
                 			<td class="labeltext tblfirstcol groovyName"> <span class="groovy-label-text" onClick="viewGroovyFile1(this);">{{fi-name}}</span></td>\
            	 			<td  class="labeltext tblrows" align="center"> {{fi-lastModifiedDate}} </td>\
                			<td class="labeltext tblrows" align="center"> {{fi-size2}} </td>\
                			<td  class="labeltext tblrows" align="center"><span class="delete remove-proxy-server jFiler-item-trash-action" onclick="deleteMe(this);"></span>&nbsp;</td>\
                		 </tr>',
             progressBar: '<div class="bar"></div>',
             itemAppendToEnd: true,
             removeConfirmation: false,
             _selectors: {
                 list: '.jFiler-item-list',
                 item: '.jFiler-item',
                 progressBar: '.bar',
                 remove: '.jFiler-item-trash-action',
             }
         },
         uploadFile: {
             url: "",
             data: {'isGroovyFile':'true'},
             type: 'POST',
             enctype: 'multipart/form-data',
             beforeSend: function(){},
             success: function(data, el){
                 var parent = el.find(".jFiler-jProgressBar").parent();
                 el.find(".jFiler-jProgressBar").fadeOut("slow", function(){
                     $("<div class=\"jFiler-item-others text-success\"><i class=\"icon-jfi-check-circle\"></i> Success</div>").hide().appendTo(parent).fadeIn("slow");    
                 });
             },
             error: function(el){
                 var parent = el.find(".jFiler-jProgressBar").parent();
                 el.find(".jFiler-jProgressBar").fadeOut("slow", function(){
                     $("<div class=\"jFiler-item-others text-error\"><i class=\"icon-jfi-minus-circle\"></i> Error</div>").hide().appendTo(parent).fadeIn("slow");    
                 });
             },
             statusCode: {},
             onProgress: function(){},
         },
         dragDrop: {
             dragEnter: function(){},
             dragLeave: function(){},
             drop: function(){onchnage();},
         },
         addMore: true,
         clipBoardPaste: true,
         excludeName: null,
         beforeShow: function(){return true},
         onSelect: function(){},
         afterShow: function(){},
         onRemove: function(){},
         onEmpty: function(){},
         captions: {
             button: "Choose Files",
             feedback: "Choose files To Upload",
             feedback2: "files were chosen",
             drop: "Drop file here to Upload",
             removeConfirmation: "Are you sure you want to remove this file?",
             errors: {
                 filesLimit: "Only {{fi-limit}} files are allowed to be uploaded.",
                 filesType: "Only Images are allowed to be uploaded.",
                 filesSize: "{{fi-name}} is too large! Please upload file up to {{fi-maxSize}} MB.",
                 filesSizeAll: "Files you've choosed are too large! Please upload files up to {{fi-maxSize}} MB."
             }
         }
     });
}

var title="";
function setTitle(titleVal){
	title=titleVal;
	var statusText = document.getElementById('headerTitle');
	$(statusText).empty();
	$(statusText).text(titleVal);
	
	 if(titleVal.length > 22){
		 $(statusText).css({lineHeight:'15px'});
	 }else{
		 $(statusText).css({lineHeight:'28px'});
	 } 
}


/*This function is used for validating groovy files for Radius/Diameter Groovy Plugin */
function validateGroovyPlugin(){
	$("form").find(".groovyFile").each(function(e,v){
		$(this).attr("name","groovyFile["+e+"]");
	});
	$('#groovyDatas').val(JSON.stringify(fileArray));
	document.forms['radiusGroovyPlugin'].submit();
}
	
function viewGroovyFile(obj){
	var fileName = $(obj).text();
	var fileId = $(obj).parent().parent().attr('data-jfiler-index');
	var file = document.getElementById('groovyFilePlugin').files[fileId];
	
	if (file) {
	        // create reader
	        var reader = new FileReader();
	        reader.readAsText(file);
	        reader.onload = function(e) {
	            // browser completed reading file - display it
	        	$('#groovyFileDialog').find('#texteditor').text(e.target.result);
	        	$('#groovyFileDialog').attr('title', fileName);
	        	$('#groovyFileDialog').dialog({width: 700,height:500});
	        };
	    
	 }
}

function onchnage(){
	
	var files = document.getElementById("groovyFilePlugin").files;
	for (var i = 0; i < files.length; i++){
	     
         var groovyFileReader = new  FileReader();
	        
         groovyFileReader.onload = (function(theFile){
	            var fileName = theFile.name;
	            var lastModified=(new Date(theFile.lastModifiedDate)).getTime();
	            
	            return function(e){
	            	var isFileExist = findAndReplace(fileArray,'fileName',fileName,e.target.result,lastModified);
	            	if( !isFileExist ){
	            		fileArray.push({'fileName':fileName,'fileContent':e.target.result,'lastModified':lastModified});
	            	}
		        };
	      })(files[i]); 
	        
         groovyFileReader.readAsText(files[i]);
	}
}

function viewGroovyFile1(obj){
	var fileName = $(obj).text();
	$('#groovyFileDialog').attr('title', fileName);
	$.each(fileArray, function(key,value) {
		 if(value.fileName == fileName){
			  var fileData = value.fileContent;
			  
			  var f = new File([fileData], fileName);
		        f.fileName=value.name;
		        f.lastModified=value.lastModified;
		        
		        var reader1 = new  FileReader();
		        reader1.readAsText(f);
		        reader1.onload = function(e1) {
		        	$('#groovyFileDialog').find('#texteditor').text(e1.target.result);
		        	var dialogTitle = value.fileName;
		        	$('.ui-dialog-title').text(dialogTitle);
		        	$('#groovyFileDialog').dialog({width: 700,height:500});
		        };
		 }
	});
}

function deleteMe(spanObject){
	var groovyFileName = $(spanObject).parent().parent().find('.groovy-label-text').html();
	var removeId = $(spanObject).parent().parent().attr('data-jfiler-index');
	
	findAndRemove(fileArray, 'fileName', groovyFileName);
	
	$('#groovyFilePlugin').trigger("filer.remove", {id:removeId});
	
	$(spanObject).parent().parent().remove();
}

function findAndRemove(array, property, value) {
	   $.each(array, function(index, result) {
	      if(result[property] == value) {
	          //Remove from array
	          array.splice(index, 1);
	      }    
	   });
	}

function findAndReplace(array, property, value,fileContent,lastModified){
	var isContentReplace = false; 
	$.each(array, function(index, result) {
	      if(result[property] == value) {
	          //Remove from array
	          array[index].fileContent =fileContent;
	          array[index].lastModified=lastModified;
	          isContentReplace = true;
	        }    
	   });
	
	return isContentReplace;
}
