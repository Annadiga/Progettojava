package com.digarcisi.progettoesame.modelDataSet;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Classe modellante di un record del dataset formato tsv
 */
public class DayCareChildren implements Serializable {
    private String indic_ur;
    private String city;
    private double[] children = new double[30];

    /**
     * Costruttore vuoto che verrà invocato nella classe Parser
     */
    public DayCareChildren() {
    }

    /**
     * Costruttore della classe
     *
     * @param indic_ur attributo di tipo String
     * @param city     attributo di tipo String
     * @param children vettore in cui sono contenuti i dati sui bambini, i cui indici sono gli anni dal 1990 al 2019
     */
    public DayCareChildren(String indic_ur, String city, double[] children) {
        this.indic_ur = indic_ur;
        this.city = city;
        this.children = children;
    }
    //metodi getter e setter

    public String getIndic_ur() {
        return indic_ur;
    }

    public void setIndic_ur(String indic_ur) {
        this.indic_ur = indic_ur;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getChildren(int year) {
        return children[year];
    }

    public double[] getChildren() {
        return children;
    }

    public void setChildren(double children, int year) {
        this.children[year] = children;

    }

    /**
     * metodo che consente la stampa
     *
     * @return una stringa in cui vengono indicati i vari campi e il loro valore
     */
    @Override
    public String toString() {
        return "DayCareChildren{" +
                "indic_ur='" + indic_ur + '\'' +
                ", city='" + city + '\'' +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
