

<!-- Common CSS -->

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap-theme.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/classic.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/classic.date.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.vertical-tabs.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/spectrum.css">

<style type="text/css">

.spinner {
    width: 35px;
    height: 35px;
    background-color: #333;

    border-radius: 100%;
    -webkit-animation: scaleout 1.0s infinite ease-in-out;
    animation: scaleout 1.0s infinite ease-in-out;
  }

  @-webkit-keyframes scaleout {
    0% { -webkit-transform: scale(0.0) }
    100% {
      -webkit-transform: scale(1.0);
      opacity: 0;
    }
  }

  @keyframes scaleout {
    0% {
      transform: scale(0.0);
      -webkit-transform: scale(0.0);
    } 100% {
      transform: scale(1.0);
      -webkit-transform: scale(1.0);
      opacity: 0;
    }
  }

  body,html {
    min-height: 1400px;
  }

  .alert .form-group {
    margin-bottom: 0px;
  }

  .red {
    border: 1px solid red;
    background: #fee;
  }

  .ace_editor { font-size: 20px !important;}
  .form {  height: 400px;  }
  .schema {  height: 800px;  }

  .btw { color: #777; font-size: 90%; padding-left: 6px;}

  [ng\:cloak], [ng-cloak], [data-ng-cloak], [x-ng-cloak], .ng-cloak, .x-ng-cloak {
    display: none !important;
  }

  .navbar-form > .form-group > .input-group {
    margin-left: 20px;
  }

  .btn:active,
  .btn:focus,
  .btn.active {
    background-image: none;
    outline: 0;
    -webkit-box-shadow: none;
            box-shadow: none;
  }

  .error {
    color: red;
  }

  .input-group-btn:last-child>.btn:not(:last-child):not(.dropdown-toggle) {
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
  }
  
  .panel-nv-invert {
  border-color: #F0F0F0;
}
.panel-nv-invert > .panel-heading {
  color: #fff;
  background-color: #F0F0F0;
  border-color: #F0F0F0;
}
.panel-nv-invert > .panel-heading + .panel-collapse > .panel-body {
  border-top-color: #F0F0F0;
}
.panel-nv-invert > .panel-heading .badge {
  color: #428bca;
  background-color: #fff;
}
.panel-nv-invert > .panel-footer + .panel-collapse > .panel-body {
  border-bottom-color: #428bca;
}
.navbar-custom {
    color: #FFFFFF;
    background-color: #015198;
}
.body-background{
   background-color: rgb(245,245,245);
}
.url-value {
  	width:100%;
   	height:100%
}

</style>