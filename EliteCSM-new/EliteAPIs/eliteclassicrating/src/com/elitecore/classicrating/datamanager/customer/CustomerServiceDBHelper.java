/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 3, 2008
 *	@author Raghu G
 *  Last Modified October 4, 2008
 */

/*
 * CustomerServiceDBHelper.java 
 * This class is used to get customer information from database.
 * Methods includes selecting customer by username, by service name
 * 
 */
package com.elitecore.classicrating.datamanager.customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

public class CustomerServiceDBHelper extends BaseDBHelper {

    private static final String MODULE = "Customer DB Helper";
    private String GET_ALL_CUSTOMERS = "SELECT CUSTOMERNAME, CUSTOMERIDENTIFIER, SERVICETYPE, " + " PACKAGEID, CUSTOMERTYPE, CUSTOMERSUBTYPE,TOTALUSAGE,VERSION FROM CUSTOMERACCOUNT";
    private String GET_CUSTOMER_BY_NAME = "SELECT CUSTOMERNAME, CUSTOMERIDENTIFIER, SERVICETYPE, " + " PACKAGEID, CUSTOMERTYPE, CUSTOMERSUBTYPE,TOTALUSAGE,VERSION FROM CUSTOMERACCOUNT " + " WHERE CUSTOMERNAME = ?";
    private String GET_CUSTOMER_BY_IDENTIFIER = "SELECT CUSTOMERNAME, CUSTOMERIDENTIFIER, SERVICETYPE, " + " PACKAGEID, CUSTOMERTYPE, CUSTOMERSUBTYPE,TOTALUSAGE,VERSION FROM CUSTOMERACCOUNT " + " WHERE CUSTOMERIDENTIFIER = ?";
    private String GET_TOTAL_USAGE_OF_CUSTOMER = "SELECT TOTALUSAGE FROM CUSTOMERACCOUNT " + " WHERE CUSTOMERNAME = ?";
    private String UPDATE_TOTAL_USAGE_OF_CUSTOMER = "UPDATE CUSTOMERACCOUNT " + " SET TOTALUSAGE =? WHERE CUSTOMERIDENTIFIER = ?";
    private String GET_CUSTOMER_BY_ID_AND_SERVICE_TYPE = "SELECT CUSTOMERNAME, CUSTOMERIDENTIFIER, SERVICETYPE, " + " PACKAGEID, CUSTOMERTYPE, CUSTOMERSUBTYPE,TOTALUSAGE,VERSION FROM CUSTOMERACCOUNT " + " WHERE CUSTOMERIDENTIFIER = ? AND SERVICETYPE=? ";

    public CustomerServiceDBHelper(ITransactionContext transactionContext) {
        super(transactionContext);
    }

    /**
     * Returns
     * @return List<CustomerServiceData> A list of all customers
     */
    public List<CustomerServiceData> getAllCustomers() throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getAllCustomers ");
        List<CustomerServiceData> customerServiceList = null;

