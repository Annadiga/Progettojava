## PROGETTO ESAME

 ## Presentazione generale

Questo progetto è stato realizzato degli studenti del secondo anno Davide Narcisi e Anna Di Gaetano per il corso di "Programmazione ad oggetti", tenutosi all'Università Politecnica delle Marche nell'anno accademico 2018-2019 all'interno del corso di laurea in Ingegneria Informatica e dell'Automazione.
Esso consiste nello sviluppo di un'applicazione Java basata sul framework Spring, il quale restituisce tramite richieste API REST GET o POST dati, metadati e statistiche (in formato JSON) di un dataset assegnato dal docente.

## Modello dataset (formato TSV)

I dati contenuti all'interno del dataset sono tratti dal sito dell'Eurostat e sono relativi ad un'indagine svolta sulle città europee al fine di valutare gli sviluppi per il miglioramento della qualità della vita urbana. Tra i tanti indicatori presenti in queste statistiche, quello tenuto in considerazione nel dataset fornito riguarda l'educazione dei bambini dai 0 ai 4 anni. 
Il dataset contiene:
- l'indic_ur (urban audit indicator), ossia il tema sul quale è stata svolta l'analisi statistica,
- un codice identificativo della città presa in considerazione (indicato con cities),
- il numero di bambini per 1000 che annualmente sono iscritti all'asilo nido (pubblici e privati) anno per anno dal 1990 al 2019. 
Alcuni campi sono mancanti e l'assenza del dato è stata indicata dai due punti: per gestire quest'eccezione si è settato tale campo a -1, che non verrà quindi conteggiato nelle statistiche.
Viene effettuato ,inoltre, un controllo nel processo di importazione se in una riga è presente un numero di campi invalido: in tal caso la riga viene saltata.Nel dataset sono state inserite delle lettere accanto ai numeri che richiedono delle precisazioni per la loro piena comprensione: esse rappresentano dei commenti e non verranno prese in considerazione dall'applicazione in esame per le statistiche sui dati.

## Struttura applicazione

Il progetto è dotato di un package principale (com.digarcisi.progettoesame) all'interno del quale sono presenti tutte le classi e i relativi codici in linguaggio Java e la classe main (ProgettoEsameApplication) che consente l'avvio di Spring. 
Le classi sono state divise a loro volta in 3 packages:
 - **`modelDataSet`**, il quale contiene la classe modellante DayCareChildren.
 - **`service`** che comprende, oltre alla classe DayCareService, due packages:
    -**utils**: all'interno del quale sono state inserite le classi Parser (contenente i metadati e i metodi necessari per aprire il dataset tramite una connessione ed effettuare il parsing) e Statistics (con i metodi per implementare le statistiche richieste dalla consegna del progetto)
    -**filters**: con la classe DayCareFilters che gestiste, appunto, il filtraggio dei dati con operatori di tipo logico e condizionale.
    
 - **`controller`** con la classe DayCareController responsabile della gestione delle richieste.


## Avvio

All'avvio l'applicazione controlla se esistono già i file seriali contenenti il dataset e i metadati: in questo caso li ricarica, altrimenti effettua il parsing e salva tali file. Essa avvia, inoltre, un web server in locale sulla porta 8080 sulla quale è possibile effettuare richieste.

## Richieste

Si possono effettuare richieste di tipo GET e POST all'indirizzo  [http://localhost:8080](http://localhost:8080/)  tramite le seguenti istruzioni:
**RICHIESTE GET**

 -   **/dataset**  per ottenere i dati in formato JSON.
 -   **/metadata**  per ottenere i metadati in formato JSON.
 -   **/stats?campo="nomecampo"**(in cui "nomecampo" può essere indic_ur , cities o, per ottenere statistiche di tipo numerico, un anno compreso tra il 1990 e il 2019 inclusi) per la restituzione delle statistiche su un determinato campo del dataset.

**RICHIESTE POST**
 - **/dataset** restituisce i dati filtrati basandosi su ciò che viene inserito all'interno del body (per la sintassi si veda il paragrafo successivo).
 - **/stats?campo="nomecampo"** per ottenere le statistiche filtrate su un campo. Se non viene passato il campo su cui calcolare le statistiche (**/stats**) si prenderà come default quello inserito all'interno del body. 

**SINTASSI FILTRO**  Per le richieste POST si inserisce il filtro nel body nel modo seguente:

> {"campo" : {"operatore" : "riferimento"} }

in "campo" inserire il nome del campo da filtrare, in "operatore" inserire un operatore tra quelli implementati, in particolare:

