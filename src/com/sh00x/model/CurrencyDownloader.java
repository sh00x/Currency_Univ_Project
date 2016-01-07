package com.sh00x.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Lukasz Pusz
 *         <p>
 *         Klasa zawierajaca logike pobierania plikow .xml z sieci,
 *         wyciagania informacji z tych plikow .xml
 */
public class CurrencyDownloader {
    /**
     * Parsuje plik xml wyciagajac z niego okreslone dane po tagach xml,
     * tworzy z tego obiekt klasy Currency, a nastepnie dodaje go do listy tablicowej
     *
     * @param xmlFilePath sciezka do pobranego pliku .xml znajdujacego sie na komputerze
     * @return ArrayList<Currency> zawierajaca wszystkie dane z okreslonego pliku xml
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ParseException
     */
    public static ArrayList<Currency> parseXml(String xmlFilePath) throws ParserConfigurationException, IOException, SAXException, ParseException {
        ArrayList<Currency> currencyList = new ArrayList<>();
        Currency currency;

        File input = new File(xmlFilePath);
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(input);
        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("pozycja");
        for (int i = 0; i < nodeList.getLength(); i++) {
            currency = new Currency();
            Node newNode = nodeList.item(i);
            if (newNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) newNode;
                currency.setCurrencyName(element.getElementsByTagName("nazwa_waluty").item(0).getTextContent());
                currency.setConversion(Integer.parseInt(element.getElementsByTagName("przelicznik").item(0).getTextContent()));
                currency.setCurrencyCode(element.getElementsByTagName("kod_waluty").item(0).getTextContent());
                currency.setAvgCourse(formatDouble("kurs_sredni", element));
                currencyList.add(currency);
            }
        }
        return currencyList;
    }

    /**
     * Metoda eliminujaca problem separatora w liczbach zmiennoprzecinkowych (tylko dla znaku "." oraz ",")
     *
     * @param elementTagName .xml tag ktory ma zostac wyciagniety i wiadomo, ze jest liczba
     * @param element        obiekt klast Element reprezentujacy zgodny z iteracja wezel w pliku xml
     * @return liczbe typu double zgodna z niemieckim (polskim) lokalizatorem
     * @throws ParseException
     */
    public static double formatDouble(String elementTagName, Element element) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        Number number = format.parse(element.getElementsByTagName(elementTagName).item(0).getTextContent());
        return number.doubleValue();
    }

    /**
     * Pobiera z internetu dwa pliki .xml i zapisuje je do okreslonej lokalizacji
     *
     * @throws IOException
     */
    public static void downloadExchangeRate() throws IOException {
        String[] urlsArray = htmlCodeParser();

        URL website = new URL(urlsArray[0]);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("src/com/sh00x/files/kursy2.xml");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        website = new URL(urlsArray[1]);
        rbc = Channels.newChannel(website.openStream());
        fos = new FileOutputStream("src/com/sh00x/files/kursy1.xml");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    /**
     * Metoda wyciagajaca z kodu html strony dwa ostatnie linki do plikow .xml
     *
     * @return tablice String[] o rozmiarze 2, z dwoma linkami do najnowszych plikow .xml
     * @throws IOException
     */
    public static String[] htmlCodeParser() throws IOException {
        String[] array = new String[2];
        ArrayList<String> list = new ArrayList<>();

        URL url;
        InputStream is = null;
        BufferedReader reader;
        String line;

        try {
            url = new URL("http://rss.nbp.pl/kursy/xml2/2015/a/");
            is = url.openStream();
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
            }
        }
        array[0] = "http://rss.nbp.pl/kursy/xml2/2015/a/15a" + list.get(list.size() - 9).substring(0, 3) + ".xml";
        array[1] = "http://rss.nbp.pl/kursy/xml2/2015/a/15a" + list.get(list.size() - 8).substring(0, 3) + ".xml";
        return array;
    }
}
