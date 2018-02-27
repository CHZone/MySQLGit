package cai.mysqlgit;

import java.io.IOException;  
import java.io.InputStream;  
import java.util.Properties;  
  
/**实现功能： 
 * 类名:  ReadSystemConfig.java 
 * 更新时间:  $Date$       
 * 最后更新者: $Author$ 
 * 修改备注： 
 */  
public class MySQLGitConfig {  
    private static Properties properties = new Properties() ;  
    static {  
        InputStream in = MySQLGitConfig.class.getClassLoader().getResourceAsStream("mysqlgit.properties");  
        try {  
            properties.load(in);  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
      
    public static String getValue(String key){  
          
        return properties.getProperty(key);  
    }  
}  
