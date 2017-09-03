package core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	public static String getFile(String path) throws Exception {
		File file = new File(path);
		if (!file.isFile()) {
			throw new Exception("it's not file!\nfile: " + path);
		}
		StringBuffer res = new StringBuffer();
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		int line = 1;
		while ((tempString = reader.readLine()) != null) {
			res.append(tempString + '\n');
		}
		reader.close();
		return res.toString();
	}

	public static byte[] getFileWithByte(String path) throws Exception {
		File file = new File(path);
		if (!file.isFile()) {
			throw new Exception("it's not file!\nfile: " + path);
		}
		InputStream input = new FileInputStream(file);

		byte[] byt = new byte[input.available()];

		input.read(byt);
		//
		// for (byte c : byt) {
		// System.out.println(c);
		// }
		return byt;

	}

	public static void copyFile(String srcFilePath, String dstFilePath) throws Exception {
		File srcFile = new File(srcFilePath);
		File dstFile = new File(dstFilePath);
		int byteread = 0;
		InputStream in = null;
		OutputStream out = null;
		if (!srcFile.exists())
			throw new FileNotFoundException();
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(dstFile);
			byte[] buffer = new byte[1024];

			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getFile("D:\\hjk\\workspace\\test\\testFile\\aa"));
	}
}
