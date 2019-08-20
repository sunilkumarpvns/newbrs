/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ViewTableAction.java
 * Created on Jul 20, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.actions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.EliteCodeGenConstant;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.DbResourceForm;
import com.elitecore.elitecodegen.dao.ConnectionFactory;
import com.elitecore.elitecodegen.exception.EliteCodeGenException;

/**
 * @author kaushikvira
 */
public class ViewTableAction extends Action {
    
    private static Logger log  = Logger.getLogger(ViewTableAction.class);
    
    private static String NEXT = "next";
    
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
                                  HttpServletResponse response ) {
        
        ViewTableAction.log.info("ViewTableAction.execute called");
        
        ActionErrors errors = new ActionErrors();
        DbResourceForm dbResource = null;
        Connection con = null;
        ResultSet rs = null;
        TableDataBean tableDataBean;
        try {
            ViewTableAction.log.info("Getting value from session");
            dbResource = (DbResourceForm) request.getSession(false).getAttribute(EliteCodeGenConstant.DbResource);
            tableDataBean = (TableDataBean) request.getSession(false).getAttribute(EliteCodeGenConstant.TableDataBean);
            
            ViewTableAction.log.info("url :" + dbResource.getStrJdbcUrl());
            ViewTableAction.log.info("driver :" + dbResource.getStrDriverName());
            ViewTableAction.log.info("userName :" + dbResource.getStrUserName());
            ViewTableAction.log.info("password :" + dbResource.getStrPassword());
            ViewTableAction.log.info("SchemaName:" + dbResource.getStrSchmaName());
            ViewTableAction.log.info("TableName :" + tableDataBean.getStrTableName());
            
            con = ConnectionFactory.Connect(dbResource);
            
            rs = con.getMetaData().getPrimaryKeys(null, dbResource.getStrSchmaName(), tableDataBean.getStrTableName());
            
            while (rs.next())
                if (rs.getString("TABLE_NAME").equals(tableDataBean.getStrTableName())) {
                    String primaryKey = rs.getString("COLUMN_NAME");
                    if (primaryKey != null) {
                        tableDataBean.setStrPrimaryKey(primaryKey);
                        ViewTableAction.log.info("Primary Key" + primaryKey);
                    }
                    break;
                }
            
            rs = null;
            
            rs = con.getMetaData().getColumns(null, dbResource.getStrSchmaName(), tableDataBean.getStrTableName(), "%");
            
            List ls = new ArrayList<ColumnBean>();
            
            ViewTableAction.log.info("Strart Iterating recordset");
            
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
               
                String strNullable = rs.getString("IS_NULLABLE");
                boolean nullable;
                if (strNullable.equals("NO")) {
                    nullable = true;
                    ViewTableAction.log.info("Nullable");
                } else {
                    nullable = false;
                    ViewTableAction.log.info("Not Nullable");
                }
                
                String javaType;
                
                if (TableDataBean.orcalVsJavaType.containsKey(columnType))
                    javaType = (String) TableDataBean.orcalVsJavaType.get(columnType);
                
                else javaType = " ";
                
                ls.add(new ColumnBean(columnSize, columnType, columnName, columnName.toLowerCase(), javaType, nullable));
                
                ViewTableAction.log.info("ColumnName:" + columnName);
            }
            tableDataBean.setCloumnList(ls);
            
            rs = null;
            con = null;
            
            // request.getSession(false).setAttribute(EliteCodeGenConstant.TableDataBean,tableDataBean);
            
        }
        catch (EliteCodeGenException e) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.enable.connect"));
            saveErrors(request, errors);
            return map.findForward("failure");
        }
        catch (Exception e) {
            e.printStackTrace();
            ViewTableAction.log.info("exceptin" + e);
            ViewTableAction.log.info("Unhandled Exception...");
        }
        finally {
            
            if (con != null)
                con = null;
            
            if (rs != null)
                rs = null;
        }
        
        return map.findForward(ViewTableAction.NEXT);
    }
    
}
