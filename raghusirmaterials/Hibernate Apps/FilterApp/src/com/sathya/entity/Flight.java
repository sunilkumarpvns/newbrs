package  com.sathya.entity;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

@Entity

@FilterDefs({
	@FilterDef(name="statusFilter1",parameters=@ParamDef(name="flightStatus", type="string")) ,
	@FilterDef(name="statusFilter2",parameters={@ParamDef(name="flightStatus1", type="string"), @ParamDef(name="flightStatus2", type="string")})
			})
@Filters({
	@Filter(name="statusFilter1",
	   condition="status=:flightStatus"),
	
	@Filter(name="statusFilter2",
	   condition="status=:flightStatus1 or  status=:flightStatus2")
	     })

		
public class Flight 
{
	@Id
	private    int     flightno;
	private   String   source;
	private   String   destination;
	private   String   status;
	
	
	public int getFlightno() {
		return flightno;
	}

	public void setFlightno(int flightno) {
		this.flightno = flightno;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public   String   toString()
	{
		return   "Flight["+flightno+"\t"+source+"\t"+destination+"\t"+status+"]";
	}
}
