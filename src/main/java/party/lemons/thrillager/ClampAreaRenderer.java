package party.lemons.thrillager;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ClampAreaRenderer extends EntityRenderer<Entity>
{
	protected ClampAreaRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return null;
	}
}
