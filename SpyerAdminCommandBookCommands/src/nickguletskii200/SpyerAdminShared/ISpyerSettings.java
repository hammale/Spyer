package nickguletskii200.SpyerAdminShared;

import org.bukkit.entity.Player;

public interface ISpyerSettings {

	public abstract int getRefreshRate();

	public abstract void setRefreshRate(int i);

	public abstract boolean getSync();

	public abstract void setSync(boolean b);

	public abstract boolean isSpy(String name);

	public abstract boolean isVisibleByMobs(String name);

	public abstract boolean canUseFun(String name);

	public abstract boolean isSeeAll(String name);

	public abstract void dump();

	public abstract void load();

	public abstract boolean canUse(Player player, String name);

}