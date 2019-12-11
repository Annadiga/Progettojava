package com.digarcisi.progettoesame.controller;

import com.digarcisi.progettoesame.service.DayCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
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

    private static Map<String, Object> filtroParsato(String body) {
        Map<String, Object> bodyParsato = new BasicJsonParser().parseMap(body);
        String nomeCampo = bodyParsato.keySet().toArray(new String[0])[0];
        Object value = bodyParsato.get(nomeCampo);
        Object riferimento;
        String op;
        if (value instanceof Map) {
            Map filtro = (Map) value;
            op = ((String) filtro.keySet().toArray()[0]).toLowerCase();
            riferimento = filtro.get(op);
        } else {
            op = "$not";
            riferimento = value;
        }
        Map<String, Object> filtro = new HashMap<>();
        filtro.put("operatore", op);
        filtro.put("campo", nomeCampo);
        filtro.put("riferimento", riferimento);
        return filtro;
    }
}
