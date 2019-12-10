package com.digarcisi.progettoesame.service.filters;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DayCareFilters {
    private static final List<String> opFiltro = Arrays.asList("$not", "$in", "$nin", "$gt", "$lt", "$bt");

    public static boolean sceltaFiltro(Object valore, String op, Object riferimento) {
        if (opFiltro.contains(op)) {
            if (valore instanceof Number) {
                double dValore = ((Number) valore).doubleValue();
                if (riferimento instanceof Number) {
                    double dRiferimento = ((Number) riferimento).doubleValue();
                    switch (op) {
                        case "$not":
                            return dValore != dRiferimento;
                        case "$gt":
                            return dValore > dRiferimento;
                        case "$lt":
                            return dValore < dRiferimento;
                        default:
                            String stampa = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampa);
                    }
                } else if (riferimento instanceof List) {
                    List lRiferimento = ((List) riferimento);
                    if (!lRiferimento.isEmpty() && lRiferimento.get(0) instanceof Number) {
                        List<Double> lDRiferimento = new ArrayList<>();
                        for (Object elem : lRiferimento) {
                            lDRiferimento.add(((Number) elem).doubleValue());
                        }
                        switch (op) {
                            case "$in":
                                return lDRiferimento.contains(dValore);
                            case "$nin":
                                return lDRiferimento.contains(dValore);
                            case "$bt":
                                double estremoInf = lDRiferimento.get(0);
                                double estremoSup = lDRiferimento.get(1);
                                return dValore <= estremoSup && dValore >= estremoInf;
                            default:
                                String stampa = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampa);
                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista di riferimenti è vuota o contiene elementi non validi");
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
            }else if(valore instanceof String){

            }

        }


    }

}
