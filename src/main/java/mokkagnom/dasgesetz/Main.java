package mokkagnom.dasgesetz;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	private Manager manager;

	// When plugin is enabled / on server start
	@Override
	public void onEnable()
	{
		manager = new Manager(this);
		this.getCommand("dgManager").setExecutor(manager);
		this.getCommand("dgManager").setTabCompleter(manager);
		manager.createConfig();
		manager.enableAll();
	}

	// When plugin is disabled / on server shutdown
	@Override
	public void onDisable()
	{
		manager.disableAll();
	}
}
