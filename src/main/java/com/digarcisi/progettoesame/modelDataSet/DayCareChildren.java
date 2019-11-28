package com.digarcisi.progettoesame.modelDataSet;

import java.util.Arrays;

public class DayCareChildren {
    private String indic_ur;
    private String city;
    private double[] children = new double[30];

    public DayCareChildren(String indic_ur, String city, double[] children) {
        this.indic_ur = indic_ur;
        this.city = city;
        this.children = children;
    }

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

    public void setTime(double children, int year) {
        this.children[year] = children;

    }

    @Override
    public String toString() {
        return "DayCareChildren{" +
                "indic_ur='" + indic_ur + '\'' +
                ", city='" + city + '\'' +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
