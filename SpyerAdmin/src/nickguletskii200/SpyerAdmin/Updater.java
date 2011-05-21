package nickguletskii200.SpyerAdmin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Update class...
 * 
 * @author nickguletskii200
 */
public class Updater {
	public static String getRepo() throws IOException {
		StringBuilder out = new StringBuilder();
		File file = new File("plugins" + File.separator + "Spyer"
				+ File.separator + "repo.conf");
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			if (!strLine.startsWith("#")) {
				out.append(strLine);
			}
		}
		in.close();
		return out.toString();
	}

	public static void download(String address, String localFileName)
			throws FileNotFoundException {
		File fl = new File(localFileName);
		localFileName = fl.getAbsolutePath();
		fl.renameTo(new File(localFileName + ".bak"));
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace(); // I don't care about some network issues.
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				SpyerLog.warning(ioe.getMessage());
			}
		}
	}

}
