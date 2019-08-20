package com.elitecore.exprlib.scanner.impl;

import com.elitecore.exprlib.scanner.Symbol;

//import com.elitecore.exprlib.scanner.Symbol;

public abstract class SymbolImpl implements Symbol {
	
	private static final long serialVersionUID = 1L;
	public String toString() {
		return "of Symbol type";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + getType();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
	
		if(obj == null){
			return false;
		}
		
		if (getClass() != obj.getClass())
			return false;
		
		Symbol other = (Symbol) obj;		
		if(other.getType() != getType()){
			return false;
		}
		
		if (getName() == null) {
			if (other.getName() != null){
				return false;
			}
		} else if (getName().equals(other.getName()) == false){
			return false;
		}
		
		return true;
	}
	
	
	
}