-   operatori logici:  `"$not","$in", "$nin",`
-   operatori condizionali:  `"$gt", "$lt", "$bt"`

in "riferimento" il valore da confrontare (compatibile con il tipo del campo). 


**ESEMPI (richieste effettuate tramite POSTMAN)**

GET

```
localhost:8080/dataset
localhost:8080/metadata
localhost:8080/stats?campo=2018

```

POST

 `localhost:8080/stats`   (nel body:  `{ "2017" : { "$not" : 30 } }`)   
 `localhost:8080/stats?campo=2018`    (nel body  `{ "2018" : { "$gt" : 30 } }`  ) se nel body viene inserito  un anno differente verrà preso in considerazione quello del nomecampo nella richiesta POST 
`localhost:8080/stats`  (nel body  `{ "indic_ur" : { "$not" : TE1001I } }`)
`localhost:8080/dataset` (nel body `{ "indic_ur" : { "$not" : TE1001V} }`)

# Changelog

**ULTERIORI RICHIESTE POSSIBILI** (Tramite Postman)
**RICHIESTA POST**

 - **/add**: consente di creare una nuova rotta per inserire mediante POST nuovi dati. Essi vengono passati in formato array JSON nel body attraverso la seguente sintassi
    

>  `{ "values" : ["stringa1", "stringa2", "valori numerici"] }`

in **"stringa1"** e **"stringa2"** inserire i valori letterali desiderati, in **"valori numerici"** 30 dati numerici corrispondenti ai campi dal 1990 al 2019 presenti nel dataset. La nuova rotta consente quindi l'aggiunta solo se gli inserimenti sono compatibili con la struttura del dataset. Se non è presente un dato in un determinato anno si può scrivere -1 per indicarlo. 
**ESEMPIO** (con status code 200 OK)

    { "values" : [indicTE01,AU001,-1,100,34.4,47,55,65,789,843,932,103,115,120,132,14.6,157,16.23,17.4,185.4,193.2,209,212,23,25,276,24.3,-1,0,24,27,30] }
   se al posto di un campo letterale viene inserito un numero e viceversa viene stampato un messaggio di errore su quel campo (in cui viene specificato il tipo di dato che si sarebbe dovuto inserire)
   **ESEMPIO** (con messaggio di errore)
 

      { "values" : [27,AU001,-1,100,34.4,47,55,65,789,843,932,103,115,120,132,14.6,157,16.23,17.4,185.4,193.2,209,212,23,25,276,24.3,-1,0,24,27,30] }
         
messaggio bad request 


> "message":  "Il 1° campo inserito :27 deve essere una stringa!"

**RICHIESTA DELETE**

 - **/delete** : attraverso questa richiesta è possibile eliminare una serie di dati filtrati. Nel body sarà possibile inserire il criterio secondo la struttura

> {"campo" : {"operatore" : "riferimento"} }

**ESEMPIO**
**DELETE**
 `localhost:8080/delete`   (nel body:  `{ "2007" : { "$gt" :33} }`)
 è possibile verificare se la rimozione è avvenuta o meno andando nuovamente a cancellare gli stessi dati indicati nel body tramite un nuovo invio di richiesta: in questo caso comparirà un messaggio di errore che comunicherà l'assenza di campi per la richiesta effettuata

     "message":  "Nessun campo trovato con questo valore!"
     

 **GESTIONE ERRORI**
Sono stati gestiti errori sia nel caso in cui il campo inserito nel body non corrisponda ad uno di quelli presenti nel dataset
(**esempio**: una richiesta (DELETE con rotta \delete o POST con rotta \stats) nel body del tipo
`{ "1984" : { "$gt" :27} }` 
restituisce un messaggio di errore di bad request con messaggio 
`"message":  "Il campo 1984 non esiste!"`)
sia nel caso in cui il valore per filtrare  non sia compatibile con i dati presenti nel dataset 
(**esempio** richiesta POST alla rotta /stats con body del tipo
`{ "2008" : { "$gt" :555555555} }`
restituisce :
`"message":  "Non è stato trovato nessun campo 2008 per un filtro con il valore selezionato."`)
**ALTRE MODIFICHE** 
Sono state apportate ulteriori modifiche formali nella classe per la realizzazione dei filtri e si è garantita una maggiore chiarezza nella spiegazione dei metodi nella classe delle statistiche. Alla luce delle aggiunte sono stati revisionati i diagrammi UML.

## DIAGRAMMI UML

Si faccia riferimento alla sezione superiore per visionare i files contenenti i diagrammi UML.
