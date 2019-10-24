package party.lemons.thrillager;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Reference.MODID)
@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThrillagerReference
{
	public static final SoundEvent CAST_SPELL = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
	public static final SoundEvent SUMMON = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
	public static final SoundEvent ATTACK = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
	public static final SoundEvent HURT = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
	public static final SoundEvent LAUGH = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
	public static final SoundEvent DEATH = SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;

	public static final EntityType THRILLAGER = EntityType.Builder.<ThrillagerEntity>create(ThrillagerEntity::new, EntityClassification.MONSTER)
			.size(0.6F, 1.95F)
			.immuneToFire()
			.setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(32)
			.setUpdateInterval(3)
			.setCustomClientFactory(ThrillagerEntity::new)
			.build("thrillager").setRegistryName(Reference.rl("thrillager"));

	public static final EntityType THRILLAGER_BAT = EntityType.Builder.<ThrillagerBat>create(ThrillagerBat::new, EntityClassification.MONSTER)
			.size(1F, 1F)
			.setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(32)
			.setUpdateInterval(3)
			.setCustomClientFactory(ThrillagerBat::new)
			.build("thrillager_bat").setRegistryName(Reference.rl("thrillager_bat"));

	public static final EntityType CLAMP_AREA = EntityType.Builder.<ClampArea>create(ClampArea::new, EntityClassification.MISC)
			.size(3F, 0.1F)
			.immuneToFire()
			.setCustomClientFactory(ClampArea::new)
			.build("clamp_area").setRegistryName(Reference.rl("clamp_area"));

	public static final Item PUMPKIN_HELMET = Items.AIR;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new SpawnEggItem(THRILLAGER, 0x2c2533, 0x9e7057, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(Reference.rl("thrillager_egg")),
				new PumpkinHelmetItem(new Item.Properties().maxDamage(600).group(ItemGroup.MISC)).setRegistryName(Reference.rl("pumpkin_helmet"))
		);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> e)
	{
		e.getRegistry().registerAll(
				new SoundEvent(Reference.rl("cast_spell")).setRegistryName(Reference.rl("cast_spell")),
				new SoundEvent(Reference.rl("summon")).setRegistryName(Reference.rl("summon")),
				new SoundEvent(Reference.rl("attack")).setRegistryName(Reference.rl("attack")),
				new SoundEvent(Reference.rl("hurt")).setRegistryName(Reference.rl("hurt")),
				new SoundEvent(Reference.rl("laugh")).setRegistryName(Reference.rl("laugh")),
				new SoundEvent(Reference.rl("death")).setRegistryName(Reference.rl("death"))
		);
	}

	@SubscribeEvent
	public static void registerEntityType(RegistryEvent.Register<EntityType<?>> e)
	{
		e.getRegistry().registerAll(
				THRILLAGER,
				THRILLAGER_BAT,
				CLAMP_AREA
		);
	}

	@SubscribeEvent
	public static void onClientInit(FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(ThrillagerEntity.class, ThrillagerRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClampArea.class, ClampAreaRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ThrillagerBat.class, ClampAreaRenderer::new);
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		ForgeRegistries.BIOMES.getEntries().forEach(b->{
			if(!hasAnyType(b.getValue(), BiomeDictionary.Type.END, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.OCEAN))
				b.getValue().getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(THRILLAGER, 1, 1, 1));
		});
	}

	private static boolean hasAnyType(Biome biome, BiomeDictionary.Type... types)
	{
		for(BiomeDictionary.Type type : types)
		{
			if(BiomeDictionary.hasType(biome, type))
				return true;
		}

		return false;
	}
}
