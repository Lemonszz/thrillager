package party.lemons.thrillager;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ThrillagerRenderer extends MobRenderer<ThrillagerEntity, ThrillagerModel>
{
	private static final ResourceLocation TEX = Reference.rl("textures/thrillager.png");

	public ThrillagerRenderer(EntityRendererManager renderManager)
	{
		super(renderManager, new ThrillagerModel(0.0F, 0.0F, 64, 64), 0.5F);
		this.addLayer(new HeadLayer<>(this));
	}

	@Override
	public void doRender(ThrillagerEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableNormalize();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.translated(0, 0.25F + (Math.sin(entity.ticksExisted / 10F) / 4F), 0);

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		GlStateManager.translated(0, -(0.25F + (Math.sin(entity.ticksExisted / 10F) / 4F)), 0);


		GlStateManager.disableBlend();
		GlStateManager.disableNormalize();
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(ThrillagerEntity entity)
	{
		return TEX;
	}
}
