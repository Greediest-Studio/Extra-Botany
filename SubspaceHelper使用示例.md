# SubspaceHelper 使用示例

## 类简介

`SubspaceHelper` 是一个用于处理亚空之矛相关功能的辅助类，位于 `com.meteor.extrabotany.common.helper` 包中。

## 主要功能

该类提供了召唤和发射亚空之矛的静态方法，支持自定义起始位置、目标位置和伤害值。

## 方法签名

### 方法1：指定伤害值
```java
public static void summonSubspace(
    World world,              // 世界对象
    EntityLivingBase thrower, // 发射者（可为null）
    float fromX,              // 起始X坐标
    float fromY,              // 起始Y坐标
    float fromZ,              // 起始Z坐标
    float toX,                // 目标X坐标
    float toY,                // 目标Y坐标
    float toZ,                // 目标Z坐标
    float damage              // 伤害值
)
```

### 方法2：使用默认伤害值（13.0f）
```java
public static void summonSubspace(
    World world,              // 世界对象
    EntityLivingBase thrower, // 发射者（可为null）
    float fromX,              // 起始X坐标
    float fromY,              // 起始Y坐标
    float fromZ,              // 起始Z坐标
    float toX,                // 目标X坐标
    float toY,                // 目标Y坐标
    float toZ                 // 目标Z坐标
)
```

### 方法3：从玩家位置发射（完全模拟原版）✨ 推荐
```java
public static void summonSubspaceFromPlayer(
    World world,              // 世界对象
    EntityPlayer player,      // 玩家实体
    float damage              // 伤害值
)
```

### 方法4：从玩家位置发射，使用默认伤害值（13.0f）✨ 推荐
```java
public static void summonSubspaceFromPlayer(
    World world,              // 世界对象
    EntityPlayer player       // 玩家实体
)
```

## 使用示例

### 示例1：最简单的用法 - 从玩家发射（推荐）✨
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

// 在某个事件处理或方法中
EntityPlayer player = ...; // 获取玩家实体
World world = player.world;

// 最简单的方式：完全模拟原版玩家发射
// 矛会从玩家上方2.5格（带随机偏移）沿玩家视线方向飞行
SubspaceHelper.summonSubspaceFromPlayer(world, player);

// 或者自定义伤害值
SubspaceHelper.summonSubspaceFromPlayer(world, player, 20.0F);
```

### 示例2：玩家位置向前方发射亚空之矛
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

// 在某个事件处理或方法中
EntityPlayer player = ...; // 获取玩家实体
World world = player.world;

// 获取玩家当前位置
float fromX = (float) player.posX;
float fromY = (float) player.posY + 1.5F; // 稍微提高起始高度
float fromZ = (float) player.posZ;

// 计算玩家前方10格的位置作为目标
Vec3d lookVec = player.getLookVec();
float toX = fromX + (float) lookVec.x * 10;
float toY = fromY + (float) lookVec.y * 10;
float toZ = fromZ + (float) lookVec.z * 10;

// 发射亚空之矛（使用默认伤害13.0f）
SubspaceHelper.summonSubspace(world, player, fromX, fromY, fromZ, toX, toY, toZ);
```

### 示例3：从指定位置向目标实体发射
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

EntityLivingBase attacker = ...; // 攻击者
EntityLivingBase target = ...; // 目标实体
World world = attacker.world;

// 起始位置（攻击者上方）
float fromX = (float) attacker.posX;
float fromY = (float) attacker.posY + 2.0F;
float fromZ = (float) attacker.posZ;

// 目标位置（目标实体中心）
float toX = (float) target.posX;
float toY = (float) target.posY + target.height / 2;
float toZ = (float) target.posZ;

// 发射亚空之矛，自定义伤害为20.0f
SubspaceHelper.summonSubspace(world, attacker, fromX, fromY, fromZ, toX, toY, toZ, 20.0F);
```

### 示例4：无发射者的亚空之矛（环境发射）
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

World world = ...; // 获取世界对象

// 从坐标(100, 70, 100)发射到坐标(150, 75, 150)
SubspaceHelper.summonSubspace(
    world, 
    null,      // 无发射者
    100F, 70F, 100F,  // 起始坐标
    150F, 75F, 150F,  // 目标坐标
    15.0F      // 伤害值
);
```

### 示例5：在Boss战斗中使用
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

public class BossEntity extends EntityLiving {
    
