package com.digarcisi.progettoesame.start;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class JSONparse {

    private String getDataUrl(String url) {
        //String url = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=2rDGENQaYvidkf7nfM2g";
        try {

            URLConnection openConnection = new URL(url).openConnection();
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
                    String urlD = (String) o1.get("url");
                    System.out.println(format + " | " + urlD);
                    if (format.equals("http://publications.europa.eu/resource/authority/file-type/TSV")) {
                        System.out.println("OK");
                        return urlD;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
