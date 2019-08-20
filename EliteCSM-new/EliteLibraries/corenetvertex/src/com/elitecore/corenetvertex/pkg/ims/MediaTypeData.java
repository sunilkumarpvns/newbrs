package com.elitecore.corenetvertex.pkg.ims;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author Dhyani.Raval
 *
 */
@Entity
@Table(name = "TBLM_MEDIA_TYPE")
public class MediaTypeData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private Long mediaIdentifier;
	private String status;

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "STATUS")
	@XmlTransient
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "MEDIA_IDENTIFIER")
	@XmlTransient
	public Long getMediaIdentifier() {
		return mediaIdentifier;
	}

	public void setMediaIdentifier(Long mediaIdentifier) {
		this.mediaIdentifier = mediaIdentifier;
	}

}
