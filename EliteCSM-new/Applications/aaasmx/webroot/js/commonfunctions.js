
/* Function to go to a specified URL on Button click. Generated by Dreamweaver*/
function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}

/* Function to open a popup. Generated by Dreamweaver*/
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}
//Sorting for single selection box
function sortSingle(box)  
{
	var temp_opts = new Array();
	var temp = new Object();
	var selText="";
	var selValue="";
	var asIndex=0;
	for(var i=0; i<box.options.length; i++)  
	{
		temp_opts[i] = box.options[i];
		//if(box.options[i].defaultSelected)
		if(box.options[i].selected)
		{
		selText = box.options[i].text; 
		selValue = box.options[i].value; 		
		}
	}

	for(var x=0; x<temp_opts.length-1; x++)  
	{
		for(var y=(x+1); y<temp_opts.length; y++)  
		{
			if(temp_opts[x].text.toUpperCase() > temp_opts[y].text.toUpperCase() && temp_opts[x].value!="") 
			{
				temp = temp_opts[x].text;
				temp_opts[x].text = temp_opts[y].text;
				temp_opts[y].text = temp;
				temp = temp_opts[x].value;
				temp_opts[x].value = temp_opts[y].value;
				temp_opts[y].value = temp;
			}
	   }
	}
	eval(box).innerHTML="";
	for(var i=0; i<temp_opts.length; i++)  
	{

		box.options[i] = new Option	(temp_opts[i].text,temp_opts[i].value);
		if((box.options[i].text == selText) && (box.options[i].value == selValue))
		{
			box[i].selected=true;			
		}
	}	
}



//For Dual Box
// Automatically sort items within lists? (1 or 0)
cmbctr=0;
sortitems = 1;  // Automatically sort items within lists? (1 or 0)
function move(fbox,tbox) 
{
	for(var i=0; i<fbox.options.length; i++) 
	{

		if(fbox.options[i].selected && fbox.options[i].value != "") 
		{
			var no = new Option();
			no.value = fbox.options[i].value;
			no.text = fbox.options[i].text;
			tbox.options[tbox.options.length] = no;
			fbox[i--] = null;
 		    cmbctr=i+1;
	   }
	}
	 if(cmbctr!=fbox.options.length)
	 {
	  if(cmbctr!=fbox.options.length)
	   fbox.options[cmbctr].selected=true;
	 }
	 else if((cmbctr==fbox.options.length) && (fbox.options.length!=0))
	 {
	  fbox.options[0].selected=true;
	 }
	 if (sortitems) 
	 {
		SortDual(tbox);
		SortDual(fbox);
	 }
}

function moveAll(fbox, tbox)
{
	for(i = 0; i < fbox.options.length; i++) 
	{
		fbox.options[i].selected=true;
	}
	move(fbox, tbox);
}

function SortDual(box)  
{

	var temp_opts = new Array();
	var temp = new Object();
	for(var i=0; i<box.options.length; i++)  
	{
		temp_opts[i] = box.options[i];
	}
	for(var x=0; x<temp_opts.length-1; x++)  
	{
		for(var y=(x+1); y<temp_opts.length; y++)  
		{
			if(temp_opts[x].text.toUpperCase() > temp_opts[y].text.toUpperCase())  
			{
				temp = temp_opts[x].text;
				temp_opts[x].text = temp_opts[y].text;
				temp_opts[y].text = temp;
				temp = temp_opts[x].value;
				temp_opts[x].value = temp_opts[y].value;
				temp_opts[y].value = temp;
		  }
	   }
	}
	for(var i=0; i<box.options.length; i++)  
	{
		box.options[i].value = temp_opts[i].value;
		box.options[i].text = temp_opts[i].text;
	}
}
 /** End of functions for moving lists from and to listboxes in
  * Dual Boxes.
  */


/**Rollover**/
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
/**Rollover**/

/** DROP DOWN MENU**/

