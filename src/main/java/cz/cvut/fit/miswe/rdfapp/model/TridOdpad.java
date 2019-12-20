package cz.cvut.fit.miswe.rdfapp.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class TridOdpad {

    private String uri;
    private String objectId;
    private String stationId;
    private String trashTypeName;
    private String cleaningFrequencyCode;
    private String containerType;
    private Map<String, String> relatedInstances;

    public TridOdpad() {
        relatedInstances = new LinkedHashMap<>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getTrashTypeName() {
        return trashTypeName;
    }

    public void setTrashTypeName(String trashTypeName) {
        this.trashTypeName = trashTypeName;
    }

    public String getCleaningFrequencyCode() {
        return cleaningFrequencyCode;
    }

    public void setCleaningFrequencyCode(String cleaningFrequencyCode) {
        this.cleaningFrequencyCode = cleaningFrequencyCode;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public Map<String, String> getRelatedInstances() {
        return relatedInstances;
    }

    public void setRelatedInstances(Map<String, String> relatedInstances) {
        this.relatedInstances = relatedInstances;
    }
}
