package cz.cvut.fit.miswe.rdfapp.model;

public class ParkingMachine {
    String uri;
    String objectId;
    String poskyt;
    String containedInPlace;
    String address;
    String branchCode;

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
}
