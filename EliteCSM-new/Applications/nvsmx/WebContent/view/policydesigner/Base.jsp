<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<title><tiles:insertAttribute name="title" ignore="true"/></title>	
	<jsp:include page="../commons/general/Includes.jsp"/>
</head>

<body class="html-body">

<div class="container-fluid">
	<div class="row">
		<div class="col-xs-12 ">
			 <tiles:insertAttribute name="header"/> 
		</div>
		<div class="col-xs-12" style="min-width: 550px">
			   <tiles:insertAttribute name="body" />		
		</div>
		<div class="col-xs-12">
			 <tiles:insertAttribute name="footer" />
		</div>
	</div>
</div>

</body>
</html>