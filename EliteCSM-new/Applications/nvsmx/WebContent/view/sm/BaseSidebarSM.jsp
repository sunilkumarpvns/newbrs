<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML>
<html lang="en">
<head>
  <title><tiles:insertAttribute name="title" ignore="true"/></title>
  <sj:head compressed="true"/>
  <jsp:include page="../commons/general/Includes.jsp"/>

  <style type="text/css">
    #sidebar{
      float: left;
      display: table;
      width: 100%;
      position: absolute;
    }
    @media(max-width:767px){
        .content{
            margin-left: 0px;
        }
    }

  </style>
</head>
<body class="html-body">
<div class="container-fluid">
  <div class="row">
    <div class="col-xs-12">
      <tiles:insertAttribute name="header" />
    </div>
    <div id="sidebar">
      <div id="sidebarContent" class="sidebarContent">
        <tiles:insertAttribute name="leftSide" />
      </div>
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