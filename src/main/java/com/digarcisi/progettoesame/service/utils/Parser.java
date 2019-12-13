package com.digarcisi.progettoesame.service.utils;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Classe Parser contenente:
 * -metodi necessati per ottenere l'url ed effettuare il parsing del dataset
 * -i metadati
 */
public class Parser {

    private String headerLine; //prima riga

    /**
     * Metodo usato per gestire un problema di reindirizzamento dell'url del dataset ed ottenere quello finale
     *
     * @param url stringa contenente l'url
     * @return url finale
     * @throws IOException per la gestione di errori
     */

    public static String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            return getFinalURL(redirectUrl);
        }
        return url;
    }

    /**
     * Metodo per ottenere l'url dal JSON
     *
     * @param urlJSON link url del JSON
     * @return ""
     */
    public String getDataUrlFromJSON(String urlJSON) {
        //String url = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=2rDGENQaYvidkf7nfM2g";
        try {

            URLConnection openConnection = new URL(urlJSON).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream in = openConnection.getInputStream();

            StringBuilder data = new StringBuilder();
            String line;
            try {
                InputStreamReader inR = new InputStreamReader(in);
                BufferedReader buf = new BufferedReader(inR);

                while ((line = buf.readLine()) != null) {
                    data.append(line);
                    //System.out.println(line);
                }
            } finally {
                in.close();
            }
            JSONObject obj = (JSONObject) JSONValue.parseWithException(data.toString());
            JSONObject objI = (JSONObject) (obj.get("result"));
            JSONArray objA = (JSONArray) (objI.get("resources"));

            for (Object o : objA) {
                if (o instanceof JSONObject) {
                    JSONObject o1 = (JSONObject) o;
                    String format = (String) o1.get("format");
                    String urlDataset = (String) o1.get("url");
                    //System.out.println(format + " | " + urlDataset);
                    if (format.equals("http://publications.europa.eu/resource/authority/file-type/TSV")) {
                        //System.out.println("OK2");
                        return getFinalURL(urlDataset);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Metodo per effettuare il parsing
     *
     * @param urlDataset stringa contenente l'url del dataset
     * @return list
     */
    public ArrayList<DayCareChildren> parsing(String urlDataset) {
        InputStream in = null;
        ArrayList<DayCareChildren> list = new ArrayList<>(); //creazione lista di tipo DayCareChildren
        try {
            URLConnection openConnection = new URL(urlDataset).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            in = openConnection.getInputStream();

            String line;
            InputStreamReader inR = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inR);
            /* stampa il file intero
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }*/

            this.headerLine = buf.readLine();  //salta la riga header
            while ((line = buf.readLine()) != null) {
                String[] fields = line.split("[,\t]"); //si usa il metodo split per dividere la stringa in sottostringhe; i caratteri separatori che si passano sono quelli del dataset, ossia la virgola e il tab
                if (fields.length != 32)
                    continue; //salta la riga se essa contiene un numero di campi invalido, ovvero diverso da 32
                if (fields[1].length() == 2)
                    continue; //salta le righe riferite alle nazioni dato che il campo richiesto è quello delle città
                DayCareChildren nuovo = new DayCareChildren(); //creazione oggetto della classe modellante, i valori li passiamo successivamente con il set
                //passiamo all'oggetto "nuovo" tutti gli elementi che sono dentro fields
                nuovo.setIndic_ur(fields[0]); //il primo elemento del vettore fields è indic_ur
                nuovo.setCity(fields[1]); //il secondo elemento di fields è city
                for (int i = 0; i < 30; i++) { //riempiamo il vettore children
                    try {
                        double val = Double.parseDouble(fields[31 - i]);
                        nuovo.setChildren(val, i);
                    } catch (NumberFormatException e) {
                        nuovo.setChildren(-1, i);    //setta il valore a -1 per identificare il campo mancante
                    }
                }
                list.add(nuovo); //per salvare l'oggetto riempito all'interno della lista
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Metodo per generare i metadati e salvarli per poterli restituire quando vengono richiesti
     *
     * @return restituisce i metadati
     */
    public ArrayList<Map> metadata() {
        Field[] fields = DayCareChildren.class.getDeclaredFields(); //estrae gli attributi da DayCareChildren
        ArrayList<Map> metaDati = new ArrayList<>();
        //line = line.replace(",", "\t");
        String[] dividedLine = headerLine.trim().replace("\\", "\t").split("[,\t]"); //divide la stringa sulla prima linea
        for (int i = 0; i < fields.length; i++) {
            Map<String, String> nameAssociation = new HashMap<>(); //creiamo una mappa con le coppie chiave-valore
            nameAssociation.put("alias", fields[i].getName());
            //inseriamo le coppie
            nameAssociation.put("sourcefield", dividedLine[i]);
            nameAssociation.put("type", fields[i].getType().getSimpleName());
            metaDati.add(nameAssociation);//salva i metadati in metaDati
        }
        return metaDati;
    }
}
