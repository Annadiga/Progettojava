package com.digarcisi.progettoesame.start;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {


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
                    System.out.println(line);
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
                    System.out.println(format + " | " + urlDataset);
                    if (format.equals("http://publications.europa.eu/resource/authority/file-type/TSV")) {
                        System.out.println("OK");
                        return urlDataset;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<DayCareChildren> parsing(String urlDataset) throws IOException {
        InputStream in = null;
        ArrayList<DayCareChildren> list = new ArrayList<>();
        try {
            URLConnection openConnection = new URL(urlDataset).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            in = openConnection.getInputStream();

            String line;
            InputStreamReader inR = new InputStreamReader(in);
            BufferedReader buf = new BufferedReader(inR);
            buf.readLine();  //salta la riga header
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                String[] fields = line.split(",\t");
                DayCareChildren nuovo = new DayCareChildren();
                nuovo.setIndic_ur(fields[0]);
                nuovo.setCity(fields[1]);
                for (int i = 0; i < 30; i++) {
                    try {
                        double val = Double.parseDouble(fields[31 - i]);
                        nuovo.setTime(val, i);
                    } catch (NumberFormatException e) {
                        System.err.println("Campo vuoto o non valido");
                    }
                }
                list.add(nuovo);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            in.close();
        }
        return list;
    }

    public List<Map> metadata(String urlDataset) throws IOException {
        Field[] fields = DayCareChildren.class.getDeclaredFields();
        List<Map> metaDati = new ArrayList<>();
        BufferedReader bR = new BufferedReader(new FileReader(urlDataset));
        String line = bR.readLine();
        line = line.replace(",", "\t");
        line = line.replace("\\", "\t");
        String[] dividedLine = line.trim().split("\t");
        int i = 0;
        for (Field f : fields) {
            Map<String, String> nameAssociation = new HashMap<>();
            nameAssociation.put("alias", f.getName());
            nameAssociation.put("sourcefield", dividedLine[i]);
            nameAssociation.put("type", f.getType().getSimpleName());
            metaDati.add(nameAssociation);
        }
        return metaDati;
    }
}
