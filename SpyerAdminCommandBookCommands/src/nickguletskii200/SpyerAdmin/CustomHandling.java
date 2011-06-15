package nickguletskii200.SpyerAdmin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import nickguletskii200.SpyerAdminShared.ICustomHandling;
import nickguletskii200.SpyerAdminShared.ISpyerAdmin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.commandbook.CommandBookPlugin;
import com.sk89q.commandbook.events.OnlineListSendEvent;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

/**
 * Scripting class.
 * 
 * @author nickguletskii200
 */
public class CustomHandling implements ICustomHandling {
	private SpyerAdminCommands plugin;
	public HashMap<String, ChatColor> ChatColour = new HashMap<String, ChatColor>();
	private SafeSpyerCheck ssc;

	public CustomHandling(SpyerAdminCommands sa) {
		plugin = sa; 
		ssc = new SafeSpyerCheck((ISpyerAdmin) plugin.getServer()
				.getPluginManager().getPlugin("SpyerAdmin"));
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
		plugin.getServer().broadcastMessage(
				new StringBuilder().append(ChatColor.YELLOW).append(
						plr.getName()).append(" left the game.").toString());
	}

	public void playerJoin(Player plr) {
		plugin.getServer().broadcastMessage(
				new StringBuilder().append(ChatColor.YELLOW).append(
						plr.getName()).append(" joined the game.").toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenickguletskii200.SpyerAdmin.ICustomHandling#who(org.bukkit.command.
	 * CommandSender)
	 */
	public void who(CommandSender sender, String[] arguments) {
		if (!ssc.hasPermission(sender, "commandbook.who")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
			return;
		}
		CommandContext args = null;
		if (arguments.length != 0) {
			args = new CommandContext(arguments);
		}
		StringBuilder out = new StringBuilder();
		Player[] online = plugin.getServer().getOnlinePlayers();

		plugin.getServer().getPluginManager().callEvent(
				new OnlineListSendEvent(sender));

		// This applies mostly to the console, so there might be 0 players
		// online if that's the case!
		if (online.length - ssc.numOfHidenPlayers() == 0) {
			sender.sendMessage("0 players are online.");
			return;
		}

		// Get filter
		String filter = null;
		if (arguments.length != 0) {
			filter = args.getString(0, "").toLowerCase();
		}

		// For filtered queries, we say something a bit different
		if (filter == null) {
			out.append(ChatColor.GRAY + "Online (");
			out.append(ChatColor.GRAY + ""
					+ (online.length - ssc.numOfHidenPlayers()));
			out.append(ChatColor.GRAY + "): ");
			out.append(ChatColor.WHITE);
		} else {
			out.append(ChatColor.GRAY + "Found players (out of ");
			out.append(ChatColor.GRAY + ""
					+ (online.length - ssc.numOfHidenPlayers()));
			out.append(ChatColor.GRAY + "): ");
			out.append(ChatColor.WHITE);
		}
		// To keep track of commas
		boolean first = true;
		// Now go through the list of players and find any matching players
		// (in case of a filter), and create the list of players.
		plrloop: for (Player player : online) {
			// Process the filter
			if (ssc.isQuit(player) && !ssc.isSeeAll(sender)) {
				continue plrloop;
			}
			if (filter != null
					&& !player.getName().toLowerCase().contains(filter)) {
				break;
			}

			if (!first) {
				out.append(", ");
			}

			out.append(player.getName());

			first = false;
		}
		// This means that no matches were found!
		if (first) {
			sender.sendMessage(ChatColor.RED + "No players (out of "
					+ online.length + ") matched '" + filter + "'.s");
			return;
		}

		sender.sendMessage(out.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ICustomHandling#playerMsg(org.bukkit.command
	 * .CommandSender, java.lang.String[], java.lang.String)
	 */
	public void playerMsg(CommandSender sender, String[] args,
			String commandLabel) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Too few arguments.");
			sender.sendMessage(ChatColor.RED
					+ plugin.getServer().getPluginCommand("msg").getUsage()
							.replace("<command>", commandLabel));// "/message <target> <message...>");
			return;
		}
		if (!ssc.hasPermission(sender, "commandbook.msg")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
			return;
		}
		// This will throw errors as needed
		CommandBookPlugin cbp = (CommandBookPlugin) plugin.getServer()
				.getPluginManager().getPlugin("CommandBook");
		CommandSender receiver;
		try {
			receiver = cbp.matchPlayerOrConsole(sender, args[0]);
			if (ssc.isQuit(receiver) && !ssc.isSeeAll(sender)) {
				throw new CommandException("No players matched query.");
			}
			String message = args[1];
			if (args.length > 2) {
				message = args[1] + " "
						+ new CommandContext(args).getJoinedStrings(1);
			}

			receiver.sendMessage(ChatColor.GRAY + "(From " + cbp.toName(sender)
					+ "): " + ChatColor.WHITE + message);

			sender.sendMessage(ChatColor.GRAY + "(To " + cbp.toName(receiver)
					+ "): " + ChatColor.WHITE + message);

			cbp.getSession(sender).setLastRecipient(receiver);

			cbp.getSession(receiver).setNewLastRecipient(sender);
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nickguletskii200.SpyerAdmin.ICustomHandling#reply(org.bukkit.command.
	 * CommandSender, java.lang.String[], java.lang.String)
	 */
	public void reply(CommandSender sender, String[] arguments,
			String commandLabel) {
		if (arguments.length < 1) {
			sender.sendMessage(ChatColor.RED + "Too few arguments.");
			sender.sendMessage(ChatColor.RED
					+ plugin.getServer().getPluginCommand("r").getUsage()
							.replace("<command>", commandLabel));// "/message <target> <message...>");
			return;
		}
		if (!ssc.hasPermission(sender, "commandbook.msg")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
			return;
		}
		try {

			CommandContext args = new CommandContext(arguments);
			String message = arguments[0];
			if (arguments.length > 1) {
				message = arguments[0] + " " + args.getJoinedStrings(0);
			}
			CommandSender receiver;
			CommandBookPlugin cbp = (CommandBookPlugin) plugin.getServer()
					.getPluginManager().getPlugin("CommandBook");
			String lastRecipient = cbp.getSession(sender).getLastRecipient();

			if (lastRecipient != null) {
				// This will throw errors as needed
				receiver = cbp.matchPlayerOrConsole(sender, lastRecipient);
				if (ssc.isQuit(receiver) && !ssc.isSeeAll(sender)) {
					throw new CommandException("No players matched query.");
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You haven't messaged anyone.");
				return;
			}

			receiver.sendMessage(ChatColor.GRAY + "(From " + cbp.toName(sender)
					+ "): " + ChatColor.WHITE + message);

			sender.sendMessage(ChatColor.GRAY + "(To " + cbp.toName(receiver)
					+ "): " + ChatColor.WHITE + message);
			cbp.getSession(receiver).setNewLastRecipient(sender);
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
	}
}
