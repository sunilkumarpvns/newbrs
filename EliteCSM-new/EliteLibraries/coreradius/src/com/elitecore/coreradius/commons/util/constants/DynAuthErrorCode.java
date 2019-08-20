package com.elitecore.coreradius.commons.util.constants;

public enum DynAuthErrorCode {
	
	ResidualSessionContextRemoved(201),
	InvalidEAPPacket(202),
	UnsupporteAttribute(401),
	MissingAttribute(402),
	NASIdentificationMismatch(403),
	InvalidRequest(404),
	UnsupportedService(405),
	UnsupportedExtension(406),
	InvalidAttributeValue(407),
	AdministrativelyProhibited(501),
	RequestNotRoutable(502),
	SessionContextNotFound(503),
	SessionContextNotRemovable(504),
	OtherProxyProcessingError(505),
	ResourcesUnavailable(506),
	RequestInitiated(507),
	MultipleSessionSelectionUnsupported(508);

	public final int value;
	DynAuthErrorCode (int value){
		this.value = value;
	}
}