function toggleRows(elm) {
 var rows = document.getElementsByTagName("TR");
 var newDisplay = "none";
 var thisID = elm.parentNode.parentNode.parentNode.id + "-";
 var matchDirectChildrenOnly = false;
 for (var i = 0; i < rows.length; i++) {
   var r = rows[i];
   if (matchStart(r.id, thisID, matchDirectChildrenOnly)) {
    if (r.style.display == "none") {
     if (document.all) newDisplay = "block"; //IE4+ specific code
     else newDisplay = "table-row"; //Netscape and Mozilla
    }
    break;
   }
 }
 if (newDisplay != "none") {
  matchDirectChildrenOnly = true;
 }
 for (var j = 0; j < rows.length; j++) {
   var s = rows[j];
   if (matchStart(s.id, thisID, matchDirectChildrenOnly)) {
     s.style.display = newDisplay;
   }
 }
}

function matchStart(target, pattern, matchDirectChildrenOnly) {
 var pos = target.indexOf(pattern);
 if (pos != 0) return false;
 if (!matchDirectChildrenOnly) return true;
 if (target.slice(pos + pattern.length, target.length).indexOf("-") >= 0) return false;
 return true;
}

function collapseAllRows() {
 var rows = document.getElementsByTagName("TR");
 for (var j = 0; j < rows.length; j++) {
   var r = rows[j];
   if (r.id.indexOf("-") >= 0) {
     r.style.display = "none";    
   }
 }
}
/**DROP DOWN MENU**/

/**Right Click disable**/
/*var message="Function Disabled!";
///////////////////////////////////
function clickIE() {if (document.all) {alert(message);return false;}}
function clickNS(e) {if
(document.layers||(document.getElementById&&!document.all)) {
if (e.which==2||e.which==3) {alert(message);return false;}}}
if (document.layers)
{document.captureEvents(Event.MOUSEDOWN);document.onmousedown=clickNS;}
else{document.onmouseup=clickNS;document.oncontextmenu=clickIE;}
document.oncontextmenu=new Function("return false")
*/

/**Swap up and down images**/
var refer=true;
function swapImages()
{
if (refer) {
  document.arrow.src='/images/sublinks-uparrow.jpg';
  refer=false;
}
else {
  document.arrow.src='/images/sublinks-dnarrow.jpg';
  refer=true;
}

}

var refer1=true;
function swapImages1()
{
if (refer1) {
  document.arrow1.src='/images/sublinks-uparrow.jpg';
  refer1=false;
}
else {
  document.arrow1.src='/images/sublinks-dnarrow.jpg';
  refer1=true;
}

}

var refer2=true;
function swapImages2()
{
if (refer2) {
  document.arrow2.src='/images/sublinks-uparrow.jpg';
  refer2=false;
}
else {
  document.arrow2.src='/images/sublinks-dnarrow.jpg';
  refer2=true;
}
}
/**Function For Toggling of Basic and Advance**/
function showhide()
{
	var args=showhide.arguments;
	for (i=0; i<(args.length); i=i+2)	
	{
		eval(args[i]+".style.display='"+args[i+1]+"'");
	}
}

/**Function to change the ACTION_MODE used to decide what action to call,
 * prior to submitting the form. Call this function on button click, passing the new mode
 * as argument. The form_name and the action_mode is taken automatically
 */
function setActionMode(theMode) {
    var ACTION_VARIABLE = "c_strActionMode";
    var theForm = formName;
    var expression = formName + ACTION_VARIABLE + ".value=" + theMode;
    eval(expression);
	
	//Below statement is commented by Lomesh
	// If you have any problem with it, let me know.
    //eval(formName + "submit()");

} 

