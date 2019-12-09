package com.digarcisi.progettoesame.service;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import com.digarcisi.progettoesame.service.utils.Parser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DayCareService {

    private ArrayList<DayCareChildren> dataset;
    private ArrayList<Map> metadata;

    public DayCareService(){
        String datasetSerialFileName = "dataset.ser";
        String metadataSerialFileName = "metadata.ser";
        if (Files.exists(Paths.get(datasetSerialFileName))){
            //carica il file seriale esistente
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datasetSerialFileName))) {
                dataset = new ArrayList<>(Arrays.asList((DayCareChildren[]) ois.readObject()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Classe non trovata");
                e.printStackTrace();
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(metadataSerialFileName))) {
                metadata = new ArrayList<>(Arrays.asList((Map[]) ois.readObject()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Classe non trovata");
                e.printStackTrace();
            }
        }
        else {
            Parser p = new Parser();
            String urlJSON = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=2rDGENQaYvidkf7nfM2g";
            String urldataset = p.getDataUrlFromJSON(urlJSON);
            //System.out.println(urldataset);
            this.dataset = p.parsing(urldataset);
            for (DayCareChildren elem : this.dataset) {
                System.out.println(elem);
            }
            this.metadata = p.metadata();
            //salvataggio file seriale dataset
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datasetSerialFileName))) {
                oos.writeObject(dataset.toArray(new DayCareChildren[0]));    // la lista viene salvata come array per evitare successivi problemi di casting in lettura
            } catch (IOException e) {
                e.printStackTrace();
            }
            //salvataggio file seriale metadata
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(metadataSerialFileName))) {
                oos.writeObject(metadata.toArray(new Map[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
