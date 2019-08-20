
package com.elitecore.elitecodegen.controller.actions;

import java.io.File;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.EliteCodeGenConstant;
import com.elitecore.elitecodegen.base.StrutsBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.ApplicationResourceCodeGen;
import com.elitecore.elitecodegen.genrator.BLManagerGen;
import com.elitecore.elitecodegen.genrator.DataClassGen;
import com.elitecore.elitecodegen.genrator.HDataManagerCodeGen;
import com.elitecore.elitecodegen.genrator.HibernateCfgXmlGen;
import com.elitecore.elitecodegen.genrator.HibernateHbmXmlGen;
import com.elitecore.elitecodegen.genrator.IDataClassGenrator;
import com.elitecore.elitecodegen.genrator.InterfaceDataManagerGen;
import com.elitecore.elitecodegen.genrator.StrutsActionCalssGen;
import com.elitecore.elitecodegen.genrator.StrutsConfigGen;
import com.elitecore.elitecodegen.genrator.StrutsFormClassGen;
import com.elitecore.elitecodegen.genrator.UtilClassGen;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.jsp.CreateJspCodeGen;

/**
 * MyEclipse Struts Creation date: 07-26-2007 XDoclet definition:
 * 
 * @struts.action path="/packageGenrationAcion" name="packageGenration" input="/jsp/packageDetail.jsp" scope="request" validate="true"
 */
public class PackageGenrationAction extends Action {
    
    /*
     * Generated Methods
     */

    /**
     * Method execute
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    
    private static Logger log = Logger.getLogger(PackageGenrationAction.class);
    
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) {
        
        PackageGenrationAction.log.info("Package genration Action called");
        
        try {
            
            PackageGenarationForm packageForm = (PackageGenarationForm) form;
            TableDataBean tableDataBean = (TableDataBean) request.getSession().getAttribute(EliteCodeGenConstant.TableDataBean);
            Random r = new Random();
            String workingDir = getServlet().getServletContext().getRealPath(File.separator + "out" + File.separator + "data" + File.separator + r.nextInt(Integer.MAX_VALUE));
            request.getSession().setAttribute(EliteCodeGenConstant.WorkingDir, workingDir);            
            tableDataBean.setStrTableName(packageForm.getStrTableName());
            StrutsBean strutBean = new StrutsBean(tableDataBean, packageForm, workingDir);
            PackageGenrationAction.log.info("Table Name" + packageForm.getStrTableName());
            PackageGenrationAction.log.info("Modual Name" + packageForm.getStrModualName());
            PackageGenrationAction.log.info("SubModual Name" + packageForm.getStrSubMoudalName());
            PackageGenrationAction.log.info("Working Dir" + workingDir);
            
            BaseCodeGen baseCodeGen;
            if (packageForm.getChkHibernet() == 1) {
                
                baseCodeGen = new HDataManagerCodeGen(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                
                baseCodeGen.genrateFile();
                PackageGenrationAction.log.info("Hibernet seleted");
                
                baseCodeGen = new HibernateHbmXmlGen(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                baseCodeGen.genrateFile();
                System.out.println("Instance Of : "+baseCodeGen.getClass());
                System.out.println("File Ext : "+   baseCodeGen.getFileExt());
                
                baseCodeGen = new HibernateCfgXmlGen((HibernateHbmXmlGen) baseCodeGen, workingDir);
                baseCodeGen.genrateFile();
                
            }
            if (packageForm.getChkBlManager() == 1) {
                PackageGenrationAction.log.info("Blmanager selected");
                
                baseCodeGen = new BLManagerGen(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                baseCodeGen.genrateFile();
                
            }
            
            if (packageForm.getChkConfig() == 1)
                PackageGenrationAction.log.info("Config selected");
            if (packageForm.getChkDataManager() == 1) {
                PackageGenrationAction.log.info("Data Manager Selected");
                
                baseCodeGen = new DataClassGen(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                baseCodeGen.genrateFile();
                
                baseCodeGen = new IDataClassGenrator(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                baseCodeGen.genrateFile();
                
                baseCodeGen = new InterfaceDataManagerGen(tableDataBean, packageForm.getStrApplicationName(), packageForm.getStrSubMoudalName(), packageForm.getStrModualName(), workingDir);
                baseCodeGen.genrateFile();
                
            }
            if (packageForm.getChkStruts() == 1) {
                
                Iterator itactionList = strutBean.getLstAction().iterator();
                
                while (itactionList.hasNext()) {
                    
                    ActionBean actionBean = (ActionBean) itactionList.next();
                    
                    baseCodeGen = new StrutsFormClassGen(tableDataBean, packageForm, actionBean, workingDir);
                    baseCodeGen.genrateFile();
                    
                    baseCodeGen = new StrutsActionCalssGen(tableDataBean, packageForm, actionBean, workingDir);
                    baseCodeGen.genrateFile();
                }
                
                baseCodeGen = new StrutsConfigGen(strutBean, packageForm, workingDir);
                baseCodeGen.genrateFile();
                
                baseCodeGen = new ApplicationResourceCodeGen(strutBean, tableDataBean, packageForm, workingDir);
                baseCodeGen.genrateFile();
                
                
                PackageGenrationAction.log.info("Search jsp Selected");
                baseCodeGen = new CreateJspCodeGen(tableDataBean, strutBean, packageForm, workingDir);
                baseCodeGen.genrateFile();
                
            }
            log.info("Select" + packageForm.getJspRadiol());
             
            if (packageForm.getJspRadiol() == 1)
            {
                PackageGenrationAction.log.info("Search jsp Selected");
                baseCodeGen = new CreateJspCodeGen(tableDataBean, strutBean, packageForm, workingDir);
                baseCodeGen.genrateFile();
            } else if (packageForm.getJspRadiol() == 2) {
                PackageGenrationAction.log.info("List jsp Selected");
                baseCodeGen = new CreateJspCodeGen(tableDataBean, strutBean, packageForm, workingDir);
                baseCodeGen.genrateFile();
            }
            PackageGenrationAction.log.info("Util Classgoing to Genrate");
            baseCodeGen = new UtilClassGen(tableDataBean,packageForm, workingDir);
            baseCodeGen.genrateFile();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        log.info("Fowrding to createZip");
        return mapping.findForward("createZip");
    }
}