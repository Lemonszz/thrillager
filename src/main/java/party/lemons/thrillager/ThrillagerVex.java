package party.lemons.thrillager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ThrillagerVex extends VexEntity
{
	public ThrillagerVex(EntityType<? extends VexEntity> p_i50190_1_, World p_i50190_2_)
	{
		super(p_i50190_1_, p_i50190_2_);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(ticksExisted < 40)
		{
			if(!world.isRemote)
			{
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
	}
}
