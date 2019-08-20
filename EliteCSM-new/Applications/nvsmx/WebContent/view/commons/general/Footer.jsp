<%@taglib uri="/struts-tags/ec" prefix="s" %>
 <div class="col-xs-12 navbar-fixed-bottom footer" >
         <span style="float: left;">
           <s:text name="sterlite.product.name"/>&nbsp;v-<s:property value="%{@com.elitecore.nvsmx.Version@getVersion()}"/>&nbsp;<s:property value="%{@com.elitecore.corenetvertex.constants.CommonConstants@HASH}"/><s:property value="%{@com.elitecore.nvsmx.Version@getSVNRevision()}"/>
         </span>
          <s:text name="copyright"/>&nbsp;&copy;&nbsp;<s:text name="year"/>
	      <a href="http://www.elitecore.com" target="_blank" style="text-decoration: none; color: inherit;"><s:text name="etpl"/></a>.
 </div>

