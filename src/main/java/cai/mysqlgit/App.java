package cai.mysqlgit;

public class App {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		MySQLRepository msr = new MySQLRepository();
		msr.openRepository();
//		msr.saveDataBase("souche_dfc");
		msr.addAll();
		msr.showStatus();
	
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + (endTime - startTime) + "ms");
	}
}
