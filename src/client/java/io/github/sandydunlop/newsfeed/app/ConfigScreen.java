package io.github.sandydunlop.newsfeed.app;

import java.net.URI;
import java.net.URL;

import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import io.github.sandydunlop.cupra.common.CupraScreen;
import io.github.sandydunlop.cupra.common.logging.Logger;
import io.github.sandydunlop.cupra.common.logging.LogManager;
import io.github.sandydunlop.cupra.common.util.Align;
import io.github.sandydunlop.cupra.common.widgets.CButton;
import io.github.sandydunlop.cupra.common.widgets.CCheckBox;
import io.github.sandydunlop.cupra.common.widgets.CContainer;
import io.github.sandydunlop.cupra.common.widgets.CDropdownTextBox;
import io.github.sandydunlop.cupra.common.widgets.CImage;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.platform.PlatformServices;


public class ConfigScreen extends CupraScreen {
	private static final Logger LOGGER = LogManager.getLogger("Newsfeed");
	private static final int VALIDATION_DELAY = 10;
	
	private int timer = 0;
	private boolean isValidFeed = true;
	private boolean isValidating = false;
	private boolean needsValidating = false;
	private Thread validationThread = null;
	private String feedUrl = null;

	private CContainer header;
	private CContainer body;
	private CContainer footer;

	private CDropdownTextBox urlField;
	private CCheckBox enabledCheckbox;
	private CCheckBox autoScrollCheckbox;
	private CCheckBox updateCheckbox;
	private CCheckBox debugCheckbox;
	private CLabel statusLabel;
	private CButton continueButton;


	public ConfigScreen() {
		super();
		final int WIDGET_HEIGHT = 20;
		setTitle("Config");
		suggestSize(440,300);
		feedUrl = NewsfeedConfig.feedUrl;

		header = new CContainer(this, true);
        header.setPadding(4);
		header.setExpandable(false);
		header.setHeight(40);
		header.setId("header");

		body = new CContainer(this, true);
		body.setAlignHorizontal(Align.Horizontal.MIDDLE);
		body.setId("body");

		footer = new CContainer(this, true);
        footer.setPadding(10);
		footer.setExpandable(false);
		footer.setAlignHorizontal(Align.Horizontal.MIDDLE);
		footer.setHeight(40);
		footer.setId("footer");

		CImage icon = new CImage(header);
		icon.fromResource("assets/newsfeed/icon-32.png");
		icon.setWidth(32);
		icon.setHeight(32);

		new CLabel(header, "Newsfeed Config")
                .fontSize(22)
                .bold();

		new CSpacer(body, 50);
		CContainer middle = new CContainer(body);
		middle.setWidth(350);
		middle.setId("middle");

		new CLabel(middle, "Feed URL");

		urlField = new CDropdownTextBox(middle, feedUrl);
		urlField.setWidth(300);
		urlField.add(new CListBoxEntry("https://feeds.bbci.co.uk/news/world/rss.xml", null));
		urlField.add(new CListBoxEntry("https://www.reddit.com/r/AskReddit/new/.rss", null));
		urlField.add(new CListBoxEntry("https://nullforums.net/forums/minecraft-rss.473/index.rss", null));
		urlField.add(new CListBoxEntry("https://www.minecraftforum.net/news.rss", null));
		urlField.add(new CListBoxEntry("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml", null));
		urlField.add(new CListBoxEntry("https://rss.nytimes.com/services/xml/rss/nyt/World.xml", null));
		urlField.setText(feedUrl);

		CContainer checkboxContainer = new CContainer(middle, false);
		checkboxContainer.setId("checkboxContainer");
		checkboxContainer.setAlignHorizontal(Align.Horizontal.MIDDLE);
		enabledCheckbox = new CCheckBox(checkboxContainer, "Enable feed", NewsfeedConfig.feedEnabled);
		autoScrollCheckbox = new CCheckBox(checkboxContainer, "Auto scroll to new articles", NewsfeedConfig.autoScrollEnabled);
		updateCheckbox = new CCheckBox(checkboxContainer, "Check for mod updates", NewsfeedConfig.updateCheckEnabled);
		debugCheckbox = new CCheckBox(checkboxContainer, "Log debug information", NewsfeedConfig.debugLogEnabled);

		statusLabel = new CLabel(middle, "");
		statusLabel.color(0xFF88FF00);
		statusLabel.setHeight(WIDGET_HEIGHT);
		new CSpacer(body, 50);

        new CButton(footer, "Cancel", click -> {
			this.close();
		});
		continueButton = new CButton(footer, "Continue", click -> {
			NewsfeedConfig.feedUrl = urlField.getText();
			NewsfeedConfig.feedEnabled = enabledCheckbox.isChecked();
			NewsfeedConfig.autoScrollEnabled = autoScrollCheckbox.isChecked();
			NewsfeedConfig.updateCheckEnabled = updateCheckbox.isChecked();
			NewsfeedConfig.debugLogEnabled = debugCheckbox.isChecked();
			NewsfeedConfig.saveConfig();
			Newsfeed.getBackgroundThread().restart();
			PlatformServices.getInstance().setDebugLogging(NewsfeedConfig.debugLogEnabled);
			this.close();
		});
		continueButton.setEnabled(false);
	}


