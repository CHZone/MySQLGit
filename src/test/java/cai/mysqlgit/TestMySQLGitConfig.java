package cai.mysqlgit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestMySQLGitConfig {
	@Test
	public void testGetProperties() {
		                                   
		File mysqlGitProperties = new File("src/main/java/mysqlgit.properties");
		List<String> lines = new ArrayList<>();
		try {
			lines = FileUtils.readLines(mysqlGitProperties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> properties = new HashMap<>();
		for(String line : lines) {
			int mid = line.indexOf('=');
			properties.put(line.substring(0,mid), line.substring(mid+1));
		}
		for(Map.Entry<String, String> entry:properties.entrySet()) {
			assertEquals(MySQLGitConfig.getValue(entry.getKey()), entry.getValue());
		}
	}
}

