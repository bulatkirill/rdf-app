package cz.cvut.fit.miswe.rdfapp.model;

public class Record {
    private int id;
    private String uri;
    private String objectId;

    public Record(int id, String uri, String objectId) {
        this.id = id;
        this.uri = uri;
        this.objectId = objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
