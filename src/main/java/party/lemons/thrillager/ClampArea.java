package party.lemons.thrillager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class ClampArea extends Entity
{
	private int time = 30;

	public ClampArea(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}

	public ClampArea(FMLPlayMessages.SpawnEntity packet, World worldIn)
	{
		super(ThrillagerReference.CLAMP_AREA, worldIn);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(world.isRemote)
		{
			float radius = 6;
			float amt = (float)Math.PI * radius * radius;

			for(int i = 0; (float)i < amt; ++i)
			{
				float age = this.rand.nextFloat() * ((float)Math.PI * 2F);
				float factor = MathHelper.sqrt(this.rand.nextFloat()) * radius;
				float xx = MathHelper.cos(age) * factor;
				float yy = MathHelper.sin(age) * factor;
				world.addParticle(ParticleTypes.SQUID_INK, this.posX + (double)xx, this.posY, this.posZ + (double)yy, (0.5D - this.rand.nextDouble()) * 0.15D, (double)0.01F, (0.5D - this.rand.nextDouble()) * 0.15D);
			}
		}

		time--;
		if(time < 0)
		{
			remove();

			float radius = 6;
			float amt = (float)Math.PI * radius * radius;
			for(int i = 0; (float)i < amt; ++i)
			{
				float age = this.rand.nextFloat() * ((float)Math.PI * 2F);
				float factor = MathHelper.sqrt(this.rand.nextFloat()) * radius;
				float xx = MathHelper.cos(age) * factor;
				float yy = MathHelper.sin(age) * factor;

				EvokerFangsEntity evokerFangsEntity = new EvokerFangsEntity(EntityType.EVOKER_FANGS, world);
				evokerFangsEntity.setPosition(posX + xx, posY, posZ + yy);
				world.addEntity(evokerFangsEntity);
			}
		}
	}

	@Override
	protected void registerData()
	{

	}

	@Override
	protected void readAdditional(CompoundNBT compound)
	{

	}

	@Override
	protected void writeAdditional(CompoundNBT compound)
	{

	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
