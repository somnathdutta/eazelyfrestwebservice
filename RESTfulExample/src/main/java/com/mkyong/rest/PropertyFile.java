package com.mkyong.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {
	
	public String getPropValues(String key) throws IOException {
		String sqlQuery="";
		InputStream inputStream=null;
		try {
			Properties prop = new Properties();
			String propFileName = "sql.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			// get the property value and print it out
			// sqlQuery = prop.getProperty("sqlcmsonload");
			 sqlQuery = prop.getProperty(key);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return sqlQuery;
	}
	
	/**
	 * Title case convert
	 * @param input
	 * @return
	 */
	public String toTitleCase(String input) {
	    StringBuilder titleCase = new StringBuilder();
	    boolean nextTitleCase = true;

	    for (char c : input.toCharArray()) {
	        if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	        } else if (nextTitleCase) {
	            c = Character.toTitleCase(c);
	            nextTitleCase = false;
	        }

	        titleCase.append(c);
	    }
	    return titleCase.toString();
	}
}
