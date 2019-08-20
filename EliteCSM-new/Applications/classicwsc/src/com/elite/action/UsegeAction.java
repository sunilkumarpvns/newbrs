package com.elite.action;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.jdbc.pool.OracleDataSource;

import com.elite.auth.db.DBConnectionManager;
import com.elite.config.ORACLEConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.model.LoginModal;
import com.elite.user.Userbean;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UsegeAction extends ActionSupport {
	Logger logger = Logger.getLogger("wsc");
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
	
	
	public String getPagestr() {
		return pagestr;
	}
	public void setPagestr(String pagestr) {
		this.pagestr = pagestr;
	}
	public ResultSet getSearchRs() {
		return searchRs;
	}
	
	public String getC_dateCreateFrom() {
		return c_dateCreateFrom;
	}
	public void setC_dateCreateFrom(String createFrom) {
		c_dateCreateFrom = createFrom;
	}
	public String getC_dateCreateTo() {
		return c_dateCreateTo;
	}
	public void setC_dateCreateTo(String createTo) {
		c_dateCreateTo = createTo;
	}
	public String getUsagelimit() {
		return usagelimit;
	}
	public void setUsagelimit(String usagelimit) {
		this.usagelimit = usagelimit;
	}
	public long getTotalusage() {
		return totalusage;
	}
	public void setTotalusage(long totalusage) {
		this.totalusage = totalusage;
	}
	String c_btnSumbit = null;
	public String getC_btnSumbit() {
		return c_btnSumbit;
	}
	public void setC_btnSumbit(String sumbit) {
		c_btnSumbit = sumbit;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	int page = 0;
	String c_dateCreateFrom = sdf.format(new Date());
	String c_dateCreateTo = sdf.format(new Date());
	ResultSet searchRs = null;
	String pagestr = null;
	String usagelimit = "Unlimited";
	long totalusage = 0;
	
	@Override
	public String execute()  {
		try
		{
			Map session = ActionContext.getContext().getSession();
			Userbean user = (Userbean)session.get("user");
			totalusage = new LoginModal().getTotalUsage(user);
			logger.info(totalusage);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	    return SUCCESS;
	}
	
	public String search()  {
		try
		{
			execute();
			String dbname = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			Map session = ActionContext.getContext().getSession();
	        Userbean user = (Userbean)session.get("user");
		    Statement stat = DBConnectionManager.getInstance(dbname).getConnection().createStatement();
//		    String sql = "select  callstart, callend, sessiontime, acctinputoctets, acctoutputoctets, " +
//		    		"volume  from cdrchargetable where customeridentifier = '"+user.getUserotherdetail().getCustomeridentifier()+"' and (chargeddate " +
//		    		"between (to_date('"+c_dateCreateFrom+"','dd MON, yyyy')) and " +
//		    				"(to_date('"+c_dateCreateTo+"','dd MON, yyyy')+1))";
		    String sql = "select  callstart, callend, sessiontime, acctinputoctets, acctoutputoctets, volume, charge, total_rows " + 
		    		"from ( "+
		            	"select  callstart, callend, sessiontime, acctinputoctets, acctoutputoctets, volume, charge, " +
		                   "COUNT(*) OVER() total_rows, "+
		                   "ROW_NUMBER() OVER (ORDER BY chargeddate) row_counter "+
		            "FROM   cdrchargetable "+
		            "where  customeridentifier = '"+user.getUsername()+"' and  "+
		            "(chargeddate between (to_date('"+c_dateCreateFrom+"','dd MON, yyyy'))  "+
		    		"and (to_date('"+c_dateCreateTo+"','dd MON, yyyy')+1)) "+
		            "ORDER BY chargeddate "+
		            ") "+
		            "where  row_counter between "+((page*10)+1)+" and "+((page*10)+10)+" ";
		    logger.info("Profile Search Query :- "+sql);
		    searchRs =  stat.executeQuery(sql);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	    return SUCCESS;
	}
	public String searchPage()  {
		try
		{
			execute();
			int pagecount = 6;
			String dbname = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			Map session = ActionContext.getContext().getSession();
	        Userbean user = (Userbean)session.get("user");
		    Statement stat = DBConnectionManager.getInstance(dbname).getConnection().createStatement();
		    String sql = "select count(*) as cnt  from cdrchargetable where " +
		    		"customeridentifier = '"+user.getUsername()+"' " +
		    				"and (chargeddate between (to_date('"+c_dateCreateFrom+"','dd MON, yyyy')) and " +
    				"(to_date('"+c_dateCreateTo+"','dd MON, yyyy')+1))";
		    logger.info("Profile Page Count Query :- "+sql);
		    ResultSet rs =  stat.executeQuery(sql);
		    if(rs.next())
		    {
		    	int count = rs.getInt("cnt");
		    	pagecount = count/10;
		    	if ((count % 10) != 0)
		    	{
		    		pagecount = pagecount + 1;
		    	}
		    }
		    pagestr = "";
		    while((pagecount--) != 0)
		    {
		    	pagestr = "\"GoUsege_details.action?page="+pagecount+"&c_dateCreateFrom="+c_dateCreateFrom+"&c_dateCreateTo="+c_dateCreateTo+"\"" + pagestr;
		    	if(pagecount > 0)
		    	{
		    		pagestr = "," + pagestr;
		    	}
		    }
		    if(pagestr.equals(""))
		    {
		    	pagestr = "\"GoUsege_details.action?page=0&c_dateCreateFrom="+c_dateCreateFrom+"&c_dateCreateTo="+c_dateCreateTo+"\"";
		    }
		    logger.info(pagestr);
		    
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	    return SUCCESS;
	    
	}
}
