package nickguletskii200;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class.
 * 
 * @author nickguletskii200
 */
public class SpyerFun extends JavaPlugin {
	Logger log;
	private SpyerSettings ss;
	private final SpyerFunPlayerListener playerListener;
	private SpyerFunItems sfi = new SpyerFunItems();
	private MobListener ml;

	public SpyerFun() {
		super();
		playerListener = new SpyerFunPlayerListener(this);
		ml = new MobListener(this);
		setSettings(new SpyerSettings(this));
		sfi.load();
	}

	public SpyerFunItems getItems() {
		return sfi;
	}

	public void onEnable() {
		log = this.getServer().getLogger();
		getSettings().load();
		PluginManager pm = getServer().getPluginManager();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " module version "
				+ pdfFile.getVersion() + ", a " + pdfFile.getDescription()
				+ ", is active. Part of the Spyer package by "
				+ pdfFile.getAuthors().toString());
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, ml, Priority.Monitor, this);

		pm.registerEvent(Event.Type.ENTITY_DAMAGE, ml, Priority.Monitor, this);

	}

	public void onDisable() {
	}

	public SpyerFunPlayerListener getPlayerListener() {
		return playerListener;
	}

	public void setSettings(SpyerSettings ss) {
		this.ss = ss;
	}

	public SpyerSettings getSettings() {
		return ss;
	}
}
