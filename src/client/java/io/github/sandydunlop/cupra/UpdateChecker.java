package io.github.sandydunlop.cupra;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URL;
import java.util.Optional;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.game.GameProvider;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UpdateChecker
{
    private String modId = null;
    private static Logger LOGGER;
    private SemanticVersion currentMinecraftVersion;
    private SemanticVersion currentModVersion;
    private SemanticVersion latestModVersion = null;


    public UpdateChecker(String modId) {
        this.modId = modId;
        UpdateChecker.LOGGER = LogManager.getLogger(modId);
    }


    @SuppressWarnings("deprecation")
    public boolean isUpdateAvailable() {
        JSONObject modrinthVersions = getModrinthVersionsJson(modId);
        getCurrentVersions();
        latestModVersion = currentModVersion;

		try{
			JSONArray releases = modrinthVersions.getJSONArray("releases");
			for (int i = 0; i < releases.length(); i++) {
				JSONObject release = releases.getJSONObject(i);
				String releaseModVersionStringRaw = release.getString("version_number");
				final String releaseModVersionString;
				if (releaseModVersionStringRaw != null) {
					int p = releaseModVersionStringRaw.indexOf('-');
					if (p > 0) {
						releaseModVersionString = releaseModVersionStringRaw.substring(0, p);
					} else {
						releaseModVersionString = releaseModVersionStringRaw;
					}
				} else {
					releaseModVersionString = null;
				}

				final SemanticVersion releaseModVersion;
				try{
					releaseModVersion = SemanticVersion.parse(releaseModVersionString);
				} catch (VersionParsingException e) {
					LOGGER.error("Unable to parse Modrinth version JSON for {}: {}", modId, e.getMessage());
					return false;
				}

				JSONArray gameVersions = release.getJSONArray("game_versions");
				for (int j = 0; j < gameVersions.length(); j++) {
					Object releaseGameVersionString = gameVersions.get(j);
                    final SemanticVersion releaseGameVersion;
                    try{
                        releaseGameVersion = SemanticVersion.parse(releaseGameVersionString.toString());
                    } catch (VersionParsingException e) {
                        LOGGER.error("Unable to parse Modrinth version JSON for {}: {}", modId, e.getMessage());
                        return false;
                    }
                    
                    if (releaseGameVersion.equals(currentMinecraftVersion)) {
						if (releaseModVersion.compareTo(latestModVersion) > 0) {
                            latestModVersion = releaseModVersion;
						} 
                    }
				}
                if (!latestModVersion.equals(currentModVersion)){
                    return true;
                }
			}
		} catch (JSONException e) {
            LOGGER.error("Unable to parse JSON: {}", e.getMessage());
		}
        return false;
    }


    public SemanticVersion getLatestVersion() {
        return latestModVersion;
    }


    private boolean getCurrentVersions() {
        Optional<ModContainer> omc = FabricLoader.getInstance().getModContainer(modId);
        if (omc.isPresent()){
            ModContainer mc = omc.get();
            if (mc.getMetadata().getVersion() == null) {
                LOGGER.error("Unable to obtain {} version string", modId);
                return false;
            }
        } else {
            LOGGER.error("Unable to obtain {} version string", modId);
            return false;
        }

        String modVersionString = FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        if (modVersionString == null) {
            LOGGER.error("Unable to obtain {} version string", modId);
            return false;
        }
        try {
            int mcvp = modVersionString.indexOf("mc");
            if (mcvp > -1) {
                modVersionString = modVersionString.substring(0, mcvp - 1);
            }
            currentModVersion = SemanticVersion.parse(modVersionString);
        } catch (VersionParsingException e) {
            LOGGER.error("Unable to parse {} version string \"{}\": {}", modId, modVersionString, e.getMessage());
            return false;
        }

        FabricLoaderImpl fabricLoader = (FabricLoaderImpl)FabricLoader.getInstance();
		GameProvider gp = fabricLoader.getGameProvider();
		String minecraftVersionString = gp.getNormalizedGameVersion();
        try {
            currentMinecraftVersion = SemanticVersion.parse(minecraftVersionString);
        } catch (VersionParsingException e) {
            LOGGER.error("Unable to parse Minecraft version string \"{}\": {}", minecraftVersionString, e.getMessage());
            return false;
        }
        return true;
    }


    private JSONObject getModrinthVersionsJson(String modId) {
        String uriString = String.format("https://api.modrinth.com/v2/project/%s/version", modId);
        URL url = null;
		try {
            url = URI.create(uriString).toURL();
			String jsonString = IOUtils.toString(url, Charset.forName("UTF-8"));
			if (jsonString.length() > 2 && 
					jsonString.charAt(0) =='[' && 
					jsonString.charAt(jsonString.length()-1) == ']'){
				jsonString = "{ releases: " + jsonString + "}";
			}
			return new JSONObject(jsonString);
        } catch (MalformedURLException e) {
            LOGGER.error("Unable to parse URL: {}", uriString);
            return new JSONObject();
		} catch (IOException e) {
            LOGGER.error("Unable to obtain JSON from URL: {}", url);
			return new JSONObject();
		}
    }
}
