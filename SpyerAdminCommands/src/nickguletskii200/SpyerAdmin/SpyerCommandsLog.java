package nickguletskii200.SpyerAdmin;
/**
 * Too lazy to write a description. Please read the class yourself :)
 * 
 * @author nickguletskii200
 */
public class SpyerCommandsLog {
	public static SpyerAdminCommands sa = null;

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
