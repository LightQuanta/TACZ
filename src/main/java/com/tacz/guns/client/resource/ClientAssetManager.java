package com.tacz.guns.client.resource;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.tacz.guns.client.animation.gltf.AnimationStructure;
import com.tacz.guns.client.model.BedrockAttachmentModel;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.client.resource.pojo.animation.bedrock.BedrockAnimationFile;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoDisplay;
import com.tacz.guns.client.resource.pojo.display.attachment.AttachmentDisplay;
import com.tacz.guns.client.resource.pojo.display.gun.GunDisplay;
import com.tacz.guns.client.resource.pojo.model.BedrockModelPOJO;
import com.tacz.guns.client.resource.pojo.model.BedrockVersion;
import com.tacz.guns.client.resource.pojo.skin.attachment.AttachmentSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 缓存 Map 的键统一为 ResourceLocation，其 namespace 为枪包的根目录的下一级文件夹的名称， path 为资源对应的 id 。
 */
@OnlyIn(Dist.CLIENT)
public enum ClientAssetManager {
    INSTANCE;
    /**
     * 枪包信息
     */
    private final Map<String, PackInfo> customInfos = Maps.newHashMap();
    /**
     * 储存 display 数据
     */
    private final Map<ResourceLocation, GunDisplay> gunDisplays = Maps.newHashMap();
    private final Map<ResourceLocation, AmmoDisplay> ammoDisplays = Maps.newHashMap();
    private final Map<ResourceLocation, AttachmentDisplay> attachmentDisplays = Maps.newHashMap();
    /**
     * 储存 skin数据
     */
    private final Map<ResourceLocation, Map<ResourceLocation, AttachmentSkin>> attachmentSkins = Maps.newHashMap();
    /**
     * 储存 GLTF 动画
     */
    private final Map<ResourceLocation, AnimationStructure> gltfAnimations = Maps.newHashMap();
    /**
     * 储存 基岩版动画
     */
    private final Map<ResourceLocation, BedrockAnimationFile> bedrockAnimations = Maps.newHashMap();
    /**
     * 储存模型
     */
    private final Map<ResourceLocation, BedrockModelPOJO> models = Maps.newHashMap();
    /**
     * 储存声音
     */
    private final Map<ResourceLocation, SoundBuffer> soundBuffers = Maps.newHashMap();
    /**
     * 存储语言
     */
    private final Map<String, Map<String, String>> languages = Maps.newHashMap();

    private final Map<ResourceLocation, BedrockAttachmentModel> tempAttachmentModelMap = Maps.newHashMap();

    private final Map<ResourceLocation, BedrockGunModel> tempGunModelMap = Maps.newHashMap();

    @Nullable
    private static BedrockAttachmentModel getAttachmentModel(BedrockModelPOJO modelPOJO) {
        BedrockAttachmentModel attachmentModel = null;
        // 先判断是不是 1.10.0 版本基岩版模型文件
        if (BedrockVersion.isLegacyVersion(modelPOJO) && modelPOJO.getGeometryModelLegacy() != null) {
            attachmentModel = new BedrockAttachmentModel(modelPOJO, BedrockVersion.LEGACY);
        }
        // 判定是不是 1.12.0 版本基岩版模型文件
        if (BedrockVersion.isNewVersion(modelPOJO) && modelPOJO.getGeometryModelNew() != null) {
            attachmentModel = new BedrockAttachmentModel(modelPOJO, BedrockVersion.NEW);
        }
        return attachmentModel;
    }

    public void putPackInfo(String namespace, PackInfo info) {
        customInfos.put(namespace, info);
    }

    public void putGunDisplay(ResourceLocation registryName, GunDisplay display) {
        gunDisplays.put(registryName, display);
    }

    public void putAmmoDisplay(ResourceLocation registryName, AmmoDisplay display) {
        ammoDisplays.put(registryName, display);
    }

