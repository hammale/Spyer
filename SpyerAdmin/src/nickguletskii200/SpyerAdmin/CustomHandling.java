package nickguletskii200.SpyerAdmin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Scripting class.
 * 
 * @author nickguletskii200
 */
public class CustomHandling {
	private SpyerAdmin plugin;
	private ScriptEngine jsEngine;
	private Invocable invocableEngine;
	public HashMap<String, ChatColor> ChatColour = new HashMap<String, ChatColor>();

	public CustomHandling(SpyerAdmin sa) {
		plugin = sa;
		ScriptEngineManager mgr = new ScriptEngineManager();
		jsEngine = mgr.getEngineByName("JavaScript");
		StringBuffer buf = strbuf("plugins" + File.separator + "Spyer"
				+ File.separator + "script.js");
		ChatColour.put("Aqua", ChatColor.AQUA);
		ChatColour.put("Black", ChatColor.BLACK);
		ChatColour.put("Blue", ChatColor.BLUE);
		ChatColour.put("DarkAqua", ChatColor.DARK_AQUA);
		ChatColour.put("DarkBlue", ChatColor.DARK_BLUE);
		ChatColour.put("DarkGray", ChatColor.DARK_GRAY);
		ChatColour.put("DarkGreen", ChatColor.DARK_GREEN);
		ChatColour.put("DarkPurple", ChatColor.DARK_PURPLE);
		ChatColour.put("DarkRed", ChatColor.DARK_RED);
		ChatColour.put("Gold", ChatColor.GOLD);
		ChatColour.put("Gray", ChatColor.GRAY);
		ChatColour.put("Green", ChatColor.GREEN);
		ChatColour.put("LightPurple", ChatColor.LIGHT_PURPLE);
		ChatColour.put("Red", ChatColor.RED);
		ChatColour.put("White", ChatColor.WHITE);
		ChatColour.put("Yellow", ChatColor.YELLOW);
		jsEngine.put("ChatColour", ChatColour);
		jsEngine.put("Server", plugin.getServer());
		jsEngine.put("SpyerAdmin", new SafeSpyerCheck(plugin));
		jsEngine.put("CLLoader", new SimpleClassLoader());
		try {
			jsEngine.eval(buf.toString());
		} catch (ScriptException ex) {
			plugin.getServer().getLogger().severe(ex.getMessage());
		}
		invocableEngine = (Invocable) jsEngine;
	}

	public StringBuffer strbuf(String filename) {
		Logger logger = plugin.getServer().getLogger();
		File file = new File(filename);
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
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.severe(e.getMessage());
			}
		}
		return contents;
	}

	public void playerLeave(Player plr) {
		try {
			invocableEngine.invokeFunction("playerLeaving", plr);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playerJoin(Player plr) {
		try {
			invocableEngine.invokeFunction("playerJoining", plr);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void who(CommandSender plr) {
		try {
			invocableEngine.invokeFunction("who", plr);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playerMsg(CommandSender from, String[] arguments,
			String commandLabel) {
		try {
			invocableEngine.invokeFunction("playerMsg", from, arguments,
					commandLabel);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reply(CommandSender from, String[] arguments,
			String commandLabel) {
		try {
			invocableEngine.invokeFunction("reply", from, arguments,
					commandLabel);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
