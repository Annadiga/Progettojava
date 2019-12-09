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

public class Parser {

    private String headerLine;

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

    public ArrayList<DayCareChildren> parsing(String urlDataset) {
        InputStream in = null;
        ArrayList<DayCareChildren> list = new ArrayList<>();
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
                String[] fields = line.split("[,\t]");
                if (fields.length!=32) continue; //salta la riga se essa contiene un numero di campi invalido, ovvero diverso da 32
                if (fields[1].length()==2) continue; //salta le righe riferite alle nazioni
                DayCareChildren nuovo = new DayCareChildren();
                nuovo.setIndic_ur(fields[0]);
                nuovo.setCity(fields[1]);
                for (int i = 0; i < 30; i++) {
                    try {
                        double val = Double.parseDouble(fields[31 - i]);
                        nuovo.setChildren(val, i);
                    } catch (NumberFormatException e) {
                        nuovo.setChildren(-1,i);    //setta il valore a -1 per identificare il campo mancante
                    }
                }
                list.add(nuovo);
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

    public ArrayList<Map> metadata() {
        Field[] fields = DayCareChildren.class.getDeclaredFields();
        ArrayList<Map> metaDati = new ArrayList<>();
        //line = line.replace(",", "\t");
        String[] dividedLine = headerLine.trim().replace("\\", "\t").split("[,\t]");
        for (int i=0; i<fields.length; i++) {
            Map<String, String> nameAssociation = new HashMap<>();
            nameAssociation.put("alias", fields[i].getName());
            nameAssociation.put("sourcefield", dividedLine[i]);
            nameAssociation.put("type", fields[i].getType().getSimpleName());
            metaDati.add(nameAssociation);
        }
        return metaDati;
    }
}