    public void attackPattern() {
        World world = this.world;
        List<EntityPlayer> players = world.getPlayers(EntityPlayer.class, p -> !p.isDead);
        
        if (!players.isEmpty()) {
            EntityPlayer target = players.get(rand.nextInt(players.size()));
            
            // Boss上方发射亚空之矛
            float fromX = (float) this.posX;
            float fromY = (float) this.posY + 3.0F;
            float fromZ = (float) this.posZ;
            
            float toX = (float) target.posX;
            float toY = (float) target.posY;
            float toZ = (float) target.posZ;
            
            // 发射强力亚空之矛
            SubspaceHelper.summonSubspace(world, this, fromX, fromY, fromZ, toX, toY, toZ, 25.0F);
        }
    }
}
```

### 示例6：在命令或技能中使用（右键技能释放）✨
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

@SubscribeEvent
public void onRightClick(PlayerInteractEvent.RightClickItem event) {
    EntityPlayer player = event.getEntityPlayer();
    World world = player.world;
    ItemStack stack = event.getItemStack();
    
    // 假设某个特定物品右键释放亚空之矛
    if (stack.getItem() == YourSpecialItem) {
        // 直接从玩家视角发射，与原版完全一致
        SubspaceHelper.summonSubspaceFromPlayer(world, player);
        
        // 添加冷却时间
        player.getCooldownTracker().setCooldown(YourSpecialItem, 100);
    }
}
```

### 示例7：连发多个亚空之矛（弹幕效果）
```java
import com.meteor.extrabotany.common.helper.SubspaceHelper;

public void spearBarrage(EntityPlayer player) {
    World world = player.world;
    
    // 在玩家前方生成5个亚空之矛，形成扇形弹幕
    for (int i = 0; i < 5; i++) {
        // 计算扩散角度（±30度范围）
        float angleOffset = -30F + (i * 15F);
        
        // 获取玩家视角方向
        float baseYaw = player.rotationYaw + angleOffset;
        float basePitch = player.rotationPitch;
        
        // 创建一个临时的"虚拟玩家"视角来计算方向
        // 注意：这里需要手动计算方向向量
        double yawRad = Math.toRadians(baseYaw);
        double pitchRad = Math.toRadians(basePitch);
        
        Vec3d direction = new Vec3d(
            -Math.sin(yawRad) * Math.cos(pitchRad),
            -Math.sin(pitchRad),
            Math.cos(yawRad) * Math.cos(pitchRad)
        );
        
        float fromX = (float) player.posX;
        float fromY = (float) player.posY + 2.0F;
        float fromZ = (float) player.posZ;
        
        float distance = 20F;
        float toX = fromX + (float) direction.x * distance;
        float toY = fromY + (float) direction.y * distance;
        float toZ = fromZ + (float) direction.z * distance;
        
        // 发射亚空之矛
        SubspaceHelper.summonSubspace(world, player, fromX, fromY, fromZ, toX, toY, toZ, 10.0F);
    }
}
```

## 技术细节

### summonSubspace 方法
- **速度**：亚空之矛的飞行速度固定为 2.45F，与原版玩家发射的速度相同
- **生命周期**：矛的存在时间为 100 ticks（5秒）
- **默认伤害**：如果不指定伤害值，默认为 13.0F
- **服务端检测**：矛只会在服务端生成（`!world.isRemote`），避免客户端重复生成

### summonSubspaceFromPlayer 方法 ✨
这是新增的便捷方法，完全模拟原版玩家使用亚空之矛的发射行为：

- **起始位置**：玩家位置上方 2.5 格 + 随机偏移（0-0.2格），然后减去 0.75 格
- **方向计算**：使用 `shoot(player, pitch, yaw, 0.0F, 2.45F, 1.0F)` 方法，自动根据玩家视角计算飞行方向
- **旋转设置**：
  - `rotationYaw = player.rotationYaw`
  - `pitch = -player.rotationPitch`
  - `rotation = MathHelper.wrapDegrees(-player.rotationYaw + 180)`
- **速度**：与原版相同，2.45F
- **推荐使用场景**：
  - 自定义物品/技能释放
  - 命令执行
  - 任何需要从玩家视角发射亚空之矛的场景

## 两种方法的区别

### summonSubspace（坐标方式）
- 需要指定起始和目标坐标
- 适合精确控制飞行路径
- 适合从任意位置向任意位置发射
- 适合 Boss 攻击模式、陷阱机关等

### summonSubspaceFromPlayer（玩家方式）✨
- 只需要传入玩家对象
- 完全模拟原版发射行为
- 更简洁，代码量更少
- 自动处理位置、方向、旋转
- 适合玩家技能、物品使用等

## 注意事项

1. 确保在服务端调用此方法，因为实体生成会自动检测 `world.isRemote`
2. `thrower` 参数可以为 `null`，这样矛将作为环境伤害源
3. 起始坐标和目标坐标的距离建议不要太远，以保证视觉效果
4. 伤害值会影响矛击中目标时造成的实际伤害
5. `summonSubspaceFromPlayer` 方法包含随机偏移，每次发射位置会略有不同（0-0.2格）

## 相关类

- `EntitySubspaceSpear`: 亚空之矛实体类
- `ItemSpearSubspace`: 亚空之矛物品类
- `EntitySubspace`: 亚空间效果实体类
- `EntityThrowableCopy`: 投掷物基类
