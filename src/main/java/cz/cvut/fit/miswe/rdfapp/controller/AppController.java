package cz.cvut.fit.miswe.rdfapp.controller;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AppController {

    @GetMapping(path = "/parkingMachines")
    public ModelAndView getParkingMachines() {
        ModelAndView modelAndView = new ModelAndView();
        List<String> result = new ArrayList<>();
        result.add("lol");
        modelAndView.addObject("records", result);
        modelAndView.setViewName("parking-machines");

        Query query = QueryFactory.create("SELECT ?s WHERE {?s a <http://schema.org/PublicToilet>}");
        try (QueryExecution queryExecution = QueryExecutionFactory.
                createServiceRequest("http://localhost:3030/all1/sparql", query)) {
            ResultSet results = queryExecution.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                RDFNode x = solution.get("s");
                result.add(x.toString());
            }
        }


        return modelAndView;
    }

}