/*******************************************************
To use this you will have to call one function in your jsp:
setParameters(<checkbox field name>);

Example:
		javascript:
		setParameters("UniId");
		
		html:
		<!--This will toggle all checkbox fields-->
		<input type="checkbox" name="toggleAll" onclick="ToggleAll(this);">

		<!--The id of this must be passed to setParameters method-->
		<input type="checkbox" name="UniId" onclick="Toggle(this);">

*******************************************************
function CheckSelected() will return true if one/more checkbox is selected,
false otherwise.
function getCheckedValue() will return checked check box value if only one checkbox is selcted,
-1 otherwise
*******************************************************/
	function Toggle(e,tableId,i,flag)
    { 
		if(e.checked) 
			eval(formName + "toggleAll").checked = AllChecked();
		else 
			eval(formName + "toggleAll").checked = false;
		changeRowColor(tableId,i,flag);
    }

    function ToggleAll(e,tableId)
    {
		if(e.checked) 
		    CheckAll(tableId);
		else
			ClearAll(tableId);
    }

    function Check(e)
    {
		e.checked = true;
    }

    function Clear(e)
    {
		e.checked = false;
    }

    function CheckAll(tableId)
	{
		var ml = eval("document." + formNamePassed);
		var len = ml.elements.length;
		var j = 0;
		for (var i = 0; i < len; i++) 
		{
			var e = ml.elements[i];
			if(e.name == checkboxName || e.id == checkboxName) 
			{
				Check(e);
				changeRowColor(tableId,j+1,true);					
				j++;
			}

		}
		ml.toggleAll.checked = true;
	}

    function ClearAll(tableId)
	{
		var ml = eval("document." + formNamePassed);
		var len = ml.elements.length;
		var j = 0;
		for(var i = 0; i < len; i++) 
		{
			var e = ml.elements[i];
			if(e.name == checkboxName || e.id == checkboxName)
			{
				Clear(e);
				changeRowColor(tableId,j+1,false);
				j++;
			}
		}
		ml.toggleAll.checked = false;
	}
	
	function AllChecked()
	{
		ml = eval("document." + formNamePassed);
		len = ml.elements.length;
		for(var i = 0 ; i < len ; i++) 
		{
			if((ml.elements[i].name == checkboxName || ml.elements[i].id == checkboxName) && !ml.elements[i].checked) 		
				return false;
		}
		return true;
	}

	//function CheckSelected() will return true if one/more checkbox is selected,
	//false otherwise.
	function CheckSelected()
	{
		ml = eval("document." + formNamePassed);
		len = ml.elements.length;
		var retVal = false;
		for(var i = 0 ; i < len ; i++) 
		{
			if((ml.elements[i].name == checkboxName || ml.elements[i].id == checkboxName) && ml.elements[i].checked) 
			{
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	//function getCheckedValue() will return checked check box value if only one checkbox is selcted,
	//-1 otherwise
	function getCheckedValue()
	{
		ml = eval("document." + formNamePassed);
		len = ml.elements.length;
		var retValue = -1;
		var count = 0;

		for(var i = 0 ; i < len ; i++) 
		{
			if((ml.elements[i].name == checkboxName || ml.elements[i].id == checkboxName) && ml.elements[i].checked) 
			{					
				count++;
				if(count > 1)
				{
					retValue = -1;
					break;
				}
				else
					retValue = ml[i].value;			
			}
		}
		return retValue;
	}
/**
 * Function to change the color of the row when selected.Sets grey back ground color 	
 * when selected else white background.Syntax to call the function
 * <INPUT TYPE="checkbox" ........... onClick="changeRowColor(<tableId>,<rowId>,this.checked)">
 * if required to be call explictly
 */

function changeRowColor(tableId,rowId,flag)
{
	var oTable = document.getElementById(tableId);
	if(flag)
	{
		oTable.rows[rowId].className="selected-row";
	}
	else
	{
		oTable.rows[rowId].className="defualt-row";
	}
} 

function makeAllSelected(id)
{
	var obj=document.getElementById(id);
	
	var count=obj.options.length;
		
	var i=0;
	while(i<count)
	{
		obj.options[i].selected=true;
		i++;
	}

}
function onLoadEvent()
{
	
}
/**
  *This function Check That Two Strings Are Matching Or Not
  *The Basic Purpose Of This Function is To match Password
*/

function matchString(strOne,strTwo)
{
	if(strOne.length!=strTwo.length)
	{
		alert("Password Do Not Match");
		return false;
	}
	else if(strOne==strTwo)
	{
		return true;
	}
	else
	{
		alert("Password Do Not Match");
		return false;
	}
	return false;
}

/**
 *This function will select the only option from the select box in case
 *the select box is mandatory and there is only one option available.
 */
 function selectMandatorySelectBox(selectBox)
 {
	select = document.getElementById(selectBox);
	var length=select.length;	
	if(length<=2)
	{
		if(select.value==null || select.value=="")
		{
			for(i=0;i<length;i++)	
			{
					if(select[i].value!=null && select[i].value!=""  )
					{
						select.options[i].selected='true';						
					}
			}	
		}
	}	
 }

 /**
 *This function will select the only option from the dual box in case
 *the dual box is mandatory and there is only one option available.
 */
 function selectMandatoryDualBox(strIdLeft,strIdRight)
 {
	selectLeft = document.getElementById(strIdLeft);	
	selectRight = document.getElementById(strIdRight);
	var leftLength=selectLeft.length;
	var rightLength=selectRight.length;		
	if(leftLength==1 && rightLength==0)
	{
		if(selectLeft[0].value!="")
		{ 
			selectLeft[0].selected="true"; 
		}
	}					
 }

/**
	*Function for trimming white spaces from string.
	*Usage:	strVar = strVar.trim();
*/
	String.prototype.trim = function()
	{
		return this.replace(/(^\s*)|(\s*$)/g, "");
	}
	

/**
 * Function Create New Form 
 * @param id : id of Form
 * @param action : action of Form
 */
function createNewForm(id, action) {
	var newform = document.createElement('form');
	if(id != undefined){
		newform.setAttribute("id",id);
	}
	if(action != undefined){
		newform.setAttribute("action",action);
	}
	document.body.appendChild(newform);
	return newform;
}

/**
 * Function Navigate Page
 * @param action : navigation page action 
 * @param appendAttrbId : attribute append to Form
 */
function navigatePage(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
					 .submit();
	
}

/**
 * Added By Punit
 * @return true if value of @param id element is empty 
 */
function isEmptyById(id)
{
	return  isEmpty($("#"+id).val()) ;    
}

/**
 * Added By
 * @return true if @param value  is empty
 */
function isEmpty(value)
{
	return  $.trim(value).length == 0 ;    
}
 // End By Punit 

/**
 * This function converts value containing {}[]'", into corresponding HTML Entity Value
 * @param value string value that you want to convert
 * @returns Converted string value
 */
function encodeHtmlEntity(value){
	value = value.replace(/{/g , "&#123;");
	value = value.replace(/}/g , "&#125;");
	value = value.replace(/'/g , "&#39;");
	value = value.replace(/,/g , "&#44;");
	value = value.replace(/\[/g , "&#91;");
	value = value.replace(/\]/g , "&#93;");
	value = value.replace(/"/g , "&#34;");
	return value;
}

/**
 * This function converts HTML Entity into normal text
 * @param value string value that you want to convert
 * @returns Converted string value
 */
function decodeHtmlEntity(value){
	return $('<div>').html(value).text()
}


/**
 * This function will check whether given number is zero or positive number or not and function does not consider empty string, it always return true for empty string
 * @param value that have to be  cheacked whether positive or zero
 * @returns <code>true</code> if val is positve number else <code>false</code>
 */
function isPositiveNumber(value) {
	var nre= /^\d+$|^$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(value)) {
		return false;
	}
	return true;
}


/** This function will gives a list of script instance list as an autocomplete array
 *  @param Array of script instance */
function setSuggestionForScript(scriptArray, className) {
	
	 $( "."+ className ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response(scriptArray);
		},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			this.value = ui.item.label ;
			return false;
		}
	});
	
};