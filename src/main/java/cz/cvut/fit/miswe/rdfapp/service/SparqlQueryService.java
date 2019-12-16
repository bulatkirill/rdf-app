package cz.cvut.fit.miswe.rdfapp.service;

import cz.cvut.fit.miswe.rdfapp.model.ParkingMachine;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;

public interface SparqlQueryService {

    Map<String, String> getParkingMachines();

    ParkingMachine getParkingMachine(String objectId);

    Map<String, String> first();
    Map<String, String> second();
    void third();

    Map<RDFNode, RDFNode> getPublicToilets();

    Map<RDFNode, RDFNode> getWasteBins();

}
