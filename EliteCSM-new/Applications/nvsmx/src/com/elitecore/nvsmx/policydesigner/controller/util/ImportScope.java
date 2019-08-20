package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.config.util.ReflectionUtil;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.validator.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for getting validator and import class for import operation.
 * Created by Ishani on 23/3/17.
 */
public class ImportScope {

    public Map<Class<?>,Validator> validatorClass;
    public Map<Class<?>,ImportOperation> importClass;

    public ImportScope(){
        this.validatorClass =  new HashMap<Class<?>, Validator>();
        this.importClass =  new HashMap<Class<?>, ImportOperation>();
    }

    public Validator getOrCreateValidator(Class<?> classType, Class<?> validatorClassType) throws Exception {
       Validator validator =  validatorClass.get(classType);
        if (validator == null) {
            validator = getValidatorBasedOnType(validatorClassType);
            if (validator != null) {
                validatorClass.put(classType, validator);
            }
        }
        return validator;
    }

    public ImportOperation getOrCreateImportOperation(Class<?> classType, Class<?> importClassType) throws Exception {
        ImportOperation importOperation =  importClass.get(classType);
        if (importOperation == null) {
            importOperation = getImportOperationBasedOnImportClass(importClassType);
            if (importOperation != null) {
                importClass.put(classType, importOperation);
            }
        }
        return importOperation;
    }


    private Validator getValidatorBasedOnType(Class<?> classType) throws Exception {
        if (classType != null) {
            return (Validator) ReflectionUtil.createInstance(classType);

        } else {
            return null;
        }

    }

    private ImportOperation getImportOperationBasedOnImportClass(Class<?> classType) throws Exception {
        if (classType != null) {
            return (ImportOperation) ReflectionUtil.createInstance(classType);

        } else {
            return null;
        }

    }

}
