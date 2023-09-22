package mokkagnom.dasgesetz.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class dasGesetz implements TabExecutor
{

	private String[] dasGesetzStr = { "(1) Die Würde eines Spielers ist unantastbar.\n",
			"(2) Niemand darf einen anderen Spieler, ohne ausdrückliche Erlaubnis des betroffenen oder des Gerichtes, töten.\n",
			"(3) Niemand darf das Haus einer anderen Person anzünden oder sprengen, weder mit TNT, noch durch einen Creeper (siehe Artikel 4).\n",
			"(4) Niemand darf einen Creeper in oder zum Haus eines anderen Spielers locken, sodass er dort explodiert.\n",
			"(5) Sollte ein Spieler unabsichtlich den Besitz eines anderen Spielers zerstören, hat er diesen umgehend zu ersetzen und den betroffenen Spieler zu benachrichtigen.\n",
			"(6) Niemand darf sich an den Kisten eines anderen Spielers bedienen, ohne die ausdrückliche Erlaubnis des Besitzers.\n",
			"(7) Wenn eines der Gesetze verletzt wird, wird ein Gericht (siehe Artikel 8 & 23) über die Strafe entscheiden.\n",
			"(8) Das Gesetzt wird spontan zusammengesetzt aus drei (siehe Artikel 9 & 18) Spielern welche sich zu dieser Zeit auf dem Server befinden.\n",
			"(9) Sollten sich zur der Zeit, wo ein Gericht benötigt wird mehr als drei Spieler auf dem Server befinden, wird die Zusammensetzung des Gerichtes gelost.\n",
			"(10) Wenn mindestens 50% der Spieler, welche sich auf dem Server befinden, möchten, dass geschlafen wird, dann müssen die Übrigen 50% der Bitte nachkommen (siehe Artikel 11).\n",
			"(11) Wenn man schlafen muss (siehe Artikel 10) kann man dies tun, indem man schläft oder den Server verlässt, bis es Tag geworden ist.\n",
			"(12) Niemand darf einem anderen Spieler eine Falle stellen, wodurch dieser zu Schaden (siehe Artikel 13) kommt.\n",
			"(13) Niemand darf einem anderen Spieler Schaden (siehe Artikel 26) zufügen, sowohl physisch als auch psychisch.\n",
			"(14) Spieler, welche die Entscheidungen des Gerichtes ignorieren, werden vorübergehend vom Schutz des Servers ausgeschlossen (siehe Artikel 24 & 25).\n",
			"(15) Das wiederholte Benutzen eines Command-Blockes innerhalb kürzester Zeit ist nicht gestattet.\n",
			"(16) Sollte die Person, welche den Schaden repariren bzw. ein Item ersetzen muss (siehe Artikel 4-6) nicht in der Lage dazu sein, so hat er dem betroffenen Spieler einen gleichwertigen Ersatz zu erbringen (siehe Artikel 17).\n",
			"(17) Ob ein Ersatz gleichwertig ist, wird von der betroffenen Person entschieden, sollte es jedoch zu Uneinigkeiten kommen, wird das Gericht darüber entscheiden.\n",
			"(18) Sollte sich zu der Zeit, in der ein Gericht benötigt wird weniger als drei unbetroffene Spieler auf dem Server befinden, so wird auf Discord nach freiwilligen unbetroffenen Spielern gefragt, sollten sich auch dort keine finden, so wird die Gerichtsverhandlung vertagt.\n",
			"(19) Sollte ein Spieler durch ein Nicht-Lebewesen während er sich unbefugten Zutritt zu einem Haus bzw. Grundstück eines anderen Spielers verschafft hat, zu Schaden kommen, muss der Spieler welchem das Haus bzw. das Grundstück gehört, nicht dafür haften.\n",
			"(20) Sollte ein Spieler sich wiederholt, regelmäßig nicht an die Gesetze des Servers halten, so wird er vom Server verbannt (siehe Artikel 21).\n",
			"(21) Bei der Verbannung eines Spielers entscheidet der ganze Server (siehe Artikel 22) per Abstimmung, in welcher mindestens 60% dafür stimmen müssen, damit das Ergebnis positiv ist, ob der Spieler verbannt werden soll. Es muss auch festgelegt werden wie lange der Spieler verbannt werden soll.\n",
			"(22) Zum Server gehören die Personen, welche mehr als eine Stunde auf dem Server gespielt haben.\n",
			"(23) Das Gericht muss nach dem Gesetz und unabhängig von anderen Personen, Entscheidungen treffen. Das Gericht darf nicht bestochen werden.\n",
			"(24) Ein Spieler ist solange vom Schutz des Staates ausgeschlossen (siehe Artikel 14), bis er einmal von einem anderen Spieler umgebracht worden ist, danach wird er wieder vom Server geschützt.\n",
			"(25) Wenn ein Spieler nicht mehr vom Server geschützt wird, muss dieser trotzdem die Gesetze beachten.\n",
			"(26) Als Schaden wird mind. ein Schaden von einem ganzen Herzen angesehen.\n", "(27) Man stimmt diesem Gesetzbuch automatisch zu indem man den Server betritt.\n",
			"(28) Ein Spieler darf nicht ohne Erlaubnis des Besitzers Gegenstände oder Blöcke in oder um das Haus eines anderen Spielers platzieren.\n",
			"(29) Ein Spieler darf keine Blöcke, welche sich auf dem Grundstück eines anderen Spielers befinden, ohne dessen Einverständnis, verändern. \n",
			"(30) Wenn der betroffene Spieler, dem Spieler, die ausdrückliche Erlaubnis gegeben hat, ist diese Tat von der Strafverfolgung ausgeschlossen. \n" };

	public String getGesetz(int a)
	{

		if (a >= 0 && a < dasGesetzStr.length)
		{
			return dasGesetzStr[a];
		}
		return "Ungültige Nummer, gültige Nummern 1-" + dasGesetzStr.length;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			for (int i = 0; i < dasGesetzStr.length; i++)
			{
				sender.sendMessage(dasGesetzStr[i]);
			}
			return true;
		}
		else if (args.length == 1)
		{
			sender.sendMessage(getGesetz(Integer.parseInt(args[0]) - 1));
			return true;
		}
		else
		{
			sender.sendMessage("Ungültiger Syntax");
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1)
		{
			List<String> list = new ArrayList<String>();
			for (int i = 1; i <= dasGesetzStr.length; i++)
			{
				list.add(Integer.toString(i));
			}
			return list;
		}
		return Arrays.asList("");
	}
}
