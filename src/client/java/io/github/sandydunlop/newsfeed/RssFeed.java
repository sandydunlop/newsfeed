package io.github.sandydunlop.newsfeed;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;


public class RssFeed
{
	private static final Logger LOGGER = LogManager.getLogger(NewsfeedModInitializer.MOD_ID);
	List<SyndEntry> currentEntries;
	List<SyndEntry> usedEntries;
	public URL feedSource;
	String feedTitle;
	MinecraftClient client;

    public RssFeed()
	{
		currentEntries = new ArrayList<SyndEntry>();
		usedEntries = new ArrayList<SyndEntry>();
		init();
	}

	public void setClient(MinecraftClient client){
		this.client = client;
	}

	private boolean hasUrlChanged(){
		if (NewsfeedConfig.feedUrl != null && !NewsfeedConfig.feedUrl.equals(feedSource.toString())){
			System.out.println("URL has changed");
			return true;
		}else{
			return false;
		}
	}

	public void init(){
		try{
			if (NewsfeedConfig.feedUrl!= null && !NewsfeedConfig.feedUrl.isEmpty()&& currentEntries.size() == 0) {
				int suppressedCount = 0;
				feedTitle = NewsfeedConfig.feedName;
				feedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				System.out.println("init loading from " + feedSource.toString());
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedSource));
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					usedEntries.add(entry);
					suppressedCount++;
				}
				String msg= String.format("%s feed loaded. Suppressing %d old articles.", feedTitle, suppressedCount);
				LOGGER.info(msg);
				if (client!=null && client.player!=null)
					client.player.sendMessage(Text.of(msg), false);
			}
		}catch(IOException e){
			String msg = String.format("Invalid feed at %s", feedSource.toString(), null);
			LOGGER.error(msg);
			if (client!=null && client.player!=null)
				client.player.sendMessage(Text.of(msg), false);
		}catch(FeedException e){
			LOGGER.error("FeedException: {}", e.getMessage());
		}
	}

	private void fetch()
	{
		if (NewsfeedConfig.feedUrl!=null && !NewsfeedConfig.feedUrl.isEmpty()){
			if (hasUrlChanged()){
				String msg = "Feed URL has changed. Reloading feed.";
				LOGGER.info(msg);
				if (client!=null && client.player!=null)
					client.player.sendMessage(Text.of(msg), false);
				currentEntries.clear();
				usedEntries.clear();
				init();
				return;	
			}
			Thread thread = new Thread(() -> {
				URL tryFeedSource = null;
				try {
					if (currentEntries.size() == 0) {
						feedTitle = NewsfeedConfig.feedName;
						tryFeedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
						if (tryFeedSource == null) {
							LOGGER.error("Feed URL is invalid.");
							feedSource = null;
							return;
						}
						System.out.println("fetch loading from " + tryFeedSource.toString());
						SyndFeedInput input = new SyndFeedInput();
						SyndFeed feed = input.build(new XmlReader(tryFeedSource));
						List<SyndEntry> entries = feed.getEntries();
						for (SyndEntry entry : entries) {
							if (!alreadyGot(entry)) {
								currentEntries.add(entry);
							}
						}
						feedSource = tryFeedSource;
					}
				}catch(IOException e){
					String msg = String.format("Invalid feed at %s", tryFeedSource.toString(), null);
					LOGGER.error(msg);
					if (client!=null && client.player!=null)
						client.player.sendMessage(Text.of(msg), false);
				}catch(FeedException e){
					LOGGER.error("FeedException1: {}", e.getMessage());
				} 
			});
			thread.start();
		}
	}
	
	public void update()
	{
		if (currentEntries.size() > 0) {
			if (NewsfeedConfig.feedEnabled){
				SyndEntry toDisplay = null;
				for (SyndEntry entry : currentEntries) {
					if (!alreadyUsed(entry)) {
						toDisplay = entry;
						usedEntries.add(entry);
						currentEntries.remove(entry);
						break;
					}
				}
				if (toDisplay != null) {
					String msg = String.format("%s: %s", feedTitle, toDisplay.getTitle());
					LOGGER.info(msg);
					if (client!=null && client.player!=null)
						client.player.sendMessage(Text.of(msg), false);
				}
			}
		}else if (currentEntries.size() == 0){
			fetch();
		}
	}

	private boolean alreadyGot(SyndEntry entry){
		for(SyndEntry e : currentEntries){
			if(e.getLink().equals(entry.getLink())){
				return true;
			}
		}
		return alreadyUsed(entry);
	}

	private boolean alreadyUsed(SyndEntry entry){
		for(SyndEntry e : usedEntries){
			if(e.getLink().equals(entry.getLink())){
				return true;
			}
		}
		return false;
	}
}
