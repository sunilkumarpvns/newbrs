package com.elitecore.corenetvertex.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * DBTypes used for Query generation.
 */
public enum DBTypes {

    NUMERIC(Types.NUMERIC, "NUMERIC", 18) {
	@Override
	public void setVal(StringBuilder builder, String val) {
	    builder.append(val);
	}
    },
    VARCHAR(Types.VARCHAR, "VARCHAR", 200) {
	@Override
	public void setVal(StringBuilder builder, String val) {
	    builder.append("'").append(val).append("'");
	}
    },
    TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP", 0) {
	@Override
	public void setVal(StringBuilder builder, String val) {
	    builder.append("'").append(val).append("'");
	}
    },
    CHAR(Types.CHAR, "CHAR", 1) {
	@Override
	public void setVal(StringBuilder builder, String val) {
	    builder.append("'").append(val).append("'");
	}
    };

    public int id;
    public String name;
    public int size;

    private static final Map<Integer, DBTypes> map;

    public static final DBTypes[] VALUES = values();

    static {
	map = new HashMap<Integer, DBTypes>(VALUES.length);
	for (DBTypes type : VALUES) {
	    map.put(type.id, type);
	}
    }

    DBTypes(int id, String name, int size) {
	this.id = id;
	this.name = name;
	this.size = size;
    }

    public static DBTypes fromId(int id) {
	return map.get(id);
    }
    
    /** wraps value with single quote whenever necessary*/
    public abstract void setVal(StringBuilder builder, String val);
}
