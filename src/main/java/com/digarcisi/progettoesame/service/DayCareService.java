package com.digarcisi.progettoesame.service;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import com.digarcisi.progettoesame.service.filters.DayCareFilters;
import com.digarcisi.progettoesame.service.utils.Parser;
import com.digarcisi.progettoesame.service.utils.Statistics;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Classe DayCareService che gestisce l'accesso al dataset
 */
@Service
public class DayCareService {

    private ArrayList<DayCareChildren> dataset;
    private ArrayList<Map> metadata;

    /**
     * Costruttore della classe DayCareService
     */
    public DayCareService() {
        String datasetSerialFileName = "dataset.ser";
        String metadataSerialFileName = "metadata.ser";
        if (Files.exists(Paths.get(datasetSerialFileName))) { //controlla se il file esiste già, in questo caso esegue l'istruzione
            //carica il file seriale esistente del dataset
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datasetSerialFileName))) {
                dataset = new ArrayList<>(Arrays.asList((DayCareChildren[]) ois.readObject()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Classe non trovata");
                e.printStackTrace();
            }
            //carica il file seriale esistente dei metadati
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(metadataSerialFileName))) {
                metadata = new ArrayList<>(Arrays.asList((Map[]) ois.readObject()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Classe non trovata");
                e.printStackTrace();
            }
        } else { //altrimenti crea tali files
            Parser p = new Parser(); // creazione oggetto di tipo Parser
            String urlJSON = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=2rDGENQaYvidkf7nfM2g";
            String urldataset = p.getDataUrlFromJSON(urlJSON);//l'url del dataset viene restituito dalla funzione getDataUrlFromJson
            //System.out.println(urldataset);
            this.dataset = p.parsing(urldataset);
            for (DayCareChildren elem : this.dataset) {
                System.out.println(elem);
            }
            this.metadata = p.metadata();
            //salvataggio file seriale dataset
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datasetSerialFileName))) {
                oos.writeObject(dataset.toArray(new DayCareChildren[0]));    // si salva la lista come array per evitare problemi di casting in lettura
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

    /**
     * Metodo che restituisce il dataset
     *
     * @return la lista degli oggetti
     */
    public ArrayList<DayCareChildren> getDataset() {
        return dataset;
    }

    /**
     * Restituisce la lista dei metadati
     *
     * @return metadati
     */
    public ArrayList<Map> getMetadata() {
        return metadata;
    }

    /**
     * Restituisce le statistiche di un campo
     *
     * @param nomeCampo campo di cui ottenere le statistiche
     * @return mappa contenente le statistiche
     */
    public Map getStats(String nomeCampo) {
        return Statistics.getAllStats(nomeCampo, getValoriCampo(nomeCampo));
    }

    /**
     * Metodo che consente di estrarre dalla lista degli oggetti quella relativa a un campo
     *
     * @param nomeCampo campo da cui ottenere i valori
     * @return lista del campo
     */
    private List getValoriCampo(String nomeCampo) {
        List<Object> values = new ArrayList<>(); //inizializzo lista che conterrà i valori del campo
        try {
            int anno = Integer.parseInt(nomeCampo);
            //caso in cui nomeCampo sia un anno: verifico che sia uno degli anni gestiti
            if (anno >= 1990 && anno <= 2019) {
                for (DayCareChildren elem : dataset) {
                    double value = elem.getChildren(anno - 1990); //considero solo l'elemento che mi interessa del metodo get
                    if (value != -1) values.add(value);
                }
            }
            //caso in cui nomeCampo non sia un anno
        } catch (NumberFormatException e) {
            //serve per scorrere tutti gli oggetti ed estrarre i valori del campo nomeCampo
            for (DayCareChildren elem : dataset) {
                if (nomeCampo.equals("indic_ur")) {
                    values.add(elem.getIndic_ur());
                } else if (nomeCampo.equals("cities")) {
                    values.add(elem.getCity());
                }
            }
        }
        return values; //ritorno la lista
    }

    /**
     * Metodo che restituisce gli oggetti filtrati
     *
     * @param nomeCampo   campo da filtrare
     * @param op          operatore
     * @param riferimento valore di riferimento
     * @return lista di oggetti che soddisfano le condizioni in base al filtro scelto
     */
    public List<DayCareChildren> getDatasetFiltrato(String nomeCampo, String op, Object riferimento) {
        List<Integer> indici = DayCareFilters.filtra(getValoriCampo(nomeCampo), op, riferimento);
        List<DayCareChildren> outputDataset = new ArrayList<>();
        for (int i : indici) {
            outputDataset.add(dataset.get(i));
        }
        return outputDataset;
    }

    /**
     * Metodo che restituisce le statistiche filtrate su un campo
     *
     * @param nomeCampoStats  nome del campo su cui si vogliono effettuare le statistiche
     * @param nomeCampoFiltro nome campo su cui si applica il filtro
     * @param op              operatore
     * @param riferimento     valore di riferimento
     * @return Mappa con le statistiche relative ad un campo filtrato
     */
    public Map getStatsFiltrate(String nomeCampoStats, String nomeCampoFiltro, String op, Object riferimento) {
        List<Integer> indici = DayCareFilters.filtra(getValoriCampo(nomeCampoFiltro), op, riferimento);
        List valoriCampo = getValoriCampo(nomeCampoStats);
        List<Object> valoriFiltrati = new ArrayList<>();
        for (int i : indici) {
            valoriFiltrati.add(valoriCampo.get(i));
        }
        return Statistics.getAllStats(nomeCampoStats, valoriFiltrati);
    }

}
