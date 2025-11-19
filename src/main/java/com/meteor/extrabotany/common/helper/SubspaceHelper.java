package com.meteor.extrabotany.common.helper;

import com.meteor.extrabotany.common.entity.EntitySubspace;
import com.meteor.extrabotany.common.entity.EntitySubspaceSpear;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * 用于处理亚空之矛相关的辅助类
 */
public class SubspaceHelper {

    /**
     * 召唤并发射亚空之矛
     * 
     * @param world 世界对象
     * @param thrower 发射者(可以为null，表示无发射者)
     * @param fromX 起始X坐标
     * @param fromY 起始Y坐标
     * @param fromZ 起始Z坐标
     * @param toX 目标X坐标
     * @param toY 目标Y坐标
     * @param toZ 目标Z坐标
     * @param damage 伤害值，默认为13.0f
     */
    public static void summonSubspace(World world, EntityLivingBase thrower, 
                                     float fromX, float fromY, float fromZ, 
                                     float toX, float toY, float toZ, 
                                     float damage) {
        if (world == null) {
            return;
        }
        
        // 创建亚空之矛实体
        EntitySubspaceSpear spear;
        if (thrower != null) {
            spear = new EntitySubspaceSpear(world, thrower);
        } else {
            spear = new EntitySubspaceSpear(world);
        }
        
        // 设置伤害
        spear.setDamage(damage);
        
        // 设置生命周期
        spear.setLife(100);
        
        // 设置初始位置
        spear.setPosition(fromX, fromY, fromZ);
        
        // 计算方向向量
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        double deltaZ = toZ - fromZ;
        
        // 计算俯仰角和偏航角
        double horizontalDistance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float pitch = (float) (MathHelper.atan2(deltaY, horizontalDistance) * (180D / Math.PI));
        float yaw = (float) (MathHelper.atan2(deltaX, deltaZ) * (180D / Math.PI));
        
        // 设置旋转
        spear.rotationYaw = yaw;
        spear.setPitch(-pitch);
        spear.setRotation(MathHelper.wrapDegrees(-yaw + 180));
        
        // 发射矛，速度为2.45f，与原版玩家发射相同
        // shoot方法参数: (x方向, y方向, z方向, 速度, 不准确度)
        double length = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (length > 0) {
            spear.shoot(deltaX / length, deltaY / length, deltaZ / length, 2.45F, 1.0F);
        }
        
        // 在世界中生成实体
        if (!world.isRemote) {
            world.spawnEntity(spear);
        }
    }
    
    /**
     * 召唤并发射亚空之矛（使用默认伤害值13.0f）
     * 
     * @param world 世界对象
     * @param thrower 发射者(可以为null，表示无发射者)
     * @param fromX 起始X坐标
     * @param fromY 起始Y坐标
     * @param fromZ 起始Z坐标
     * @param toX 目标X坐标
     * @param toY 目标Y坐标
     * @param toZ 目标Z坐标
     */
    public static void summonSubspace(World world, EntityLivingBase thrower,
                                     float fromX, float fromY, float fromZ, 
                                     float toX, float toY, float toZ) {
        summonSubspace(world, thrower, fromX, fromY, fromZ, toX, toY, toZ, 13.0F);
    }
    
    /**
     * 召唤并发射亚空之矛（带光环效果，从给定位置向目标位置发射）
     * 
     * @param world 世界对象
     * @param thrower 发射者(可以为null，表示无发射者)
     * @param fromX 起始X坐标
     * @param fromY 起始Y坐标
     * @param fromZ 起始Z坐标
     * @param toX 目标X坐标
     * @param toY 目标Y坐标
     * @param toZ 目标Z坐标
     * @param damage 伤害值
     */
    public static void summonSubspaceWithEffect(World world, EntityLivingBase thrower,
                                               float fromX, float fromY, float fromZ,
                                               float toX, float toY, float toZ,
                                               float damage) {
        if (world == null) {
            return;
        }
        
        // 计算方向向量用于设置光环的旋转
        double deltaX = toX - fromX;
        double deltaZ = toZ - fromZ;
        float yaw = (float) (MathHelper.atan2(deltaX, deltaZ) * (180D / Math.PI));
        
        // 创建亚空间光环实体
        EntitySubspace sub;
        if (thrower != null) {
            sub = new EntitySubspace(world, thrower);
        } else {
            sub = new EntitySubspace(world);
        }
        
        sub.setLiveTicks(24);
        sub.setDelay(5);
        sub.posX = fromX;
        sub.posY = fromY;
        sub.posZ = fromZ;
        sub.rotationYaw = yaw;
        sub.setRotation(MathHelper.wrapDegrees(-yaw + 180));
        sub.setType(1); // Type 1 表示会生成亚空之矛
        sub.setSize(0.40F + world.rand.nextFloat() * 0.15F);
        sub.setDamage(damage);
        
        // 设置目标坐标，让光环知道应该向哪里发射
        sub.setTarget(toX, toY, toZ);
        
        // 在世界中生成光环实体
        if (!world.isRemote) {
            world.spawnEntity(sub);
        }
    }
    
