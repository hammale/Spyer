package nickguletskii200.Packets;

import java.lang.reflect.Field;

import net.minecraft.server.EntityPlayer;
import nickguletskii200.SpyerFunPlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
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
			for (Object e : ((CraftServer) Bukkit.getServer()).getHandle().players) {
				EntityPlayer ep = (EntityPlayer) e;
				if (ep.id == i) {
					return ep.name;
				}
			}
			return "{!?}Notaplayer!{!?}";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "{!?}Error!{!?}";
	}
}
