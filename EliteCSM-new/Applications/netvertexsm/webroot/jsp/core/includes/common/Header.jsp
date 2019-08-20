
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%  String basePath = request.getContextPath(); %>

<html>
<head>
<title>Server Manager</title>
<link rel="stylesheet" href="<%=basePath%>/jquery/development/themes/base/jquery.ui.all.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.collapsibleCheckboxTree.css" type="text/css" />
<script src="<%=basePath%>/js/jquery-1.9.1.js"></script>
<script src="<%=basePath%>/jquery/development/ui/jquery-ui-1.9.2.custom.min.js"></script>
<%--<script src="<%=basePath%>/js/jquery-migrate-1.4.1.js"></script>--%>
<%--<script src="<%=basePath%>/jquery/development/external/jquery.bgiframe-2.1.1.js"></script>--%>
<script src="<%=basePath%>/jquery/development/ui/jquery.ui.resizable.js"></script>
<script src="<%=request.getContextPath() %>/js/jquery.collapsibleCheckboxTree.js" type="text/javascript"></script>
<script src="<%=request.getContextPath() %>/js/editor.js" type="text/javascript"></script>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/popcalendar.css" >
<script language="javascript" src="<%=basePath%>/js/validation.js"></script>
<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/cookie.js"></script>
<script language="javascript" src="<%=basePath%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>
<script language="javascript" src="<%=basePath%>/js/data.js"></script>

<%@ taglib uri="/WEB-INF/config/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/tld/elite_netvertex.tld" prefix="sm"%>
<%@ taglib uri="/WEB-INF/config/tlds/displaytag.tld" prefix="display" %>
<%@ page errorPage="/jsp/core/response/JspErrorPage.jsp" %>
<style>

	textarea:focus {
    	background: #FFFACD;
	}
	
	input[type='text']:focus {
    	background: #FFFACD;
	}
	
	select:focus {
    	background: #FFFACD;
	}
	 
	.ui-state-focus{
		background-color: rgb(198, 222, 255);
		color: black;
	}
	.ui-autocomplete {
		max-height: 150px;
		overflow-y: auto;
		max-width: 300px;
		position:absolute;
		cursor:default;
		z-index:4000 !important		
	}
	html .ui-autocomplete {
		height: 150px;
		max-width: 300px;
	}
	body {
	font-size: 62.5%;
	}		
		
	.ac_results {
		padding: 0px;
		border: 1px solid black;
		background-color: white;
		overflow: hidden;
		z-index: 99999;
		height: 180px;
	}
	
	.ac_results ul {
		width: 100%;
		list-style-position: outside;
		list-style: none;
		padding: 0;
		margin: 0;
	}
	
	.ac_results li {
		margin: 0px;
		padding: 2px 5px;
		cursor: default;
		display: block;
		/* 
		if width will be 100% horizontal scrollbar will apear 
		when scroll mode will be used
		*/
		/*width: 100%;*/
		font: menu;
		font-size: 12px;
		/* 
		it is very important, if line-height not setted or setted 
		in relative units scroll will be broken in firefox
		*/
		line-height: 16px;
		overflow: hidden;
	}
	
	.ac_loading {
		background: white url('indicator.gif') right center no-repeat;
	}
	
	.ac_over {
		background-color: #C6DEFF;
		color: black;
	}
	
	.elite-suggestion {
		    position: absolute;
			z-index: 99999;
	}
	
	input:focus,select:focus,textarea:focus,option:focus,checkbox:focus
	{ 		
		border: 1px solid orange;
	}
	
.success {
    box-shadow: 0px 0px 7px #52D017;
    border-color: #52D017;
}

.failure {
    box-shadow: 0px 0px 7px #FF2400;
    border-color: #FF2400;
}
	
</style>

<script language="javascript">
function setTitle(titleVal){
	$('#headerTitle').text(titleVal);
}
$(document).ready(function(){
	$("#name").focus();
});

function isAmount(value){
	if(value=="" || $.trim(value).length==0){
		return false;	
	}
	
	var patt = new RegExp(/^\d*\.?\d+$/);			
	var flag = patt.test(value);
	return !flag;
}
function validateMe(shouldBeNull, name, value, regex, minSize, maxSize){
	
		shouldBeNull = shouldBeNull.toLowerCase();
	if((shouldBeNull=="no" || shouldBeNull=="false") && (value=="" || $.trim(value).length==0)){			
		alert(name+" should not be Empty");
		return false;
	}
	
	if($.trim(value).length<parseInt(minSize) || $.trim(value).length> parseInt(maxSize)){
		alert(name+" value not in its length Range");
		return false;
	}
	
	var patt = new RegExp(regex,'g');			
	if(patt.test(value)==false){
		alert(name+" field contains Invalid value");
		return false;
	}
	
	return true;		
}

function storeDOM()
{
	pagecontent=('<pre><html>' + (document.documentElement.innerHTML) + '</html></pre>');
	
}

setContextPathForImageDir('<%=basePath%>');	

function parameterDescription(val,opt) { 		
	document.getElementById("maindiv").style.visibility = "visible";
	var dailogContent  =  document.getElementById("maindiv");
	dailogContent.innerHTML = '<p class="labeltext">'+ val+'</p>';	   
	$("#maindiv").dialog({
		modal: false,
		title:opt,
		width: 500,
		height: 250,  
		position:['top', 150],
	    open: function () {
	                    
	    }
	});
}

