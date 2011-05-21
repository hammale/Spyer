package nickguletskii200;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

/**
 * Handle events for all player related events
 * 
 * @author nickguletskii200
 */
public class SpyerFunItems extends HashMap<Integer, Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6529073539370867525L;

	public void dump() {
		Yaml yaml = new Yaml();
		String out = yaml.dump(this);
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("plugins"
					+ File.separator + "Spyer" + File.separator + "items.yml"));
			pw.print(out);
			pw.close();
		} catch (IOException e) {
			System.out.println("IO Exception, could not save spyer data. \n"
					+ e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void load() {
		File file = new File("plugins/Spyer/items.yml");
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				contents.append(text).append(
						System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Yaml yaml = new Yaml();
		Object object = yaml.load(contents.toString());
		try {
			HashMap<Integer, Integer> sysSS = (HashMap<Integer, Integer>) object;
			this.clear();
			Set<Integer> keySet = sysSS.keySet();
			for (Integer cur : keySet) {
				this.put(cur, sysSS.get(cur));
			}
		} catch (Exception e) {
			System.out
					.println("Error: could not fetch settings from YAML document (items.yml). Make sure it is correct.");
		}
	}
}
