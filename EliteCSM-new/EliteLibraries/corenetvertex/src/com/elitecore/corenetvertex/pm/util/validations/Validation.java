package com.elitecore.corenetvertex.pm.util.validations;

import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

@FunctionalInterface
public interface Validation<K> {
    void validate(K t) throws OperationFailedException;

    default Validation<K> and(Validation<K> other) throws OperationFailedException {
        return param -> {
            this.validate(param);
            other.validate(param);
        };
    }
}