        PreparedStatement pStmt = null;
        ResultSet rSet = null;
        try {
            pStmt = super.getTransactionContext().prepareStatement(GET_ALL_CUSTOMERS);

            rSet = pStmt.executeQuery();

            while (rSet.next()) {

                if (customerServiceList == null) {
                    customerServiceList = new ArrayList<CustomerServiceData>();
                }
                CustomerServiceData customerServiceData = new CustomerServiceData();
                customerServiceData.setCustomerName(rSet.getString("CUSTOMERNAME"));
                customerServiceData.setCustomerIdentifier(rSet.getString("CUSTOMERIDENTIFIER"));
                customerServiceData.setServiceType(rSet.getString("SERVICETYPE"));
                customerServiceData.setPackageId(rSet.getInt("PACKAGEID"));
                customerServiceData.setCustomerType(rSet.getString("CUSTOMERTYPE"));
                customerServiceData.setCustomerSubType(rSet.getString("CUSTOMERSUBTYPE"));
                customerServiceData.setTotalUsage(rSet.getLong("TOTALUSAGE"));
                customerServiceData.setVersion(rSet.getString("VERSION"));
                customerServiceList.add(customerServiceData);
            }
        } catch (SQLException ex) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
            LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
            throw ex;
        } finally {
            try {
                if (rSet != null) {
                    rSet.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
            try {
                if (pStmt != null) {
                    super.getTransactionContext().closePreparedStatement(pStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getAllCustomers with customerServiceList=" + customerServiceList);
        return customerServiceList;
    }

    public CustomerServiceData getCustomerByIdentifier(String customerIdentifier) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getCustomerByIdentifier with customerId= " + customerIdentifier);
        return executeQuery(GET_CUSTOMER_BY_IDENTIFIER, customerIdentifier);
    }

    public CustomerServiceData getCustomerByName(String customerName) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getCustomerByName with customerName= " + customerName);
        return executeQuery(GET_CUSTOMER_BY_NAME, customerName);
    }

    public CustomerServiceData executeQuery(String sqlQuery, String parameter) throws SQLException {
        PreparedStatement pStmt = null;
        ResultSet rSet = null;
        CustomerServiceData customerServiceData = null;
        try {
            pStmt = super.getTransactionContext().prepareStatement(sqlQuery);
            pStmt.setString(1, parameter);
            rSet = pStmt.executeQuery();
            if (rSet.next()) {
                customerServiceData = new CustomerServiceData();
                customerServiceData.setCustomerName(rSet.getString("CUSTOMERNAME"));
                customerServiceData.setCustomerIdentifier(rSet.getString("CUSTOMERIDENTIFIER"));
                customerServiceData.setServiceType(rSet.getString("SERVICETYPE"));
                customerServiceData.setPackageId(rSet.getInt("PACKAGEID"));
                customerServiceData.setCustomerType(rSet.getString("CUSTOMERTYPE"));
                customerServiceData.setCustomerSubType(rSet.getString("CUSTOMERSUBTYPE"));
                customerServiceData.setTotalUsage(rSet.getLong("TOTALUSAGE"));
                customerServiceData.setVersion(rSet.getString("VERSION"));
            }
        } catch (SQLException ex) {
				LogManager.getLogger().trace(MODULE, ex.toString());
            LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
            throw ex;
        } finally {
            try {
                if (rSet != null) {
                    rSet.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
            try {
                if (pStmt != null) {
                    super.getTransactionContext().closePreparedStatement(pStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getCustomer with customerServiceData= " + customerServiceData);
        return customerServiceData;
    }

    public double getTotalUsageOfCustomer(String customerIdentifier) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getTotalUsageOfCustomer with customerId= " + customerIdentifier);
        PreparedStatement pStmt = null;
        ResultSet rSet = null;
        double totalUsage = -1;
        try {
            pStmt = super.getTransactionContext().prepareStatement(
                    GET_TOTAL_USAGE_OF_CUSTOMER);
            pStmt.setString(1, customerIdentifier);
            rSet = pStmt.executeQuery();
            if (rSet.next()) {
                totalUsage = rSet.getDouble("TOTALUSAGE");
            }
        } catch (SQLException ex) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
            LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
            throw ex;

        } finally {
            try {
                if (rSet != null) {
                    rSet.close();
                }
            } catch (SQLException ex) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, ex.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + ex.getMessage());
                throw ex;
            }

            try {
                if (pStmt != null) {
                    super.getTransactionContext().closePreparedStatement(pStmt);
                }
            } catch (SQLException ex) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, ex.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + ex.getMessage());
                throw ex;
            }
        }
        LogManager.getLogger().info(MODULE, "Into getTotalUsageOfCustomer with (customerId= " + customerIdentifier + ")= (" + totalUsage + ")");
        return totalUsage;
    }

    public boolean updateTotalUsageOfCustomer(String customerIdentifier, double totalUsage) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into updateTotalUsageOfCustomer with customerId= " + customerIdentifier + "and totalUsage=" + totalUsage);
        int rowsAffected = -1;
        PreparedStatement pStmt = null;
        try {
            pStmt = super.getTransactionContext().
                    prepareStatement(UPDATE_TOTAL_USAGE_OF_CUSTOMER);
            pStmt.setDouble(1, totalUsage);
            pStmt.setString(2, customerIdentifier);
            rowsAffected = pStmt.executeUpdate();
        } catch (SQLException ex) {
            super.getTransactionContext().setRollbackOnly();
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
            LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
            throw ex;
        } finally {
            try {
                if (pStmt != null) {
                    super.getTransactionContext().closePreparedStatement(pStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of updateTotalUsageOfCustomer with successful updation");
        return (rowsAffected == 1);
    }

    public CustomerServiceData getCustomerByIdAndServiceType(String customerId, String serviceType) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getCustomerByIdAndServiceType with customerId= " + customerId + "and serviceType=" + serviceType);
        PreparedStatement pStmt = null;
        ResultSet rSet = null;

        CustomerServiceData customerServiceData = null;

        try {
            pStmt = super.getTransactionContext().prepareStatement(GET_CUSTOMER_BY_ID_AND_SERVICE_TYPE);
            pStmt.setString(1, customerId);
            pStmt.setString(2, serviceType);

            rSet = pStmt.executeQuery();

            if (rSet.next()) {
                customerServiceData = new CustomerServiceData();
                customerServiceData.setCustomerName(rSet.getString("CUSTOMERNAME"));
                customerServiceData.setCustomerIdentifier(rSet.getString("CUSTOMERIDENTIFIER"));
                customerServiceData.setServiceType(rSet.getString("SERVICETYPE"));
                customerServiceData.setPackageId(rSet.getInt("PACKAGEID"));
                customerServiceData.setCustomerType(rSet.getString("CUSTOMERTYPE"));
                customerServiceData.setCustomerSubType(rSet.getString("CUSTOMERSUBTYPE"));
                customerServiceData.setTotalUsage(rSet.getLong("TOTALUSAGE"));
                customerServiceData.setVersion(rSet.getString("VERSION"));
            }
        } catch (SQLException ex) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
            LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
            throw ex;
        } finally {
            try {
                if (rSet != null) {
                    rSet.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
            try {
                if (pStmt != null) {
                    super.getTransactionContext().closePreparedStatement(pStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set" + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of  getCustomerByIdAndServiceType with customerServiceData= " + customerServiceData);
        return customerServiceData;
    }
}
