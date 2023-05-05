# Wiki Neo4j Starter

Dieses Projekt beinhaltet Skripte zum Einrichten und Verwalten von Neo4j sowie drei Starter-Applikationen.

## Ziel

Das Ziel ist es nun unser gelerntes Graph und cypher wissen von Java aus anzuwenden.
Als datenbasis verwenden wir Wikipedia seiten und wie diese untereinander verlinkt sind.
Jetzt wollen wir dem Benutzer helfen Möglichst gut beim Spiel https://www.thewikigame.com/ zu werden.
Falls ihr das Spiel nicht kennt, schaut es euch mal an und spielt eine runde.

## Setup

Wählt entweder https://en.wikipedia.org/ (benötigt ca. 20 GB Speicherplatz) oder die leichtgewichtigere
Version https://simple.wikipedia.org/ (benötigt ca. 2 GB Speicherplatz). Die ZIP-Dateien mit den Wikipedia-Daten werden
vom Lehrer bereitgestellt.

Entpackt die gewählte Datei im Verzeichnis ~/wikidata/, sodass die CSV-Dateien unter ~/wikidata/en_wiki/ bzw. ~
/wikidata/simple_wiki/ zu finden sind.

Für das Setup gibt es für beide Wikis separate Ordner: neo4j-en-wiki und neo4j-simple-wiki. Nachdem ihr die CSV-Dateien
am richtigen Ort entpackt habt, navigiert zum gewünschten Ordner und befolgt die Anweisungen im README.md, um das Setup
durchzuführen und Neo4j zu starten oder zu stoppen.

## Format

Die importierte Datenbank enthält eine art von Knoten :Page und eine art von Kanten :LINKS_TO.
Die Pages Knoten haben folgende Attribute:

| Feld               | Beispiel    | Beschreibung                                                                                                    |
|--------------------|-------------|-----------------------------------------------------------------------------------------------------------------|
| urlTitle:ID        | Alan_Turing | Dieses Feld wird als eindeutige ID in Neo4j verwendet und ist der Titel, formatiert für den Wikipedia-URL-Pfad. |
| title:string       | Alan Turing | Der Titel der Seite.                                                                                            |
| pageId:long        | 1261710     | Die interne Seiten-ID von Wikipedia, die mit der Wikipedia-API verwendet werden kann.                           |
| isRedirect:boolean | false       | Gibt an, ob die Seite auf eine andere Seite weiterleitet.                                                       |

Die Links Kanten haben folgende Attribute:

| Feld               | Beispiel       | Beschreibung                                                  |
|--------------------|----------------|---------------------------------------------------------------|
| :START_ID          | Alan_Turing    | Der URL-Titel der Seite, auf der sich der Link befindet.      |
| :END_ID            | Human_rights   | Der URL-Titel der Zielseite.                                  |
| text:string        | Menschenrechte | Der auf der Webseite sichtbare Linktext.                      |
| isRedirect:boolean | false          | Gibt an, ob die :START_ID-Seite eine Weiterleitungsseite ist. |
| index:long         | 45             | Die Position des Links auf der Seite.                         |

## Web GUI

Nachdem ihr das gewünschte Wiki importiert habt, könnt ihr das Admin GUI lokal unter http://localhost:7474/ öffnen. Dort
werdet ihr mit einem Anmeldebildschirm begrüßt. Wählt einfach "no authentication" aus. Anschließend könnt ihr euren
ersten Cypher-Query testen. Verwendet zum Beispiel:

```cypher
MATCH (page:Page {urlTitle: 'Switzerland'})
RETURN page
```

Ihr solltet anschließend folgendes Resultat sehen:

![](./assets/admin-gui-start.png)

