/**
 * Copyright (C) Elitecore Technologies Ltd.
 * DataClassGenrationAction.java
 * Created on Jul 23, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.EliteCodeGenConstant;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.genrator.DataClassGenrator;

/**
 * @author kaushikvira
 */
public class DataClassGenrationAction extends Action {
    
    private static Logger log = Logger.getLogger(DataClassGenrationAction.class);
    
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) {
        
      
        
        try {
            HttpSession session = request.getSession();
            Map map = (HashMap) request.getSession(false).getAttribute(EliteCodeGenConstant.DataClassMap);
            DataClassGenrator baseCodeGen;
            
            String workingDir = (String) session.getAttribute(EliteCodeGenConstant.WorkingDir);
            
            DataClassGenrationAction.log.info(workingDir);
            
            for ( int i = 0; i < map.size(); i++ ) {
                DataClassGenrationAction.log.info("filgnearted : " + i);
                baseCodeGen = new DataClassGenrator((TableDataBean) (map.get(new Integer(i))), workingDir);
                File f = new File(workingDir);
                DataClassGenrationAction.log.info("Working dir" + f.getAbsolutePath());
                baseCodeGen.genrateDataFile(baseCodeGen.getPrintWriter());
            }
            
        }
        
        catch (Exception e) {
            e.printStackTrace();
            DataClassGenrationAction.log.info("exceptin" + e);
            DataClassGenrationAction.log.info("Unhandled Exception...");
        }
        DataClassGenrationAction.log.info(mapping.findForward("success"));
        return mapping.findForward("success");
        
    }
    
}