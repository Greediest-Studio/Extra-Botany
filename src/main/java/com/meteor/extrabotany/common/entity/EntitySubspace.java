package com.meteor.extrabotany.common.entity;

import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.common.entity.gaia.EntityVoidHerrscher;
import com.meteor.extrabotany.common.item.ModItems;
import com.meteor.extrabotany.common.item.relic.ItemExcaliber;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.botania.common.entity.EntityManaBurst;

public class EntitySubspace extends EntityThrowableCopy {

	private static final String TAG_LIVE_TICKS = "liveTicks";
	private static final String TAG_DELAY = "delay";
	private static final String TAG_ROTATION = "rotation";
	private static final String TAG_INTERVAL = "interval";
	private static final String TAG_SIZE = "size";
	private static final String TAG_COUNT = "count";
	private static final String TAG_TYPE = "type";
	private static final String TAG_EVIL = "isevil";
	private static final String TAG_DAMAGE = "damage";
	private static final String TAG_TARGET_X = "targetX";
	private static final String TAG_TARGET_Y = "targetY";
	private static final String TAG_TARGET_Z = "targetZ";
	private static final String TAG_HAS_TARGET = "hasTarget";

	private static final DataParameter<Integer> LIVE_TICKS = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> DELAY = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> ROTATION = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Integer> INTERVAL = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> SIZE = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Integer> COUNT = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.VARINT);
	private static final DataParameter<Boolean> EVIL = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> TARGET_X = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> TARGET_Y = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> TARGET_Z = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> HAS_TARGET = EntityDataManager.createKey(EntitySubspace.class,
			DataSerializers.BOOLEAN);

	public EntitySubspace(World world) {
		super(world);
	}

	public EntitySubspace(World world, EntityLivingBase thrower) {
		super(world, thrower);
	}

	@Override
	public void onUpdate() {

		motionX = 0;
		motionY = 0;
		motionZ = 0;

		super.onUpdate();

		if (ticksExisted < getDelay())
			return;

		if (ticksExisted > getLiveTicks() + getDelay())
			setDead();
		EntityLivingBase thrower = getThrower();
		if (!world.isRemote && (thrower == null || thrower.isDead)) {
			setDead();
			return;
		}

		if (!world.isRemote)
			if (getType() == 0) {
				if (ticksExisted % getInterval() == 0 && getCount() < 5 && ticksExisted > getDelay() + 5
						&& ticksExisted < getLiveTicks() - getDelay() - 10) {
					if (!(thrower instanceof EntityPlayer))
						setDead();
					EntityPlayer player = (EntityPlayer) getThrower();
					if (ExtraBotanyAPI.cantAttack(player, player)) {
						setDead();
					}
					EntityManaBurst burst = ItemExcaliber.getBurst(player, new ItemStack(ModItems.excaliber));
					burst.setPosition(posX, posY, posZ);
					burst.setColor(0XFFAF00);
					player.world.spawnEntity(burst);
					setCount(getCount() + 1);
				}
			} else if (getType() == 1) {
				if (ticksExisted > getDelay() + 8 && getCount() < 1) {
					EntitySubspaceSpear spear = new EntitySubspaceSpear(world, thrower);
					// 使用自定义伤害值，如果未设置则使用默认值
					float damage = getDamage() > 0 ? getDamage() : 12;
					if (thrower instanceof EntityVoidHerrscher) {
						damage = 14;
						spear.setLiveTicks(1);
					}
					spear.setDamage(damage);
					spear.setLife(100);
					
					// 检查是否有自定义目标坐标
					if (getHasTarget()) {
						// 使用自定义目标坐标
						double deltaX = getTargetX() - posX;
						double deltaY = getTargetY() - (posY - 0.75F);
						double deltaZ = getTargetZ() - posZ;
						double length = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
						
						if (length > 0) {
							double horizontalDistance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
							float pitch = (float) (MathHelper.atan2(deltaY, horizontalDistance) * (180D / Math.PI));
							float yaw = (float) (MathHelper.atan2(deltaX, deltaZ) * (180D / Math.PI));
							
							spear.rotationYaw = yaw;
							spear.setPitch(-pitch);
							spear.setRotation(MathHelper.wrapDegrees(-yaw + 180));
							spear.shoot(deltaX / length, deltaY / length, deltaZ / length, 2.45F, 1.0F);
						}
					} else {
						// 使用thrower的视角（原版行为）
						spear.rotationYaw = thrower.rotationYaw;
						spear.setPitch(-thrower.rotationPitch);
						spear.setRotation(MathHelper.wrapDegrees(-thrower.rotationYaw + 180));
						spear.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 2.45F, 1.0F);
					}
					
					spear.setPosition(posX, posY - 0.75F, posZ);
					thrower.world.spawnEntity(spear);
					setCount(getCount() + 1);
				}
			} else if (getType() == 2) {
				if (ticksExisted % getInterval() == 0 && getCount() < 6 && ticksExisted > getDelay() + 5
						&& ticksExisted < getLiveTicks() - getDelay() - 10) {
					if (!(thrower instanceof EntityVoidHerrscher))
						setDead();
					EntityVoidHerrscher herr = (EntityVoidHerrscher) getThrower();
					if (herr.getPlayersAround().isEmpty())
						setDead();
					if (ExtraBotanyAPI.cantAttack(thrower, herr.getPlayersAround().get(0))) {
						setDead();
					}
					EntityManaBurst burst = ItemExcaliber.getBurst(herr.getPlayersAround().get(0),
							new ItemStack(ModItems.excaliber));
					burst.setPosition(posX, posY, posZ);
					burst.setColor(0XFFD700);
					burst.shoot(thrower, thrower.rotationPitch + 15F, thrower.rotationYaw, 0F, 1F, 0F);
					thrower.world.spawnEntity(burst);
					setCount(getCount() + 1);
				}
			}
	}

	@Override
	protected void entityInit() {
		setSize(0F, 0F);
		dataManager.register(LIVE_TICKS, 0);
		dataManager.register(DELAY, 0);
		dataManager.register(ROTATION, 0F);
		dataManager.register(SIZE, 0F);
		dataManager.register(INTERVAL, 0);
		dataManager.register(COUNT, 0);
		dataManager.register(TYPE, 0);
		dataManager.register(DAMAGE, 0F);
		dataManager.register(TARGET_X, 0F);
		dataManager.register(TARGET_Y, 0F);
		dataManager.register(TARGET_Z, 0F);
		dataManager.register(HAS_TARGET, false);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound cmp) {
		setLiveTicks(cmp.getInteger(TAG_LIVE_TICKS));
		setDelay(cmp.getInteger(TAG_DELAY));
		setRotation(cmp.getFloat(TAG_ROTATION));
		setSize(cmp.getFloat(TAG_SIZE));
		setInterval(cmp.getInteger(TAG_INTERVAL));
		setCount(cmp.getInteger(TAG_COUNT));
		setType(cmp.getInteger(TAG_TYPE));
		setDamage(cmp.getFloat(TAG_DAMAGE));
		setTargetX(cmp.getFloat(TAG_TARGET_X));
		setTargetY(cmp.getFloat(TAG_TARGET_Y));
		setTargetZ(cmp.getFloat(TAG_TARGET_Z));
		setHasTarget(cmp.getBoolean(TAG_HAS_TARGET));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound cmp) {
		cmp.setInteger(TAG_LIVE_TICKS, getLiveTicks());
		cmp.setInteger(TAG_DELAY, getDelay());
		cmp.setFloat(TAG_ROTATION, getRotation());
		cmp.setFloat(TAG_SIZE, getSize());
		cmp.setInteger(TAG_INTERVAL, getInterval());
		cmp.setInteger(TAG_COUNT, getCount());
		cmp.setInteger(TAG_TYPE, getType());
		cmp.setFloat(TAG_DAMAGE, getDamage());
		cmp.setFloat(TAG_TARGET_X, (float) getTargetX());
		cmp.setFloat(TAG_TARGET_Y, (float) getTargetY());
		cmp.setFloat(TAG_TARGET_Z, (float) getTargetZ());
		cmp.setBoolean(TAG_HAS_TARGET, getHasTarget());
	}

	public int getLiveTicks() {
		return dataManager.get(LIVE_TICKS);
	}

	public void setLiveTicks(int ticks) {
		dataManager.set(LIVE_TICKS, ticks);
	}

	public int getDelay() {
		return dataManager.get(DELAY);
	}

	public void setDelay(int delay) {
		dataManager.set(DELAY, delay);
	}

	public int getCount() {
		return dataManager.get(COUNT);
	}

	public void setCount(int delay) {
		dataManager.set(COUNT, delay);
	}

	public int getType() {
		return dataManager.get(TYPE);
	}

	public void setType(int delay) {
		dataManager.set(TYPE, delay);
	}

	public int getInterval() {
		return dataManager.get(INTERVAL);
	}

	public void setInterval(int delay) {
		dataManager.set(INTERVAL, delay);
	}

	public float getRotation() {
		return dataManager.get(ROTATION);
	}

	public void setRotation(float rot) {
		dataManager.set(ROTATION, rot);
	}

	public float getSize() {
		return dataManager.get(SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SIZE, size);
	}

	public float getDamage() {
		return dataManager.get(DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(DAMAGE, damage);
	}

	public float getTargetX() {
		return dataManager.get(TARGET_X);
	}

	public void setTargetX(float x) {
		dataManager.set(TARGET_X, x);
	}

	public float getTargetY() {
		return dataManager.get(TARGET_Y);
	}

	public void setTargetY(float y) {
		dataManager.set(TARGET_Y, y);
	}

	public float getTargetZ() {
		return dataManager.get(TARGET_Z);
	}

	public void setTargetZ(float z) {
		dataManager.set(TARGET_Z, z);
	}

	public boolean getHasTarget() {
		return dataManager.get(HAS_TARGET);
	}

	public void setHasTarget(boolean hasTarget) {
		dataManager.set(HAS_TARGET, hasTarget);
	}

	public void setTarget(double x, double y, double z) {
		setTargetX((float) x);
		setTargetY((float) y);
		setTargetZ((float) z);
		setHasTarget(true);
	}

	@Override
	protected void onImpact(RayTraceResult result) {

	}

}
