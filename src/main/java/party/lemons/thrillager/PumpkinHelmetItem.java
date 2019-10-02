package party.lemons.thrillager;

import com.google.common.collect.Multimap;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PumpkinHelmetItem extends Item
{
	public static final IArmorMaterial PUMPKIN_MATERIAL = new Material();

	public PumpkinHelmetItem(Item.Properties properties)
	{
		super(properties);
		DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
	{
		return armorType == EquipmentSlotType.HEAD;
	}

	public int getItemEnchantability() {
		return this.PUMPKIN_MATERIAL.getEnchantability();
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return this.PUMPKIN_MATERIAL.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
		ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);
		if (itemstack1.isEmpty()) {
			playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
			itemstack.setCount(0);
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, itemstack);
		}
	}

	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlotType.HEAD) {
			multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), "Armor modifier", PUMPKIN_MATERIAL.getDamageReductionAmount(EquipmentSlotType.HEAD), AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), "Armor toughness", PUMPKIN_MATERIAL.getToughness(), AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Nullable
	@Override
	public EquipmentSlotType getEquipmentSlot(ItemStack stack)
	{
		return EquipmentSlotType.HEAD;
	}

	public static class Material implements IArmorMaterial
	{
		@Override
		public int getDurability(EquipmentSlotType slotIn)
		{
			return 529;
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn)
		{
			return 6;
		}

		@Override
		public int getEnchantability()
		{
			return 25;
		}

		@Override
		public SoundEvent getSoundEvent()
		{
			return ThrillagerReference.LAUGH;
		}

		@Override
		public Ingredient getRepairMaterial()
		{
			return Ingredient.fromStacks(new ItemStack(Blocks.PUMPKIN));
		}

		@Override
		public String getName()
		{
			return "pumpkin";
		}

		@Override
		public float getToughness()
		{
			return 2;
		}
	}

	public static ItemStack dispenseArmor(IBlockSource blockSource, ItemStack stack) {
		BlockPos blockpos = blockSource.getBlockPos().offset(blockSource.getBlockState().get(DispenserBlock.FACING));
		List<LivingEntity> list = blockSource.getWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(blockpos), EntityPredicates.NOT_SPECTATING.and(new EntityPredicates.ArmoredMob(stack)));
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			LivingEntity livingentity = list.get(0);
			EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(stack);
			ItemStack itemstack = stack.split(1);
			livingentity.setItemStackToSlot(equipmentslottype, itemstack);
			if (livingentity instanceof MobEntity) {
				((MobEntity)livingentity).setDropChance(equipmentslottype, 2.0F);
				((MobEntity)livingentity).enablePersistence();
			}

			return stack;
		}
	}

	public static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			ItemStack itemstack = dispenseArmor(source, stack);
			return itemstack.isEmpty() ? super.dispenseStack(source, stack) : itemstack;
		}
	};
}
