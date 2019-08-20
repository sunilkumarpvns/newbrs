package com.elitecore.nvsmx.sm.controller.database;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.HttpHeaders;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by dhyani on 22/8/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/database")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","database"}),

})
public class DatabaseCTRL extends RestGenericCTRL<DatabaseData> {

    @Override
    public ACLModules getModule() {
        return ACLModules.DATABASE;
    }

    @Override
    public DatabaseData createModel() {
        return new DatabaseData();
    }

    @Override
    public HttpHeaders create() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called create()");
        }

        DatabaseData databaseData = (DatabaseData) getModel();

        try {
            databaseData.setPassword(PasswordUtility.getEncryptedPassword(databaseData.getPassword()));
        } catch (NoSuchEncryptionException | EncryptionFailedException e) {
            getLogger().error(getLogModule(),"Error while encrypt password for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while encrypt password "+ getModule().getDisplayLabel());
        }

        return super.create();
    }

    @Override
    public HttpHeaders update() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called create()");
        }

        DatabaseData databaseData = (DatabaseData) getModel();
        DatabaseData tempDatabaseData = CRUDOperationUtil.get(DatabaseData.class, databaseData.getId());
        if (Objects.isNull(tempDatabaseData)) {
            addActionError(getText("database.datasource.does.not.exist"));
        }else{
            databaseData.setPassword(tempDatabaseData.getPassword()); //required to set for rest services
        }

        return super.update();
    }

    @Override
    public void validate() {

        DatabaseData databaseData = (DatabaseData) getModel();
        String methodName = getMethodName();
        if(CRUDOperationUtil.MODE_CREATE.equalsIgnoreCase(methodName)){
            //PASSWORD IS REQUIRED AT CREATION TIME
            if (Strings.isNullOrBlank(databaseData.getPassword())) {
                addFieldError("password", getText("error.valueRequired"));
            }
        }

        if(databaseData.getMinimumPool() > databaseData.getMaximumPool()) {
            addFieldError("minimumPool", getText("database.min.max.pool.value.invalid"));
        }
        super.validate();
    }


}