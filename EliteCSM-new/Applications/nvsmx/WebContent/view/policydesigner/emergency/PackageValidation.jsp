<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript">


	function checkForPackageMode(){
		var pkgMode =$("#packageMode").val();
		if(pkgMode=="LIVE2"){
			$("#pkgName").attr("disabled","true");
			$("#pkgDescription").attr("disabled","true");
			$("#groupNames").attr("disabled","true");
			$("#pkgQuotaProfileType").attr("disabled","true");
			$("#pkgType").attr("disabled","true");
			$("#exclusiveAddOn").attr("disabled","true");
			$("#pkgUpdate").attr("action","updateStatus");
		}
	}
</script>