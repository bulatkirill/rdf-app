package cz.cvut.fit.miswe.rdfapp.model;

public class TridOdpad {

    String uri;
    String objectId;
    String stationId;
    String trashTypeName;
    String cleaningFrequencyCode;
    String containerType;

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
}
