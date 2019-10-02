package party.lemons.thrillager;

import net.minecraft.util.ResourceLocation;

public class Reference
{
	public static final String MODID = "thrillager";

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MODID, path);
	}
}