	@Override
	public void onClose() {
		if (validationThread != null && validationThread.isAlive()){
			validationThread.interrupt();
			validationThread = null;
		}
	}


	@Override
	public void onShow() {
		enabledCheckbox.setChecked(NewsfeedConfig.feedEnabled);
		autoScrollCheckbox.setChecked(NewsfeedConfig.autoScrollEnabled);
		updateCheckbox.setChecked(NewsfeedConfig.updateCheckEnabled);
		debugCheckbox.setChecked(NewsfeedConfig.debugLogEnabled);

		validationThread = new Thread(this::validationChecker);
		validationThread.setName("Newsfeed-Vldt");
		validationThread.start();
	}


	public void validationChecker() {
		do {
			// If URL is changed, wait VALIDATION_DELAY ms before checking if it's a valid feed
			if (urlField!= null && !urlField.getText().equals((feedUrl))){
				timer = VALIDATION_DELAY;
				feedUrl = urlField.getText();
				isValidFeed = false;
			}

			if (timer > 0){
				if (--timer == 0) {
					needsValidating = true;
				}			
			}

			if (needsValidating && !isValidating){
				isValidating = true;
				needsValidating = false;
				Thread thread = new Thread(() -> {
					if (validateFeed()){
						isValidFeed = true;
						statusLabel.setText("");
						PlatformServices.getInstance().render();
					}else{
						statusLabel.setText("Invalid Feed URL");
						isValidFeed = false;
						PlatformServices.getInstance().render();
					}
					isValidating = false;
				});
				thread.start();
			}

			if (urlField == null) {
				continue;
			}
			boolean continueButtonEnabled = continueButton.isEnabled();
			if (isValidating){
				continueButton.setEnabled(false);
			}else if(!isValidFeed && !NewsfeedConfig.feedUrl.equals(urlField.getText())){
				continueButton.setEnabled(false);
			}else if (!NewsfeedConfig.feedUrl.equals(urlField.getText()) ||
				NewsfeedConfig.feedEnabled != enabledCheckbox.isChecked() ||
				NewsfeedConfig.autoScrollEnabled != autoScrollCheckbox.isChecked() ||
				NewsfeedConfig.debugLogEnabled != debugCheckbox.isChecked() ||
				NewsfeedConfig.updateCheckEnabled != updateCheckbox.isChecked()){
				continueButton.setEnabled(true);
			}else{
				continueButton.setEnabled(false);
			}
			if (continueButtonEnabled != continueButton.isEnabled()) {
				PlatformServices.getInstance().render();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} while (!Thread.interrupted());
		if (validationThread != null && validationThread.isAlive()){
			LOGGER.info("Validation thread interrupted");
			validationThread.interrupt();
			validationThread = null;
		}
	}


	private boolean validateFeed()
	{
		URL feedSource = null;
		try {
			if (urlField.getText() == null || urlField.getText().isEmpty()){
				return false;
			}
			feedSource = URI.create(urlField.getText()).toURL();
			SyndFeedInput input = new SyndFeedInput();
			input.build(new XmlReader(feedSource));
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
