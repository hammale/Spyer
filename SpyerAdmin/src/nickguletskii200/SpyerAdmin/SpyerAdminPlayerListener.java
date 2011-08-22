package nickguletskii200.SpyerAdmin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import nickguletskii200.SpyerAdmin.Packets.PacketHand;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.SpoutManager;

/**
 * Handle events for all player related events
 * 
 * @author nickguletskii200
 */
public class SpyerAdminPlayerListener extends PlayerListener {
	private SpyerAdmin plugin;
	// TODO: use the BukkitTimer class I made for SpyerFun.
	public HashMap<String, Timer> timers = new HashMap<String, Timer>();
	public final HashMap<String, Integer> schedulers = new HashMap<String, Integer>();
	public HashSet<String> commonPlayers = new HashSet<String>(); // Contains
	// all hidden people. Not sure what is best for this - a hashset or an
	// arraylist.
	public HashMap<String, ArrayList<String>> playerHideTree = new HashMap<String, ArrayList<String>>();
	// TODO: persistence of people to hide when they join
	HashMap<String, Boolean> hideOnJoin = new HashMap<String, Boolean>();
	private Indicator ind = new Indicator();

	public SpyerAdminPlayerListener(SpyerAdmin _plug) {
		plugin = _plug;
		SpoutManager.getPacketManager().addListener(5, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(17, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(18, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(19, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(20, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(28, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(30, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(31, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(32, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(33, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(34, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(38, new PacketHand(this));
		SpoutManager.getPacketManager().addListener(39, new PacketHand(this));
	}

	// Tried to make it as fast as I could - this will obviously save some
	// bandwidth; not sure about performance.
	public boolean outsideSight(Location loc1, Location loc2) {
		World w1 = loc1.getWorld();
		World w2 = loc2.getWorld();
		if (!w1.getName().equals(w2.getName())) {
			// We don't need to hide people from different worlds! Woohoo,
			// multiworld friendly!
			return false;
		}
		Chunk chG = w2.getChunkAt(loc2.getBlock());
		Chunk ch = w1.getChunkAt(loc1.getBlock());
		int maxX = chG.getX() + 16; // Just making sure nobody will still be
		// visible
		int minX = chG.getX() - 16; // TODO: tweak the numbers
		int maxZ = chG.getZ() + 16;
		int minZ = chG.getZ() - 16;
		if ((ch.getX() <= maxX || ch.getX() >= minX)
				|| (ch.getZ() <= maxZ || ch.getZ() >= minZ)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean continueSend(Player player, String name) {
		return !((commonPlayers.contains(name)) && !plugin.getSettings()
				.isSeeAll(name));
	}

	public void invisible(Player p1, Player p2, boolean force) {
		if (outsideSight(p1.getLocation(), p2.getLocation())) {
			return;
		}
		CraftPlayer hide = (CraftPlayer) p1;
		CraftPlayer hideFrom = (CraftPlayer) p2;

		if (!playerHideTree.containsKey(p1.getName())) {
			playerHideTree.put(p1.getName(), new ArrayList<String>());
		}
		if ((!playerHideTree.get(p1.getName()).contains(p2.getName()) || force)
				&& !plugin.getSettings().isSeeAll(p2.getName())) {
			if (p1 != p2) {
				try {
					hideFrom.getHandle().netServerHandler
							.sendPacket(new Packet29DestroyEntity(hide
									.getEntityId()));
					playerHideTree.get(p1.getName()).add(p2.getName());
				} catch (Exception e) {
					// Why would I care about some networking exceptions? Ha ha
					// ha...
				}
			}
		}

	}

	private void uninvisible(Player p1, Player p2) {
		CraftPlayer unHide = (CraftPlayer) p1;
		CraftPlayer unHideFrom = (CraftPlayer) p2;
		if (p1 != p2 && playerHideTree.containsKey(p1.getName())) {
			if (playerHideTree.get(p1.getName()).contains(p2.getName())) {
				unHideFrom.getHandle().netServerHandler
						.sendPacket(new Packet20NamedEntitySpawn(unHide
								.getHandle()));
				playerHideTree.get(p1.getName()).remove(p2.getName());
			}
		}
	}

	public void vanish(final Player player) {
		final String name = player.getName();
		if (commonPlayers.contains(player.getName())) {
			return;
		}
		commonPlayers.add(player.getName());
		plugin.getSettings().load();
		schedulers.put(player.getName(), plugin.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(plugin, new Runnable() {
					@Override
					public void run() {
						try {
							if (!player.isOnline()) {
								plugin.getServer().getScheduler()
										.cancelTask(schedulers.get(name));
								schedulers.remove(name);
								commonPlayers.remove(player.getName());
								playerHideTree.remove(player.getName());
								return;
							}
							try {
								ind.indicate(player, true);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (Exception e) {
						}
					}
				}, 0, plugin.getSettings().getRefreshRate()));
		// }
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			invisible(player, p, false);
		}
		player.sendMessage(ChatColor.RED + "You are now invisible!");
	}

	public void cleanuptimers(Player player) {
		if (timers.containsKey(player.getName())) {
			timers.get(player.getName()).cancel();
			timers.remove(player.getName());
		}
		if (schedulers.containsKey(player.getName())) {
			plugin.getServer().getScheduler()
					.cancelTask(schedulers.get(player.getName()));
			schedulers.remove(player.getName());
		}
	}

	public void reappear(Player player) {
		if (!commonPlayers.contains(player.getName())) {
			return;
		}
		commonPlayers.remove(player.getName());
		cleanuptimers(player);
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			if (!p.getName().equals(player.getName())) {
				uninvisible(player, p);
			}
		}
		playerHideTree.remove(player.getName());
		player.sendMessage(ChatColor.RED + "You are now visible!");
		try {
			ind.indicate(player, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void onPlayerJoin(PlayerJoinEvent event) {

		if (hideOnJoin.containsKey(event.getPlayer().getName())) {
			if (hideOnJoin.get(event.getPlayer().getName())) {
				event.setJoinMessage(null);
				plugin.antigrief.add(event.getPlayer().getName());
			}
			vanish(event.getPlayer());
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "You are still invisible.");
			hideOnJoin.remove(event.getPlayer().getName());
		}
		if (commonPlayers.isEmpty()) {
			return;
		}
		String obj[] = commonPlayers.toArray(new String[1]);
		for (String plrnm : obj) {
			if (plrnm != null) {
				Player plr = plugin.getServer().getPlayer(plrnm);
				if (plr != null) {
					invisible(plr, event.getPlayer(), false);
				}
			}
		}

	}

	@SuppressWarnings("static-access")
	public void quit(Player plr) {
		if (!commonPlayers.contains(plr.getName())) {
			return;
		}
		commonPlayers.remove(plr.getName());
		playerHideTree.remove(plr.getName());
		plugin.antigrief.remove(plr.getName());
		cleanuptimers(plr);
	}

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (!commonPlayers.contains(player.getName())) {
			return;
		}
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			invisible(player, p, true);
		}
	}

	@SuppressWarnings("static-access")
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (commonPlayers.contains(event.getPlayer().getName())) {
			hideOnJoin.put(event.getPlayer().getName(),
					plugin.antigrief.contains(event.getPlayer().getName()));
		}
		if (plugin.antigrief.contains(event.getPlayer().getName())) {
			event.setQuitMessage(null);
		}
		quit(event.getPlayer());

	}

	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (plugin.getSettings().pickup) {
			return;
		} else if (commonPlayers.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	public void onPlayerRespawn(PlayerRespawnEvent event) {
		quit(event.getPlayer());
	}

	@SuppressWarnings("static-access")
	public void onPlayerChat(PlayerChatEvent event) {
		if (plugin.antigrief.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
			Player[] plrs = plugin.getServer().getOnlinePlayers();
			for (Player p : plrs) {
				if (plugin.getSettings().isSeeAll(p.getName())) {
					p.sendMessage(ChatColor.GRAY + "[QUITSIM]"
							+ ChatColor.DARK_RED + event.getPlayer().getName()
							+ ChatColor.GRAY + " " + event.getMessage());
				}
			}
		}
	}
}
