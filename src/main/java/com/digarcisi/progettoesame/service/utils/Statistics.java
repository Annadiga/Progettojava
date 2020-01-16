package com.digarcisi.progettoesame.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe contenente l'implementazione dei metodi per le statistiche
 */
public class Statistics {
    /**
     * Metodo che conta gli elementi di una lista che gli viene passata, usato anche come metodo ausiliario nel calcolo della media
     *
     * @param lista lista di valori su cui effettuare il conteggio
     * @return il numero di elementi contenuti nella lista
     */
    public static int count(List lista) {
        return lista.size();
    }

    /**
     * Metodo che effettua la somma degli elementi contenuti nella lista presa in analisi, usato come metodo ausiliario nel calcolo della media
     *
     * @param lista lista degli elementi di tipo numerico dei quali effettuare la somma
     * @return il contatore inizializzato all'interno del metodo contenente il valore della somma
     */
    public static double sum(List<Number> lista) {
        double s = 0;
        for (Number n : lista) {
            s += n.doubleValue();
        }
        return s;
    }

    /**
     * Metodo che effetua il calcolo del valore medio degli elementi numerici della lista esaminata
     *
     * @param lista lista degli elementi numerici sdei quali va calcolata la media
     * @return la media aritmetica: il risultato del quoziente tra i valori ricavati dalla chiamata di altri 2 metodi
     */
    public static double avg(List<Number> lista) {
        return sum(lista) / count(lista);
    }

    /**
     * Metodo che stampa il valore minore tra gli elementi presenti nella lista numerica presa in esame
     *
     * @param lista lista di elementi su cui effettuare la ricerca
     * @return valore minimo
     */
    public static double min(List<Number> lista) {
        double min = lista.get(0).doubleValue();
        for (Number n : lista) {
            double nmin = n.doubleValue();
            if (nmin < min) min = nmin;
        }
        return min;
    }

    /**
     * Metodo che stampa il valore maggiore tra gli elementi presenti nella lista numerica presa in esame
     *
     * @param lista lista di elementi su cui effettuare la ricerca
     * @return valore massimo
     */
    public static double max(List<Number> lista) {
        double max = lista.get(0).doubleValue();
        for (Number n : lista) {
            double nmax = n.doubleValue();
            if (nmax > max) max = nmax;
        }
        return max;
    }

    /**
     * Metodo per il calcolo della deviazione standard degli elementi di una lista numerica
     *
     * @param lista lista valori su cui effettuare il calcolo
     * @return il valore della deviazione standard degli elementi
     */
    public static double devStd(List<Number> lista) {
        double avg = avg(lista);
        double risultSqrd = 0;
        for (Number n : lista) {
            risultSqrd += Math.pow(n.doubleValue() - avg, 2);
        }
        return Math.sqrt(risultSqrd);
    }

    /**
     * Metodo per il conteggio delle istanze di un elemento all'interno di una lista di stringhe
     *
     * @param lista lista degli elementi da conteggiare
     * @return Mappa contenente come chiave l'elemento e come valore il numero di volte in cui compare
     */
    public static Map<Object, Integer> uniElemCount(List lista) {
        Map<Object, Integer> map = new HashMap<>();
        for (Object elem : lista) {
            if (map.containsKey(elem)) {
                map.replace(elem, map.get(elem) + 1);
            } else {
                map.put(elem, 1);
            }
        }
        return map;
    }

    /**
     * Metodo che restituisce tutte le statistiche del campo richiesto dall'utente chiamando i metodi sopra implementati
     *
     * @param fieldName nome del campo su cui vogliamo calcolare le statistiche(si tiene presente se si tratta di un campo numerico o di stringhe)
     * @param lista     lista degli elementi presenti nel campo
     * @return una mappa che ha come chiave i nomi delle statistiche disponibili per il campo richiesto e come valore l'effettivo valore della statistica
     */
    public static Map getAllStats(String fieldName, List lista) {
        Map<String, Object> map = new HashMap<>();
        map.put("field", fieldName);
        if (!lista.isEmpty()) {
            if (lista.get(0) instanceof Number) {
                List<Number> listaNum = new ArrayList<>();
                for (Object elem : lista) {
                    listaNum.add(((Number) elem));
                }
                map.put("count", count(listaNum));
                map.put("sum", sum(listaNum));
                map.put("min", min(listaNum));
                map.put("max", max(listaNum));
                map.put("avg", avg(listaNum));
                map.put("devStd", devStd(listaNum));
                return map;
            } else {
                map.put("uniqueElements", uniElemCount(lista));
                map.put("count", count(lista));
            }
        }
        return map;
    }
}
