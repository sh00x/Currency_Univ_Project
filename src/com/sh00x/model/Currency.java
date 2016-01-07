package com.sh00x.model;

/**
 * @author Lukasz Pusz
 *         <p>
 *         Klasa reprezentujaca walute, zgodna z kategoria "A"
 *         plikow .xml ze strony NBP (inne kategorie maja inne tagi,
 *         ja tutaj wykorzystuje taki sam rodzaj pliku jak w przykladzie na stronie)
 */
public class Currency {
    private String currencyName;
    private int conversion;
    private String currencyCode;
    private double avgCourse;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Currency setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public Currency setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        return this;
    }

    public int getConversion() {
        return conversion;
    }

    public Currency setConversion(int conversion) {
        this.conversion = conversion;
        return this;
    }

    public Currency setAvgCourse(double avgCourse) {
        this.avgCourse = avgCourse;
        return this;
    }

    public double getAvgCourse() {
        return avgCourse;
    }
}
