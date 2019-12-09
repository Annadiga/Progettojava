package com.digarcisi.progettoesame.controller;

import com.digarcisi.progettoesame.service.DayCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;
import java.util.Map;

@RestController
public class DayCareController {
    private DayCareService service;

    @Autowired
    public DayCareController(DayCareService service) {
        this.service = service;
    }

    @GetMapping("/dataset")
    public List getDataset() {
        return service.getDataset();
    }

    @GetMapping("/metadata")
    public List getMetadata() {
        return service.getMetadata();
    }

    @GetMapping("/stats")
    public Map getStats(@RequestParam(value = "campo", required = true) String nomeCampo) {
        return service.getStats(nomeCampo);
    }
}