    public void putAttachmentDisplay(ResourceLocation registryName, AttachmentDisplay display) {
        attachmentDisplays.put(registryName, display);
    }

    public void putAttachmentSkin(ResourceLocation registryName, AttachmentSkin skin) {
        attachmentSkins.compute(skin.getParent(), (name, map) -> {
            if (map == null) {
                map = Maps.newHashMap();
            }
            map.put(registryName, skin);
            return map;
        });
    }

    public void putGltfAnimation(ResourceLocation registryName, AnimationStructure animation) {
        gltfAnimations.put(registryName, animation);
    }

    public void putBedrockAnimation(ResourceLocation registryName, BedrockAnimationFile bedrockAnimationFile) {
        bedrockAnimations.put(registryName, bedrockAnimationFile);
    }

    public void putModel(ResourceLocation registryName, BedrockModelPOJO model) {
        models.put(registryName, model);
    }

    public void putSoundBuffer(ResourceLocation registryName, SoundBuffer soundBuffer) {
        soundBuffers.put(registryName, soundBuffer);
    }

    public void putLanguage(String region, Map<String, String> lang) {
        Map<String, String> languageMaps = languages.getOrDefault(region, Maps.newHashMap());
        languageMaps.putAll(lang);
        languages.put(region, languageMaps);
    }

    public GunDisplay getGunDisplay(ResourceLocation registryName) {
        return gunDisplays.get(registryName);
    }

    public AmmoDisplay getAmmoDisplay(ResourceLocation registryName) {
        return ammoDisplays.get(registryName);
    }

    @Nullable
    public AttachmentDisplay getAttachmentDisplay(ResourceLocation registryName) {
        return attachmentDisplays.get(registryName);
    }

    public Map<ResourceLocation, AttachmentSkin> getAttachmentSkins(ResourceLocation registryName) {
        return attachmentSkins.get(registryName);
    }

    public AnimationStructure getGltfAnimations(ResourceLocation registryName) {
        return gltfAnimations.get(registryName);
    }

    public BedrockAnimationFile getBedrockAnimations(ResourceLocation registryName) {
        return bedrockAnimations.get(registryName);
    }

    public BedrockModelPOJO getModels(ResourceLocation registryName) {
        return models.get(registryName);
    }

    public SoundBuffer getSoundBuffers(ResourceLocation registryName) {
        return soundBuffers.get(registryName);
    }

    public Map<String, String> getLanguages(String region) {
        return languages.get(region);
    }

    @Nullable
    public PackInfo getPackInfo(ResourceLocation id) {
        return customInfos.get(id.getNamespace());
    }

    /**
     * @return 如果模型缓存中没有对应模型、模型 POJO 缓存也没有对应的 POJO，则返回 null。
     */
    @Nullable
    public BedrockAttachmentModel getOrLoadAttachmentModel(@Nullable ResourceLocation modelLocation) {
        if (modelLocation == null) {
            return null;
        }
        BedrockAttachmentModel model = tempAttachmentModelMap.get(modelLocation);
        if (model != null) {
            return model;
        }
        BedrockModelPOJO modelPOJO = getModels(modelLocation);
        if (modelPOJO == null) {
            return null;
        }
        BedrockAttachmentModel attachmentModel = getAttachmentModel(modelPOJO);
        if (attachmentModel == null) {
            return null;
        }
        tempAttachmentModelMap.put(modelLocation, attachmentModel);
        return attachmentModel;
    }

    /**
     * 清除所有缓存
     */
    public void clearAll() {
        this.customInfos.clear();
        this.gunDisplays.clear();
        this.ammoDisplays.clear();
        this.attachmentDisplays.clear();
        this.attachmentSkins.clear();
        this.gltfAnimations.clear();
        this.bedrockAnimations.clear();
        this.models.clear();
        this.soundBuffers.clear();
        this.languages.clear();
        this.tempGunModelMap.clear();
        this.tempAttachmentModelMap.clear();
    }
}
