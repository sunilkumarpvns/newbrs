/**
 * 
 */
package com.elitecore.classicrating.datamanager.customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

/**
 * @author sheetalsoni
 * 
 */
public class CustomerBalanceDBHelper extends BaseDBHelper {

    private static final String MODULE = "CUSTOMER_BALANCE_DB";
    private static final String GET_CUSTOMER_BALANCE = "SELECT CUSTOMERNAME, CUSTOMERIDENTIFIER,BALANCE FROM " + "CUSTOMERBALANCE WHERE CUSTOMERIDENTIFIER =? ";
    private static final String UPDATE_PREPAID_CUSTOMER_BALANCE = "UPDATE CUSTOMERBALANCE " + "SET BALANCE = ? WHERE CUSTOMERIDENTIFIER = ?";
    private static final String UPDATE_POSTPAID_CUSTOMER_BALANCE = "UPDATE CUSTOMERACCOUNT " + "SET TOTALUSAGE = ? WHERE CUSTOMERIDENTIFIER = ?";

    public CustomerBalanceDBHelper(ITransactionContext transactionContext) {
        super(transactionContext);
    }

    public CustomerBalanceData getCustomerBalance(String customerIdetifier) throws SQLException {
        LogManager.getLogger().trace(MODULE, "Into getCustomerBalance with customerId= " + customerIdetifier);
        CustomerBalanceData customerBalanceData = null;
        PreparedStatement pstmtRateDefinition = null;
        ResultSet rsBalance = null;

        try {

            pstmtRateDefinition = super.getTransactionContext().prepareStatement(GET_CUSTOMER_BALANCE);
            pstmtRateDefinition.setString(1, customerIdetifier);

            rsBalance = pstmtRateDefinition.executeQuery();

            if (rsBalance.next()) {
                customerBalanceData = new CustomerBalanceData();

                customerBalanceData.setCustomerName(rsBalance.getString("CUSTOMERNAME"));
                customerBalanceData.setCustomerIdetiifer(rsBalance.getString("CUSTOMERIDENTIFIER"));
                customerBalanceData.setBalance(rsBalance.getDouble("BALANCE"));
            }
        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().debug(MODULE, "Exception caught while reading Customer Balance Information." + e.toString());
            throw e;
        } finally {
            try {
                if (rsBalance != null) {
                    rsBalance.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().debug(MODULE, "Exception caught while closing Result set");
                throw e;
            }
            try {
                if (pstmtRateDefinition != null) {
                    super.getTransactionContext().closePreparedStatement(pstmtRateDefinition);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().trace(MODULE, "Out of getCustomerBalance with customerBalanceData= " + customerBalanceData);
        return customerBalanceData;
    }

    /**
     * Updates customer balance in case of Prepaid.
     *
     * @method updateCustomerBalanceForPrepaid
     * @param customerIdentifier, balance
     * @return
     * @author sheetalsoni
     * @created 10/07/2008
     * */
    public void updateCustomerBalance(String customerIdentifier, double availBalance) throws SQLException {
        LogManager.getLogger().trace(MODULE, "Into updateCustomerBalanceForPrepaid with customerIdentifier= " + customerIdentifier + " and balace=" + availBalance);
        PreparedStatement pstmtCustomerBalance = null;

        try {
            pstmtCustomerBalance = super.getTransactionContext().prepareStatement(UPDATE_PREPAID_CUSTOMER_BALANCE);
            pstmtCustomerBalance.setDouble(1, availBalance);
            pstmtCustomerBalance.setString(2, customerIdentifier);
            pstmtCustomerBalance.executeUpdate();

        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            super.getTransactionContext().setRollbackOnly();
            LogManager.getLogger().trace(MODULE, "Exception caught while Updating Prepaid Customer Balance");
            throw e;
        } finally {
            try {
                if (pstmtCustomerBalance != null) {
                    super.getTransactionContext().closePreparedStatement(pstmtCustomerBalance);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().trace(MODULE, "Out of updateCustomerBalanceForPrepaid with successful updation");
    }

    /**
     * Updates customer balance in case of POSTPAID, total amount of usage of
     * customer would be added
     *
     * @method updateCustomerBalanceForPostpaid
     * @param customerIdentifier, balance
     * @author sheetalsoni
     * @created 10/6/2008
     * @return
     */
    public void updateTotalUsage(String customerIdentifier, double totalUsage) throws SQLException{
		LogManager.getLogger().trace(MODULE, "InTo updateTotalUsage()");
		
		PreparedStatement pstmtCustomerBalance = null;
		try {
			pstmtCustomerBalance = super.getTransactionContext().prepareStatement(UPDATE_POSTPAID_CUSTOMER_BALANCE);
			
			pstmtCustomerBalance.setDouble(1, totalUsage);
			pstmtCustomerBalance.setString(2, customerIdentifier);
			
			pstmtCustomerBalance.executeUpdate();
		} catch (SQLException e) {
			super.getTransactionContext().setRollbackOnly();
			LogManager.getLogger().trace(MODULE, "Exception caught while Updating Post Paid Customer Balance");
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
			throw e;
		} finally {
			try {
				if (pstmtCustomerBalance != null) {
					super.getTransactionContext().closePreparedStatement(pstmtCustomerBalance);
				}
			} catch (SQLException e) {
				//	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
				LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement"+ e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, "OutOf updateTotalUsage()");
		}
    }	
}
