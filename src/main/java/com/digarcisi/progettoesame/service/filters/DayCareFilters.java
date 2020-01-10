package com.digarcisi.progettoesame.service.filters;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import com.digarcisi.progettoesame.service.DayCareService;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Classe astratta DayCareFilters all'interno della quale sono contenuti i metodi per filtrare i campi
 */
public class DayCareFilters {
    //lista operatori
    private static final List<String> opFiltro = Arrays.asList("$not", "$in", "$nin", "$gt", "$lt", "$bt");

    /**
     * viene implementato un metodo per effettuare il confronto tra "valore" e "riferimento" in base
     * all'operatore che viene scelto
     *
     * @param valore      valore su cui effettuare il confronto
     * @param op          operatore
     * @param riferimento valore di riferimento
     * @return un valore booleano
     */
    public static boolean sceltaFiltro(Object valore, String op, Object riferimento) {
        if (opFiltro.contains(op)) {     // si eseguono le istruzioni nell'if se l'operatore è contenuto nella lista opFiltro
            if (valore instanceof Number) {   //se è un valore numerico si entra nell'if
                double dValore = ((Number) valore).doubleValue();  //lo si converte in double
                if (riferimento instanceof Number) {  // se riferimento è un numero si eseguono le istruzioni successive
                    double dRiferimento = ((Number) riferimento).doubleValue(); //conversione in double
                    switch (op) { //confronta op con i vari case
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
                } else if (riferimento instanceof List) {  //se riferimento è una lista
                    List lRiferimento = ((List) riferimento); //conversione in List
                    if (!lRiferimento.isEmpty() && lRiferimento.get(0) instanceof Number) {  /* si controlla che la lista sia diversa
                        dalla lista vuota e se al suo interno vi siano numeri, in questo caso entra nell'if*/
                        List<Double> lDRiferimento = new ArrayList<>();
                        for (Object elem : lRiferimento) { //for-each
                            lDRiferimento.add(((Number) elem).doubleValue());
                        }
                        switch (op) {
                            case "$in":
                                return lDRiferimento.contains(dValore);
                            case "$nin":
                                return lDRiferimento.contains(dValore);
                            case "$bt": // caso in cui devono ritornare valori compresi tra due estremi
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
            } else if (valore instanceof String) { // altrimenti se valore è una stringa
                String sValue = ((String) valore); //conversione in String
                if (riferimento instanceof String) { //se riferimento è una stringa
                    String sRiferimento = ((String) riferimento); // conversione come sopra
                    switch (op) {
                        case "$not":
                            return !sValue.equals(sRiferimento); // si utilizza il metodo equals() della classe String per il confronto
                        default:
                            String stampaErrore = "L'operatore: " + op + " non è valido per i dati " + valore + ", " + riferimento;
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, stampaErrore);
                    }
                } else if (riferimento instanceof List) { //se riferimento è una lista
                    List lRiferimento = ((List) riferimento); //conversione in List
                    if (!lRiferimento.isEmpty() && lRiferimento.get(0) instanceof String) { //se la lista è diversa da quella vuota e contiene stringhe
                        List<String> lSRiferimento = new ArrayList<>();
                        for (Object elem : lRiferimento) {  //for-each
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il valore: " + valore + " non è valido");

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'operatore: " + op + " non è valido");

    }

    /**
     * Metodo per il filtraggio
     *
     * @param valori      lista valori da confrontare
     * @param op          operatore
     * @param riferimento valore per il confronto
     * @return la lista di valori filtrati
     */

    public static List<Integer> filtra(List valori, String op, Object riferimento) {
        List<Integer> valoriFiltrati = new ArrayList<>();
        for (int i = 0; i < valori.size(); i++) {
            Object val = valori.get(i);
            if (val!=null && sceltaFiltro(val, op, riferimento)) //chiamata al metodo sceltaFiltro precedentemente implementato
                valoriFiltrati.add(i);
        }
        return valoriFiltrati;
    }

    /**
     * Metodo per ottenere la lista privata degli operatori
     *
     * @return lista operatori
     */
    public static List<String> getOpFiltro() {
        return opFiltro;
    }


}