function retriveTableFields(dbId,tableName,txtField) {
	var dbFieldStr;
	var dbFieldArray;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		$( "#"+ txtField).autocomplete({
			source: dbFieldArray
		});
	});	
}

function setFields(field,columnArray) {	
	commonAutoComplete(field,columnArray);	
}


function retriveDictionaryAttributes(searchNameOrAttributeId,txtField) {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	$.post("SearchAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#, ");
	
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0];
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}
		
	$( "#"+ txtField).autocomplete({
			minLength: 0,
			source: myArray,
			focus: function( event, ui ) {
				$( "#"+ txtField ).val( ui.item.value );
				return false;
			},
			select: function( event, ui ) {
				$( "#"+ txtField ).val( ui.item.value );
				return false;
			}
			
		}).data( "autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "</a>" )
			.appendTo( ul );
		};	
	});
}

function ListItem(value,label) {
	this.value = value;
	this.label = label;
}

function callVerifyValidFormat(instanceType,divId) {
	var verifyFormat = verifyValidFormat(instanceType);
	
	if(verifyFormat.trim() == 'false'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Invalid Name [Name should contain A-Z, a-z, 0-9, - and _ is allowed.].</font>");
		return false;
	}else if(verifyFormat.trim()=='invalidPolicy'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Invalid Name [Name should contain a-z, A-Z,0-9,(space) _ % ` ~ ! @ $ ^ * ( ) + | : ; . , { } [ ] ]</font>");
		return false;
	}else if(verifyFormat.trim() == 'invalid'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Invalid Name [Name should contain A-Z, a-z, 0-9, (space) . : % - and _ is allowed.].</font>");
		return false;
	}else{
		$('#' + divId).html("");
		return true;
	}
	
}

function verifyInstanceName(instanceType,divId) {
			var verifyName = escape(instanceType.searchName);
			if(isNull(verifyName)){
				$('#' + divId).html('');
				return;
			}
			var result = callVerifyValidFormat(instanceType,divId);
			if(result == false){
				return false;	
			}
			var data  = $.ajax({
					type: "POST",
					url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
					async:false,
					data: instanceType,
			}).responseText;
			if(data.trim()=='true') {
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Name.");
				return true;
			}else if(data.trim()=='false'){
				$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
				return false;
			}else{
				var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
				$('#' + divId).html(temp);
			} 
}

function verifyConnectionURL(instanceType,divId) {
	var verifyURL = escape(instanceType.searchName);
	if(isNull(verifyURL)){
		$('#' + divId).html('');
		return;
	}
	var data  = $.ajax({
			type: "POST",
			url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
			async:false,
			data: instanceType,
	}).responseText;
	if(data.trim()=='true') {
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Connection URL.");
		return true;
	}else if(data.trim()=='false'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
		return false;
	}else{
		var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
		$('#' + divId).html(temp);
	} 
}
function verifyCustomizedMenuName(instanceType,divId) {
	var verifyName = escape(instanceType.searchName);
	if(isNull(verifyName)){
		$('#' + divId).html('');
		return;
	}
	var result = callVerifyValidFormat(instanceType,divId); 
	if(result == false){
		return false;
	}
	var data  = $.ajax({
			 type: "POST",
			 url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
			 async:false,
			 data:instanceType
	}).responseText;

	if(data.trim()=='true') {
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Name.");
		return true;
	}else if(data.trim()=='false'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
		return false;
	}else{
		var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
		$('#' + divId).html(temp);
	}
	
}


function verifyPriorityValue(instanceType,searchName,mode,id,divId) {
	var verifyName = escape(searchName);
	var data  = $.ajax({
	   type: "POST",
	   url: "<%=request.getContextPath()%>/VerifyInstanceNameServlet",
	   async:false,
	   data: "instanceType="+instanceType+"&searchName="+verifyName+"&mode="+mode+"&id="+id+"&isCheckForPriority=yes"
	}).responseText;

	if(verifyName == ''){
		$('#' + divId).html('');
	}else if(data.trim()=='validPriority' && verifyName != '') {
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/tick.jpg'/> Valid Prioriy.");
		return true;
	}else if(data.trim()=='priorityAlreadyExist'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists! please check priority table</font>");
		return false;
	}else if(data.trim() == 'notAllow'){
		$('#' + divId).html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Priority should be greater than 0</font>");
		return false;
	}else{
		var temp = "<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>" +data.trim()+"</font>";
		$('#' + divId).html(temp);
	}
}

</script>
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('<%=basePath%>/images/csv-hover.jpg','<%=basePath%>/images/pdf-hover.jpg','<%=basePath%>/images/html-hover.jpg','<%=basePath%>/images/filter-hover.jpg','<%=basePath%>/images/previous-hover.jpg','<%=basePath%>/images/next-hover.jpg','<%=basePath%>/images/dnarrow-y.jpg','<%=basePath%>/images/sublinks-dnarrow.jpg','<%=request.getContextPath()%>/images/sublinks-uparrow.jpg'); collapseAllRows();" onhelp="openHelpPage();return false;" onBlur="storeDOM()">
<div id="maindiv" style="display: none" title="header">					
</div>
