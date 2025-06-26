package io.github.sandydunlop.newsfeed.app;

import java.net.URI;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.rometools.rome.io.XmlReader;

import io.github.sandydunlop.cupra.common.CupraScreen;
import io.github.sandydunlop.cupra.common.util.Align;
import io.github.sandydunlop.cupra.common.widgets.CButton;
import io.github.sandydunlop.cupra.common.widgets.CCheckBox;
import io.github.sandydunlop.cupra.common.widgets.CContainer;
import io.github.sandydunlop.cupra.common.widgets.CDropdownTextBox;
import io.github.sandydunlop.cupra.common.widgets.CFlexiSpacer;
import io.github.sandydunlop.cupra.common.widgets.CImage;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.platform.PlatformServices;


public class ConfigScreen extends CupraScreen {
	private static final Logger LOGGER = LogManager.getLogger("newsfeed");
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
	private CButton continueButton;
	private CLabel statusLabel;


	public ConfigScreen() {
		super();
		final int WIDGET_HEIGHT = 20;
		setTitle("Config");
		suggestSize(440,300);
        setAlignHorizontal(Align.Horizontal.SPREAD);
		feedUrl = NewsfeedConfig.feedUrl;

		header = new CContainer(this, true);
        header.setPadding(4);
		header.setExpandable(false);

		body = new CContainer(this, true);
		body.setPadding(10);
		body.setExpandable(true);

		footer = new CContainer(this, true);
		footer.setPadding(10);
		footer.setExpandable(false);

		CImage icon = new CImage(header);
		icon.fromResource("assets/newsfeed/icon-32.png");
		icon.setWidth(32);
		icon.setHeight(32);

		new CLabel(header, "Newsfeed Config")
                .fontSize(22)
                .bold();

		new CFlexiSpacer(body);
		CContainer middle = new CContainer(body);

		new CSpacer(middle, WIDGET_HEIGHT);
		new CLabel(middle, "Feed URL");
		new CSpacer(middle, 4);

		urlField = new CDropdownTextBox(middle, feedUrl);
		urlField.setWidth(400);
		urlField.add(new CListBoxEntry("https://feeds.bbci.co.uk/news/world/rss.xml", null));
		urlField.add(new CListBoxEntry("https://www.reddit.com/r/AskReddit/new/.rss", null));
		urlField.add(new CListBoxEntry("https://nullforums.net/forums/minecraft-rss.473/index.rss", null));
		urlField.add(new CListBoxEntry("https://www.minecraftforum.net/news.rss", null));
		urlField.add(new CListBoxEntry("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml", null));
		urlField.add(new CListBoxEntry("https://rss.nytimes.com/services/xml/rss/nyt/World.xml", null));
		urlField.setText(feedUrl);

		CContainer checkboxContainer = new CContainer(middle, false);
		checkboxContainer.setPadding(0);
		checkboxContainer.setAlignHorizontal(Align.Horizontal.SPREAD);
		new CSpacer(checkboxContainer, 40);
		enabledCheckbox = new CCheckBox(checkboxContainer, "Enable feed", NewsfeedConfig.feedEnabled);
		new CSpacer(checkboxContainer, 10);
		autoScrollCheckbox = new CCheckBox(checkboxContainer, "Auto scroll to new articles", NewsfeedConfig.autoScroll);
		new CSpacer(checkboxContainer, 10);
		updateCheckbox = new CCheckBox(checkboxContainer, "Check for mod updates", NewsfeedConfig.updateCheckEnabled);

		new CSpacer(middle, WIDGET_HEIGHT);
		statusLabel = new CLabel(middle, "");
		statusLabel.color(0xFF88FF00);
		statusLabel.setHeight(WIDGET_HEIGHT);
		new CFlexiSpacer(body);

		new CFlexiSpacer(footer);
        new CButton(footer, "Cancel", click -> {
			this.close();
		});
		continueButton = new CButton(footer, "Continue", click -> {
			NewsfeedConfig.feedUrl = urlField.getText();
			NewsfeedConfig.feedEnabled = enabledCheckbox.isChecked();
			NewsfeedConfig.autoScroll = autoScrollCheckbox.isChecked();
			NewsfeedConfig.updateCheckEnabled = updateCheckbox.isChecked();
			NewsfeedConfig.saveConfig();
			Newsfeed.getBackgroundThread().restart();
			this.close();
		});
		continueButton.setEnabled(false);
		new CFlexiSpacer(footer);
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
		validationThread = new Thread(this::validationChecker);
		validationThread.setName("Newsfeed Validation Thread");
		validationThread.start();
	}


	public void validationChecker() {
		do {
			// If URL is changed, wait VALIDATION_DELAY ticks before checking if it's a valid feed
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

			boolean continueButtonEnabled = continueButton.isEnabled();
			if (isValidating){
				continueButton.setEnabled(false);
			}else if(!isValidFeed && !NewsfeedConfig.feedUrl.equals(urlField.getText())){
				continueButton.setEnabled(false);
			}else if (!NewsfeedConfig.feedUrl.equals(urlField.getText()) ||
				NewsfeedConfig.feedEnabled != enabledCheckbox.isChecked() ||
				NewsfeedConfig.autoScroll != autoScrollCheckbox.isChecked() ||
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


	// public void fff(){
	// 	// Logo and title
	// 	int logoTop = 5;
	// 	int logoLeft = (int)(this.width * 0.1);
	// 	Identifier texture = Identifier.of(NewsfeedModInitializer.MOD_ID, "icon-32.png");
	// 	context.drawTexture(RenderLayer::getGuiTextured, texture, logoLeft, logoTop, 0, 0, 32, 32, 32, 32);
	// 	context.getMatrices().push();
	// 	context.getMatrices().scale(2.0F, 2.0F, 1F);  
	// 	context.drawText(client.textRenderer, "newsfeed.config.title", (logoLeft + 40) / 2, logoTop + 1, 0xFFFFFFFF, true);
	// 	context.getMatrices().pop();

	// 	// Status text
	// 	int alpha = (int)(statusAlpha * 255);
	// 	if (alpha > 10){
	// 		int color = (alpha << 24) | (255 << 16) |  (207 << 8);
	// 		int screenWidth = client.getWindow().getScaledWidth();
	// 		int screenHeight = client.getWindow().getScaledHeight();
	// 		int x = (screenWidth - client.textRenderer.getWidth(statusText)) / 2;
	// 		int y = screenHeight - 60;
	// 		context.drawText(client.textRenderer, statusText, x, y, color, false);
	// 	}
	// }


	// private void drawBackground(DrawContext context) {
	// 	context.fill(0, 40, this.width, this.height - 40, 0x88000000);
	// 	context.drawHorizontalLine(0, this.width, 40, 0xFF3F3F3F);
	// 	context.drawHorizontalLine(0, this.width, 41, 0xFF000000);
	// 	context.drawHorizontalLine(0, this.width, this.height - 41, 0xFF000000);
	// 	context.drawHorizontalLine(0, this.width, this.height - 40, 0xFF3F3F3F);
	// }

	
	private boolean validateFeed()
	{
		URL feedSource = null;
		try {
			if (urlField.getText() == null || urlField.getText().isEmpty()){
				return false;
			}
			feedSource = URI.create(urlField.getText()).toURL();
			SyndFeedInput input = new SyndFeedInput();
			@SuppressWarnings("unused")
			SyndFeed feed = input.build(new XmlReader(feedSource));
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
