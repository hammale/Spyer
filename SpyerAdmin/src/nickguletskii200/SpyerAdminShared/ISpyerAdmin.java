package nickguletskii200.SpyerAdminShared;

import java.util.HashSet;


import org.bukkit.plugin.Plugin;

public interface ISpyerAdmin extends Plugin{

	public HashSet<String> antigrief = new HashSet<String>();
	public ISpyerSettings getSettings();

}
