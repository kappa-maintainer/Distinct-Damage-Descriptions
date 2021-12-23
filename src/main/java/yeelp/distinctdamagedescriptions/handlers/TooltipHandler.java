package yeelp.distinctdamagedescriptions.handlers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yeelp.distinctdamagedescriptions.ModConsts;
import yeelp.distinctdamagedescriptions.config.ModConfig;
import yeelp.distinctdamagedescriptions.util.DDDFontRenderer;
import yeelp.distinctdamagedescriptions.util.tooltipsystem.TooltipDistributor;

public class TooltipHandler extends Handler {
	private static final ResourceLocation ICONS = new ResourceLocation(ModConsts.MODID, "textures/tooltips/internaldamagetypes.png");

	@SuppressWarnings("static-method")
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(value = Side.CLIENT)
	public void onTooltip(ItemTooltipEvent evt) {
		evt.getToolTip().addAll(1, TooltipDistributor.getDistributor(evt.getItemStack()).getTooltip(evt.getItemStack()));
	}

	@SuppressWarnings("static-method")
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onToolTipPre(RenderTooltipEvent.Pre evt) {
		evt.setFontRenderer(DDDFontRenderer.getInstance(evt.getFontRenderer()));
	}

	@SuppressWarnings("static-method")
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTooltipPost(RenderTooltipEvent.PostText evt) {
		if(ModConfig.client.useIcons) {
			Minecraft mc = Minecraft.getMinecraft();
			GL11.glPushMatrix();
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			mc.getTextureManager().bindTexture(ICONS);
			TooltipDistributor.getDistributor(evt.getStack()).getIcons(evt.getStack(), evt.getX(), evt.getY(), evt.getLines()).forEach((i) -> Gui.drawModalRectWithCustomSizedTexture(i.getX(), i.getY(), i.getU(), 0, 10, 10, 256, 256));
			GL11.glPopMatrix();
		}
	}
}
