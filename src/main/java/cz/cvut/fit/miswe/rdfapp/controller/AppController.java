package cz.cvut.fit.miswe.rdfapp.controller;

import cz.cvut.fit.miswe.rdfapp.service.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

    private SparqlQueryService sparqlQueryService;

    @GetMapping(path = "/parkingMachines")
    public ModelAndView getParkingMachines() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("records", sparqlQueryService.getParkingMachines());
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
        modelAndView.addObject("records", sparqlQueryService.third());
        modelAndView.setViewName("third");
        return modelAndView;
    }


    @Autowired
    public void setSparqlQueryService(SparqlQueryService sparqlQueryService) {
        this.sparqlQueryService = sparqlQueryService;
    }
}
