/**
 * Copyright (C) Elitecore Technologies Ltd.
 * CreateJspCodeGen.java
 * Created on Jul 31, 2007
 * Last Modified on 
 * @author : kaushikvira
 */
package com.elitecore.elitecodegen.genrator.jsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.StrutsBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseJspCodeGen;

public class CreateJspCodeGen extends BaseJspCodeGen {
    
    private static String filePreFix  = "Create";
    private StrutsBean    strutsBean;
    private String        modualName;
    private String        keyPrefix;
    
    private static String classPreFix = "com.elitecore";
    
    public CreateJspCodeGen( TableDataBean tableBean ,
                             StrutsBean strutsBean ,
                             PackageGenarationForm packageForm ,
                             String workingDir ) {
        
        this.tableBean = tableBean;
        this.realPath = workingDir;
        this.strutsBean = strutsBean;
        this.modualName = packageForm.getStrModualName();
        this.keyPrefix = strutsBean.getBaseModualName() + "." + modualName;
        
        if (packageForm.getStrSubMoudalName().equals("")) {
            this.filePath = BaseJspCodeGen.prefixPath + File.separator + packageForm.getStrModualName().toLowerCase();
        } else {
            this.filePath = BaseJspCodeGen.prefixPath + File.separator + convertToPath(packageForm.getStrSubMoudalName().toLowerCase()) + File.separator + packageForm.getStrModualName().toLowerCase();
        }
        
        this.fileName = CreateJspCodeGen.filePreFix + initCap(packageForm.getStrModualName());
        this.log = Logger.getLogger(CreateJspCodeGen.class);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.elitecore.elitecodegen.genrator.base.BaseCodeGen#genrateFile()
     */
    @Override
    public void genrateFile() throws FileNotFoundException {
        
    	// Here in create" + initCap(this.modualName) + actionprefix must be samll because in actionBean class initsmall is used 
    	
        ActionBean actionBean = strutsBean.getActionBean("create" + initCap(this.modualName) + "Form");
        
        writer = this.getPrintWritter();
        writer.println("<%@ include file=\"/jsp/core/includes/common/Header.jsp\"%> ");
        writer.println("<%@ page import=\"java.util.*\" %>");
        writer.println("<%@ page import=\"" + actionBean.getFormClass() + "\" %> ");
        writer.println("");
        writer.println("<%@ taglib uri=\"/WEB-INF/struts-bean.tld\" prefix=\"bean\" %>");
        writer.println("<%@ taglib uri=\"/WEB-INF/struts-html.tld\" prefix=\"html\" %>");
        writer.println("<%@ taglib uri=\"/WEB-INF/struts-logic.tld\" prefix=\"logic\"%> ");
        writer.println("<%@ taglib uri=\"/WEB-INF/taglibs-input.tld\" prefix=\"elitecore\" %> ");
        writer.println("");
        writer.println("<%");
        writer.println("    String basePath = request.getContextPath(); ");
        writer.println("    " + actionBean.getFormClassName() + " " + initSmall(actionBean.getFormClassName()) + " = new" + actionBean.getFormClassName() + "();");
        writer.println("    Calendar c = Calendar.getInstance();");
        writer.println("%>");
        writer.println("");
        writer.println("<script> ");
        writer.println("function validateCreate(){ ");
        writer.println("}");
        writer.println("</script>");
        writer.println(" ");
        writer.println("<html:form action=\"" + actionBean.getPath() + "\">");
        writer.println(" ");
        writer.println(" ");
        writer.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"828\"> ");
        writer.println("                <tr> ");
        writer.println("                        <td width=\"10\">&nbsp;</td> ");
        writer.println("                        <td colspan=\"2\"> ");
        writer.println("                                <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
        writer.println("                                        <tr >");
        writer.println("                                          <td width=\"26\" rowspan=\"2\" valign=\"top\" ><img  src=\"<%=basePath%>/images/left-curve.jpg\"  border=\"0\" ></td>");
        writer.println("                                          <td width=\"133\" align=\"center\" class=\"page-header\" background=\"<%=basePath%>/images/header-gradient.jpg\" rowspan=\"2\" ><bean:message key=\""+ keyPrefix + "\"/></td> ");
        writer.println("                                          <td width=\"32\" rowspan=\"2\" ><img  src=\"<%=basePath%>/images/right-curve.jpg\"  border=\"0\" ></td>");
        writer.println("                                          <td width=\"633\" >");
        writer.println("                                                                <a  href=\"#\" ><img  src=\"<%=basePath%>/images/csv.jpg\"  name=\"Image1\"");
        writer.println("                                                                onmouseover=\"MM_swapImage('Image1','','/images/csv-hover.jpg',1)\"");
        writer.println("                                                                onmouseout=\"MM_swapImgRestore()\"  alt=\"Save as CSV\"  border=\"0\" ></a>");
        writer.println(" ");
        writer.println("                                                                <a  href=\"#\" ><img  src=\"<%=basePath%>/images/pdf.jpg\"  name=\"Image2\"");
        writer.println("                                                                 onmouseover=\"MM_swapImage('Image2','','/images/pdf-hover.jpg',1)\" ");
        writer.println("                                                                 onmouseout=\"MM_swapImgRestore()\"  alt=\"Save as PDF\"  border=\"0\" ></a> ");
        writer.println(" ");
        writer.println("                                                                 <a  href=\"#\" ><img  src=\"<%=basePath%>/images/html.jpg\"  name=\"Image3\"");
        writer.println("                                                                 onmouseover=\"MM_swapImage('Image3','','/images/html-hover.jpg',1)\"");
        writer.println("                                                                 onmouseout=\"MM_swapImgRestore()\"  alt=\"Save as HTML\"  border=\"0\" ></a>");
        writer.println(" ");
        writer.println("                                                                 <a  href=\"#\" ><img  src=\"<%=basePath%>/images/filter.jpg\"  name=\"Image4\"");
        writer.println("                                                                  onmouseover=\"MM_swapImage('Image4','','/images/filter-hover.jpg',1)\" ");
        writer.println("                                                                  onmouseout=\"MM_swapImgRestore()\"  alt=\"Apply Filter\"  border=\"0\" ></a> ");
        writer.println("                                                          </td>");
        writer.println("                                         </tr> ");
        writer.println(" ");
        writer.println("                                         <tr > ");
        writer.println("                                          <td width=\"633\" valign=\"bottom\" ><img  src=\"<%=basePath%>/images/line.jpg\"  border=\"0\" ></td>");
        writer.println("                                         </tr> ");
        writer.println("                                </table> ");
        writer.println("                        </td>");
        writer.println("                </tr>");
        writer.println("                <tr >");
        writer.println("                        <td class=\"small-gap\" width=\"7\" >&nbsp;</td> ");
        writer.println("                </tr>");
        writer.println(" ");
        writer.println(" ");
        writer.println("                <tr> ");
        writer.println("                        <td width=\"10\">&nbsp;</td> ");
        writer.println("                        <td class=\"box\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"> ");
        writer.println("                                <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
        writer.println("                                        <tr> ");
        writer.println("                                                <td class=\"table-header\" colspan=\"5\"><bean:message key=\"" + keyPrefix + ".create" + modualName.toLowerCase() + "\"/></td>");
        writer.println("                                        </tr>");
        writer.println("                                        <tr >");
        writer.println("                                                <td class=\"small-gap\" colspan=\"3\" >&nbsp;</td> ");
        writer.println("                                         </tr> ");
        writer.println("                                          <tr> ");
        writer.println("                                                <td class=\"btns-td\" valign=\"middle\" colspan=\"3\" >");
        writer.println(" ");
        writer.println("                                                        <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" height=\"30%\" >");
        writer.println(" ");
        
        Iterator<ColumnBean> itColBean = tableBean.getCloumnList().iterator();
        
        while (itColBean.hasNext()) {
            ColumnBean colBean = itColBean.next();
            
            writer.println("                                                         <tr > ");
            writer.println("                                                              <td align=\"left\" class=\"labeltext\" valign=\"top\" width=\"18%\" ><bean:message key=\"" + keyPrefix + "." + colBean.getName().toLowerCase() + "\"/></td> ");
            writer.println("                                                              <td align=\"left\" class=\"labeltext\" valign=\"top\" colspan=\"2\" >");
            writer.println("                                                                       <html:text property=\"" + colBean.getJavaName()+ "\" size=\"10\" maxlength=\"15\"/><font color=\"#FF0000\"> *</font> ");
            writer.println("                                                              </td>");
            writer.println("                                                        </tr>"); }
        writer.println(" ");
        writer.println("                                                        <tr >");
        writer.println("                                                                <td class=\"btns-td\" valign=\"middle\" >&nbsp;</td> ");
        writer.println("                                                                <td class=\"btns-td\" valign=\"middle\" colspan=\"2\" >");
        writer.println("                                                                        <input type=\"button\" name=\"c_btnCreate\"  onclick=\"validateCreate()\"  id=\"c_btnCreate2\"  value=\"Create\"  class=\"light-btn\"> ");
        writer.println("                                                                        <input type=\"reset\" name=\"c_btnDeletePolicy\" onclick=\"javascript:location.href='<%=basePath%>/~~~~~%%%%~~~~/>'\"   value=\"Cancel\" class=\"light-btn\"> ");
        writer.println("                                                                </td> ");
        writer.println("                                                        </tr> ");
        writer.println("                                                         </table> ");
        writer.println("                                                </td> ");
        writer.println("                                        </tr> ");
        writer.println("                              </table>");
        writer.println("                     </td>");
        writer.println("               <tr> ");
        writer.println("");
        writer.println("                <tr>");
        writer.println("                        <td width=\"10\">&nbsp;</td>");
        writer.println("                        <td colspan=\"2\" valign=\"top\" align=\"right\"> ");
        writer.println("                          <table width=\"99%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        writer.println("                                <tr>");
        writer.println("                                        <td width=\"82%\" valign=\"top\"><img src=\"<%=basePath%>/images/btm-line.jpg\"></td> ");
        writer.println("                                        <td align=\"right\" rowspan=\"2\" valign=\"top\"><img src=\"<%=basePath%>/images/btm-gradient.jpg\" width=\"140\" height=\"23\"></td> ");
        writer.println("                                </tr> ");
        writer.println("                                <tr>");
        writer.println("                                         <td width=\"82%\" valign=\"top\" align=\"right\" class=\"small-text-grey\">Copyright ");
        writer.println("                                        &copy; <a href=\"http://www.elitecore.com\" target=\"_blank\">Elitecore Technologies Ltd.</a> </td> ");
        writer.println("                                </tr> ");
        writer.println("                        </table>");
        writer.println("                      </td> ");
        writer.println("               </tr>");
        writer.println("</table>");
        writer.println("</html:form>");
        writer.println("<%@ include file=\"/jsp/core/includes/common/Footer.jsp\" %>  ");
        
        writer.close();
        
    }
}
