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

	private CContainer header;
	private CContainer body;
	private CContainer footer;

	private CDropdownTextBox urlFieldWidget;
	private CContainer checkboxContainer;
	private CCheckBox enabledCheckboxlWidget;
	private CCheckBox updateCheckboxlWidget;
	private CButton continueButton;
	private CLabel statusLabel;

	private String feedUrl = null;
	private boolean feedEnabled = true;
	private boolean updateCheckEnabled = true;
	private Thread validationThread = null;

	public ConfigScreen() {
		super();
		setTitle("Config");
		suggestSize(440,300);
		feedUrl = NewsfeedConfig.feedUrl;
		feedEnabled = NewsfeedConfig.feedEnabled;
		updateCheckEnabled = NewsfeedConfig.updateCheckEnabled;

		this.setTooltip("SCREEN");

		final int WIDGET_HEIGHT = 20;

        this.setAlignHorizontal(Align.Horizontal.SPREAD);

		header = new CContainer(this, true);
        header.setPadding(4);
		header.setExpandable(false);
		//header.setHeight(30);

		body = new CContainer(this);
		body.setPadding(10);
		header.setExpandable(true);

		footer = new CContainer(this, true);
		footer.setPadding(10);
		footer.setExpandable(false);

        CLabel title = new CLabel(header, "Newsfeed Config")
                .fontSize(22)
                .bold();
        //title.setHeight(28);

		new CLabel(body, "Feed URL");

		urlFieldWidget = new CDropdownTextBox(body, feedUrl);
		urlFieldWidget.setWidth(400);
		urlFieldWidget.add(new CListBoxEntry("https://feeds.bbci.co.uk/news/world/rss.xml", null));
		urlFieldWidget.add(new CListBoxEntry("https://www.reddit.com/r/AskReddit/new/.rss", null));
		urlFieldWidget.setText(feedUrl);

		checkboxContainer = new CContainer(body, true);
		checkboxContainer.setPadding(20);
		enabledCheckboxlWidget = new CCheckBox(checkboxContainer, "Enable feed", feedEnabled);
		updateCheckboxlWidget = new CCheckBox(checkboxContainer, "Check for mod updates", updateCheckEnabled);

		new CSpacer(body, WIDGET_HEIGHT);
		statusLabel = new CLabel(body, "");
		statusLabel.color(0xFF88FF00);
		statusLabel.setHeight(WIDGET_HEIGHT);

        new CButton(footer, "Cancel", click -> {
			this.close();
		});
		continueButton = new CButton(footer, "Continue", click -> {
			NewsfeedConfig.feedUrl = urlFieldWidget.getText();
			NewsfeedConfig.feedEnabled = enabledCheckboxlWidget.isChecked();
			NewsfeedConfig.updateCheckEnabled = updateCheckboxlWidget.isChecked();
			NewsfeedConfig.saveConfig();
			Newsfeed.getBackgroundThread().restart();
			this.close();
		});
		continueButton.setEnabled(false);

		// header.setTooltip("HEADER");
		// header.setDebug(0xffffff00);
		// body.setDebug(0xFF00AAAA);
		// footer.setDebug(0xFFAAAA00);
		// checkboxContainer.setDebug(0xFFFFFFFF);
		// statusLabel.setDebug(0xFF00FF00);

		validationThread = new Thread(this::validationChecker);
		validationThread.setName("Newsfeed Validation Thread");
		validationThread.start();
	}


	@Override
	public void onClose() {
		if (validationThread != null && validationThread.isAlive()){
			validationThread.interrupt();
			validationThread = null;
		}
	}


	public void validationChecker() {
		do {
			// If URL is changed, wait VALIDATION_DELAY ticks before checking if it's a valid feed
			if (urlFieldWidget!= null && !urlFieldWidget.getText().equals((feedUrl))){
				timer = VALIDATION_DELAY;
				feedUrl = urlFieldWidget.getText();
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
						statusLabel.setText("newsfeed.config.invalid.status");
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
			}else if(!isValidFeed && !NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText())){
				continueButton.setEnabled(false);
			}else if (!NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText()) ||
				NewsfeedConfig.feedEnabled != enabledCheckboxlWidget.isChecked() ||
				NewsfeedConfig.updateCheckEnabled != updateCheckboxlWidget.isChecked()){
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
			if (urlFieldWidget.getText() == null || urlFieldWidget.getText().isEmpty()){
				return false;
			}
			feedSource = URI.create(urlFieldWidget.getText()).toURL();
			SyndFeedInput input = new SyndFeedInput();
			@SuppressWarnings("unused")
			SyndFeed feed = input.build(new XmlReader(feedSource));
			return true;
		}catch(Exception e){
			return false;
		}
	}


	// @Override
	// public void close() {
	// 	this.client.setScreen(this.parent);
	// }

}
