package io.github.sandydunlop.newsfeed;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import io.github.sandydunlop.cupra.gui.CButton;
import io.github.sandydunlop.cupra.gui.CCheckBox;
import io.github.sandydunlop.cupra.gui.CContainer;
import io.github.sandydunlop.cupra.gui.CGUIScreen;
import io.github.sandydunlop.cupra.gui.CLabel;
import io.github.sandydunlop.cupra.gui.CSpacer;
import io.github.sandydunlop.cupra.gui.CTextBox;


public class NewsfeedConfigScreen extends CGUIScreen {
	private static final Logger LOGGER = LogManager.getLogger(NewsfeedModInitializer.MOD_ID);
	private final int VALIDATION_DELAY = 40;
	
	private int timer = 0;
	private String statusText = "";
	private float statusAlpha = 0.0f;
	private boolean statusIsFading = false;
	private boolean isValidFeed = true;
	private boolean isValidating = false;
	private boolean needsValidating = false;
	
	private CLabel nameLabelWidget;
	private CTextBox nameFieldWidget;
	private CLabel urlLabelWidget;
	private CTextBox urlFieldWidget;
	private CContainer checkboxContainer;
	private CCheckBox enabledCheckboxlWidget;
	private CCheckBox updateCheckboxlWidget;
	private CButton cancelButton;
	private CButton continueButton;

	private Screen parent;
	private Path configFilePath;
	public String feedName = null;
	public String feedUrl = null;
	public boolean feedEnabled = true;
	public boolean updateCheckEnabled = true;


	public NewsfeedConfigScreen(Text title, Screen parent, Path configFile) {
		super(title);
		this.parent = parent;
		configFilePath = configFile;
		feedName = NewsfeedConfig.feedName;
		feedUrl = NewsfeedConfig.feedUrl;
		feedEnabled = NewsfeedConfig.feedEnabled;
		updateCheckEnabled = NewsfeedConfig.updateCheckEnabled;
	}

	@Override
	protected void init() {
		super.init();
		final int WIDGET_HEIGHT = 20;
		final int MEDIUM_VERTICAL_GAP = 10;
		final int SMALL_VERTICAL_GAP = 5;

		nameLabelWidget = new CLabel(this, Text.translatable("newsfeed.config.feedName.label"));
		this.addToBody(nameLabelWidget);

		nameFieldWidget = new CTextBox(this, feedName);
		nameFieldWidget.setText(feedName);
		this.addToBody(nameFieldWidget);

		this.addToBody(new CSpacer(MEDIUM_VERTICAL_GAP));

		urlLabelWidget = new CLabel(this, Text.translatable("newsfeed.config.feedUrl.label"));
		this.addToBody(urlLabelWidget);

		urlFieldWidget = new CTextBox(this, feedUrl);
		urlFieldWidget.addClearButton();
		urlFieldWidget.addPasteButton();
		urlFieldWidget.setText(feedUrl);
		this.addToBody(urlFieldWidget);

		this.addToBody(new CSpacer(MEDIUM_VERTICAL_GAP));

		checkboxContainer = new CContainer(true);
		checkboxContainer.setIsWide(true);
		checkboxContainer.setHeight(WIDGET_HEIGHT);

		enabledCheckboxlWidget = new CCheckBox(this, Text.translatable("newsfeed.config.feedEnabled.label"), feedEnabled);
		checkboxContainer.add(enabledCheckboxlWidget);

		this.addToBody(new CSpacer(SMALL_VERTICAL_GAP));

		updateCheckboxlWidget = new CCheckBox(this, Text.translatable("newsfeed.config.updateCheckEnabled.label"), updateCheckEnabled);
		checkboxContainer.add(updateCheckboxlWidget);

		this.addToBody(checkboxContainer);

		cancelButton = new CButton(this, Text.translatable("newsfeed.config.cancel.button"), (btn) -> {
			this.close();
		});
		this.addToFooter(cancelButton);

		continueButton = new CButton(this, Text.translatable("newsfeed.config.continue.button"), (btn) -> {
			NewsfeedConfig.feedUrl = urlFieldWidget.getText();
			NewsfeedConfig.feedName = nameFieldWidget.getText();
			NewsfeedConfig.feedEnabled = enabledCheckboxlWidget.isChecked();
			NewsfeedConfig.updateCheckEnabled = updateCheckboxlWidget.isChecked();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("feedName", nameFieldWidget.getText());
			jsonObject.put("feedUrl", urlFieldWidget.getText());
			jsonObject.put("feedEnabled", enabledCheckboxlWidget.isChecked());
			jsonObject.put("updateCheckEnabled", updateCheckboxlWidget.isChecked());

			try (FileWriter file = new FileWriter(configFilePath.toString(), Charset.forName("UTF-8"))) {
				file.write(jsonObject.toString(4));
				file.flush();
			} catch (IOException e) {
				LOGGER.error("Problem saving config: {}", e.getMessage());
			}
			NewsfeedClientModInitializer.updateNow();
			this.close();
		});
		continueButton.setEnabled(false);
		this.addToFooter(continueButton);

		layout();
	}


	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (client.player != null){
			this.applyBlur();
			this.renderInGameBackground(context);
		}
		super.render(context, mouseX, mouseY, delta);
		if (client.player == null){
			drawBackground(context);
		}

