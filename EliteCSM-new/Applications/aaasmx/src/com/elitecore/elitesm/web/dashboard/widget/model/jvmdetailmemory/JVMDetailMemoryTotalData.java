package com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory;

public class JVMDetailMemoryTotalData {

    private Long time;
	private Long psEdenSpace;
	private Long psSurvivorSpace;
	private Long psOldGenSpace;
	private Long psPermGenSpace;
	private String instanceName;
	
	public JVMDetailMemoryTotalData(Long time, String instanceName) {
		this.time = time;
		this.instanceName = instanceName;
	}
	
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}


	/**
	 * @return the psEdenSpace
	 */
	public Long getPsEdenSpace() {
		return psEdenSpace;
	}


	/**
	 * @return the psSurvivorSpace
	 */
	public Long getPsSurvivorSpace() {
		return psSurvivorSpace;
	}


	/**
	 * @return the psOldGen
	 */
	public Long getPsOldGenSpace() {
		return psOldGenSpace;
	}


	/**
	 * @return the psPermGen
	 */
	public Long getPsPermGenSpace() {
		return psPermGenSpace;
	}


	/**
	 * @return the instanceName
	 */
	public String getInstanceName() {
		return instanceName;
	}


	

	
	public static class JVMDetailMemoryBuilder{
		
		private JVMDetailMemoryTotalData jvmDetailmemoryData;
		
		public JVMDetailMemoryBuilder(long time, String name) {
			jvmDetailmemoryData = new JVMDetailMemoryTotalData(time,name);
		}
		
		public JVMDetailMemoryTotalData build(){
			return jvmDetailmemoryData;
		}
		
		public JVMDetailMemoryBuilder withEdenSpace(long psEdenSpace) {
			jvmDetailmemoryData.psEdenSpace=psEdenSpace;
			return this;
		}

		public JVMDetailMemoryBuilder withSurvivorSpace(long psSurvivorSpace) {
			jvmDetailmemoryData.psSurvivorSpace = psSurvivorSpace;
			return this;
		}

		public JVMDetailMemoryBuilder withPermGen(long psPermGenSpace) {
		jvmDetailmemoryData.psPermGenSpace = psPermGenSpace;
			return this;
		}
       
		public JVMDetailMemoryBuilder withOldGen(long psOldGenSpace) {
			jvmDetailmemoryData.psOldGenSpace = psOldGenSpace;
				return this;
			}
	}
}
