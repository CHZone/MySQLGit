package cai.mysqlgit;

import java.util.ArrayList;

public class App {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		MySQLRepository msr = new MySQLRepository();
		msr.openRepository();
		ArrayList<String> latest2Commits = msr.getLatest2Commits();
//		msr.diffFilesOf2Commits( latest2Commits.get(1),latest2Commits.get(0));
		msr.fileInDiffOf2Commits(latest2Commits.get(1),latest2Commits.get(0),"souche_dfc/app_splash_screen_inc_bak/app_splash_screen_inc_bak.sql");
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + (endTime - startTime) + "ms");
	}
}
