package com.elitecore.netvertex.pm;

public interface QoSSelectionAcrossGroupStrategy {
	void select(FinalQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData);
}
