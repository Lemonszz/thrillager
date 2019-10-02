package party.lemons.thrillager;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import java.util.List;

public class ThrillagerEntity extends MonsterEntity
{
	private static final DataParameter<Integer> SPELL_STATE = EntityDataManager.createKey(ThrillagerEntity.class, DataSerializers.VARINT);
	private int stateTime = 0;
	private List<Updateable> updateables = Lists.newArrayList();

	public ThrillagerEntity(EntityType<ThrillagerEntity> entityType, World world)
	{
		super(entityType, world);
		onConstruct();
	}

	public ThrillagerEntity(FMLPlayMessages.SpawnEntity packet, World worldIn)
	{
		super(ThrillagerReference.THRILLAGER, worldIn);
		onConstruct();
	}

	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0D);
		this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15);
	}

	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
		this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
		this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
		this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
	}

	private void onConstruct()
	{
		this.noClip = true;
		setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
	}

	@Override
	public void tick()
	{
		super.tick();
		updateables.forEach(u->u.update(world));
		updateables.stream().filter(Updateable::isRemoved).forEach(u->u.onEnd(world));
		updateables.removeIf(Updateable::isRemoved);

		PlayerEntity e = world.getClosestPlayer(posX, posY, posZ);
		if(e != null)
			getLookController().func_220679_a(e.posX, e.posY + (double)e.getEyeHeight(), e.posZ);

		if(world.isRemote)
		{

			return;
		}
		stateTime--;

		List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox().grow(3D));

		if(!players.isEmpty())
		{
			setState(State.PUSHING_PLAYER, 30);
			players.forEach(p->
			{
				if(!p.isCreative() && !p.isSpectator())
				{
					double xx = this.posX - p.posX;
					double yy;

					for(yy = this.posZ - p.posZ; xx * xx + yy * yy < 1.0E-4D; yy = (Math.random() - Math.random()) * 0.01D)
					{
						xx = (Math.random() - Math.random()) * 0.01D;
					}

					playSpellSound();

					p.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 60, 0));
					p.knockBack(this, 0.6F, xx, yy);
					p.velocityChanged = true;
				}
			});
		}
		else if(stateTime <= 0)
		{
			if(getState() == State.SUMMONING_BATS)
			{
				setState(State.PUSHING_BATS, 120);
			}
			else if(getAttackTarget() != null)
			{
				switch(rand.nextInt(3))
				{
					case 0:
						setState(State.SUMMONING_BATS, 150);
						break;
					case 1:
						setState(State.CLAMPERS, 150);
						break;
					case 2:
						setState(State.LIGHTNING, 150);
						break;
				}
			}
			else
			{
				setState(State.IDLE, 5);
			}
		}
		else
		{
			switch(getState())
			{
				case IDLE:
					break;
				case SUMMONING_BATS:
					if(stateTime % 30 == 0)
					{
						playSummonSound();
						ThrillagerBat bat = new ThrillagerBat(world, getPosition().add(0, rand.nextFloat() * 3, 0));
						bat.setPosition(posX, posY, posZ);
						world.addEntity(bat);
					}
					break;
				case PUSHING_BATS:
					List<ThrillagerBat> bats = world.getEntitiesWithinAABB(ThrillagerBat.class, getBoundingBox().grow(10D));

					bats.forEach(b->{
						b.remove();
						ThrillagerVex vexEntity = new ThrillagerVex(EntityType.VEX, world);
						vexEntity.setLimitedLife(300);
						vexEntity.setPosition(b.posX, b.posY, b.posZ);
						world.addEntity(vexEntity);

						double xx = this.posX - vexEntity.posX;
						double yy;

						for(yy = this.posZ - vexEntity.posZ; xx * xx + yy * yy < 1.0E-4D; yy = (Math.random() - Math.random()) * 0.01D)
						{
							xx = (Math.random() - Math.random()) * 0.01D;
						}

						playSpellSound();

						vexEntity.knockBack(this, 0.7F, xx, yy);
						vexEntity.velocityChanged = true;
					});
					break;
				case LIGHTNING:
					if(stateTime % 30 == 0)
					{
						List<PlayerEntity> pl = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox().grow(24), EntityPredicates.CAN_AI_TARGET);
						if(pl.isEmpty())
							setState(State.IDLE, 1);
						else
						{
							pl.forEach(p->{
								playSpellSound(p.getPosition());
								updateables.add(new LightingPos(p.getPosition()));
							});
						}

					}
					break;
				case PUSHING_PLAYER:
					break;
			}
		}

		setMotion(getMotion().x, 0, getMotion().z);
	}

	public void setState(State state, int time)
	{
		onStateExit(getState());
		dataManager.set(SPELL_STATE, state.ordinal());
		stateTime = time;

		onEnterState(state);
	}

	public void onStateExit(State state)
	{

	}

	public void onEnterState(State state)
	{
		setItemStackToSlot(EquipmentSlotType.HEAD, state.headStack);
		if(state == State.CLAMPERS)
		{
			if(world.isRemote)
				return;

			List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, getBoundingBox().grow(15), EntityPredicates.CAN_AI_TARGET);

			for(int i = 0 ; i < players.size(); i++)
			{
				PlayerEntity p = players.get(i);
				ClampArea area = new ClampArea(ThrillagerReference.CLAMP_AREA, world);
				area.setPosition(p.posX, p.posY, p.posZ);
				world.addEntity(area);

				playAttackSound(p.getPosition());
			}
		}
	}

	protected void registerData()
	{
		super.registerData();
		this.dataManager.register(SPELL_STATE, 0);
	}

	public int getVerticalFaceSpeed() {
		return 100;
	}

	public int getHorizontalFaceSpeed() {
		return 100;
	}

	public int func_213396_dB() {
		return 100;
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio)
	{
		//NOFU
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected void playSpellSound()
	{
		playSpellSound(getPosition());
	}

	protected void playSpellSound(BlockPos pos)
	{
		world.playSound(null, pos, ThrillagerReference.CAST_SPELL, SoundCategory.HOSTILE, 0.8F, 1F);
	}

	protected void playAttackSound(BlockPos pos)
	{
		world.playSound(null, pos, ThrillagerReference.ATTACK, SoundCategory.HOSTILE, 0.8F, 1F);
	}

	protected void playSummonSound()
	{
		world.playSound(null, getPosition(), ThrillagerReference.SUMMON, SoundCategory.HOSTILE, 0.8F, 1F);
	}

	@Override
	public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
	{
		BlockState state = worldIn.getBlockState(getPosition().down());
		if(spawnReasonIn == SpawnReason.NATURAL && (posY < world.getSeaLevel() - 5 || !state.getBlock().isAir(state, world,getPosition().down())))
			return false;

		return super.canSpawn(worldIn, spawnReasonIn);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound()
	{
		return ThrillagerReference.LAUGH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_)
	{
		return ThrillagerReference.HURT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return ThrillagerReference.DEATH;
	}

	public ArmPose getArmPose()
	{
		return getState().pose;
	}

	public State getState()
	{
		return State.values()[dataManager.get(SPELL_STATE)];
	}

	private enum State
	{
		IDLE(ArmPose.CROSSED, new ItemStack(Blocks.PUMPKIN)),
		SUMMONING_BATS(ArmPose.SPELLCASTING, new ItemStack(Blocks.JACK_O_LANTERN)),
		PUSHING_PLAYER(ArmPose.CELEBRATING, new ItemStack(Blocks.CARVED_PUMPKIN)),
		PUSHING_BATS(ArmPose.ATTACKING, new ItemStack(Blocks.JACK_O_LANTERN)),
		CLAMPERS(ArmPose.BOW_AND_ARROW, new ItemStack(Blocks.CARVED_PUMPKIN)),
		LIGHTNING(ArmPose.BOW_AND_ARROW, new ItemStack(Blocks.JACK_O_LANTERN));

		public ArmPose pose;
		public ItemStack headStack;
		State(ArmPose pose, ItemStack headStack)
		{
			this.pose = pose;
			this.headStack = headStack;
		}
	}

	private interface Updateable
	{
		void update(World world);
		boolean isRemoved();
		void onEnd(World world);
	}

	private static class LightingPos implements Updateable
	{
		private BlockPos pos;
		private int timer = 35;

		public LightingPos(BlockPos pos)
		{
			this.pos = pos;
		}

		@Override
		public void update(World world)
		{
			if(world.isRemote)
				return;

			timer--;
			ServerWorld sWorld = (ServerWorld) world;
			for(int i = 0; i < 5; i++)
			{
				float xPos = world.rand.nextFloat();
				float yPos = world.rand.nextFloat();
				float zPos = world.rand.nextFloat();

				sWorld.spawnParticle(ParticleTypes.END_ROD, pos.getX() + xPos, pos.getY() + yPos, pos.getZ() + zPos, 1, 0, world.rand.nextFloat() / 10F, 0, 0.025F);
			}
		}

		@Override
		public boolean isRemoved()
		{
			return timer <= 0;
		}

		@Override
		public void onEnd(World world)
		{
			if(world.isRemote)
				return;

			LightningBoltEntity lightningboltentity = new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), false);
			((ServerWorld)world).addLightningBolt(lightningboltentity);
		}
	}

	public enum ArmPose {
		CROSSED,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		CELEBRATING;
	}
}
