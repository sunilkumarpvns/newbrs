<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Order ${model.id}</title>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet">
    <%--<link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">--%>
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="page-header">
                <h1>Order ${model.id}</h1>
            </div>
            <table class="table table-striped">
                <tr>
                    <td class="span3">ID</td>
                    <td class="span9"><s:property value="model.id"/></td>
                </tr>
                <tr>
                    <td class="span3">Client</td>
                    <td class="span9"><s:property value="model.clientName"/></td>
                </tr>
                <tr>
                    <td class="span3">Amount</td>
                    <td class="span9"><s:property value="model.amount"/></td>
                </tr>
            </table>
	        <a href="${pageContext.request.contextPath}/sm/order/order" class="btn btn-info">
		        <i class="icon icon-arrow-left"></i> Back to Orders
	        </a>
        </div><!--/row-->
    </div><!--/span-->
</div><!--/row-->
</body>
</html>
	