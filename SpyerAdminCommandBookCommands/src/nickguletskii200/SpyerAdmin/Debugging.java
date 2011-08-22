package nickguletskii200.SpyerAdmin;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.SwingWorker;

/**
 * Logger. By the time I released it most of the bugs died out.
 * 
 * @author nickguletskii200
 */
public class Debugging {
	public static File log = new File("spyeradmin.log");
	public static String queued = "";
	public static SwingWorker<Object, Object> run = new SwingWorker<Object, Object>() {

		@Override
		protected Object doInBackground() throws Exception {
			// TODO Auto-generated method stub
			while (!isCancelled()) {
				Thread.sleep(10000);
				appendFile(queued);
				queued = "";
			}
			return null;
		}
	};
	public static void start() {
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		run.execute();
		queued = "[" + dateTime() + "]SESSION START\n";
	}

	public static void stop() {
		run.cancel(false);
		queued += "\n[" + dateTime() + "]SESSION STOP\n";
		appendFile(queued);
		queued = "";
	}

	private static String dateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static void logException(Exception e, String id) {
		//StackTraceElement[] st = e.getStackTrace();
		String buf = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream pos = new PrintStream(bos);
		e.printStackTrace(pos);
		pos.close();
		try {
			bos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		buf += bos.toString();
		String str = "[" + dateTime() + "][ERROR]" + "Exception ID " + id
				+ ": \n" + buf + "\n";
		append(str);
	}

	public static void logHashMap(HashMap<?, ?> hm, String id) {
		String str = "[" + dateTime() + "][COLLECTION DUMP]" + "HashMap ID "
				+ id + ": \n";
		for (Object key : hm.keySet()) {
			Object val = hm.get(key);
			str += val.toString();
		}
		str += "\n";
		append(str);
	}

	public static void logFile(String path) {
		String str = "[" + dateTime() + "][FILE]" + "File " + path
				+ ", exists: ";
		str += new File(path).exists();
		str += "\n";
		append(str);
	}

	public static void logFile(File file) {
		String str = "[" + dateTime() + "][FILE]" + "File "
				+ file.getAbsolutePath() + ", exists: ";
		str += file.exists();
		str += "\n";
		append(str);
	}

	public static void log(String str) {
		append("[" + dateTime() + "][INFO]" + str + "\n");
	}

	private static void append(String str) {
		queued += str;
	}

	private static void appendFile(String arg) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(log, true));
			bw.write(arg);
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					SpyerLogCommands
							.warning("IO exception while closing advanced log!");
				}
		}

	}
}