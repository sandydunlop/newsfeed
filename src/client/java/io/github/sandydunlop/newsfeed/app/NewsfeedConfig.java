package io.github.sandydunlop.newsfeed.app;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


public class NewsfeedConfig {
	private static final Logger LOGGER = LogManager.getLogger("newsfeed.app");
    public static Path configFilePath = null;

    // These are the default values overridden by the config file
    public static String feedUrl = "https://www.reddit.com/r/AskReddit/new/.rss";; 
    public static boolean feedEnabled = true;
    public static boolean autoScroll = true;
    public static boolean updateCheckEnabled = true;


	public static void loadConfig(Path configFilePath) {
        NewsfeedConfig.configFilePath = configFilePath;
		try {
			String json = IOUtils.toString(NewsfeedConfig.configFilePath.toUri(), StandardCharsets.UTF_8);
			JSONObject jsonObject = new JSONObject(json);
			NewsfeedConfig.feedUrl = jsonObject.getString("feedUrl");
			NewsfeedConfig.autoScroll = jsonObject.getBoolean("autoScroll");
			NewsfeedConfig.feedEnabled = jsonObject.getBoolean("feedEnabled");
			NewsfeedConfig.updateCheckEnabled = jsonObject.getBoolean("updateCheckEnabled");
		} catch(JSONException e){
			LOGGER.error("Problem loading config: {}", e.getMessage());
		} catch(IOException e){
			LOGGER.error("Unable to read config file: {}", e.getMessage());
		}
	}

    public static void saveConfig() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedUrl", NewsfeedConfig.feedUrl);
        jsonObject.put("feedEnabled", NewsfeedConfig.feedEnabled);
        jsonObject.put("autoScroll", NewsfeedConfig.autoScroll);
        jsonObject.put("updateCheckEnabled", NewsfeedConfig.updateCheckEnabled);

        try (FileWriter file = new FileWriter(NewsfeedConfig.configFilePath.toString(), StandardCharsets.UTF_8)) {
            file.write(jsonObject.toString(4));
            file.flush();
        } catch (IOException e) {
            LOGGER.error("Problem saving config: {}", e.getMessage());
        }
    }
}
