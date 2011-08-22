package nickguletskii200.Packets;

import java.lang.reflect.Field;

import nickguletskii200.SpyerFunPlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class PacketHand implements PacketListener {

	private final SpyerFunPlayerListener spyer;

	public PacketHand(SpyerFunPlayerListener spyer) {
		this.spyer = spyer;
	}

	@Override
	public boolean checkPacket(Player player, MCPacket packet) {
		return spyer.continueSend(player, name(packet));
	}

	public String name(MCPacket packet) {
		Integer i;
		try {
			Field f = (((MCCraftPacket) packet).getPacket()).getClass()
					.getField("a");
			if (f == null) {
				return "{!?}Null!{!?}";
			}
			i = (Integer) f.get((((MCCraftPacket) packet).getPacket()));
			Entity e = ((CraftWorld) Bukkit.getServer().getWorld(""))
					.getHandle().getEntity(i).getBukkitEntity();
			if (e instanceof Player) {
				System.out.println(((Player) e).getName());
				return ((Player) e).getName();
			} else {
				return "{!?}UnknownPlayer!{!?}";
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}
		return "{!?}Error!{!?}";
	}
}
