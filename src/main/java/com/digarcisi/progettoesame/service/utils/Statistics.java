package com.digarcisi.progettoesame.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Statistics {

    public static int count(List lista) {
        return lista.size();
    }

    public static double sum(List<Number> lista) {
        double s = 0;
        for (Number n : lista) {
            s += n.doubleValue();
        }
        return s;
    }

    public static double avg(List<Number> lista) {
        return sum(lista) / count(lista);
    }

    public static double min(List<Number> lista) {
        double min = lista.get(0).doubleValue();
        for (Number n : lista) {
            double nmin = n.doubleValue();
            if (nmin < min) min = nmin;
        }
        return min;
    }

    public static double max(List<Number> lista) {
        double max = lista.get(0).doubleValue();
        for (Number n : lista) {
            double nmax = n.doubleValue();
            if (nmax > max) max = nmax;
        }
        return max;
    }

    public static double devStd(List<Number> lista) {
        double avg = avg(lista);
        double risultSqrd = 0;
        for (Number n : lista) {
            risultSqrd += Math.pow(n.doubleValue() - avg, 2);
        }
        return Math.sqrt(risultSqrd);
    }

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
