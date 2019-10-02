package party.lemons.thrillager;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ThrillagerEvents
{
	@SubscribeEvent
	public static void onPlayerDamage(LivingHurtEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity)
		{
			ItemStack head = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD);
			if(!head.isEmpty() && head.getItem() == ThrillagerReference.PUMPKIN_HELMET)
			{
				float amt = event.getAmount();
				amt -= event.getEntityLiving().getRNG().nextInt(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, head));

				amt /= 5;
				head.damageItem((int)Math.ceil(amt), (PlayerEntity)event.getEntityLiving(), playerEntity ->{
					playerEntity.world.playSound(null, playerEntity.getPosition(), ThrillagerReference.LAUGH, SoundCategory.PLAYERS, 1F, 1F);
				});
			}
		}
	}
}
