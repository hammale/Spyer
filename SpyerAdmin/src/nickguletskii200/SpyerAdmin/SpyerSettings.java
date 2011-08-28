package nickguletskii200.SpyerAdmin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import nickguletskii200.SpyerAdminShared.ISpyerAdmin;
import nickguletskii200.SpyerAdminShared.ISpyerSettings;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.yaml.snakeyaml.Yaml;

/**
 * Main config class. Now with permissions.
 * 
 * @author nickguletskii200
 */
public class SpyerSettings extends HashMap<String, Object> implements
		ISpyerSettings {

	private static final long serialVersionUID = 7771694943392484453L;
	// private PermissionHandler Permissions;
	// private Boolean UsePermissions = null;
	private ISpyerAdmin plugin;
	public boolean pickup = false;
	public boolean invincible = false;
	public boolean noHit = false;
	public boolean wait = true;
	private long step = 8000;
	public Event.Priority chatPriority = Event.Priority.Monitor;
	private HashMap<String, booleanAndInt> seeAllCache = new HashMap<String, booleanAndInt>();

	public SpyerSettings(ISpyerAdmin plugin) {
		this.put("refreshRate", 1000);
		this.put("syncWithScheduler", false);
		this.plugin = plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#getRefreshRate()
	 */
	public int getRefreshRate() {
		if (!this.containsKey("refreshRate")) {
			return 1000;
		}
		if (!(this.get("refreshRate") == null)) {
			return (Integer) this.get("refreshRate");
		}
		return 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#setRefreshRate(int)
	 */
	public void setRefreshRate(int i) {
		this.remove("refreshRate");
		this.put("refreshRate", i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#getSync()
	 */
	public boolean getSync() {
		if (!this.containsKey("syncWithScheduler")) {
			return false;
		}
		if (!(this.get("syncWithScheduler") == null)) {
			return (Boolean) this.get("syncWithScheduler");
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#setSync(boolean)
	 */
	public void setSync(boolean b) {
		this.remove("syncWithScheduler");
		this.put("syncWithScheduler", b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#isSpy(java.lang.String)
	 */
	public boolean isSpy(String name) {
		try {
			return canUse(plugin.getServer().getPlayer(name), "spyer.spy");
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ISpyerSettings#isVisibleByMobs(java.lang.
	 * String)
	 */
	public boolean isVisibleByMobs(String name) {
		try {
			return !canUse(plugin.getServer().getPlayer(name),
					"spyer.stopmobs.admin");
		} catch (Exception e) {
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ISpyerSettings#canUseFun(java.lang.String)
	 */
	public boolean canUseFun(String name) {
		try {
			Player player = Bukkit.getServer().getPlayer(name);
			if (player == null) {
				return false;
			}
			return canUse(player, "spyer.fun");
		} catch (Exception e) {
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ISpyerSettings#isSeeAll(java.lang.String)
	 */
	public boolean isSeeAll(String name) {
		// try {
		boolean ret = false;
		if (seeAllCache.containsKey(name)) {
			booleanAndInt b = seeAllCache.get(name);
			if (System.currentTimeMillis() - b.time <= step) {
				ret = b.result;
			} else {
				ret = canUse(plugin.getServer().getPlayer(name), "spyer.seeAll");
				seeAllCache.get(name).time = System.currentTimeMillis();
				seeAllCache.get(name).result = ret;
			}
		} else {
			ret = canUse(plugin.getServer().getPlayer(name), "spyer.seeAll");
			booleanAndInt tmp = new booleanAndInt();
			tmp.time = System.currentTimeMillis();
			tmp.result = ret;
			seeAllCache.put(name, tmp);
		}
		return ret;
		// /} catch (Exception e) {
		// }
		// return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#dump()
	 */
	public void dump() {
		Yaml yaml = new Yaml();
		String out = yaml.dump(this);
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("plugins"
					+ File.separator + "Spyer" + File.separator + "spyer.yml"));
			pw.print(out);
			pw.close();
		} catch (IOException e) {
			SpyerLog.info("IO Exception, could not save spyer data. \n"
					+ e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nickguletskii200.SpyerAdmin.ISpyerSettings#load()
	 */
	@SuppressWarnings("unchecked")
	public void load() {
		File file = new File("plugins" + File.separator + "Spyer"
				+ File.separator + "spyer.yml");
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				contents.append(text).append(
						System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Yaml yaml = new Yaml();
		Object object = yaml.load(contents.toString());
		try {
			HashMap<String, Object> sysSS = (HashMap<String, Object>) object;
			this.clear();
			if (sysSS.containsKey("refreshRate")) {
				setRefreshRate((Integer) sysSS.get("refreshRate"));
			}
			if (sysSS.containsKey("syncWithScheduler")) {
				setSync((Boolean) sysSS.get("syncWithScheduler"));
			}
			if (sysSS.containsKey("pickUp")) {
				pickup = (Boolean) sysSS.get("pickUp");
			}
			if (sysSS.containsKey("invincible")) {
				invincible = (Boolean) sysSS.get("invincible");
			}
			if (sysSS.containsKey("noHit")) {
				noHit = (Boolean) sysSS.get("noHit");
			}
			if (sysSS.containsKey("wait")) {
				wait = (Boolean) sysSS.get("wait");
			}
			if (sysSS.containsKey("chatPriority")) {
				chatPriority = Event.Priority.valueOf((String) sysSS
						.get("chatPriority"));
			}

		} catch (Exception e) {
			System.out
					.println("Error: could not fetch settings from YAML document. Make sure it is correct.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ISpyerSettings#canUse(org.bukkit.entity.Player
	 * , java.lang.String)
	 */
	public boolean canUse(Player player, String name) {
		if (player == null) {
			return false;
		}
		boolean flag = player.hasPermission(new Permission(name,
				PermissionDefault.OP));
		return flag;
	}
}

class booleanAndInt {
	long time;
	boolean result;
}