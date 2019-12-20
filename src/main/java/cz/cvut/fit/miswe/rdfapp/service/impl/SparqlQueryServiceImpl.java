package cz.cvut.fit.miswe.rdfapp.service.impl;

import cz.cvut.fit.miswe.rdfapp.model.ParkingMachine;
import cz.cvut.fit.miswe.rdfapp.model.TridOdpad;
import cz.cvut.fit.miswe.rdfapp.service.SparqlQueryService;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SparqlQueryServiceImpl implements SparqlQueryService {

    public static final String RECORDS_QUERY =
            "SELECT ?uri ?objectId WHERE {?uri a <http://schema.org/%s>; " +
                    "<http://www.kyrylo.bulat.com/resource/objectid> ?objectId}" +
                    " ORDER BY ASC(?objectId)";

    public static final String PARKING_MACHINE_QUERY =
            "SELECT distinct ?parkingMachine ?objectId ?poskyt ?containedInPlace ?address ?branchCode ?relatedTo\n" +
                    "WHERE\n" +
                    "  { ?parkingMachine\n" +
                    "              a                     <http://schema.org/ParkingFacility> ;\n" +
                    "              <http://www.kyrylo.bulat.com/resource/objectid>  %s ;\n" +
                    "              <http://www.kyrylo.bulat.com/resource/objectid>  ?objectId ;\n" +
                    "              <http://www.kyrylo.bulat.com/resource/poskyt>  ?poskytovatel ;\n" +
                    "              <http://schema.org/containedInPlace>  ?containedInPlaceHelp ;\n" +
                    "              <http://schema.org/address>  ?address ;\n" +
                    "              <http://schema.org/branchCode>  ?branchCode .\n" +
                    "    ?poskytovatel\n" +
                    "              a                     <http://schema.org/Organization> ;\n" +
                    "              <http://schema.org/identifier>  ?poskyt .\n" +
                    "    OPTIONAL {?parkingMachine\n" +
                    "              <http://www.w3.org/2004/02/skos/core#related> ?relatedTo. }\n" +
                    "    ?containedInPlaceHelp\n" +
                    "              a                     <http://www.w3.org/2004/02/skos/core#Concept> ;\n" +
                    "              <http://www.w3.org/2004/02/skos/core#notation>  ?containedInPlace\n" +
                    "  }";

    public static final String TRID_ODPAD_QUERY =
            "SELECT ?uri ?objectId ?stationId ?trashTypeName ?celaningfrequencycode ?containerType " +
                    "WHERE {?uri a <http://schema.org/Place>; " +
                    "<http://www.kyrylo.bulat.com/resource/objectid> %s;" +
                    "<http://www.kyrylo.bulat.com/resource/objectid> ?objectId;" +
                    "<http://www.kyrylo.bulat.com/resource/stationid> ?stationId;" +
                    "<http://www.kyrylo.bulat.com/resource/trashtypename> ?trashTypeNameHelp;" +
                    "<http://www.kyrylo.bulat.com/resource/celaningfrequencycode> ?celaningfrequencycode;" +
                    "<http://www.kyrylo.bulat.com/resource/containertype> ?containerTypeHelp." +
                    "" +
                    " ?trashTypeNameHelp a <http://www.w3.org/2004/02/skos/core#Concept>;" +
                    " <http://www.w3.org/2004/02/skos/core#notation> ?trashTypeName." +

                    " ?containerTypeHelp a <http://www.w3.org/2004/02/skos/core#Concept>;" +
                    " <http://www.w3.org/2004/02/skos/core#notation> ?containerType." +
                    "}";

    public static final String SPARQL_API = "http://localhost:3030/all2/sparql";

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

    private static final String SECOND = "SELECT distinct ?objectId ?stationid\n" +
            "WHERE {\n" +
            "  ?tridodpad a schema:Place;\n" +
            "        \tkbr:objectid ?objectId;\n" +
            "      \t\tkbr:stationid ?stationid;\t\n" +
            "  .\n" +
            "  FILTER(?stationid > 5000).\n" +
            "  \n" +
            "  ?parkomaty a schema:ParkingFacility;\n" +
            "            skos:related ?tridodpad.\n" +
            "}\n" +
            "LIMIT 50\n";

    private static final String THIRD = "CONSTRUCT {\n" +
            "\t?wc a schema:PublicToilet;\n" +
            "\t\tkbr:objectid ?objectId;\n" +
            "    \tschema:address ?address, ?parkomatAddress;\n" +
            "      \tkbr:openingHours ?openingHours ;\n" +
            "   \t\tkbr:cena ?price.\n" +
            "}\n" +
            "WHERE {\n" +
            "  ?wc a schema:PublicToilet;\n" +
            "        \tkbr:objectid ?objectId;\n" +
            "\t      \tkbr:openingHours ?openingHours ;\n" +
            "   \t\t\tkbr:cena ?priceEntity.  \n" +
            "  \n" +
            "  \t?parkomaty a schema:ParkingFacility;\n" +
            "              skos:related ?wc;\n" +
            "              schema:address ?parkomatAddress.\n" +
            "  \t?priceEntity a skos:Concept ;\n" +
            "  \t\tskos:notation ?price .\n" +
            "  OPTIONAL {\n" +
            "\t?wc  schema:address ?address.\n" +
            "  }\n" +
            "}\n";

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
        return getListCommon("ParkingFacility");
    }

    @Override
    public Map<String, String> getTridOdpads() {
        return getListCommon("Place");
    }

    private Map<String, String> getListCommon(String entityName) {
        Map<String, String> result = new LinkedHashMap<>();
        Query query = QueryFactory.create(String.format(RECORDS_QUERY, entityName));
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                RDFNode node = solution.get("uri");
                RDFNode objectId = solution.get("objectId");
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
                RDFNode relatedTo = solution.get("relatedTo");
                if (relatedTo != null) {
                    result.getRelatedInstances().add(relatedTo.toString());
                }
            }
        }
        return result;
    }

    @Override
    public TridOdpad getTridOdpad(String objectId) {
        TridOdpad result = new TridOdpad();
        Query query = QueryFactory.create(String.format(TRID_ODPAD_QUERY, objectId));
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                result.setUri(solution.get("uri").toString());
                result.setObjectId(solution.get("objectId").toString());
                result.setStationId(solution.get("stationId").toString());
                result.setTrashTypeName(solution.get("trashTypeName").toString());
                result.setCleaningFrequencyCode(solution.get("celaningfrequencycode").toString());
                result.setContainerType(solution.get("containerType").toString());
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
    public Map<String, String> second() {
        Map<String, String> result = new LinkedHashMap<>();
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(SECOND);
        pss.setNsPrefixes(prefixes);
        Query query = pss.asQuery();
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                result.put(solution.get("objectId").toString(), solution.get("stationid").toString());
            }
        }
        return result;
    }

    @Override
    public Map<String, List<Triple>> third() {
        Map<String, List<Triple>> result = new LinkedHashMap<>();
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(THIRD);
        pss.setNsPrefixes(prefixes);
        Query query = QueryFactory.create(pss.toString(), Syntax.syntaxARQ);
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest(SPARQL_API, query)) {
            Iterator<Triple> results = queryExecution.execConstructTriples();
            while (results.hasNext()) {
                Triple solution = results.next();
                String subject = solution.getSubject().toString();
                List<Triple> triples = result.get(subject);
                if (triples != null) {
                    triples.add(solution);
                } else {
                    triples = new ArrayList<>();
                    triples.add(solution);
                    result.put(subject, triples);
                }
            }
        }
        return result;
    }


    @Override
    public Map<RDFNode, RDFNode> getPublicToilets() {
        return null;
    }

}
