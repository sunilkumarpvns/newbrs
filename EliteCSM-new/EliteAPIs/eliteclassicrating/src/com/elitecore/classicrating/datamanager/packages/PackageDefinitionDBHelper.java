/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *  Last Modified October 1, 2008
 */

/*
 * PackageDefinitionDBHelper.java 
 * This class is used to get package definition information from database.
 * Methods include getting List of all packages, selecting package by Id or Name or packageType
 * 
 */

package com.elitecore.classicrating.datamanager.packages;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.classicrating.blmanager.packages.PackageNotFoundException;
import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

public class PackageDefinitionDBHelper extends BaseDBHelper {

	private static final String MODULE = "Package DB Helper";
	
	
	private String GET_ALL_PACKAGES = "SELECT PACKAGEID, PACKAGENAME, PACKAGETYPE, "
			+ " UNITTYPE, MINCAP, MAXCAP FROM PACKAGEDEFINITION";

	private String GET_PACKAGE_BY_ID = "SELECT PACKAGEID, PACKAGENAME, PACKAGETYPE, "
			+ "UNITTYPE, MINCAP, MAXCAP FROM PACKAGEDEFINITION "
			+ " WHERE PACKAGEID = ?";

	private String GET_PACKAGE_BY_NAME = "SELECT PACKAGEID, PACKAGENAME, PACKAGETYPE, "
			+ "UNITTYPE, MINCAP, MAXCAP FROM PACKAGEDEFINITION "
			+ " WHERE PACKAGENAME= ?";

	private String GET_PACKAGE_BY_TYPE = "SELECT PACKAGEID, PACKAGENAME, PACKAGETYPE, "
			+ "UNITTYPE, MINCAP, MAXCAP FROM PACKAGEDEFINITION "
			+ " WHERE PACKAGETYPE= ?";

	public PackageDefinitionDBHelper(ITransactionContext transactionContext) {
		super(transactionContext);
	}