		// If URL is changed, wait VALIDATION_DELAY ticks before checking if it's a valid feed
		if (urlFieldWidget!= null && !urlFieldWidget.getText().equals((feedUrl))){
			timer = VALIDATION_DELAY;
			statusIsFading = true;
			feedUrl = urlFieldWidget.getText();
			isValidFeed = false;
		}

		if (statusIsFading){
			if (statusAlpha > 0.01f){
				statusAlpha -= 0.01f;
			}else{
				statusIsFading = false;
			}
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
					statusIsFading = true;
					isValidFeed = true;
				}else{
					statusText = Text.translatable("newsfeed.config.invalid.status").getString();
					statusAlpha = 1.0f;
					statusIsFading = false;
					isValidFeed = false;
				}
				isValidating = false;
			});
			thread.start();
		}

		if (isValidating){
			continueButton.setEnabled(false);
		}else if(!isValidFeed && !NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText())){
			continueButton.setEnabled(false);
		}else if (!NewsfeedConfig.feedName.equals(nameFieldWidget.getText()) ||
			!NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText()) ||
			NewsfeedConfig.feedEnabled != enabledCheckboxlWidget.isChecked() ||
			NewsfeedConfig.updateCheckEnabled != updateCheckboxlWidget.isChecked()){
			continueButton.setEnabled(true);
		}else{
			continueButton.setEnabled(false);
		}

		// Logo and title
		int logoTop = 5;
		int logoLeft = (int)(this.width * 0.1);
		Identifier texture = Identifier.of(NewsfeedModInitializer.MOD_ID, "icon-32.png");
		context.drawTexture(RenderLayer::getGuiTextured, texture, logoLeft, logoTop, 0, 0, 32, 32, 32, 32);
		context.getMatrices().push();  
		context.getMatrices().scale(2.0F, 2.0F, 1F);  
		context.drawText(client.textRenderer, Text.translatable("newsfeed.config.title"), (logoLeft + 40) / 2, logoTop + 1, 0xFFFFFFFF, true);
		context.getMatrices().pop();

		// Status text
		int alpha = (int)(statusAlpha * 255);
		if (alpha > 10){
			int color = (alpha << 24) | (255 << 16) |  (207 << 8);
			int screenWidth = client.getWindow().getScaledWidth();
			int screenHeight = client.getWindow().getScaledHeight();
			int x = (screenWidth - client.textRenderer.getWidth(statusText)) / 2;
			int y = screenHeight - 60;
			context.drawText(client.textRenderer, statusText, x, y, color, false);
		}
	}


	private void drawBackground(DrawContext context) {
		context.fill(0, 40, this.width, this.height - 40, 0x88000000);
		context.drawHorizontalLine(0, this.width, 40, 0xFF3F3F3F);
		context.drawHorizontalLine(0, this.width, 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 41, 0xFF000000);
		context.drawHorizontalLine(0, this.width, this.height - 40, 0xFF3F3F3F);
	}

	
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


	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
