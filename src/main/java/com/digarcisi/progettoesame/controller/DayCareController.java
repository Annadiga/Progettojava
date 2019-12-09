package com.digarcisi.progettoesame.controller;

import com.digarcisi.progettoesame.service.DayCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List getMetedata() {
        return service.getMetadata();
    }
}
