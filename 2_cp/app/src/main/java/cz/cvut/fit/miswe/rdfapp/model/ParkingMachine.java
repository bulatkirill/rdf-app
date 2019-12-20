package cz.cvut.fit.miswe.rdfapp.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParkingMachine {
    private String uri;
    private String objectId;
    private String poskyt;
    private String containedInPlace;
    private String address;
    private String branchCode;
    private Map<String, String> relatedInstances;

    public ParkingMachine() {
        relatedInstances = new LinkedHashMap<>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPoskyt() {
        return poskyt;
    }

    public void setPoskyt(String poskyt) {
        this.poskyt = poskyt;
    }

    public String getContainedInPlace() {
        return containedInPlace;
    }

    public void setContainedInPlace(String containedInPlace) {
        this.containedInPlace = containedInPlace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Map<String, String> getRelatedInstances() {
        return relatedInstances;
    }

    public void setRelatedInstances(Map<String, String> relatedInstances) {
        this.relatedInstances = relatedInstances;
    }
}
