<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
	<title>NetVertex - Login</title>
	<jsp:include page="../general/Includes.jsp"/>

<style type="text/css">
	#logindiv{
		top: 20px;
	}

@media ( min-width : 768px) {
	#logindiv{
		top: 70px;
	}
}
#systeminfodiv{
	position: fixed!important;
	top: 120px;
	right:0px;
	z-index: 2000;
}

#systeminfodiv a{
	display: table;
	height: 45px;
	width: 35px;
	left:-35px;
	float:left;
	font-size: 18px;
	background: rgba(0, 0, 0, 0.75);
	text-align: center;
	color: #e5e5e5;
	outline: 0 !important;
	-webkit-border-bottom-left-radius: 5px;
	-moz-border-radius-bottomleft: 5px;
	border-bottom-left-radius: 5px;
	-webkit-border-top-left-radius: 5px;
	-moz-border-radius-topleft: 5px;
	border-top-left-radius: 5px;
	text-decoration: none;
}
#systeminfodiv a:hover{
	color: #fff;
	text-shadow: rgba(255, 255, 255, 0.6) 0 0 5px;
}
#systeminfodiv a span{
	display: table-cell;
	vertical-align: middle;
	float: none;
}
#systeminfo{
	float:right;
	background: rgba(0, 0, 0, 0.75); 
	padding: 10px;
	-webkit-border-bottom-left-radius: 5px;
	-moz-border-radius-bottomleft: 5px;
	border-bottom-left-radius: 5px;
}
#systeminfodiv .collapsing{
 	height:inherit !important;
 	-webkit-transition: height 0s ease;
       -o-transition: height 0s ease;
          transition: height 0s ease;
 	width: 0px;
 	-webkit-transition: width .35s ease;
       -o-transition: width .35s ease;
          transition: width .35s ease;
}
#systeminfodiv .list-group-item{
 	background: none;
 	color: white;
}
#login{
	background-color: #4679BD;
}
 
</style>
</head>
<body class="html-body" id="login">
	
		<div class="container-fluid col-xs-12 col-sm-10 col-sm-offset-1">
		<div class="col-xs-12 col-sm-10 col-md-8 col-lg-6 col-xs-offset-0 col-sm-offset-1 col-md-offset-2 col-lg-offset-3" id="logindiv"> 
			<div class="row">
				<div class="col-xs-12 header">
					<h1>
						<img src="${pageContext.request.contextPath}/images/logo.png" height="50" />&nbsp;NetVertex</h1>
				</div>
			</div>
			<div class="row">
				<tiles:insertAttribute name="body"/>
			</div>
		</div>
		<div class="col-xs-12">
			 <tiles:insertAttribute name="footer" />
		</div>
	</div>
</body>
</html>