In der Sektion [Beispielabfragen Cypher](#beispielabfragen-cypher) findet ihr noch mehr beispiele.

## Indizes

Da unsere Suchen meist über das 'urlTitle'-Attribut laufen, können wir einen Index darüber erstellen. Dies wird die
Abfragen erheblich beschleunigen. Führt dazu lediglich folgendes Query im Admin GUI aus (Das Anlegen des Indexes kann
eine Weile dauern):

```cypher
CREATE CONSTRAINT pages_urlTitle
FOR (p:Page) REQUIRE p.urlTitle IS UNIQUE
```

## Projekte

TODO:

## Beispielabfragen Cypher

Ein paar Beispielabfragen die ihr im Admin GUI ausprobieren könnt.

### Kürzester Pfad

Ermitteln des kürzesten Pfades zwischen der Schweiz und den USA und dessen Rückgabe:

```cypher
MATCH path=shortestPath((startPage:Page)-[:LINKS_TO*1..20]->(endPage:Page))
WHERE startPage.urlTitle = "Switzerland" AND endPage.urlTitle = "USA"
RETURN path
```

Wenn ihr das vorherige Beispiel ausführt und dann im Admin GUI zur Tabellenansicht wechselt, seht ihr nur eine Zeile mit
einer großen JSON-Datei. Um mehrere Zeilen mit nur den gewünschten Informationen zu erhalten, können wir folgendes
anwenden.

Ihr könnt die Funktion nodes() auf den Pfad anwenden, um eine Liste der Knoten darin zu erhalten. Wenn ihr nur "RETURN
nodes(path)" zurückgeben würdet und die Tabellenansicht betrachtet, säht ihr erneut nur eine Zeile mit einer JSON-Liste.
Daher müssen wir einen zweiten Schritt mit "UNWIND" durchführen. Mit "UNWIND nodes(path) AS node" erhalten wir alle
Knoten im Pfad als separate Zeilen, was dem SQL-Standardverhalten bei Joins ähnlicher ist.

```cypher
MATCH path=shortestPath((startPage:Page)-[:LINKS_TO*1..20]->(endPage:Page))
WHERE startPage.urlTitle = "Switzerland" AND endPage.urlTitle = "USA"
UNWIND nodes(path) AS node
RETURN node.urlTitle as urlTitle
```

### Summieren aller indexen in einem Pfad

Suchen des kürzesten Pfades und summieren aller indexen in diesem Pfad.
Hir wird WITH verwendet, um sich die Zwischenergebnisse wie variablen zu merken.
Die Syntax ist wie folgt.

```
WITH <value> as <variable>, <value> as <variable>, ...
```

Zu beachten ist, dass nach der With Klausel nur die Variablen verwendet werden können, die in der With Klausel definiert
wurden.  
Mit der funktion reduce wird die Index summe berechnet. Diese funktioniert sehr ähnlich wie die reduce funktion in java.
Die Syntax sieht nur etwas anders aus.

```
reduce(<initial_value>, <variable> IN <list> | <expression>)
```

Die gegebene expression wird dann für jedes element in der Liste ausgeführt.
Das resultat der expression wird dan in der nächsten iteration als initial_value verwendet.

```cypher
MATCH path=shortestPath((startPage:Page)-[:LINKS_TO*1..20]->(endPage:Page))
WHERE startPage.urlTitle = "Switzerland" AND endPage.urlTitle = "USA"
WITH
    startPage as startPage,
    endPage as endPage,
    length(path) as pathLength,
    reduce(indexSum = 0, rel IN relationships(path) | indexSum + rel.index) AS indexSum
RETURN startPage.urlTitle, endPage.urlTitle, indexSum, pathLength
```

## Links

- Cypher Spickzettel: https://neo4j.com/docs/cypher-cheat-sheet/current/
- Cypher Match: https://neo4j.com/docs/cypher-manual/5/clauses/match/
- Cypher Listenfunktionen: https://neo4j.com/docs/cypher-manual/current/functions/list/
- Cypher Listenfunktionen reduce: https://neo4j.com/docs/cypher-manual/current/functions/list/#functions-reduce
- Cypher kürzester Pfad: https://neo4j.com/docs/cypher-manual/current/clauses/match/#query-shortest-path
- Cypher alle kürzesten Pfade: https://neo4j.com/docs/cypher-manual/current/clauses/match/#all-shortest-paths
