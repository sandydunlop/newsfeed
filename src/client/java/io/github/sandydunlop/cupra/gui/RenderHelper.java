package io.github.sandydunlop.cupra.gui;

import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.*;
// import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.*;
//;.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class RenderHelper {

	// public static void fill(DrawContext context, int bottom, int left, int top, int right, int color) {
		
	// 	context.fill(RenderLayer.getGui(), left, bottom, right, top, color);
	// }
	
	public static void drawItemWithScale(DrawContext context, ItemStack stack, int x, int y, float scale) {
        
		if (stack.isEmpty()) return;
		
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // BakedSimpleModel bakedModel = mc.getItemRenderer().getModel(stack, null, null, 0);
        
        // context.getMatrices().push();
        // context.getMatrices().translate(x + 8, y + 8, 25);
        
        // try {
        	
        //     context.getMatrices().multiplyPositionMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
        //     context.getMatrices().scale(16.0f * scale, 16.0f * scale, 16.0f * scale);

        //     boolean bl = !bakedModel.isSideLit();
            
        //     if (bl) DiffuseLighting.disableGuiDepthLighting();
            
        //     mc.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, context.getMatrices(), context.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel);
        //     context.draw();
            
        //     if (bl) DiffuseLighting.enableGuiDepthLighting();
        
        // } catch (Throwable throwable) {
            
        // 	CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"Rendering item");
        //     CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
        //     crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
        //     crashReportSection.add("Item Damage", () -> String.valueOf(stack.getDamage()));
        //     crashReportSection.add("Item NBT", () -> String.valueOf(stack.getNbt()));
        //     crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
        //     throw new CrashException(crashReport);
        // }
        // context.getMatrices().pop();
    }
	
	// public static void drawDirtBackgroundWithBrightness(DrawContext context, float brightness, int x, int y, int width, int height) {
		
	// 	context.setShaderColor(brightness, brightness, brightness, 1.0f);
	// 	context.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, x, y, 0, 0.0f, 0.0f, width, height, 32, 32);
	// 	context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	// }
}
