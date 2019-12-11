## Presentazione generale del progetto

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
**`modelDataSet`**, il quale contiene la classe modellante DayCareChildren.
**`service`** che comprende, oltre alla classe DayCareService due packages:
      -**utils**: all'interno del quale sono state inserite le classi Parser (contenente i metadati e i metodi necessari per aprire il dataset tramite una connessione  
                  ed effettuare il parsing) e Statistics (con i metodi per implementare le statistiche richieste)
      -**filters**: con la classe DayCareFilters che gestiste, appunto, il filtraggio dei dati con operatori di tipo logico e condizionale.
**`controller`** con la classe DayCareController responsabile della gestione delle richieste.

## Avvio

All'avvio l'applicazione controlla se esistono già i file seriali contenenti il dataset e i metadati: in questo caso li ricarica, altrimenti effettua il parsing e salva tali file. Essa avvia, inoltre, un web server in locale sulla porta 8080 sulla quale è possibile effettuare richieste.

## Richieste

Si possono effettuare richieste di tipo GET e POST all'indirizzo http://localhost:8080 tramite le seguenti istruzioni:
**/dataset** per ottenere i dati in formato JSON
**/metadata** per ottenere i metadati in formato JSON
**/stats?campo="nomecampo"**(in cui "nomecampo" può essere indic_ur , cities o, per ottenere statistiche di tipo numerico, un anno compreso tra il 1990 e il 2019 inclusi) per la restituzione delle statistiche sul dataset
