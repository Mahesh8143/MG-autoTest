package com.csc.sics.mg.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {

	public static Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

	private static Map<String, String> cache = new HashMap<String, String>();

	public static String loadFileResourceAsString(String fileName) {
		if (cache.containsKey(fileName)) {
			return cache.get(fileName);
		}
		LOG.info("Loading file '" + fileName + "'.");
		StringBuilder result = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new InputStreamReader(ResourceLoader.class.getResourceAsStream("/" + fileName)));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
		} catch (Exception e) {
			LOG.error("Failed to read file '" + fileName + "'.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		cache.put(fileName, result.toString());
		return cache.get(fileName);
	}

	public Properties loadFileAsProperties(String fileName) {
		LOG.info("Loading file '" + fileName + "'.");
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = ResourceLoader.class.getResourceAsStream("/" + fileName);
			props.load(in);
			return props;
		} catch (Exception e) {
			LOG.error("Failed to read file '" + fileName + "'.", e);
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				LOG.error("Failed to close file '" + fileName + "'.", e);
			}
		}
	}

}
