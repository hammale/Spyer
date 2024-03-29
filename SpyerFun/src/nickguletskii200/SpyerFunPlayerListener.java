package nickguletskii200;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import nickguletskii200.Packets.PacketHand;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.SpoutManager;

/**
 * Handle events for all Player related events
 * 
 * @author nickguletskii200
 */
public class SpyerFunPlayerListener extends PlayerListener {
	private SpyerFun plugin;
	public HashMap<String, BukkitTimer> timers = new HashMap<String, BukkitTimer>();
	public ArrayList<String> commonPlayers = new ArrayList<String>();
	public HashMap<String, ArrayList<String>> playerHideTree = new HashMap<String, ArrayList<String>>();
	public HashMap<String, Long> coolDown = new HashMap<String, Long>();

	public SpyerFunPlayerListener(SpyerFun _plug) {
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

	public boolean isCorrect(Integer i) {
		SpyerFunItems sfi = plugin.getItems();
		return sfi.containsKey(i);
	}

	public int getTime(Integer i) {
		SpyerFunItems sfi = plugin.getItems();
		return sfi.get(i);
	}

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (!commonPlayers.contains(player.getName())) {
			return;
		}
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			invisible(player, p);
		}
		// if (commonPlayers.contains(event.getPlayer().getName())) {
		// if (!(event.getTo().getBlock().getLightLevel() > plugin
		// .getSettings().lightlevel)) {
		// reappear(event.getPlayer());
		// }
		// }
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK
					&& plugin.getSettings().onBlock) {
				reappear(event.getPlayer());
			}
			return;
		}
		if (event.getItem() == null) {
			return;
		}
		if (coolDown.containsKey(event.getPlayer().getName())) {
			if (System.currentTimeMillis()
					- coolDown.get(event.getPlayer().getName()) < plugin
						.getSettings().coolDown) {
				return;
			}
		}
		// if (plugin.getSettings().shadows) {
		// if (!(event.getPlayer().getLocation().getBlock().getLightLevel() <=
		// plugin
		// .getSettings().lightlevel)) {
		// return;
		// }
		// }
		plugin.getSettings().load();
		if (isCorrect(event.getItem().getTypeId())) {
			if (commonPlayers.contains(event.getPlayer().getName())) {
				reappear(event.getPlayer());
				System.out
						.println(event.getPlayer().getName() + " reappeared.");
			} else if (plugin.getSettings().canUseFun(
					event.getPlayer().getName())) {
				vanish(event.getPlayer(), getTime(event.getItem().getTypeId()));
				System.out.println(event.getPlayer().getName()
						+ " attempted to hide.");
			}
		}
	}

	// public void onPlayerMove(PlayerMoveEvent event) {
	// if (commonPlayers.contains(event.getPlayer().getName())) {
	// if (!(event.getTo().getBlock().getLightLevel() > plugin
	// .getSettings().lightlevel)) {
	// reappear(event.getPlayer());
	// }
	// }
	// }

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

	public void invisible(Player p1, Player p2) {
		if (outsideSight(p1.getLocation(), p2.getLocation())) {
			return;
		}
		CraftPlayer hide = (CraftPlayer) p1;
		CraftPlayer hideFrom = (CraftPlayer) p2;
		if (!playerHideTree.containsKey(p1.getName())) {
			playerHideTree.put(p1.getName(), new ArrayList<String>());
		}
		if (!playerHideTree.get(p1.getName()).contains(p2.getName())
				&& !plugin.getSettings().isSeeAll(p2.getName())) {
			if (p1 != p2) {
				hideFrom.getHandle().netServerHandler
						.sendPacket(new Packet29DestroyEntity(hide
								.getEntityId()));
				playerHideTree.get(p1.getName()).add(p2.getName());
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

	public boolean continueSend(Player player, String name) {
		if (player == null) {
			return true;
		}
		return !((commonPlayers.contains(name)) && !plugin.getSettings()
				.isSeeAll(name));
	}

	public void vanish(final Player player, int time) {
		if (commonPlayers.contains(player.getName())) {
			return;
		}
		commonPlayers.add(player.getName());
		final BukkitTimer plt = new BukkitTimer(plugin);
		TimerTask tsk = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					System.out.println(player.getItemInHand().getAmount());
					if (!player.isOnline()) {
						plt.cancel();
						timers.remove(player.getName());
						commonPlayers.remove(player.getName());
						playerHideTree.remove(player.getName());
						return;
					}
					if (player.getItemInHand().getAmount() == 1
							&& isCorrect(player.getItemInHand().getTypeId())) {
						System.out.println("hi");
						reappear(player);
						commonPlayers.remove(player.getName());
						playerHideTree.remove(player.getName());
						plt.cancel();
						timers.remove(player.getName());
						player.getInventory().clear(
								player.getInventory().getHeldItemSlot());
						// player.setItemInHand(null);
						return;
					}
					Player[] playerList = plugin.getServer().getOnlinePlayers();
					for (Player p : playerList) {
						invisible(player, p);
					}
					if (player.getItemInHand() == null
							|| !isCorrect(player.getItemInHand().getTypeId())) {
						reappear(player);
						commonPlayers.remove(player.getName());
						playerHideTree.remove(player.getName());
						timers.get(player.getName()).cancel();
						timers.remove(player.getName());
						return;
					}
					// if (player.getItemInHand().getAmount() == 1) {
					// player.setItemInHand(null);
					// reappear(player);
					// commonPlayers.remove(player.getName());
					// playerHideTree.remove(player.getName());
					// timers.get(player.getName()).cancel();
					// timers.remove(player.getName());
					// return;
					// }
					if (isCorrect(player.getItemInHand().getTypeId())) {
						player.getItemInHand().setAmount(
								player.getItemInHand().getAmount() - 1);
					}
					if (player.getItemInHand().getAmount() == 0
							&& isCorrect(player.getItemInHand().getTypeId())) {
						reappear(player);
						commonPlayers.remove(player.getName());
						playerHideTree.remove(player.getName());
						plt.cancel();
						timers.remove(player.getName());
						return;
					}
				} catch (Exception e) {
				}
			}
		};
		plt.scheduleAtFixedRate(tsk, 0, time);
		timers.put(player.getName(), plt);
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			invisible(player, p);
		}
		player.sendMessage(ChatColor.RED + "You are now invisible!");

		if (player.getItemInHand().getAmount() == 1) {
			System.out.println("hi");
			player.setItemInHand(null);
		} else {
			player.getItemInHand().setAmount(
					player.getItemInHand().getAmount() - 1);
		}
	}

	public void reappear(Player player) {
		if (!commonPlayers.contains(player.getName())) {
			return;
		}
		commonPlayers.remove(player.getName());
		timers.get(player.getName()).cancel();
		timers.remove(player.getName());
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player p : playerList) {
			if (!p.getName().equals(player.getName())) {
				uninvisible(player, p);
			}
		}
		playerHideTree.remove(player.getName());
		player.sendMessage(ChatColor.RED + "You are now visible!");
		coolDown.remove(player.getName());
		coolDown.put(player.getName(), System.currentTimeMillis());
	}

	public void onItemHeldChange(PlayerItemHeldEvent event) {
		if (commonPlayers.contains(event.getPlayer().getName())) {

			reappear(event.getPlayer());
		}
		// timers.get(event.getPlayer().getName()).cancel();
		// timers.remove(event.getPlayer().getName());
		// ItemStack is =
		// event.getPlayer().getInventory().getItem(event.getPreviousSlot());
		// System.out.println(is.getAmount());
		// is.setAmount(is.getAmount());
		// System.out.println(is.getAmount());
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		if (commonPlayers.isEmpty()) {
			return;
		}
		for (String plrnm : commonPlayers) {
			if (plrnm != null) {
				Player plr = plugin.getServer().getPlayer(plrnm);
				if (plr != null) {
					invisible(plr, event.getPlayer());
				}
			}
		}
	}

	public void quit(Player plr) {
		// reappear(plr);
		if (!commonPlayers.contains(plr.getName())) {
			return;
		}
		commonPlayers.remove(plr.getName());
		playerHideTree.remove(plr.getName());
		timers.get(plr.getName()).cancel();
		timers.remove(plr.getName());
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		quit(event.getPlayer());
	}

	public void onPlayerRespawn(PlayerRespawnEvent event) {
		quit(event.getPlayer());
	}

}
