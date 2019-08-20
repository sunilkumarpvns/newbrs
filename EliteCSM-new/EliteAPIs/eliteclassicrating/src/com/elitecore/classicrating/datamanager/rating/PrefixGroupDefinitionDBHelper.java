/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu
 *      Last Modified November 6, 2008
 */
package com.elitecore.classicrating.datamanager.rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

/**
 * Provides DB queries for the PrefixGroupDefinition Model.
 * 
 * @author Raghu
 * 
 */
public class PrefixGroupDefinitionDBHelper extends BaseDBHelper {

    public static final String MODULE = "PREFIX_GROUP_DEFINITION_DB";
    private static final String GET_ALL_PREFIX_GROUP_DEFINITIONS = "SELECT * FROM PREFIXGROUPDEFINITION";
    private static final String GET_PREFIX_GROUP_DEFINITION_BY_NAME = "SELECT * FROM PREFIXGROUPDEFINITION WHERE PREFIXGROUPNAME=?";
    private static final String GET_PREFIX_GROUP_DEFINITION_BY_NUMBER = "SELECT * FROM PREFIXGROUPDEFINITION WHERE PREFIXGROUPNUMBER=?";

    public PrefixGroupDefinitionDBHelper(ITransactionContext transactionContext) {
        super(transactionContext);
    }

    /**
     * Returns all the Prefix Group Definition
     * @return List<PrefixGroupDefinitionData>
     *
     */
    public List<PrefixGroupDefinitionData> getAllPrefixGroupDefinition() throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getAllPrefixGroupDefinition");
        PreparedStatement prefixGroupStmt = null;
        ResultSet rsPrefixGroups = null;
        PrefixGroupDefinitionData data = null;

        List<PrefixGroupDefinitionData> prefixGroupList = new ArrayList<PrefixGroupDefinitionData>();

