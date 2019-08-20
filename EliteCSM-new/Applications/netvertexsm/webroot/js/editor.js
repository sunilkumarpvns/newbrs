
var isHTMLMode=false;

function button_over(eButton)	{
	eButton.style.backgroundColor = "#B5BDD6";
	eButton.style.borderColor = "darkblue darkblue darkblue darkblue";
	eButton.style.borderWidth = '1px';
	eButton.style.borderStyle = 'solid'; 
}

function button_out(eButton) {
	eButton.style.backgroundColor = "#D9E6F6";
	eButton.style.borderColor = "#D9E6F6";
}

function button_down(eButton) {
	eButton.style.backgroundColor = "#8494B5";
	eButton.style.borderColor = "darkblue darkblue darkblue darkblue";
}

function button_up(eButton) {
	eButton.style.backgroundColor = "#B5BDD6";
	eButton.style.borderColor = "darkblue darkblue darkblue darkblue";
	eButton = null; 
}


function cmdExec(cmd,opt) {
  	if (isHTMLMode) {
		alert("Please uncheck 'Edit HTML'");
		return;
	}  	
  	idContent.document.execCommand(cmd,false,null);
	idContent.focus();
}

function setMode(bMode) {
	var sTmp;
  	isHTMLMode = bMode;
  	if (isHTMLMode) {
		sTmp=idContent.document.body.innerHTML;
		idContent.document.body.innerText=sTmp;
		toolbar.style.display = 'none';
	} else {
		sTmp=idContent.document.body.innerText;
		idContent.document.body.innerHTML=sTmp;
		toolbar.style.display = 'inline';
	}
  	idContent.focus();
}

function createLink() {
	if (isHTMLMode) {
		alert("Please uncheck 'Edit HTML'");
		return;
	}
	cmdExec("CreateLink");
}

function insertImageLink() {
	if (isHTMLMode) {
		alert("Please uncheck 'Edit HTML'");
		return;
	}
	var sImgSrc=prompt("Insert Image File (You can use your local image file) : ", "http://www.elitecore.com/images/logo.jpg");
	if(sImgSrc!=null) cmdExec("InsertImage",sImgSrc);
}

function insertImageLocal() {
	if (isHTMLMode) {
		alert("Please uncheck 'Edit HTML'");
		return;
	}
	var sImgSrc = showModalDialog("selectImage.asp","","dialogHeight: 500px; dialogWidth: 400px; dialogTop: px; dialogLeft: px; edge: Raised; center: Yes; help: No; resizable: Yes; status: No;");
	if(sImgSrc!=null) cmdExec("InsertImage",sImgSrc);
}

function uploadImages() {
	var newWindow;
	var props = 'scrollBars=yes,resizable=yes,toolbar=no,menubar=no,location=no,directories=no,width=500,height=500,top=100,left=200';
	newWindow = window.open('upload.asp', 'Upload_Images_to_server', props);
}

function SubmitContent() {
	if (isHTMLMode) {
		alert("Please uncheck 'Edit HTML'");
		return (false);
	}
	document.editor.YOUR_CONTENT.value = idContent.document.body.innerHTML;
	document.editor.submit();
}

function foreColor(basePath)	{
	var jlinkPath=basePath+"/html/selcolor.htm";
	var arr = showModalDialog(jlinkPath,"","font-family:Verdana; font-size:12; dialogWidth:45em; dialogHeight:24em" );
	if (arr != null) cmdExec("ForeColor",arr);	
}

function tableDialog()
{
   //----- Creates A Table Dialog And Passes Values To createTable() -----
   var rtNumRows = null;
   var rtNumCols = null;
   var rtTblAlign = null;
   var rtTblWidth = null;
   showModalDialog("table.htm",window,"status:false;dialogWidth:16em;dialogHeight:13em");
}
function createTable()
{
   //----- Creates User Defined Tables -----
   var cursor = idContent.document.selection.createRange();
   if (rtNumRows == "" || rtNumRows == "0")
   {
      rtNumRows = "1";
   }
   if (rtNumCols == "" || rtNumCols == "0")
   {
      rtNumCols = "1";
   }
   var rttrnum=1
   var rttdnum=1
   var rtNewTable = "<table border='1' align='" + rtTblAlign + "' cellpadding='0' cellspacing='0' width='" + rtTblWidth + "'>"
   while (rttrnum <= rtNumRows)
   {
      rttrnum=rttrnum+1
      rtNewTable = rtNewTable + "<tr>"
      while (rttdnum <= rtNumCols)
      {
         rtNewTable = rtNewTable + "<td>&nbsp;</td>"
         rttdnum=rttdnum+1
      }
      rttdnum=1
      rtNewTable = rtNewTable + "</tr>"
   }
   rtNewTable = rtNewTable + "</table>"
   idContent.focus();
   cursor.pasteHTML(rtNewTable);
}

function doPreview(){
     temp = idContent.document.body.innerHTML;
     preWindow= open('', 'previewWindow', 'width=500,height=440,status=yes,scrollbars=yes,resizable=yes,toolbar=no,menubar=yes');
     preWindow.document.open();
     preWindow.document.write(temp);
     preWindow.document.close();
}

function SetParagraph(name,value) {
	idContent.focus();
	if (value == '<body>')
	{
		idContent.document.execCommand('formatBlock','','Normal');
		idContent.document.execCommand('removeFormat');
		return;
	}
	idContent.document.execCommand('formatblock','',value);
}

function insertTag(value)
{
	idContent.focus();
	iframeElm  = document.getElementById('idContent');
	tagObject = iframeElm.contentWindow.document.selection.createRange();
	tagObject.pasteHTML(value);
}