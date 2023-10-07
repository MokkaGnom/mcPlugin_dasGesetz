----------------------
Das Gesetz
by MokkaGnom ©2023
----------------------

"dasGesetz" ist ein Minecraft Bukkit Plugin, welches mehrere Sub-Plugins beinhaltet.
Diese können mit dem "/dgManager <plugin> <0/1>" -command aktiviert oder deaktiviert werden.
Die Änderungen werden erst nach einem Serverneustart wirksam.
Anstelle des commands kann auch die config.yml-Datei benutzt werden.
In der config.yml-Datei können auch Einstellungen für die Sub-Plugins getroffen werden.

WICHTIG: Nach dem Installieren des Plugins muss der Server zweimal neugestartet werden, damit das Plugin voll funktionsfähig ist.

Timber:
	Funktioniert nur mit Äxten, welche eine ausreichende Haltbarkeit haben.
	Alle Blöcke des gleichen Stammtypes über dem abgebauten Block werden entfernt und in das Inventar des Spielers gelegt, wenn das Inventar voll ist, werden die Items auf den Boden gedroppt.
	Die Haltbarkeit wird entsprechend von der Axt abgezogen.
	Wenn in der config "BreakLeaves" auf true ist, werden alle (außer vom Spieler platzierte) Blätter um den Baum mit entfernt.

EasyFarming:
	Wenn man Rechtsklick auf reifes Weizen, Rote Beete, Kartoffeln oder Karotten macht, wird das entsprechende Item abgebaut und automatisch wieder angepflanzt.

DeathChest:
	Wenn ein Spieler stirbt droppen die Items nicht, sondern es wird an dem Todesort eine Kiste platziert, dort werden die Items gelagert.
	Nur der Spieler, welchem die Kiste gehört, kann die Kiste öffnen.
	Wenn ein anderer Spieler versucht die Kiste zu öffnen, wird der Besitzer benachrichtigt.
	Wenn man sie sneak-öffnet dann werden so viele Items wie möglich von der Kiste ins Inventar des Spielers gelegt.
	Wenn die Kiste leer ist, verschwindet sie.
	Bei Serverneustart verschwinden alle DeathChests!
	
BlockLock:
	Truhen/Türen/Hopper/Öfen/etc. werden beim platzieren automatisch verschlossen (Doppeltruhen müssen einzeln gelockt/geunlockt werden).
	Man kann das Schloss und die Kompatibilität (z.B. Redstone, Trichter, etc.) mit commands "/lockBlock <lock/unlock/addFriend/removeFriend>" oder mit einem shift + Rechtsklick verwalten.
	Der Block ist geschützt vor: Benuten/Abbauen von anderen Spielern, Redstone Signal, Trichter, Abbauen vom Block darunter, Explosionen (TNT, Creeper, Wither).
	
Home:
	Ein Spieler kann mit dem command "/home add <name>" einen tp-Punkt an seiner aktuellen Position erstellen.
	Zu seinen erstellen Punkten kann er sich mit "/home tp <name>" teleportieren.
	Mit "/home remove <name>" kann er den Punkt löschen.

Sonstige Commands:
	/coords <Player> Gibt die Koordinaten des jeweiligen Spielers.
	/weatherClear Setzt das Wetter auf klar.
	/dasGesetz <Artikel> Gibt das Gesetz, oder nur den jeweiligen Artikel.
	
BlockLogger:
	Wenn ein Spieler einen Sweet-Berry-Busch oder einen Blitzableiter platziert, wird der Ort und die Zeit in der jeweiligen log-Datei gespeichert.
	
Messages:
	Wenn ein Spieler dem Server joint wird ihm eine Nachricht angezeigt.
	Die Nachricht wird in der Config-Datei festgelegt.
	
Ping:
	Wenn ein Spieler einen Stick in der off-Hand hält und dann einen Rechtsklick macht, "pinged" er an der Stelle, wo er hinguckt.
	Ein Ping ist eine Effekt-Wolke mit Nametag (Name des Spielers).
	Der Ping hat einen cooldown, welcher in der Config-Datei festgelegt werden kann.
	Der Ping ist nur für eine gewisse Zeit sichtbar. Die Zeit kann in der config-Datei angepasst werden.
	Die Farbe der Wolke kann mit dem Command: "/ping setColor <colorcode>" verändert werden.
	Die Zeiten in der Config-Datei sind in Millisekunden anzugeben.
	