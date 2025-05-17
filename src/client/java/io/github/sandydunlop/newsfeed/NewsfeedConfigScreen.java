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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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


public class NewsfeedConfigScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger(NewsfeedModInitializer.MOD_ID);
	private final int VALIDATION_DELAY = 40;
	
	private int timer = 0;
	private String statusText = "";
	private float statusAlpha = 0.0f;
	private boolean statusIsFading = false;
	private boolean isValidFeed = true;
	private boolean isValidating = false;
	private boolean needsValidating = false;
	
	private TextFieldWidget nameFieldWidget;
	private TextFieldWidget urlFieldWidget;
	private CheckboxWidget enabledCheckboxlWidget;
	private ButtonWidget continueButton;

	private Screen parent;
	private Path configFilePath;
	public String feedName = null;
	public String feedUrl = null;
	public boolean feedEnabled = false;


	public NewsfeedConfigScreen(Text title, Screen parent, Path configFile) {
		super(title);
		this.parent = parent;
		configFilePath = configFile;
		feedName = NewsfeedConfig.feedName;
		feedUrl = NewsfeedConfig.feedUrl;
		feedEnabled = NewsfeedConfig.feedEnabled;
	}

	@Override
	protected void init() {
		final int WIDGET_HEIGHT = 20;
		final int MEDIUM_VERTICAL_GAP = 10;
		final int SMALL_VERTICAL_GAP = 5;
		final int screenWidth = this.width;
		final int screenHeight = this.height;
		final int marginLeft = (int)(screenWidth * 0.1);
		int y = screenHeight / 2 - 60;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		int nameLabelWidth = (int)(screenWidth * 0.8);
		int nameLabelHeight = WIDGET_HEIGHT;
		int nameLabelX = marginLeft;
		TextWidget nameLabelWidget = new TextWidget(nameLabelWidth, nameLabelHeight,Text.translatable("newsfeed.config.feedName.label"), textRenderer);
		nameLabelWidget.setX(nameLabelX);
		nameLabelWidget.setY(y);
		nameLabelWidget.alignLeft();
		nameLabelWidget.setAlpha(1.0f);
		this.addDrawableChild(nameLabelWidget);
		y+= WIDGET_HEIGHT;

		int nameFieldWidth = (int)(screenWidth * 0.8);
		int nameFieldHeight = WIDGET_HEIGHT;
		int nameFieldX = marginLeft;
		nameFieldWidget = new TextFieldWidget(textRenderer, nameFieldWidth, nameFieldHeight, Text.of("") );
		nameFieldWidget.setX(nameFieldX);
		nameFieldWidget.setY(y);
		nameFieldWidget.setMaxLength(200);
		nameFieldWidget.setText(feedName);
		nameFieldWidget.setEditable(true);
		nameFieldWidget.setVisible(true);
		this.addDrawableChild(nameFieldWidget);
		y+= WIDGET_HEIGHT;

		y+=MEDIUM_VERTICAL_GAP;

		int urlLabelWidth = (int)(screenWidth * 0.8);
		int urlLabelHeight = WIDGET_HEIGHT;
		int urlLabelX = marginLeft;
		TextWidget urlLabelWidget = new TextWidget(urlLabelWidth, urlLabelHeight,Text.translatable("newsfeed.config.feedUrl.label"), textRenderer);
		urlLabelWidget.setX(urlLabelX);
		urlLabelWidget.setY(y);
		urlLabelWidget.alignLeft();
		urlLabelWidget.setAlpha(1.0f);
		this.addDrawableChild(urlLabelWidget);
		y+= WIDGET_HEIGHT;

		int urlFieldWidth = (int)(screenWidth * 0.8);
		int urlFieldHeight = WIDGET_HEIGHT;
		int urlFieldX = marginLeft;
		urlFieldWidget = new TextFieldWidget(textRenderer, urlFieldWidth, urlFieldHeight, Text.of("") );
		urlFieldWidget.setX(urlFieldX);
		urlFieldWidget.setY(y);
		urlFieldWidget.setMaxLength(200);
		urlFieldWidget.setText(feedUrl);
		urlFieldWidget.setEditable(true);
		urlFieldWidget.setVisible(true);
		this.addDrawableChild(urlFieldWidget);
		urlFieldWidget.setChangedListener(null);
		y+= WIDGET_HEIGHT;

		y+= SMALL_VERTICAL_GAP;
		int enabledCheckboxX = marginLeft;
		enabledCheckboxlWidget = CheckboxWidget.builder(Text.translatable("newsfeed.config.feedEnabled.label"), textRenderer)
			.pos(enabledCheckboxX, y)
			.checked(feedEnabled)
			.build();
		this.addDrawableChild(enabledCheckboxlWidget);
		y+= WIDGET_HEIGHT;


		y = screenHeight - MEDIUM_VERTICAL_GAP - WIDGET_HEIGHT;
		int cancelButtonX = screenWidth/2 - 130;
		ButtonWidget cancelButton = ButtonWidget.builder(Text.translatable("newsfeed.config.cancel.button"), (btn) -> {
			this.close();
		}).build();
		cancelButton.setWidth(120);
		cancelButton.setHeight(WIDGET_HEIGHT);
		cancelButton.setX(cancelButtonX);
		cancelButton.setY(y);
		this.addDrawableChild(cancelButton);


		y = screenHeight - MEDIUM_VERTICAL_GAP - WIDGET_HEIGHT;
		int continueButtonX = screenWidth/2 + 10;
		continueButton = ButtonWidget.builder(Text.translatable("newsfeed.config.continue.button"), (btn) -> {
			NewsfeedConfig.feedUrl = urlFieldWidget.getText();
			NewsfeedConfig.feedName = nameFieldWidget.getText();
			NewsfeedConfig.feedEnabled = enabledCheckboxlWidget.isChecked();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("feedName", nameFieldWidget.getText());
			jsonObject.put("feedUrl", urlFieldWidget.getText());
			jsonObject.put("feedEnabled", enabledCheckboxlWidget.isChecked());

			try (FileWriter file = new FileWriter(configFilePath.toString(), Charset.forName("UTF-8"))) {
				file.write(jsonObject.toString(4));
				file.flush();
			} catch (IOException e) {
				LOGGER.error("Problem saving config: {}", e.getMessage());
			}
			NewsfeedClientModInitializer.updateNow();
			this.close();
		}).build();
		continueButton.setWidth(120);
		continueButton.setHeight(WIDGET_HEIGHT);
		continueButton.setX(continueButtonX);
		continueButton.setY(y);
		continueButton.active = false;
		this.addDrawableChild(continueButton);
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
		if (!urlFieldWidget.getText().equals((feedUrl))){
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
			continueButton.active = false;
		}else if(!isValidFeed && !NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText())){
			continueButton.active = false;
		}else if (!NewsfeedConfig.feedName.equals(nameFieldWidget.getText()) ||
			!NewsfeedConfig.feedUrl.equals(urlFieldWidget.getText()) ||
			NewsfeedConfig.feedEnabled != enabledCheckboxlWidget.isChecked()){
			continueButton.active = true;
		}else{
			continueButton.active = false;
		}

		// Logo and title
		int logoTop = 5;
		int logoLeft = (int)(this.width * 0.1);
		Identifier texture = Identifier.of(NewsfeedModInitializer.MOD_ID, "icon-32.png");
		context.drawTexture(RenderLayer::getGuiTextured, texture, logoLeft, logoTop, 0, 0, 32, 32, 32, 32);
		context.getMatrices().push();  
		context.getMatrices().scale(2.0F, 2.0F, 1F);  
		context.drawText(client.textRenderer, Text.translatable("newsfeed.config.title"), 45, logoTop + 1, 0xFFFFFFFF, true);
		context.getMatrices().pop();

		// Status text
		int alpha = (int)(statusAlpha * 255);
		if (alpha > 10){
			int color = (alpha << 24) | (255 << 16) |  (207 << 8);
			int screenWidth = client.getWindow().getScaledWidth();
			int screenHeight = client.getWindow().getScaledHeight();
			int x = (screenWidth - client.textRenderer.getWidth(statusText)) / 2;
			int y = screenHeight - 50;
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