    /**
     * 召唤并发射亚空之矛（带光环效果，从给定位置向目标位置发射，使用默认伤害13.0f）
     * 
     * @param world 世界对象
     * @param thrower 发射者(可以为null，表示无发射者)
     * @param fromX 起始X坐标
     * @param fromY 起始Y坐标
     * @param fromZ 起始Z坐标
     * @param toX 目标X坐标
     * @param toY 目标Y坐标
     * @param toZ 目标Z坐标
     */
    public static void summonSubspaceWithEffect(World world, EntityLivingBase thrower,
                                               float fromX, float fromY, float fromZ,
                                               float toX, float toY, float toZ) {
        summonSubspaceWithEffect(world, thrower, fromX, fromY, fromZ, toX, toY, toZ, 13.0F);
    }
    
    /**
     * 从玩家位置召唤并发射亚空之矛，完全模拟原版发射场景
     * 亚空之矛会从玩家上方稍高处（+2.5格）发射，沿玩家视线方向飞行
     * 包含光环效果（EntitySubspace）
     * 
     * @param world 世界对象
     * @param player 玩家实体
     * @param damage 伤害值
     */
    public static void summonSubspaceFromPlayer(World world, EntityPlayer player, float damage) {
        summonSubspaceFromPlayerWithEffect(world, player, damage);
    }
    
    /**
     * 从玩家位置召唤并发射亚空之矛（使用默认伤害值12.0f）
     * 完全模拟原版效果，包含光环
     * 
     * @param world 世界对象
     * @param player 玩家实体
     */
    public static void summonSubspaceFromPlayer(World world, EntityPlayer player) {
        summonSubspaceFromPlayerWithEffect(world, player, 12.0F);
    }
    
    /**
     * 从玩家位置召唤并发射亚空之矛（带光环效果）
     * 完全模拟原版发射场景，包含光环效果
     * 
     * @param world 世界对象
     * @param player 玩家实体
     * @param damage 伤害值
     */
    public static void summonSubspaceFromPlayerWithEffect(World world, EntityPlayer player, float damage) {
        if (world == null || player == null) {
            return;
        }
        
        // 创建亚空间光环实体（与原版完全一致）
        EntitySubspace sub = new EntitySubspace(world, player);
        sub.setLiveTicks(24);
        sub.setDelay(5);
        sub.posX = player.posX;
        sub.posY = player.posY + 2.5F + world.rand.nextFloat() * 0.2F;
        sub.posZ = player.posZ;
        sub.rotationYaw = player.rotationYaw;
        sub.setRotation(MathHelper.wrapDegrees(-player.rotationYaw + 180));
        sub.setType(1); // Type 1 表示会生成亚空之矛
        sub.setSize(0.40F + world.rand.nextFloat() * 0.15F);
        sub.setDamage(damage); // 设置自定义伤害值
        
        // 在世界中生成光环实体
        // EntitySubspace会在延迟后自动生成EntitySubspaceSpear
        if (!world.isRemote) {
            world.spawnEntity(sub);
        }
    }
    
    /**
     * 从玩家位置召唤并发射亚空之矛（带光环效果，使用默认伤害值12.0f）
     * 完全模拟原版发射场景，包含光环效果
     * 
     * @param world 世界对象
     * @param player 玩家实体
     */
    public static void summonSubspaceFromPlayerWithEffect(World world, EntityPlayer player) {
        summonSubspaceFromPlayerWithEffect(world, player, 12.0F);
    }
    
    /**
     * 从玩家位置直接发射亚空之矛（无光环效果，但支持自定义伤害）
     * 
     * @param world 世界对象
     * @param player 玩家实体
     * @param damage 伤害值
     */
    public static void summonSubspaceFromPlayerDirect(World world, EntityPlayer player, float damage) {
        if (world == null || player == null) {
            return;
        }
        
        // 创建亚空之矛实体
        EntitySubspaceSpear spear = new EntitySubspaceSpear(world, player);
        
        // 设置伤害
        spear.setDamage(damage);
        
        // 设置生命周期
        spear.setLife(100);
        
        // 设置旋转角度（与原版一致）
        spear.rotationYaw = player.rotationYaw;
        spear.setPitch(-player.rotationPitch);
        spear.setRotation(MathHelper.wrapDegrees(-player.rotationYaw + 180));
        
        // 使用原版shoot方法，根据玩家视角发射
        spear.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.45F, 1.0F);
        
        // 设置位置：模拟光环延迟后的位置
        float offsetY = 2.5F + world.rand.nextFloat() * 0.2F - 0.75F;
        spear.setPosition(player.posX, player.posY + offsetY, player.posZ);
        
        // 在世界中生成实体
        if (!world.isRemote) {
            world.spawnEntity(spear);
        }
    }
    
    /**
     * 从玩家位置直接发射亚空之矛（无光环效果，使用默认伤害13.0f）
     * 
     * @param world 世界对象
     * @param player 玩家实体
     */
    public static void summonSubspaceFromPlayerDirect(World world, EntityPlayer player) {
        summonSubspaceFromPlayerDirect(world, player, 13.0F);
    }
}
