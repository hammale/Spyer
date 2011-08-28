package nickguletskii200.SpyerAdmin;

import java.util.HashSet;
import java.util.logging.Logger;

import nickguletskii200.SpyerAdminShared.ICustomHandling;
import nickguletskii200.SpyerAdminShared.IMainGetters;
import nickguletskii200.SpyerAdminShared.ISpyerAdmin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class.
 * 
 * @author nickguletskii200
 */
public class SpyerAdmin extends JavaPlugin implements ISpyerAdmin {
	Logger log;
	private SpyerSettings ss;
	private final SpyerAdminPlayerListener playerListener;
	// A list o people currently spying.
	private HashSet<String> spying = new HashSet<String>();
	public ICustomHandling ch;
	// HashSet<String> antigrief = new HashSet<String>();
	private MobListener ml;
	private boolean firstrun = true;

	public SpyerAdmin() {
		super();
		playerListener = new SpyerAdminPlayerListener(this);
		ml = new MobListener(this);
		ss = (new SpyerSettings(this));
		// SpyerLog.sa = this;
		Debugging.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerAdmin#onEnable()
	 */
	public void onEnable() {
		log = this.getServer().getLogger();
		Debugging.log("Loading Spyer settings");
		getSettings().load();
		ch = ((IMainGetters) getServer().getPluginManager().getPlugin(
				"SpyerAdminCommands")).getCustomHandling();
		PluginDescriptionFile pdfFile = this.getDescription();
		SpyerLog.info(pdfFile.getName() + " module version "
				+ pdfFile.getVersion() + ", a " + pdfFile.getDescription()
				+ ", is active. Part of the Spyer package by "
				+ pdfFile.getAuthors().toString());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener,
				getSettings().chatPriority, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener,
				Priority.Monitor, this);

		pm.registerEvent(Event.Type.ENTITY_TARGET, ml, Priority.Monitor, this);
		if (!firstrun) {
			for (String str : getPlayerListener().hideOnJoin.keySet()) {
				getPlayerListener().vanish(getServer().getPlayer(str));
				getServer().getPlayer(str).sendMessage(
						ChatColor.GREEN
								+ "You were made invisible after a reload.");
				getPlayerListener().hideOnJoin.remove(str);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerAdmin#onDisable()
	 */
	public void onDisable() {
		firstrun = false;
		Debugging.stop();
		for (String str : getPlayerListener().commonPlayers) {
			Player plr = getServer().getPlayer(str);
			// plr.sendMessage("Reload in progress! Invisiblity will be lost.");
			if (getSettings().wait) {
				plr.sendMessage("Reload in progress! In 10 seconds invisiblity will be lost.");
			} else {
				plr.sendMessage("Reload in progress! Invisiblity will be lost.");
			}
		}
		if (getSettings().wait) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (String str : getPlayerListener().commonPlayers) {
			Player plr = getServer().getPlayer(str);
			getPlayerListener().reappear(plr);
			getPlayerListener().hideOnJoin.put(plr.getName(),
					antigrief.contains(plr.getName()));
			getPlayerListener().quit(plr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerAdmin#getPlayerListener()
	 */
	public SpyerAdminPlayerListener getPlayerListener() {
		return playerListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ISpyerAdmin#onCommand(org.bukkit.command.
	 * CommandSender, org.bukkit.command.Command, java.lang.String,
	 * java.lang.String[])
	 */
	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		try {
			String command = cmd.getName();
			Debugging.log("Command issued: " + cmd.getName());
			log = this.getServer().getLogger();

			if ((sender instanceof Player)) {
				Player localObject = (Player) sender;
				this.getSettings().load();
				if (command.equals("spy")
						&& (getSettings().isSpy(localObject.getName()) || localObject
								.isOp())) {
					if (args.length == 0) {
						if (spying.contains((localObject).getName())) {
							getPlayerListener().reappear(localObject);
							spying.remove((localObject).getName());
						} else {
							getPlayerListener().vanish(localObject);
							spying.add((localObject).getName());
						}
					} else {
						if (args[0].equals("off")) {
							getPlayerListener().reappear(localObject);
							spying.remove((localObject).getName());
						}
						if (args[0].equals("on")) {
							getPlayerListener().vanish(localObject);
							spying.add((localObject).getName());
						}
					}
					return true;
				}
				if (command.equals("quit")
						&& (this.getSettings().isSpy(localObject.getName()) || localObject
								.isOp())) {
					if (args.length == 0) {
						if (this.antigrief.contains((localObject).getName())) {
							getPlayerListener().reappear(localObject);
							antigrief.remove((localObject).getName());
							ch.playerJoin((Player) sender);
						} else {
							getPlayerListener().vanish(localObject);
							antigrief.add((localObject).getName());
							ch.playerLeave((Player) sender);

						}
					} else {
						if (args[0].equals("off")
								&& this.antigrief.contains((localObject)
										.getName())) {
							getPlayerListener().reappear(localObject);
							antigrief.remove((localObject).getName());
							ch.playerJoin((Player) sender);
						}
						if (args[0].equals("on")
								&& !this.antigrief.contains((localObject)
										.getName())) {
							getPlayerListener().vanish(localObject);
							antigrief.add((localObject).getName());
							ch.playerLeave((Player) sender);
						}
					}
					return true;
				}
			}
			if (!sender.isOp()) {
				if (sender instanceof Player) {
					Player plr = (Player) sender;
					if (!getSettings().isSpy(plr.getName())) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			String key = "CMD" + System.currentTimeMillis();
			Debugging
					.log("BEGIN EXCEPTION REPORT: WHILE EXECUTING COMMAND. ID: "
							+ key);
			Debugging.logException(e, key);
			Debugging.log("VAR TRACE:");
			Debugging.log("Timers:");
			Debugging.logHashMap(this.playerListener.timers, key);
			Debugging.log("Schedulers:");
			Debugging.logHashMap(this.playerListener.schedulers, key);
			Debugging.log("playerHideTree:");
			Debugging.logHashMap(this.playerListener.playerHideTree, key);
			Debugging.log("END EXCEPTION REPORT: WHILE EXECUTING COMMAND. ID: "
					+ key);
			SpyerLog.error("Exception caught in SpyerAdmin! Exception ID "
					+ key
					+ ". Please upload spyeradmin.log and notify the author (nickguletskii200).");
			return true;
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerAdmin#getSettings()
	 */
	public SpyerSettings getSettings() {
		return ss;
	}
}