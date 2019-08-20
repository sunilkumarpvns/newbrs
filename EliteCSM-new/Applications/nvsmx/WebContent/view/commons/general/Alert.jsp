<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<head>
<script type="text/javascript">
  var contextPath = '<%=request.getContextPath()%>';
  $(function () {
    $(".focusElement").focus();
    $('[data-toggle="tooltip"]').tooltip();
    scriptForSuccessAndFailurePopUp();
    var previousPopUpHeight;
    $(".alert").each(function(){
      if(isNullOrEmpty(previousPopUpHeight) == false){
        $(this).css("top",previousPopUpHeight + 80 + "px");
      }
      previousPopUpHeight = $(this).height();
    });

  });
  /*
   Notification popup script
   prepend close button in popup
   fade css for success popup
   added fix log for failure popup
   */
  function scriptForSuccessAndFailurePopUp(){
    $("#actionMessages").fadeTo(3000, 500).slideUp(500, function(){
      $("#actionMessages").alert('close');
    });
    var button = "<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"
    $(".popup").prepend(button);
    $("#warning-restricted").children("button").attr( "onclick", "removeSessionAttribute()");


  }
  function removeSessionAttribute() {
    $.ajax({
      url : "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/removeSesionAttribute"
    });
  }

</script>
  <style type="text/css">

    .popup {
      position: absolute;
      z-index: 1100;
      right:1%;
      top: 40px;
      font-size: 14px;
      max-width: 750px;
      display: inline-block;
    }
    @media(min-width : 768px){
      .popup{
        min-width: 35%;
      }
    }
    @media(min-width : 992px){
      .popup{
        min-width: 25%;
      }
    }
    .warning-popup{
      color: red;
    }
  </style>
</head>

<s:if test="#session.unauthorizedUser != null ">
  <div class="alert alert-danger popup" id="warning-restricted" ><s:property value="#session.unauthorizedUser"/> </div>
</s:if>
<s:if test="hasActionErrors()">
  <s:actionerror cssClass="popup" id="actionErrors" escape="false" />
</s:if>
<s:elseif test="hasActionMessages()">
  <s:actionmessage cssClass="popup" id="actionMessages"/>
</s:elseif>
<div class="popup" style="display: none"></div>

