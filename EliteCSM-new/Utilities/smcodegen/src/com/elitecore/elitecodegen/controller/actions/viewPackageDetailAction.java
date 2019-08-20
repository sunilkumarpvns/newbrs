/**
 * Copyright (C) Elitecore Technologies Ltd.
 * ViewPackageDetailAction.java
 * Created on Jul 26, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitecodegen.base.ColumnBean;
import com.elitecore.elitecodegen.base.EliteCodeGenConstant;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.controller.form.NavigateDataClassForm;

/**
 * @author kaushikvira
 */

public class ViewPackageDetailAction extends Action {
    
    private static Logger log  = Logger.getLogger(ViewPackageDetailAction.class);
    
    private static String NEXT = "next";
    
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form ,
                                  HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        
        NavigateDataClassForm navigationForm = (NavigateDataClassForm) form;
        
        TableDataBean tableDataBean = (TableDataBean) request.getSession(false).getAttribute(EliteCodeGenConstant.TableDataBean);
        
        tableDataBean.setStrTableName(navigationForm.getStrTableName());
        
        int totalCol = tableDataBean.getCloumnList().size();
        String[] javaName = navigationForm.getStrJavaName();
        int[] colId = navigationForm.getChioce();
        String[] javaType = navigationForm.getStrJavaType();
        
        if (colId != null) {
            ViewPackageDetailAction.log.info("NO of Column processing :" + totalCol);
            for ( int j = 0; j < totalCol; j++ ) {
                ColumnBean colBean = tableDataBean.getCloumn(j);
                
                ViewPackageDetailAction.log.info("Column under Process :" + j);
                for ( int k = 0; k < colId.length; k++ )
                    if (j == colId[k]) {
                        ViewPackageDetailAction.log.info("Column is selected..");
                        colBean.setSelectionStaus(true);
                        break;
                    } else colBean.setSelectionStaus(false);
                
                ViewPackageDetailAction.log.info("javaType" + javaType[j]);
                if (javaType[j].equals(""))
                    colBean.setJavaType("Type" + j);
                else colBean.setJavaType(javaType[j]);
                
                ViewPackageDetailAction.log.info("javaName :" + javaName[j]);
                colBean.setJavaName(javaName[j]);
                
                if (javaName[j].equals(""))
                    colBean.setJavaName("Name" + j);
                else colBean.setJavaName(javaName[j]);
                
            }
            
        }
        
        return mapping.findForward(ViewPackageDetailAction.NEXT);
    }
    
}
