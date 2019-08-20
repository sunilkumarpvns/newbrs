/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author tejasmudgal
 *  Last Modified October 1, 2008
 */
package com.elitecore.classicrating.datamanager.packages;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

/**
 * Provides DB queries for the SlabDefinition Model.
 * 
 * @author tejasmudgal
 * 
 */
public class SlabDefinitionDBHelper extends BaseDBHelper {

    public static final String MODULE = "SLAB_DEFINITION_DB_HELPER";
    private static final String GET_SLAB_DEFINITION_FOR_PACKAGE = "SELECT * FROM SLABDEFINITION WHERE PACKAGEID = ?";

    public SlabDefinitionDBHelper(ITransactionContext transactionContext) {
        super(transactionContext);
    }

    /**
     * Returns the Slab Definition for the given Package.
     *
     * Slab can be exist for Package Type such as
     * 		SLAB-WISE
     * @param packageId
     * @return SlabDefinitionData
     */
    public SlabDefinitionData getSlabDefinitionForPackage(int packageId) throws SQLException {
        LogManager.getLogger().trace(MODULE, "Into getSlabDefinitionForPackage with packageId= " + packageId);
        PreparedStatement slabDefinitionStmt = null;
        ResultSet rsSlabDefinition = null;

        SlabDefinitionData slabDefinitionData = null;

        try {
            slabDefinitionStmt = super.getTransactionContext().prepareStatement(GET_SLAB_DEFINITION_FOR_PACKAGE);
            slabDefinitionStmt.setInt(1, packageId);

            rsSlabDefinition = slabDefinitionStmt.executeQuery();

            if (rsSlabDefinition.next()) {
                slabDefinitionData = new SlabDefinitionData();
                slabDefinitionData.setPackageId(rsSlabDefinition.getInt("PACKAGEID"));
                slabDefinitionData.setUnit1(rsSlabDefinition.getInt("UNIT1"));
                slabDefinitionData.setUnit2(rsSlabDefinition.getInt("UNIT2"));
                slabDefinitionData.setUnit3(rsSlabDefinition.getInt("UNIT3"));
            }
        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            LogManager.getLogger().error(MODULE, "Exception caught while reading Slab Definition." + e.getMessage());
            throw e;
        } finally {
            try {
                if (rsSlabDefinition != null) {
                    rsSlabDefinition.close();
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing resultset." + e.getMessage());
                throw e;
            }
            try {
                if (slabDefinitionStmt != null) {
                    super.getTransactionContext().closePreparedStatement(slabDefinitionStmt);
                }
            } catch (SQLException e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().error(MODULE, "Exception caught while closing prepared statement." + e.getMessage());
                throw e;
            }
        }
        LogManager.getLogger().trace(MODULE, "Out of getSlabDefinitionForPackage with (packageId= " + packageId + ") = (" + slabDefinitionData + ")");
        return slabDefinitionData;

    }
}
