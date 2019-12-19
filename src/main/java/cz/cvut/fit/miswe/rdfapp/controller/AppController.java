package cz.cvut.fit.miswe.rdfapp.controller;

import cz.cvut.fit.miswe.rdfapp.service.SparqlQueryService;
import org.apache.jena.graph.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {

    private SparqlQueryService sparqlQueryService;

    @GetMapping(path = "/parkingMachines")
    public ModelAndView getParkingMachines() {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> parkingMachines = sparqlQueryService.getParkingMachines();
        parkingMachines.forEach((key, value) -> {
            value = value.substring(0, value.indexOf('^'));
            parkingMachines.put(key, value);
        });
        modelAndView.addObject("records", parkingMachines);
        modelAndView.setViewName("parking-machines");
        return modelAndView;
    }

    @GetMapping(path = "/parkingMachines/{objectId}")
    public ModelAndView getParkingMachines(@PathVariable String objectId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("record", sparqlQueryService.getParkingMachine(objectId));
        modelAndView.setViewName("parking-machine-detail");
        return modelAndView;
    }

    @GetMapping(path = "/first")
    public ModelAndView first() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("records", sparqlQueryService.first());
        modelAndView.setViewName("first");
        return modelAndView;
    }

    @GetMapping(path = "/second")
    public ModelAndView second() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("records", sparqlQueryService.second());
        modelAndView.setViewName("second");
        return modelAndView;
    }

    @GetMapping(path = "/third")
    public ModelAndView third() {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, List<Triple>> third = sparqlQueryService.third();
        List<String> result = new ArrayList<>();
        third.forEach((key, triples) -> {
            StringBuilder subResult = new StringBuilder("Entity: " + key);
            boolean hadAddress = false;
            for (Triple triple : triples) {
                String subject = triple.getObject().toString();
                switch (triple.getPredicate().toString()) {
                    case "http://www.w3.org/1999/02/22-rdf-syntax-ns#type":
                        subResult.append(" is a ").append(subject.substring(subject.lastIndexOf("/") + 1)).append(".");
                        break;
                    case "http://schema.org/address":
                        subject = subject.substring(0, subject.lastIndexOf('@'));
                        if (hadAddress) {
                            subResult.append(", ").append(subject);
                        } else {
                            subResult.append(" It is situated on this address: ").append(subject);
                            hadAddress = true;
                        }
                        break;
                    case "http://www.kyrylo.bulat.com/resource/cena":
                        subject = subject.substring(0, subject.lastIndexOf('@'));
                        subResult.append(", it's price is ").append(subject).append(".");
                        break;
                    case "http://www.kyrylo.bulat.com/resource/objectid":
                        subject = subject.substring(0, subject.indexOf("^"));
                        subResult.append(" Internal object id of this instance is: ").append(subject).append(".");
                        break;
                    case "http://www.kyrylo.bulat.com/resource/openingHours":
                        subject = subject.substring(0, subject.indexOf("@"));
                        subResult.append(" Days and hours of work of this instance are ").append(subject).append(".");
                        break;
                }
            }
            result.add(subResult.toString());
        });
        modelAndView.addObject("records", result);
        modelAndView.setViewName("third");
        return modelAndView;
    }


    @Autowired
    public void setSparqlQueryService(SparqlQueryService sparqlQueryService) {
        this.sparqlQueryService = sparqlQueryService;
    }
}
