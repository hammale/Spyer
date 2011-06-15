package nickguletskii200.SpyerAdmin;

import java.util.HashSet;

import nickguletskii200.SpyerAdminShared.ISpyerAdmin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Scripting interface.
 * 
 * @author nickguletskii200
 */
public class SafeSpyerCheck {
	private ISpyerAdmin plugin;

	public SafeSpyerCheck(ISpyerAdmin plug) {
		plugin = plug;
		System.out.println(plugin);
	}

	public boolean hasPermission(CommandSender send, String perm) {
		if (send instanceof Player) {
			return plugin.getSettings().canUse(
					plugin.getServer().getPlayer(((Player) send).getName()),
					perm);
		} else {
			return true;
		}
	}

	@SuppressWarnings("static-access")
	public boolean isQuit(String name) {
		return plugin.antigrief.contains(name);
	}

	public boolean isSeeAll(CommandSender cms) {
		if (cms instanceof Player) {
			return isSeeAll(((Player) cms).getName());
		}
		return true;
	}

	public boolean isSeeAll(String name) {
		return plugin.getSettings().isSeeAll(name);
	}

	@SuppressWarnings("static-access")
	public HashSet<String> quitPlayers() {
		return plugin.antigrief;
	}

	public boolean isQuit(CommandSender cms) {
		if (cms instanceof Player) {
			return isQuit(((Player) cms).getName());
		}
		return false;
	}

	public StringBuilder newStringBuilder() {
		return new StringBuilder();
	}

	@SuppressWarnings("static-access")
	public int numOfHidenPlayers() {
		return plugin.antigrief.size();
	}
}
