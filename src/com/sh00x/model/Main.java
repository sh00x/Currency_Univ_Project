package com.sh00x.model;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * @author Lukasz Pusz
 * @version 1.0
 *          <p>
 *          Mala aplikacja sluzaca do pobierania dwoch dokumentow .xml z danymi na temat walut z danego okresu
 *          i wyliczajaca zmiany w srednim kursie walut. Tworzy plik .txt oraz .html z danymi, a takze zapisuje
 *          dwa wymienione wyzej pliki .xml
 */
public class Main {
    public static final int TXT_TYPE = 1;
    public static final int HTML_TYPE = 2;

    private static ArrayList<Currency> currencies1;
    private static ArrayList<Currency> currencies2;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException {
        CurrencyDownloader.downloadExchangeRate();
        currencies1 = CurrencyDownloader.parseXml("src/com/sh00x/files/kursy1.xml");
        currencies2 = CurrencyDownloader.parseXml("src/com/sh00x/files/kursy2.xml");
        kursyWalut("plikTekstowy", "plikHtml");

    }

    /**
     * Metoda ktora nalezalo stworzyc zgodnie z poleceniem z Zadania 2 ze strony jkozak.pl
     *
     * @param export     nazwa pliku .txt ktory ma zostac stworzony
     * @param exportHtml nazwa pliku .html ktory ma zostac stworzony
     * @throws IOException
     */
    private static void kursyWalut(String export, String exportHtml) throws IOException {
        currencyArrayToFile(currencies1, currencies2, export, TXT_TYPE);
        currencyArrayToFile(currencies1, currencies2, exportHtml, HTML_TYPE);
    }

    /**
     * Metoda obliczajaca zmiany w kursie danej waluty z dwoch okreslonych
     * plikow .xml, a nastepnie zapisujaca dane do pliku .txt lub .html
     *
     * @param currencies1 dane z starszego pliku .xml
     * @param currencies2 dane z nowszego pliku .xml
     * @param filename    nazwa dla pliku ktory ma zostac stworzony
     * @param type        typ pliku ktory ma zostac stworzony, 1 - .txt, 2 - .html
     * @throws IOException
     */
    private static void currencyArrayToFile(ArrayList<Currency> currencies1, ArrayList<Currency> currencies2, String filename, int type) throws IOException {
        double[] changeArray = new double[currencies1.size()];
        for (int i = 0; i < currencies2.size(); i++) {
            changeArray[i] = currencies1.get(i).getAvgCourse() - currencies2.get(i).getAvgCourse();
        }

        if (type == TXT_TYPE) {
            Formatter output = new Formatter("src/com/sh00x/files/" + filename + ".txt");
            output.format("%24s %24s %24s %24s", "NAZWA WALUTY", "KOD WALUTY", "KURS ŚREDNI", "ZMIANA");
            for (int i = 0; i < currencies2.size(); i++) {
                Currency tmp = currencies2.get(i);
                output.format("%n%24s %24s %24f %24.4f", tmp.getCurrencyName(), 1 + " " + tmp.getCurrencyCode(), tmp.getAvgCourse(), changeArray[i]);
            }
            output.close();
        } else if (type == HTML_TYPE) {
            PrintWriter output = new PrintWriter("src/com/sh00x/files/" + filename + ".html");
            output.write("<!DOCTYPE html><html><body>");
            output.write("<table align=\"left\" style=\"width:50%\">");
            output.write("<tr>");
            output.write("<td>NAZWA WALUTY</td>");
            output.write("<td>KOD WALUTY</td>");
            output.write("<td>KURS ŚREDNI</td>");
            output.write("<td>ZMIANA</td>");
            output.write("</tr>");

            for (int i = 0; i < currencies2.size(); i++) {
                Currency tmp = currencies2.get(i);
                output.write("<tr>");
                output.write("<td>" + tmp.getCurrencyName() + "</td>");
                output.write("<td>" + 1 + " " + tmp.getCurrencyCode() + "</td>");
                output.write("<td>" + tmp.getAvgCourse() + "</td>");
                if (changeArray[i] > 0)
                    output.format("<td><font color=\"green\">%.4f</font></td>", changeArray[i]);
                else if (changeArray[i] < 0)
                    output.format("<td><font color=\"red\">%.4f</font></td>", changeArray[i]);
                else
                output.write("<td>" + changeArray[i] + "</td>");
                output.write("</tr>");
            }
            output.write("</table>");
            output.write("</body>");
            output.write("</html>");
            output.close();
        }
    }
}
