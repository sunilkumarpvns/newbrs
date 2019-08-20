package com.elitecore.corenetvertex.pkg.ratinggroup;

import com.elitecore.commons.base.Collectionz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Ishani on 6/10/16.
 */
@XmlRootElement(name="rating-group-container")
public class RatingGroupContainer {

    private List<RatingGroupDataExt> ratingGroup;
    public RatingGroupContainer(){ this.ratingGroup = Collectionz.newArrayList();}

    @XmlElementWrapper(name="ratingGroups")
    @XmlElement(name="ratingGroup")
    public List<RatingGroupDataExt> getRatingGroup() {
        return ratingGroup;
    }

    public void setRatingGroup(List<RatingGroupDataExt> ratingGroup) {
        this.ratingGroup = ratingGroup;
    }
}
