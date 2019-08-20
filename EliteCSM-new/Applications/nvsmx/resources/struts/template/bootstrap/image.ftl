<style>

#refresh-btn{
	float:left;
	vertical-align: middle;
	cursor: pointer;
	padding-top: 8%;
	padding-left: 4%;
	font-size : 15px;
	width: 25%;
	
}
#captchaImage{
	float:left;
	width: 75%;
	position:relative;
}
#captcha-response{
	clear:both; 
	padding-top:15px;
	padding-bottom:15px;
}
#jCaptchaResponse{
	padding-right:42.5px;
}
</style>

<div>
<#if parameters.label?exists>
<#lt/>${parameters.label?html}<#rt/>
<#else>
<#lt/>Enter Captcha code<#rt/>
</#if>
</div>

<div class="form-group">
<div class="controls" >
<img src="<@s.url action='jcaptcha_image' />"<#rt/>
<#if parameters.width?exists>
 width="${parameters.width?html}"<#rt/>
</#if>
<#if parameters.height?exists>
 height="${parameters.height?html}"<#rt/>
</#if>
id="captchaImage"<#rt/>
/>
<span class="glyphicon glyphicon-refresh" id="refresh-btn" onclick="refreshCaptcha();" ></span>
</div>
</div>
<br>
<div id="captcha-response">
<@s.textfield id="jCaptchaResponse" name="jCaptchaResponse" cssClass="form-control"/>
</div>

<script type="text/javascript">
$(document).ready(function() {

 $.ajaxSetup({
      cache: false
    });
    
  var timestamp = (new Date()).getTime();


    $("#refresh-btn").click(function() {

        var timestamp = (new Date()).getTime();
        var newSrc = $("#captchaImage").attr("src").split("?");
        newSrc = newSrc[0] + "?" + timestamp;
        $("#captchaImage").attr("src", newSrc);
        $("#captchaImage").slideDown("fast");

     });   
    
});
	
</script>




