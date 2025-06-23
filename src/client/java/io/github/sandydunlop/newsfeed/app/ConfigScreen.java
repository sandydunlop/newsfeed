package io.github.sandydunlop.newsfeed.app;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.json.JSONObject;

import com.rometools.rome.feed.synd.SyndEntry;
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
import io.github.sandydunlop.cupra.common.widgets.CFormattedLabel;
import io.github.sandydunlop.cupra.common.widgets.CLabel;
import io.github.sandydunlop.cupra.common.widgets.CListBox;
import io.github.sandydunlop.cupra.common.widgets.CListBoxEntry;
import io.github.sandydunlop.cupra.common.widgets.CSpacer;
import io.github.sandydunlop.cupra.common.widgets.CTextBox;
import io.github.sandydunlop.cupra.platform.PlatformServices;
import io.github.sandydunlop.newsfeed.mod.NewsfeedClientModInitializer;
import io.github.sandydunlop.newsfeed.mod.NewsfeedModInitializer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;


public class ConfigScreen extends CupraScreen {
	private static final Logger LOGGER = LogManager.getLogger("newsfeed");
	private final int VALIDATION_DELAY = 10;
	
	private int timer = 0;
	private String statusText = "";
	private float statusAlpha = 0.0f;
	private boolean statusIsFading = false;
	private boolean isValidFeed = true;
	private boolean isValidating = false;
	private boolean needsValidating = false;

	private CContainer header;
	private CContainer body;
	private CContainer footer;

	private CLabel urlLabelWidget;
	private CTextBox urlFieldWidget;
	private CContainer checkboxContainer;
    private CContainer buttonContainer;
	private CCheckBox enabledCheckboxlWidget;
	private CCheckBox updateCheckboxlWidget;
	private CButton cancelButton;
	private CButton continueButton;
	private CLabel statusLabel;

	public String feedName = null;
	public String feedUrl = null;
	public boolean feedEnabled = true;
	public boolean updateCheckEnabled = true;
	private Thread thread = null;
	private boolean redraw = false; //NOSONAR - It tells me to make this a local variable, it's modified in a thread so I can't

	public ConfigScreen() {
		super();
		suggestSize(400,300);
		feedName = NewsfeedConfig.feedName;
		feedUrl = NewsfeedConfig.feedUrl;
		feedEnabled = NewsfeedConfig.feedEnabled;
		updateCheckEnabled = NewsfeedConfig.updateCheckEnabled;

		final int WIDGET_HEIGHT = 20;
		final int MEDIUM_VERTICAL_GAP = 10;
		final int SMALL_VERTICAL_GAP = 5;

		this.setPadding(4);

		header = new CContainer(this, true);
		header.setPadding(10);

		body = new CContainer(this);
		body.setPadding(10);

		// new CSpacer(this, MEDIUM_VERTICAL_GAP);

		urlLabelWidget = new CLabel(body, "Feed URL");

		urlFieldWidget = new CTextBox(body, feedUrl);
		// urlFieldWidget.addClearButton();
		// urlFieldWidget.addPasteButton();
		urlFieldWidget.setText(feedUrl);

        new CSpacer(body, MEDIUM_VERTICAL_GAP);

		checkboxContainer = new CContainer(body, true);
		checkboxContainer.setPadding(20);
		checkboxContainer.setHeight(WIDGET_HEIGHT);
		enabledCheckboxlWidget = new CCheckBox(checkboxContainer, "Enable feed", feedEnabled);
        // new CSpacer(body, MEDIUM_VERTICAL_GAP);
		updateCheckboxlWidget = new CCheckBox(checkboxContainer, "Checl for mod updates", updateCheckEnabled);

		statusLabel = new CLabel(body, "")
			.color(0xFF88FF00);

		buttonContainer = new CContainer(this, true);
		buttonContainer.setPadding(10);
        cancelButton = new CButton(buttonContainer, "Cancel", click -> {
			this.close();
		});
		continueButton = new CButton(buttonContainer, "Continue", click -> {
			NewsfeedConfig.feedUrl = urlFieldWidget.getText();
			NewsfeedConfig.feedEnabled = enabledCheckboxlWidget.isChecked();
			NewsfeedConfig.updateCheckEnabled = updateCheckboxlWidget.isChecked();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("feedUrl", urlFieldWidget.getText());
			jsonObject.put("feedEnabled", enabledCheckboxlWidget.isChecked());
			jsonObject.put("updateCheckEnabled", updateCheckboxlWidget.isChecked());

			try (FileWriter file = new FileWriter(NewsfeedConfig.configFilePath.toString(), StandardCharsets.UTF_8)) {
				file.write(jsonObject.toString(4));
				file.flush();
			} catch (IOException e) {
				LOGGER.error("Problem saving config: {}", e.getMessage());
			}
			NewsfeedClientModInitializer.updateNow();
			this.close();
		});
		continueButton.setEnabled(false);

		thread = new Thread(this::validationChecker);
		thread.start();
	}


	@Override
	public void onClose() {
		if (thread != null && thread.isAlive()){
			LOGGER.info("Validation thread interrupted");
			thread.interrupt();
			thread = null;
		}
	}


	public void validationChecker() {
		do {
			redraw = false;
			// If URL is changed, wait VALIDATION_DELAY ticks before checking if it's a valid feed
			if (urlFieldWidget!= null && !urlFieldWidget.getText().equals((feedUrl))){
				timer = VALIDATION_DELAY;
				// statusIsFading = true;
				feedUrl = urlFieldWidget.getText();
				isValidFeed = false;
			}

			// if (statusIsFading){
			// 	if (statusAlpha > 0.01f){
			// 		statusAlpha -= 0.01f;
			// 	}else{
			// 		statusIsFading = false;
			// 	}
			// }

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
						// statusIsFading = true;
						isValidFeed = true;
						statusLabel.setText("");
						redraw = true;
					}else{
						statusLabel.setText("newsfeed.config.invalid.status");
						redraw = true;
						// statusAlpha = 1.0f;
						// statusIsFading = false;
						isValidFeed = false;
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
			if (continueButtonEnabled != continueButton.isEnabled() || redraw) {
				PlatformServices.getInstance().render();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			//LOGGER.info("Validation thread running: {}", Thread.currentThread().isAlive());
		} while (!Thread.interrupted());
		if (thread != null && thread.isAlive()){
			LOGGER.info("Validation thread interrupted");
			thread.interrupt();
			thread = null;
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