	public List<PackageDefinitionData> getAllPackages() throws SQLException, PackageNotFoundException {
                LogManager.getLogger().trace(MODULE, "Into getAllPackages ");
		List<PackageDefinitionData> packageList = null;
		ResultSet rSet = null;
		PreparedStatement pStmt = null;
		try {
			pStmt = super.getTransactionContext().prepareStatement(GET_ALL_PACKAGES);
			rSet = pStmt.executeQuery();
			if (!rSet.next()) {
				LogManager.getLogger().warn(MODULE, "No Packages found");
				throw new PackageNotFoundException(
						"No package found with given id");

			} else {

				packageList = new ArrayList<PackageDefinitionData>();

				do {
					PackageDefinitionData packageDefinitionData = new PackageDefinitionData();
					packageDefinitionData.setPackageID(rSet.getInt("PACKAGEID"));
					packageDefinitionData.setPackageName(rSet.getString("PACKAGENAME"));
					packageDefinitionData.setPackageType(rSet.getString("PACKAGETYPE"));
					packageDefinitionData.setUnitType(rSet.getString("UNITTYPE"));
					packageDefinitionData.setMinCap(rSet.getDouble("MINCAP"));
					packageDefinitionData.setMaxCap(rSet.getDouble("MAXCAP"));
					packageList.add(packageDefinitionData);
				} while (rSet.next());
			}
		} catch (SQLException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, "Error in executing query : " + ex.getMessage());
                        throw ex;
		} catch (PackageNotFoundException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, "No Packages found.");
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
				LogManager.getLogger().debug(MODULE, "Exception caught while closing Result set");
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
				LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set"+ e.getMessage());
                                throw e;
			}
		}
                 LogManager.getLogger().trace(MODULE, "Out of getAllPackages with packageList=" + ( packageList!=null? packageList.toString(): "null" ));
		return packageList;
	}

	public PackageDefinitionData getPackageByID(int packageID) throws SQLException, PackageNotFoundException {
                LogManager.getLogger().trace(MODULE, "Into getPackageByID with packageId= " + packageID);
		PackageDefinitionData packageDefinitionData = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			pStmt = super.getTransactionContext().prepareStatement(
					GET_PACKAGE_BY_ID);
			pStmt.setInt(1, packageID);
			rSet = pStmt.executeQuery();
			if (rSet.next()) {
				packageDefinitionData = new PackageDefinitionData();
				packageDefinitionData.setPackageID(rSet.getInt("PACKAGEID"));
				packageDefinitionData.setPackageName(rSet.getString("PACKAGENAME"));
				packageDefinitionData.setPackageType(rSet.getString("PACKAGETYPE"));
				packageDefinitionData.setUnitType(rSet.getString("UNITTYPE"));
				packageDefinitionData.setMinCap(rSet.getDouble("MINCAP"));
				packageDefinitionData.setMaxCap(rSet.getDouble("MAXCAP"));
				rSet.close();
			} else {
				throw new PackageNotFoundException("No package found with given id: " + packageID);
			}
		} catch (SQLException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, "Error in executing query : "+ ex.getMessage());
                        throw ex;
		} catch (PackageNotFoundException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, ex.getMessage());
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
				LogManager.getLogger().debug(MODULE, "Exception caught while closing Result set");
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
				LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set"+ e.getMessage());
                                throw e;
					
			}
		}
                LogManager.getLogger().trace(MODULE, "Out of getPackageByID with (packageId= " + packageID+ ") = ("+ packageDefinitionData +")");
		return packageDefinitionData;
	}

	
	public PackageDefinitionData getPackageByName(String packageName) throws SQLException, PackageNotFoundException {
                LogManager.getLogger().trace(MODULE, "Into getPackageByName with packageName= " + packageName);
		PackageDefinitionData packageDefinitionData = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			pStmt = super.getTransactionContext().prepareStatement(GET_PACKAGE_BY_NAME);
			pStmt.setString(1, packageName);
			rSet = pStmt.executeQuery();
			if (rSet.next()) {
				packageDefinitionData = new PackageDefinitionData();
				packageDefinitionData.setPackageID(rSet.getInt("PACKAGEID"));
				packageDefinitionData.setPackageName(rSet.getString("PACKAGENAME"));
				packageDefinitionData.setPackageType(rSet.getString("PACKAGETYPE"));
				packageDefinitionData.setUnitType(rSet.getString("UNITTYPE"));
				packageDefinitionData.setMinCap(rSet.getDouble("MINCAP"));
				packageDefinitionData.setMaxCap(rSet.getDouble("MAXCAP"));
				rSet.close();
			} else {
				throw new PackageNotFoundException("No package found with given name: " + packageName);
			}
		} catch (SQLException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, "Error in executing query : "+ ex.getMessage());
                        throw ex;
		} catch (PackageNotFoundException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, ex.getMessage());
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
				LogManager.getLogger().debug(MODULE, "Exception caught while closing Result set");
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
				LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set"+ e.getMessage());
                                throw e;
					
			}
		}

                LogManager.getLogger().trace(MODULE, "Out of getPackageByID with (packageName= " + packageName+ ") = ("+ packageDefinitionData +")");
		return packageDefinitionData;
	}	
	

	public ArrayList<PackageDefinitionData> getPackageByType(String packageType) throws SQLException, PackageNotFoundException {

                LogManager.getLogger().trace(MODULE, "Into getPackageByType with packageType= " + packageType);
                ArrayList<PackageDefinitionData> packageDefinitionList= null;
		PackageDefinitionData packageDefinitionData = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			pStmt = super.getTransactionContext().prepareStatement(GET_PACKAGE_BY_TYPE);
			pStmt.setString(1, packageType);
			rSet = pStmt.executeQuery();
			if (rSet.next()) {
                            
                                packageDefinitionList= new ArrayList<PackageDefinitionData>();
                              do{ 
				packageDefinitionData = new PackageDefinitionData();
				packageDefinitionData.setPackageID(rSet.getInt("PACKAGEID"));
				packageDefinitionData.setPackageName(rSet.getString("PACKAGENAME"));
				packageDefinitionData.setPackageType(rSet.getString("PACKAGETYPE"));
				packageDefinitionData.setUnitType(rSet.getString("UNITTYPE"));
				packageDefinitionData.setMinCap(rSet.getDouble("MINCAP"));
				packageDefinitionData.setMaxCap(rSet.getDouble("MAXCAP"));
                                packageDefinitionList.add(packageDefinitionData);
                              }while(rSet.next());
                                
				rSet.close();
			} else {
				throw new PackageNotFoundException("No package found with given type: " + packageType);
			}
		} catch (SQLException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, "Error in executing query : "+ ex.getMessage());
                        throw ex;
		} catch (PackageNotFoundException ex) {
			//	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, ex.toString());
			LogManager.getLogger().debug(MODULE, ex.getMessage());
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
				LogManager.getLogger().debug(MODULE, "Exception caught while closing Result set");
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
				LogManager.getLogger().trace(MODULE, "Exception Caught while closing Result Set"+ e.getMessage());
                                throw e;
			}
		}
                LogManager.getLogger().trace(MODULE, "Out of getPackageByID with (packageType= " + packageType+ ") = ("+ packageDefinitionList +")");
		return packageDefinitionList;
	}	
	

}
