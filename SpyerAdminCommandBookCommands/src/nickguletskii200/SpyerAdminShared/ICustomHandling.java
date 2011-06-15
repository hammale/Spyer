package nickguletskii200.SpyerAdminShared;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ICustomHandling {

	public abstract void playerLeave(Player plr);

	public abstract void playerJoin(Player plr);

	public abstract void who(CommandSender plr, String[] arguments);

	public abstract void playerMsg(CommandSender from, String[] arguments,
			String commandLabel);

	public abstract void reply(CommandSender from, String[] arguments,
			String commandLabel);

}