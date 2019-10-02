package party.lemons.thrillager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class ThrillagerBat extends MobEntity
{
	private BlockPos centerPoint = null;
	private double angle = 0;

	protected ThrillagerBat(EntityType<ThrillagerBat> type, World worldIn)
	{
		super(type, worldIn);

		onConstruct();
	}

	public ThrillagerBat(FMLPlayMessages.SpawnEntity packet, World worldIn)
	{
		super(ThrillagerReference.THRILLAGER_BAT, worldIn);
		onConstruct();
	}

	public ThrillagerBat(World world, BlockPos centerPos)
	{
		super(ThrillagerReference.THRILLAGER_BAT, world);
		this.centerPoint = centerPos;
		onConstruct();
	}

	private void onConstruct()
	{
		noClip = true;
		if(centerPoint == null)
		{
			centerPoint = new BlockPos(0, 100, 0);
		}

		angle = world.rand.nextInt(360);
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}


	@Override
	public void tick()
	{
		super.tick();
		if(centerPoint == null || world.isRemote)
			return;

		angle += 0.15D;

		posX = Math.cos(angle) * 4D + (double)centerPoint.getX();
		posZ = Math.sin(angle) * 4D + (double)centerPoint.getZ();
		posY = centerPoint.getY() + (Math.sin(angle * 2) / 2);

		setMotion(getMotion().x, 0, getMotion().z);

		if(!world.isRemote)
		{
			if(ticksExisted > 500)
				remove();

			ServerWorld sworld = (ServerWorld) world;
			for(int i = 0; i < 4; i++)
			{
				double oX = -0.5 + rand.nextFloat();
				double oY = -0.5 + rand.nextFloat();
				double oZ = -0.5 + rand.nextFloat();

				int par = rand.nextInt(4);
				BasicParticleType p;
				switch(par)
				{
					case 0:
					default:
						p = ParticleTypes.CRIT;
						break;
					case 1:
						p = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
						break;
					case 2:
						p = ParticleTypes.CAMPFIRE_COSY_SMOKE;
						break;
					case 3:
						p = ParticleTypes.SMOKE;
						break;
				}

				sworld.spawnParticle(p, posX + oX, posY + oY, posZ + oZ, 1, 0, (-0.5F + rand.nextFloat()) / 5, 0, 0.025F);
			}
		}
	}

	@Override
	public void readAdditional(CompoundNBT compound)
	{
		centerPoint = NBTUtil.readBlockPos(compound.getCompound("center"));

		super.readAdditional(compound);
	}

	@Override
	public void writeAdditional(CompoundNBT compound)
	{
		compound.put("center", NBTUtil.writeBlockPos(centerPoint));

		super.writeAdditional(compound);
	}
}
