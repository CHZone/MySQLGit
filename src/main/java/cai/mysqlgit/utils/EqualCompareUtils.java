package cai.mysqlgit.utils;

public class EqualCompareUtils {
	private EqualCompareUtils(){
		
	}
	public static boolean isEqual(String a,String b){
		if (a == null && b == null) {
			return true;
		} else if (a != null) {
			return a.equals(b);
		} else {
			return false;
		}
	}
	
}
