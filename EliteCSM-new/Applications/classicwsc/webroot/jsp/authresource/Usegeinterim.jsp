<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    response.setDateHeader ("Expires", 0);
    response.setHeader ("Pragma", "no-cache");
    if (request.getProtocol().equals ("HTTP/1.1")) {
       response.setHeader ("Cache-Control", "no-cache");
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>

<title>Dynamic Drive DHTML Scripts- Ajax Pagination script</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/ajaxpagination.css" />

<script src="<%=request.getContextPath()%>/js/ajaxpagination.js" type="text/javascript">

/***********************************************
* Ajax Pagination script- (c) Dynamic Drive DHTML code library (www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
***********************************************/

</script>
</head>

<body>
<div id="paginate-top"> </div>
<div id="bookcontent"> </div>
<div id="paginate-bottom"> </div>

<script type="text/javascript">

var usage={
pages: [<%=request.getAttribute("pagestr")%>],
selectedpage: 0 //set page shown by default (0=1st page)
}

var usageinstance=new ajaxpageclass.createBook(usage, "bookcontent", ["paginate-top", "paginate-bottom"])

</script>
</body>

</html>