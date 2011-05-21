package nickguletskii200.SpyerAdmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Indicator item clas... Should have been merged with the main config...
 * 
 * @author nickguletskii200
 */
// TODO: Merge with the main config class.
public class Indicator {
	private Properties conf = new Properties();

	public Indicator() {
		try {
			conf.load(new FileInputStream(new File("plugins" + File.separator
					+ "Spyer" + File.separator + "SpyerAdmin.conf")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void indicate(Player plr, boolean hid) throws FileNotFoundException,
			IOException {
		Inventory inv = plr.getInventory();
		for (int i = 0; i <= 8; i++) {
			if (inv.getItem(i).getTypeId() == indicatorID1()
					&& inv.getItem(i).getAmount() == 1 && hid) {
				inv.getItem(i).setTypeId(indicatorID2());
			}
			if (inv.getItem(i).getTypeId() == indicatorID2()
					&& inv.getItem(i).getAmount() == 1 && !hid) {
				inv.getItem(i).setTypeId(indicatorID1());
			}
		}

	}

	private int indicatorID1() throws FileNotFoundException, IOException {

		return Integer.valueOf(conf.getProperty("IndicatorItem"));
	}

	private int indicatorID2() throws FileNotFoundException, IOException {
		return Integer.valueOf(conf.getProperty("IndicatorItemFired"));
	}
}
