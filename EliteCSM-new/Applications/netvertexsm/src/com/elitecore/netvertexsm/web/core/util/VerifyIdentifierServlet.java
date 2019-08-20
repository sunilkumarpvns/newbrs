package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.corenetvertex.pkg.servicetype.RatingGroupData;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.netvertexsm.util.logger.Logger;

/**
 * Servlet implementation class VerifyIdentifierServlet
 */
public class VerifyIdentifierServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="VERIFY_IDENTIFIER_SERVLET";
	private static final long MAX_ALLOWED_VALUE=4294967294L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyIdentifierServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.logInfo(MODULE,"Enter in Servlet " + getClass().getName());
		
		String identifier = request.getParameter("identifier");
		String mode = request.getParameter("mode");
		String ratingGroupID = request.getParameter("ratingGroupID");
        long identifierNumericValue;       
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		if(identifier == null || identifier.length() <= 0){
			out.println("blank");
			return;
		}
		
		try{
			identifierNumericValue=Long.parseLong(identifier);
		}catch (NumberFormatException e) {
			out.println("invalid");
			return;
		}
		if((identifierNumericValue<=MAX_ALLOWED_VALUE)==false){
			out.println("out_of_range");
			return;
		}
		try{
			boolean flag =verifyIdentifier(identifier,ratingGroupID,mode);
			if(flag){
				out.println("true");
			}else {
				out.println("false");
			}
		}catch(DuplicateInstanceNameFoundException e){
			Logger.logError(MODULE, e.getMessage());
			out.println("false");
		}catch(DataManagerException e){
			out.print(e.getMessage());
			Logger.logError(MODULE, e.getMessage());
		}finally{
			out.close();
		}
	}
	

	private boolean verifyIdentifier(String identifier,String ratingGroupID,String mode) throws DataManagerException{
		List<Object[]> list =null;
		Criteria criteria =null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			criteria  = ((HibernateDataSession)session).getSession().createCriteria(RatingGroupData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("identifier"));
			criteria.setProjection(proList);
			if(mode!=null && mode.equalsIgnoreCase("update")){
				criteria.add(Restrictions.ne("ratingGroupID", Long.parseLong(ratingGroupID)));
			}
			list = criteria.add(Restrictions.eq("identifier",Long.parseLong(identifier))).list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}finally{
			if(session!=null)
				session.close();
		}
		
		if(list!=null && !list.isEmpty()){
			throw new DuplicateInstanceNameFoundException("Identifier Is Duplicated.");
		}else{
			return true;
		}
	}
}