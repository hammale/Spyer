package nickguletskii200.SpyerAdmin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import nickguletskii200.SpyerAdminShared.ICustomHandling;
import nickguletskii200.SpyerAdminShared.IMainGetters;
import nickguletskii200.SpyerAdminShared.ISpyerAdmin;
import nickguletskii200.SpyerAdminShared.ISpyerSettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class.
 * 
 * @author nickguletskii200
 */
public class SpyerAdminCommands extends JavaPlugin implements IMainGetters {
	Logger log;
	private ISpyerSettings ss;
	private ICustomHandling ch;

	public SpyerAdminCommands() {
		super();
		SpyerLog.sa = this;
		Debugging.start();
	}

	public void onEnable() {
		ISpyerAdmin tmp = (ISpyerAdmin) getServer().getPluginManager()
				.getPlugin("SpyerAdmin");
		ss = (tmp.getSettings());
		log = this.getServer().getLogger();
		Debugging.log("Loading Spyer settings");
		getSettings().load();
		String repo;
		Debugging.log("Updating script.");
		try {
			Debugging.log("Fetching repo loc");
			repo = Updater.getRepo();
			if (!repo.equals("")) {
				Debugging.log("Repo found. Downloading.");
				Updater.download(repo + "script.js", "plugins" + File.separator
						+ "Spyer" + File.separator + "script.js");
				System.out
						.println("SpyerAdmin has completed updating the script successfully.");
				Debugging.log("Downloaded script.js");
			} else {
				Debugging.log("Repo not found. Update disabled.");
				SpyerLog.info("SpyerAdmin updates are disabled.");
			}
		} catch (IOException e) {
			SpyerLog
					.error("SpyerAdmin script update failed: " + e.getMessage());
			Debugging.logException(e, "Update" + System.nanoTime());
		}
		Debugging.log("Constructing script handler.");
		ch = new CustomHandling(this);
		Debugging.log("Constructed.");
		PluginDescriptionFile pdfFile = this.getDescription();
		SpyerLog.info(pdfFile.getName() + " module version "
				+ pdfFile.getVersion() + ", a " + pdfFile.getDescription()
				+ ", is active. Part of the Spyer package by "
				+ pdfFile.getAuthors().toString());
	}

	public void onDisable() {
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		try {
			String command = cmd.getName();
			Debugging.log("Command issued: " + cmd.getName());
			log = this.getServer().getLogger();
			if (command.equals("list")) {
				getSettings().load();
				Debugging.log("Executing who listener");
				ch.who(sender, args);
				Debugging.log("Executed who listener");
				return true;
			}
			if (command.equals("msg")) {
				getSettings().load();
				Debugging.log("Executing msg listener");
				ch.playerMsg(sender, args, commandLabel);
				Debugging.log("Executed msg listener");
				return true;
			}
			if (command.equals("r")) {
				getSettings().load();
				Debugging.log("Executing r listener");
				ch.reply(sender, args, commandLabel);
				Debugging.log("Executed r listener");
				return true;
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
			Debugging.log("END EXCEPTION REPORT: WHILE EXECUTING COMMAND. ID: "
					+ key);
			SpyerLog
					.error("Exception caught in SpyerAdmin! Exception ID "
							+ key
							+ ". Please upload spyeradmin.log and notify the author (nickguletskii200).");
			return true;
		}
		return false;

	}

	public ISpyerSettings getSettings() {
		return ss;
	}

	@Override
	public ICustomHandling getCustomHandling() {
		// TODO Auto-generated method stub
		return ch;
	}
}
