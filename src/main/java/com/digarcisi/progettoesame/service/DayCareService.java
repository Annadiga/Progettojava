package com.digarcisi.progettoesame.service;

import com.digarcisi.progettoesame.modelDataSet.DayCareChildren;
import com.digarcisi.progettoesame.service.filters.DayCareFilters;
import com.digarcisi.progettoesame.service.utils.Parser;
import com.digarcisi.progettoesame.service.utils.Statistics;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                    else values.add(null);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il campo " + nomeCampo + " non esiste!");
            }
            //caso in cui nomeCampo non sia un anno
        } catch (NumberFormatException e) {
            //serve per scorrere tutti gli oggetti ed estrarre i valori del campo nomeCampo
            for (DayCareChildren elem : dataset) {
                if (nomeCampo.equals("indic_ur")) {
                    values.add(elem.getIndic_ur());
                } else if (nomeCampo.equals("cities")) {
                    values.add(elem.getCity());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il campo " + nomeCampo + " non esiste!");
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
        if (valoriFiltrati.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Non è stato trovato nessun campo " + nomeCampoStats + " per un filtro con il valore selezionato.");

        return Statistics.getAllStats(nomeCampoStats, valoriFiltrati);
    }

    /**
     * Metodo che crea una mappa per aggiungere dati
     *
     * @param body in cui inserire i nuovi dati
     * @return ultima riga del dataset (aggiornata con il nuovo record)
     */
    public DayCareChildren parseadd(String body) {
        Map<String, Object> mappa = new BasicJsonParser().parseMap(body);
        Object appoggio = mappa.get("values");
        List elem = (List) appoggio;
        //controlla che i campi inseriti siano 32, ossia gli stessi del dataset
        if (elem.size() != 32) {//stampa messaggio di errori se il numero è diverso da 32
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore! Non sono stati inseriti 32 campi");
        }
        for (int i = 0; i < elem.size(); i++) {//scorre la lista
            if (i == 0 || i == 1) {
                if (!(elem.get(i) instanceof String)) {//se i primi due campi non sono stringhe stampa un messaggio di errore
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il " + (i + 1) + "° campo inserito :" + elem.get(i) + " deve essere una stringa!");
                }
            }
            if (i > 1) {
                if (!(elem.get(i) instanceof Number)) {//se i campi successivi a 1 non sono numeri stampa un messaggio di errore
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il " + (i + 1) + "° campo inserito :" + elem.get(i) + " deve essere un numero!");
                }
            }
        }
        double[] c = new double[30];// creo un vettore di double in cui poter inserire i valori numerici
        for (int i = 2; i < elem.size(); i++) {//il vettore parte dalla seconda posizione e arriva fino alla fine della lista
            c[i - 2] = ((Number) elem.get(i)).doubleValue();
        }
        DayCareChildren d = new DayCareChildren(elem.get(0).toString(), elem.get(1).toString(), c);// creo il nuovo record costituito da due stringhe
        //come primi elementi e da 30 numeri
        getDataset().add(d);//salvo tale oggetto nel dataset
        return (dataset.get(dataset.size() - 1));//restituisco l'oggetto in fondo al dataset
    }

    /**
     * Metodo che restituisce i dati eliminati
     *
     * @param nomecampo   su cui applicare la selezione
     * @param op          operatore per il filtraggio
     * @param riferimento valore di confronto
     * @return lista di dati che sono stati eliminati in base al filtro applicato
     */
    public List<DayCareChildren> deletebycampo(String nomecampo, String op, Object riferimento) {
        List<DayCareChildren> dati_da_eliminare = getDatasetFiltrato(nomecampo, op, riferimento); // si crea una lista per i dati da eliminare
        //e la si pone uguale alla lista di oggetti che soddisfano i criteri indicati
        dataset.removeAll(dati_da_eliminare); //si rimuovono i dati contenuti nella lista
        if (dati_da_eliminare.isEmpty())//si stampa un messaggio di errore se la lista è vuota
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun campo trovato con questo valore!");
        return dati_da_eliminare;
    }
}
