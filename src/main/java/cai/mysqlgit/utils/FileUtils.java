package cai.mysqlgit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	public static boolean existsDirectory(String strDir) {
		File file = new File(strDir);
		return file.exists() && file.isDirectory();
	}

	public static boolean existsFile(String strFile) {
		File file = new File(strFile);
		return file.exists();
	}

	public static boolean createDir(String strDir) {
		File file = new File(strDir);
		if (file.exists()) {
			return true;
		}
		return file.mkdirs();
	}

	public static boolean deleteFile(String strFile) {
		File file = new File(strFile);
		boolean result = false;
		if (file.exists() && file.isFile()) {
			result = file.delete();
		}
		return result;
	}

	public static void deleteDir(String strDir) {
		File file = new File(strDir);
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteDir(strDir + "/" + f.getName());
			}
			file.delete();
		}
	}
	
	public static void saveTableSqlFile(String path, String sqlStr) {
		writeFileByFileWriter(path,sqlStr);
	}
	
	public static void writeFileByFileWriter(String filePath, String content) {
		File file  = new File(filePath);
		synchronized (file) {
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(content);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
