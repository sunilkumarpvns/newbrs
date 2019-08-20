package com.elitecore.netvertexsm.util.opts;

import java.util.ArrayList;
import java.util.List;

public class OptionRepository {
	
	private List<Option> qciOptionList;
	private static OptionRepository optionRepository;
	
	private OptionRepository(){
		
	}
	
	public static OptionRepository getInstance(){
		if( optionRepository == null){
			optionRepository = new  OptionRepository();
		}
		return  optionRepository;
	}
	
	
	
	public List<Option> getQCIOptionList(){
		if(qciOptionList==null){
			qciOptionList = new ArrayList<Option>();
			qciOptionList.add(new Option("1 - GBR- Conversational Voice","1"));
			qciOptionList.add(new Option("2 - GBR - Conversational Video (Live Streaming) ","2"));
			qciOptionList.add(new Option("3 - GBR - Real Time Gaming","3"));
			qciOptionList.add(new Option("4 - GBR - Non-Conversational Video ( Buffered )","4"));
			qciOptionList.add(new Option("5 - Non-GBR - IMS Signalling","5"));
			qciOptionList.add(new Option("6 - Non-GBR - Video, e-mail, chat, p2p, ftp etc.( Live )","6"));
			qciOptionList.add(new Option("7 - Non-GBR - Voice, Video , Interactive Gaming","7"));
			qciOptionList.add(new Option("8 - Non-GBR - Video, e-mail, chat, p2p, ftp etc. ( Buffered )","8"));
			qciOptionList.add(new Option("9 - Non-GBR - Video, e-mail, chat, p2p, ftp etc. ( Buffered )(low)","9"));
			
		}
		return qciOptionList;
	}

}
