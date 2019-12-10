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
                            String stampaErrore = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampaErrore);
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
                                String stampaErrore = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampaErrore);
                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista di riferimenti è vuota o contiene elementi non validi");
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il riferimento: " + riferimento + " non è compatibile con il valore: " + valore);
            } else if (valore instanceof String) {
                String sValue = ((String) valore);
                if (riferimento instanceof String) {
                    String sRiferimento = ((String) riferimento);
                    switch (op) {
                        case "$not":
                            return !sValue.equals(sRiferimento);
                        default:
                            String stampaErrore = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampaErrore);
                    }
                } else if (riferimento instanceof List) {
                    List lRiferimento = ((List) riferimento);
                    if (!lRiferimento.isEmpty() && lRiferimento.get(0) instanceof String) {
                        List<String> lSRiferimento = new ArrayList<>();
                        for (Object elem : lRiferimento) {
                            lSRiferimento.add((String) elem);
                        }
                        switch (op) {
                            case "$in":
                                return lSRiferimento.contains(sValue);
                            case "$nin":
                                return lSRiferimento.contains(sValue);
                            default:
                                String stampaErrore = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampaErrore);
                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista di riferimenti è vuota o contiene elementi non validi");
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il riferimento: " + riferimento + " non è compatibile con il valore: " + valore);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il valore: " + valore + "non è valido");

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'opratore: " + op + " non è valido");

    }


}
