<#--
/*Author : Raj Kirpalsinh
 *This template is used to create column's heading  for the table created by the datatable java script framework 
 */
-->

<th id="${parameters.beanProperty?default("")?html}" style="${parameters.style?default("")?html}" tdCssClass="${parameters.tdCssClass?default("")?html}" tdStyle="${parameters.tdStyle?default("")?html}" class="${parameters.cssClass?default("")?html}" width="${parameters.width?default("")?html}" hrefurl="${parameters.hrefurl?default("")?html}" icon="${parameters.icon?default("")?html}" sortable="${parameters.sortable?default("")?html}" hiddenElement="${parameters.hiddenElement?default("")?html}" type="${parameters.type?default("date")?html}" format="${parameters.format?default("")?html}"
	disableWhen="${parameters.disableWhen?default("")?html}" renderFunction="${parameters.renderFunction?default("")?html}" >
		${parameters.title?default("")}
</th>