package com.digarcisi.progettoesame.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe astratta contenente l'implementazione dei metodi per le statistiche
 */
public abstract class Statistics {
    /**
     * Metodo per contare gli elementi di una lista
     *
     * @param lista lista valori
     * @return dimensione della lista
     */
    public static int count(List lista) {
        return lista.size();
    }

    /**
     * Metodo per sommare gli elementi di una lista di numeri
     *
     * @param lista lista di valori
     * @return valore somma
     */
    public static double sum(List<Number> lista) {
        double s = 0;
        for (Number n : lista) {
            s += n.doubleValue();
        }
        return s;
    }

    /**
     * Metodo per il calcolo della media di una lista di numeri
     *
     * @param lista lista di valori numerici
     * @return media
     */
    public static double avg(List<Number> lista) {
        return sum(lista) / count(lista);
    }

    /**
     * Metodo che trova il minimo tra gli elementi presenti in una lista di numeri
     *
     * @param lista lista di valori numerici
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
     * Metodo che trova il massimo valore degli elementi della lista numerica
     *
     * @param lista lista di valori numerici
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
     * Metodo che calcola la deviazione standard
     *
     * @param lista lista valori su cui effettuare il calcolo
     * @return deviazione standard dei valori
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
     * Metodo per calcolare il numero di volte in cui è presente un elemento
     *
     * @param lista lista di valori
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
     * Metodo che restituisce tutte le statistiche chiamando i metodi sopra implementati
     *
     * @param fieldName nome del campo su cui vogliamo calcolare le statistiche
     * @param lista     lista di valori
     * @return una mappa che ha come chiave il nome della statistica e come valore il suo corrispettivo
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
