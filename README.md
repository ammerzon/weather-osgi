# Weather OSGi

## Parameter Konfiguration
* Go to [http://localhost:8181/system/console](http://localhost:8181/system/console)
* Use karaf/karaf to log in

## Funktionale Anforderungen

Erweitern Sie das Beispiel aus der Übung (osgi-weather) um folgende Aspekte:
* **Regensensor**: Erweitern Sie die Wetterstation um einen Regensensor, der die aktuell gemessene Regenmenge an die Wetterstation meldet. Auf welchen Zeitbereich sich die Messung bezieht, soll konfigurierbar sein.
* **Niederschlag akkumulieren**: Ermitteln Sie die kumulierte Regenmenge in einem konfigurierbaren Zeitintervall (z. B. über einen Tag). Diese Aufgabe soll die Wetterstation und nicht der Regensensor (der möglichst „dumm“ sein soll) erledigen.
* **Visualisierung Niederschlag**: Die gemessenen Regenmengen (aktuelle und kumulierte Regenmenge) sind über einen REST-Endpunkt zu exportieren und in der Web-Anwendung zu visualisieren (überlagerte Darstellung).
* **Sonneneinstrahlungs-Sensor**: Erweitern Sie die Wetterstation um eine Möglichkeit zur Messung der Sonneneinstrahlung (Watt/m2). Da die Messung dieses Wertes sehr fehlerbehaftet ist, sollen in der Wetterstation dafür mehrere Sensoren installierbar sein. Die Wetterstation sammelt die aktuellen Werte aller verfügbaren Sonneneinstrahlungs-Sensoren und ermittelt daraus einen Mittelwert. So kann der Messfehler verringert werden.
* **Visualisierung Sonneneinstrahlung**: Der Verlauf der Sonneneinstrahlung ist in der Web-Anwendung zu visualisieren.

## Technische Anforderungen
* **Dynamische Services**: Alle Systemkomponenten müssen in Form von dynamischen Services realisiert werden. Beim Deaktivieren einzelner Komponenten muss das Gesamtsystem weiterhin funktionieren. Alle Systemkomponenten müssen im laufenden Betrieb gegen neuere Version ausgetauscht werden können. Sonneneinstrahlungs-Sensoren müssen hinzugefügt bzw. entfernt werden können.
* **Visualisierung**: Gestalten Sie Ihre Anwendung so, dass Sie vor allem die dynamischen Aspekte Ihres Systems gut demonstrieren können. In der Webanwendung soll klar sichtbar gemacht werden, welche Komponenten gerade aktiv sind.
* **Konfigurierbarkeit** (Bonusaufgabe, nur für Beurteilung mit „Sehr Gut“ erforderlich): Machen Sie Ihre Services noch flexibler, indem wesentliche Parameter dynamisch konfiguriert werden können. Verwenden Sie dazu das OSGi-Standardservice Configuration Admin Service (https://osgi.org/specification/osgi.cmpn/7.0.0/service.cm.html). Änderungen an Konfigurationsparametern kann man beispielsweise über die Webanwendung oder über ein benutzerdefiniertes Karaf-Kommando durchführen.