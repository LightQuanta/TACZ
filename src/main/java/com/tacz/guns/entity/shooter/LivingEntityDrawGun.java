package com.tacz.guns.entity.shooter;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public class LivingEntityDrawGun {
    private final ShooterDataHolder data;

    public LivingEntityDrawGun(ShooterDataHolder data) {
        this.data = data;
    }

    public void draw(Supplier<ItemStack> gunItemSupplier) {
        // 重置各个状态
        data.initialData();
        // 更新切枪时间戳
        if (data.drawTimestamp == -1) {
            data.drawTimestamp = System.currentTimeMillis();
        }
        long drawTime = System.currentTimeMillis() - data.drawTimestamp;
        if (drawTime >= 0) {
            // 如果不处于收枪状态，则需要计算收枪时长
            if (drawTime < data.currentPutAwayTimeS * 1000) {
                // 从开始切枪到现在，抬枪的时间小于收枪需要的时间，则按抬枪时间计算。
                data.drawTimestamp = System.currentTimeMillis() + drawTime;
            } else {
                // 从开始切枪到现在，抬枪的时间大于收枪需要的时间，则按收枪时间计算。
                data.drawTimestamp = System.currentTimeMillis() + (long) (data.currentPutAwayTimeS * 1000);
            }
        }
        data.currentGunItem = gunItemSupplier;
        updatePutAwayTime();
    }

    public long getDrawCoolDown() {
        if (data.currentGunItem == null) {
            return 0;
        }
        ItemStack currentGunItem = data.currentGunItem.get();
        if (!(currentGunItem.getItem() instanceof IGun iGun)) {
            return 0;
        }
        ResourceLocation gunId = iGun.getGunId(currentGunItem);
        Optional<CommonGunIndex> gunIndex = TimelessAPI.getCommonGunIndex(gunId);
        return gunIndex.map(index -> {
            long coolDown = (long) (index.getGunData().getDrawTime() * 1000) - (System.currentTimeMillis() - data.drawTimestamp);
            // 给 5 ms 的窗口时间，以平衡延迟
            coolDown = coolDown - 5;
            if (coolDown < 0) {
                return 0L;
            }
            return coolDown;
        }).orElse(-1L);
    }

    private void updatePutAwayTime() {
        ItemStack gunItem = data.currentGunItem == null ? ItemStack.EMPTY : data.currentGunItem.get();
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            Optional<CommonGunIndex> gunIndex = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunItem));
            data.currentPutAwayTimeS = gunIndex.map(index -> index.getGunData().getPutAwayTime()).orElse(0F);
        } else {
            data.currentPutAwayTimeS = 0;
        }
    }
}
