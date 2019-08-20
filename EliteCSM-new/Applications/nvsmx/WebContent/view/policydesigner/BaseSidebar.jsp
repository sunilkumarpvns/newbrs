<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML> 
<html lang="en">
<head>
	<title><tiles:insertAttribute name="title" ignore="true"/></title>	
	<sj:head compressed="true"/>
	<jsp:include page="../commons/general/Includes.jsp"/>
	
<style type="text/css">
#sidebarPanelToggler {
	position: fixed;
	z-index : 1000; 
	top: 33px;
	bottom: 0;
	background-color: white;
	border: none; 
}

#sidebarPanelToggler.sidebarToggler:after {
	font-family: 'Glyphicons Halflings'; /* essential for enabling glyphicon */
	content: "\e079";
}

#sidebarPanelToggler.sidebarToggler.collapsed:after {
	content: "\e080";
}

#sidebar{
	float: left;
	display: table;
	height: 100%;
	position: absolute;
}
</style>
</head>
<body class="html-body">
	<div class="container-fluid">
		<div class="row">
			<div class="col-xs-12">
				<tiles:insertAttribute name="header" />
			</div>
			<div id="sidebar" style="float: left;display: table;height: 100%;position: absolute;">
				<div id="sidebarContent" class="sidebarContent">
					<tiles:insertAttribute name="leftSide" />
				</div>
				<button class="btn btn-default sidebarToggler visible-xs"
					data-toggle="collapse" data-parent="sidebar"
					data-target="#sidebarContent" id="sidebarPanelToggler">
				</button>
			</div>
			<div class="content">
				<tiles:insertAttribute name="rightSide" />
			</div>
			<div class="col-xs-12">
				<tiles:insertAttribute name="footer" />
			</div>
		</div>
	</div>
</body>
</html>