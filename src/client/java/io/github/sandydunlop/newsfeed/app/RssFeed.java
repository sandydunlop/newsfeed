package io.github.sandydunlop.newsfeed.app;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import io.github.sandydunlop.cupra.platform.PlatformServices;


public class RssFeed {
	private static final Logger LOGGER = LogManager.getLogger("newsfeed");
	List<SyndEntry> currentEntries;
	List<SyndEntry> usedEntries;
	public URL feedSource;
	String feedTitle;


    public RssFeed()
	{
		currentEntries = new ArrayList<SyndEntry>();
		usedEntries = new ArrayList<SyndEntry>();
		init();
	}


	public URL getFeedSource() {
		return feedSource;
	}


	private boolean hasUrlChanged(){
		if (NewsfeedConfig.feedUrl != null && !NewsfeedConfig.feedUrl.equals(feedSource.toString())){
			return true;
		}else{
			return false;
		}
	}


	public void init(){
		try{
			if (NewsfeedConfig.feedUrl!= null && !NewsfeedConfig.feedUrl.isEmpty()&& currentEntries.size() == 0) {
				int suppressedCount = 0;
				feedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(feedSource));
				feedTitle = feed.getTitle();
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					usedEntries.add(entry);
					suppressedCount++;
				}
				String msg= String.format("%s feed loaded. Suppressing %d old articles.", feedTitle, suppressedCount);
				LOGGER.info(msg);
			}
		}catch(IOException e){
			String msg = String.format("Invalid feed at %s", feedSource.toString(), null);
			LOGGER.error(msg);
			PlatformServices.getInstance().showNotification(msg);
		}catch(FeedException e){
			LOGGER.error("FeedException: {}", e.getMessage());
		}
	}


	public void fetch()
	{
		System.out.println("fetch");
		if (NewsfeedConfig.feedUrl!=null && !NewsfeedConfig.feedUrl.isEmpty()){
			for (SyndEntry entry : currentEntries) {
				usedEntries.add(entry);
			}
			currentEntries.clear();
			URL tryFeedSource = null;
			try {
				tryFeedSource = URI.create(NewsfeedConfig.feedUrl).toURL();
				if (tryFeedSource == null) {
					LOGGER.error("Feed URL is invalid.");
					feedSource = null;
					return;
				}
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = input.build(new XmlReader(tryFeedSource));
				feedTitle = feed.getTitle();
				List<SyndEntry> entries = feed.getEntries();
				for (SyndEntry entry : entries) {
					if (!alreadyUsed(entry)) {
						currentEntries.add(entry);
						PlatformServices.getInstance().showNotification(entry.getTitle());
					}
				}
				feedSource = tryFeedSource;
				
			}catch(IOException e){
				String msg = String.format("Invalid feed at %s", tryFeedSource.toString(), null);
				LOGGER.error(msg);
				PlatformServices.getInstance().showNotification(msg);
			}catch(FeedException e){
				LOGGER.error("FeedException1: {}", e.getMessage());
			} 
		}
	}
	

	public void update()
	{
		if (currentEntries.size() > 0 && this.feedSource.toString().equals(NewsfeedConfig.feedUrl)) {
			if (NewsfeedConfig.feedEnabled){
				SyndEntry toDisplay = null;
				for (SyndEntry entry : currentEntries) {
					if (!alreadyUsed(entry)) {
						toDisplay = entry;
						usedEntries.add(entry);
						currentEntries.remove(entry);
						//break;
						PlatformServices.getInstance().showNotification(entry.getTitle());
					}
				}
				if (toDisplay != null) {
					String msg = toDisplay.getTitle();
					LOGGER.info(msg);
					// PlatformServices.getInstance().showNotification(msg);
				}
			}
		}else if (currentEntries.size() == 0 ||
				!this.feedSource.toString().equals(NewsfeedConfig.feedUrl)){
			fetch();
		}
	}


	public SyndEntry getEntry(int n) {
		if (usedEntries.size() > n) {
			return usedEntries.get(n);
		}
		return null;
	}


	private boolean alreadyGot(SyndEntry entry) {
		for(SyndEntry e : currentEntries){
			if(e.getLink().equals(entry.getLink())) {
				return true;
			}
		}
		return alreadyUsed(entry);
	}


	private boolean alreadyUsed(SyndEntry entry) {
		for(SyndEntry e : usedEntries){
			if(e.getLink().equals(entry.getLink())) {
				return true;
			}
		}
		return false;
	}


}
