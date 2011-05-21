package nickguletskii200;

/**
 * Little logging class... Not used much now.
 * 
 * @author nickguletskii200
 */
public class SpyerLog {
	public static SpyerFun sa = null;

	public static void info(String arg) {
		if (sa != null) {
			sa.getServer().getLogger().info(arg);
		} else {
			System.out.println(arg);
		}
	}

	public static void error(String arg) {
		if (sa != null) {
			sa.getServer().getLogger().severe(arg);
		} else {
			System.out.println(arg);
		}
	}

	public static void warning(String arg) {
		if (sa != null) {
			sa.getServer().getLogger().warning(arg);
		} else {
			System.out.println(arg);
		}
	}
}
