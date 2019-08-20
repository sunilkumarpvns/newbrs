package com.elitecore.netvertexsm.ws.cxfws.session.data;

import com.elitecore.commons.io.IndentingWriter;

import java.util.List;



/**
 * @author kirpalsinh.raj
 *
 */
public class SessionData{
	
	private List<Entry> entry;
	
	public SessionData() { }
	
	public SessionData(List<Entry> entry){
		this.setEntry(entry);		
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}
	
	public void toString(IndentingWriter out) {

		out.incrementIndentation();
		out.println();
		for(Entry en : entry){
			out.print(en.getKey());
			out.print(" : ");
			out.println(en.getValue());
		}		
		out.decrementIndentation();
	}
	
}