/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ViewTableListAction.java
 * Created on Jul 20, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.EliteCodeGenConstant;
import com.elitecore.elitecodegen.controller.form.DbResourceForm;
import com.elitecore.elitecodegen.dao.ConnectionFactory;
import com.elitecore.elitecodegen.exception.EliteCodeGenException;

/**
 * @author kaushikvira
 */
public class ViewTableListAction extends Action {
    
    private static Logger log = Logger.getLogger(ViewTableListAction.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see com.elitecore.elitecodegen.controller.actions.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute( ActionMapping map ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        ViewTableListAction.log.info("ViewTableListAction.execute called");
        
        ActionErrors errors = null;
        DbResourceForm dbResource = null;
        Connection con = null;
        DatabaseMetaData dbMetadata = null;
        ResultSet rs = null;
        try {
            
            /* setting working dir */

            Random r = new Random();
            String workingDir = getServlet().getServletContext().getRealPath(File.separator + "out" + File.separator + "data" + File.separator + r.nextInt(Integer.MAX_VALUE));
            request.getSession().setAttribute(EliteCodeGenConstant.WorkingDir, workingDir);
            errors = new ActionErrors();
            dbResource = (DbResourceForm) form;
            
            ViewTableListAction.log.info("name" + EliteCodeGenConstant.DbResource);
            ViewTableListAction.log.info("dirver" + dbResource.getStrDriverName());
            
            request.getSession().setAttribute(EliteCodeGenConstant.DbResource, dbResource);
            con = ConnectionFactory.Connect(dbResource);
            dbMetadata = con.getMetaData();
            
            String types[] = { "TABLE" };
            
            rs = dbMetadata.getTables(null, dbResource.getStrSchmaName(), "%", types);
            
            List tableList = new ArrayList();
            if (rs != null) {
                ViewTableListAction.log.info("Strart Iterating recordset");
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    tableList.add(rs.getString("TABLE_NAME"));
                    ViewTableListAction.log.info("Table :" + tableName);
                }
            }
            request.setAttribute("tableList", tableList);
            rs = null;
            con = null;
        }
        catch (EliteCodeGenException e) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.enable.connect"));
            saveErrors(request, errors);
            return map.findForward("failure");
        }
        catch (Exception e) {
            e.printStackTrace();
            ViewTableListAction.log.info("Unhandled Exception...");
        }
        finally {
            
            if (con != null)
                con = null;
            
            if (rs != null)
                rs = null;
        }
        return map.findForward("success");
    }
}
