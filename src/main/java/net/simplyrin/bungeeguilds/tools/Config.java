package net.simplyrin.bungeeguilds.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Created by SimplyRin on 2018/10/08.
 *
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Config {

	public static void saveConfig(Configuration config, String file) {
		saveConfig(config, new File(file));
	}

	public static void saveConfig(Configuration config, File file) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Configuration getConfig(String file) {
		return getConfig(new File(file));
	}

	public static Configuration getConfig(File file) {
		try {
			return getProvider().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Configuration loadConfig(String file) {
		return getConfig(new File(file));
	}

	public static Configuration loadConfig(File file) {
		return getConfig(file);
	}


	public static Configuration getConfig(File file, Charset charset) {
		try {
			return getProvider().load(new InputStreamReader(new FileInputStream(file), charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Configuration getConfig(URL url) throws Exception {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			InputStream inputStream = connection.getInputStream();
			return getProvider().load(inputStream);
		} catch (Exception e) {
			throw e;
		}
	}

	private static ConfigurationProvider getProvider() {
		return ConfigurationProvider.getProvider(YamlConfiguration.class);
	}

}