        try {
            prefixGroupStmt = super.getTransactionContext().prepareStatement(GET_ALL_PREFIX_GROUP_DEFINITIONS);
            rsPrefixGroups = prefixGroupStmt.executeQuery();

            while (rsPrefixGroups.next()) {
                data = new PrefixGroupDefinitionData();
                data.setPrefixGroupName(rsPrefixGroups.getString("PREFIXGROUPNAME"));
                data.setPrefixGroupNumber(rsPrefixGroups.getLong("PREFIXGROUPNUMBER"));

                prefixGroupList.add(data);
            }
        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().error(MODULE, "Exception caught while reading Prefix Group Definition." + e.getMessage());
            throw e;
        } finally {
            try {
                if (rsPrefixGroups != null) {
                    rsPrefixGroups.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing resultset." + e.getMessage());
                throw e;
            }
            try {
                if (prefixGroupStmt != null) {
                    super.getTransactionContext().closePreparedStatement(prefixGroupStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getAllPrefixGroupDefinition with prefixGroupList = " + prefixGroupList);
        return prefixGroupList;
    }

    /**
     * Returns the PrefixGroupDefinitionData based on PrefixGroup Name
     * @param prefixGroupName
     * @return PrefixGroupDefinitionData
     */
    public PrefixGroupDefinitionData getPrefixGroupDefinitionByName(String prefixGroupName) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getPrefixGroupDefinitionByName with prefixGroupName=" + prefixGroupName);

        PreparedStatement prefixGroupStmt = null;
        ResultSet rsPrefixGroup = null;

        PrefixGroupDefinitionData prefixGroupData = null;

        try {
            // Creates statement and sets the prefixGroupName as parameter
            prefixGroupStmt = super.getTransactionContext().prepareStatement(GET_PREFIX_GROUP_DEFINITION_BY_NAME);
            prefixGroupStmt.setString(1, prefixGroupName);

            rsPrefixGroup = prefixGroupStmt.executeQuery();

            while (rsPrefixGroup.next()) {
                prefixGroupData = new PrefixGroupDefinitionData();
                prefixGroupData.setPrefixGroupName(rsPrefixGroup.getString("PREFIXGROUPNAME"));
                prefixGroupData.setPrefixGroupNumber(rsPrefixGroup.getLong("PREFIXGROUPNUMBER"));
            }
        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().error(MODULE, "Exception caught while reading Prefix Group Definition." + e.getMessage());
            throw e;
        } finally {
            try {
                if (rsPrefixGroup != null) {
                    rsPrefixGroup.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing resultset." + e.getMessage());
                throw e;
            }
            try {
                if (prefixGroupStmt != null) {
                    super.getTransactionContext().closePreparedStatement(
                            prefixGroupStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getPrefixGroupDefinitionByName with prefixGroupData = " + prefixGroupData);
        return prefixGroupData;

    }

    /**
     * Returns the PrefixGroupDefinitionData based on PrefixGroupNumber.
     * @param prefixGroupNumber
     * @return
     */
    public PrefixGroupDefinitionData getPrefixGroupDefinitionByNumber(int prefixGroupNumber) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getPrefixGroupDefinitionByNumber with prefixGroupNumber=" + prefixGroupNumber);
        PreparedStatement prefixGroupStmt = null;
        ResultSet rsPrefixGroup = null;

        PrefixGroupDefinitionData prefixGroupData = null;

        try {
            prefixGroupStmt = super.getTransactionContext().prepareStatement(GET_PREFIX_GROUP_DEFINITION_BY_NUMBER);
            prefixGroupStmt.setInt(1, prefixGroupNumber);

            rsPrefixGroup = prefixGroupStmt.executeQuery();

            while (rsPrefixGroup.next()) {
                prefixGroupData = new PrefixGroupDefinitionData();
                prefixGroupData.setPrefixGroupName(rsPrefixGroup.getString("PREFIXGROUPNAME"));
                prefixGroupData.setPrefixGroupNumber(rsPrefixGroup.getLong("PREFIXGROUPNUMBER"));
            }
        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().error(MODULE, "Exception caught while reading Prefix Group Definition." + e.getMessage());
            throw e;

        } finally {
            try {
                if (rsPrefixGroup != null) {
                    rsPrefixGroup.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing resultset." + e.getMessage());
                throw e;
            }
            try {
                if (prefixGroupStmt != null) {
                    super.getTransactionContext().closePreparedStatement(prefixGroupStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getPrefixGroupDefinitionByNumber with prefixGroupData = " + prefixGroupData);
        return prefixGroupData;
    }

    /**
     * Returns the PrefixGroupDefinitionData based on PrefixGroupNumber.
     * @param prefixGroupNumber
     * @return
     */
    public PrefixGroupDefinitionData getPrefixGroupDefinitionByNumber(String prefixGroupString) throws SQLException {
        LogManager.getLogger().info(MODULE, "Into getPrefixGroupDefinitionByNumber with prefixGroupString=" + prefixGroupString);
        PreparedStatement prefixGroupStmt = null;
        ResultSet rsPrefixGroup = null;
        PrefixGroupDefinitionData prefixGroupData = null;
        List<PrefixGroupDefinitionData> prefixGroupList = null;

        String GET_PREFIX_GROUP_DEFINITION_BY_ORDER = "SELECT * FROM PREFIXGROUPDEFINITION Order By PREFIXGROUPNUMBER DESC "; //WHERE PREFIXGROUPNUMBER >= ?
        try {
            prefixGroupStmt = super.getTransactionContext().prepareStatement(GET_PREFIX_GROUP_DEFINITION_BY_ORDER);

            rsPrefixGroup = prefixGroupStmt.executeQuery();
            while (rsPrefixGroup.next()) {

                if (prefixGroupList == null) {
                    prefixGroupList = new ArrayList<PrefixGroupDefinitionData>();
                }
                PrefixGroupDefinitionData  tmpPrefixGroupData = new PrefixGroupDefinitionData();
                tmpPrefixGroupData.setPrefixGroupName(rsPrefixGroup.getString("PREFIXGROUPNAME"));
                tmpPrefixGroupData.setPrefixGroupNumber(rsPrefixGroup.getLong("PREFIXGROUPNUMBER"));
                prefixGroupList.add(tmpPrefixGroupData);
            }

        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().error(MODULE, "Exception caught while reading Prefix Group Definition." + e.getMessage());
            throw e;
        } finally {
            try {
                if (rsPrefixGroup != null) {
                    rsPrefixGroup.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing resultset." + e.getMessage());
                throw e;
            }
            try {
                if (prefixGroupStmt != null) {
                    super.getTransactionContext().closePreparedStatement(prefixGroupStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }

        boolean found = false;
        if (prefixGroupList != null) {
            Iterator<PrefixGroupDefinitionData> iterator = prefixGroupList.iterator();
            while (iterator.hasNext() && !found) {
            	PrefixGroupDefinitionData tmpPrefixGroupData = (PrefixGroupDefinitionData) iterator.next();
                for (int i = prefixGroupString.length(); i > 0; i--) {
                    long prefixNumber = Long.parseLong(prefixGroupString.substring(0, i));
                    LogManager.getLogger().trace(MODULE, "Matching prefixNumber= " + prefixNumber + " with prefixData.getPrefixNumber()= " + tmpPrefixGroupData.getPrefixGroupNumber());
                    if (tmpPrefixGroupData.getPrefixGroupNumber() == prefixNumber) {
                        found = true;
                        prefixGroupData = tmpPrefixGroupData;
                    }
                }
            }
        }
        LogManager.getLogger().info(MODULE, "Out of getPrefixGroupDefinitionByNumber with found = " + found + " and prefixGroupData = " + prefixGroupData);
        return prefixGroupData;
    }
}
