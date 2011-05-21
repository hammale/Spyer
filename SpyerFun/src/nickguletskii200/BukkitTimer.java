package nickguletskii200;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Cool class.
 * 
 * @author nickguletskii200
 */
public class BukkitTimer {
	private int id;
	private SpyerFun plugin;
	private boolean Mode;
	private Timer tmr;

	public BukkitTimer(SpyerFun plug) {
		plugin = plug;
		Mode = mode();
	}

	private boolean mode() {
		return (plugin.getSettings().getSync());
	}

	public void scheduleAtFixedRate(TimerTask tsk, int delay, int step) {
		if (Mode) {
			int ticks = step / 50;
			int ticksDelay = step / 50;
			id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
					plugin, tsk, ticksDelay, ticks);
		} else {
			tmr = new Timer();
			tmr.scheduleAtFixedRate(tsk, delay, step);
		}
	}

	public void cancel() {
		if (Mode) {
			plugin.getServer().getScheduler().cancelTask(id);
		} else {
			tmr.cancel();
		}
	}
}
