package cz.cvut.fit.miswe.rdfapp.service.impl;

import cz.cvut.fit.miswe.rdfapp.model.ParkingMachine;
import cz.cvut.fit.miswe.rdfapp.service.SparqlQueryService;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SparqlQueryServiceImpl implements SparqlQueryService {

    public static final String WC = "wc";
    public static final String OBJECT_ID = "objectId";
    public static final String PARKING_MACHINES_QUERY = String.format(
            "SELECT ?%s ?%s WHERE {?%s a <http://schema.org/ParkingFacility>; " +
                    "<http://www.kyrylo.bulat.com/resource/objectid> ?%s}" +
                    " ORDER BY ASC(?%s)", WC, OBJECT_ID, WC, OBJECT_ID, OBJECT_ID);
    public static final String PARKING_MACHINE_QUERY =
            "SELECT ?parkingMachine ?objectId ?poskyt ?containedInPlace ?address ?branchCode " +
                    "WHERE {?parkingMachine a <http://schema.org/ParkingFacility>; " +
                    "<http://www.kyrylo.bulat.com/resource/objectid> %s;" +
                    "<http://www.kyrylo.bulat.com/resource/objectid> ?objectId;" +
                    "<http://www.kyrylo.bulat.com/resource/poskyt> ?poskyt;" +
                    "<http://schema.org/containedInPlace> ?containedInPlace;" +
                    "<http://schema.org/address> ?address;" +
                    "<http://schema.org/branchCode> ?branchCode." +
                    "}";

    public static final String SPARQL_API = "http://localhost:3030/all1/sparql";

    private static final String FIRST = "SELECT\n" +
            "?trashtypename (if(count(?tridodpad) > 4000, \"More than 4000\", count(?tridodpad)) as ?count)\n" +
            "WHERE {\n" +
            "  ?tridodpad a schema:Place;\n" +
            "        \tkbr:objectid ?objectId;\n" +
            "         \tkbr:trashtypename ?trashtypename;\n" +
            "  \t\t\tkbr:containertype ?containertype.\n" +
            "\n" +
            "  ?trashtypename a skos:Concept;\n" +
            "  \t\t\t\t skos:notation ?notation.\n" +
            "\n" +
            "}\n" +
            "GROUP BY ?trashtypename\n" +
            "HAVING(count(?tridodpad) > 1500)\n" +
            "ORDER BY DESC(count(?tridodpad))\n";

    private final Map<String, String> prefixes;

    {
        prefixes = new LinkedHashMap<>();
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("schema", "http://schema.org/");
        prefixes.put("kbr", "http://www.kyrylo.bulat.com/resource/");
        prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        prefixes.put("kbrwcp", "http://www.kyrylo.bulat.com/resource/wcprice/");
        prefixes.put("skos", "http://www.w3.org/2004/02/skos/core#");
    }

    @Override
    public Map<String, String> getParkingMachines() {
        Map<String, String> result = new LinkedHashMap<>();
        Query query = QueryFactory.create(PARKING_MACHINES_QUERY);
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                RDFNode node = solution.get(WC);
                RDFNode objectId = solution.get(OBJECT_ID);
                result.put(node.toString(), objectId.toString());
            }
        }
        return result;
    }

    @Override
    public ParkingMachine getParkingMachine(String objectId) {
        ParkingMachine result = new ParkingMachine();
        Query query = QueryFactory.create(String.format(PARKING_MACHINE_QUERY, objectId));
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                result.setUri(solution.get("parkingMachine").toString());
                result.setObjectId(solution.get("objectId").toString());
                result.setPoskyt(solution.get("poskyt").toString());
                result.setContainedInPlace(solution.get("containedInPlace").toString());
                result.setAddress(solution.get("address").toString());
                result.setBranchCode(solution.get("branchCode").toString());
            }
        }
        return result;
    }

    @Override
    public Map<String, String> first() {
        Map<String, String> result = new LinkedHashMap<>();
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(FIRST);
        pss.setNsPrefixes(prefixes);
        Query query = pss.asQuery();
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                result.put(solution.get("trashtypename").toString(), solution.get("count").toString());
            }
        }
        return result;
    }

    @Override
    public void second() {

    }

    @Override
    public void third() {

    }


    @Override
    public Map<RDFNode, RDFNode> getPublicToilets() {
        return null;
    }

    @Override
    public Map<RDFNode, RDFNode> getWasteBins() {
        return null;
    }
}
