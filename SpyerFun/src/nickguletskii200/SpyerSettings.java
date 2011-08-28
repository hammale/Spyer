package nickguletskii200;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.yaml.snakeyaml.Yaml;

/**
 * Was used for configs. Now used for configs + permissions.
 * 
 * @author nickguletskii200
 */
public class SpyerSettings extends HashMap<String, Object> {

	private static final long serialVersionUID = 7771694943392484453L;
	private SpyerFun plugin;
	public int coolDown = 0;
	public boolean onDamage = false;
	public boolean onBlock = false;
	public boolean noDamage = false;
	public boolean shadows = false;
	public byte lightlevel = 7;

	public SpyerSettings(SpyerFun plugin) {
		this.put("refreshRate", 1000);
		this.put("syncWithScheduler", false);
		this.plugin = plugin;
	}

	public boolean isVisibleByMobs(String name) {
		try {
			Player player = Bukkit.getServer().getPlayer(name);
			if (player == null) {
				return false;
			}
			return !canUse(player, "spyer.stopmobs.fun");
		} catch (Exception e) {
		}
		return true;
	}

	public int getRefreshRate() {
		if (!this.containsKey("refreshRate")) {
			return 1000;
		}
		if (!(this.get("refreshRate") == null)) {
			return (Integer) this.get("refreshRate");
		}
		return 1000;
	}

	public void setRefreshRate(int i) {
		this.remove("refreshRate");
		this.put("refreshRate", i);
	}

	public boolean getSync() {
		if (!this.containsKey("syncWithScheduler")) {
			return false;
		}
		if (!(this.get("syncWithScheduler") == null)) {
			return (Boolean) this.get("syncWithScheduler");
		}
		return false;
	}

	public void setSync(boolean b) {
		this.remove("syncWithScheduler");
		this.put("syncWithScheduler", b);
	}

	public boolean isSpy(String name) {
		try {
			Player player = Bukkit.getServer().getPlayer(name);
			if (player == null) {
				return false;
			}
			return canUse(player, "spyer.spy");
		} catch (Exception e) {
		}
		return false;
	}

	public boolean canUseFun(String name) {
		try {
			Player player = plugin.getServer().getPlayer(name);
			if (player == null) {
				return false;
			}
			return player.hasPermission(new Permission("spyer.fun",
					PermissionDefault.TRUE));
		} catch (Exception e) {
			SpyerLog.error(e.getMessage());
		}
		return true;
	}

	public boolean isSeeAll(String name) {

		try {
			Player player = Bukkit.getServer().getPlayer(name);
			if (player == null) {
				return false;
			}
			return canUse(player, "spyer.seeAll");
		} catch (Exception e) {
		}
		return false;
	}

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
			if (sysSS.containsKey("coolDown")) {
				coolDown = (Integer) sysSS.get("coolDown");
			}
			if (sysSS.containsKey("onDamage")) {
				onDamage = (Boolean) sysSS.get("onDamage");
			}
			if (sysSS.containsKey("onBlock")) {
				onBlock = (Boolean) sysSS.get("onBlock");
			}
			if (sysSS.containsKey("noDamage")) {
				noDamage = (Boolean) sysSS.get("noDamage");
			}
		} catch (Exception e) {
			System.out
					.println("Error: could not fetch settings from YAML document. Make sure it is correct.");
		}
	}

	public boolean canUse(Player player, String name) {
		return player.hasPermission(new Permission(name, PermissionDefault.OP));
	}
}
