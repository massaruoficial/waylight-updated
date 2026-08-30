package com.soradotwav.mixin.client;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class WaylightMixinPlugin implements IMixinConfigPlugin {
    private boolean punchyPresent;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("punchy.client.render.PunchyArmRenderer", false, WaylightMixinPlugin.class.getClassLoader());
            this.punchyPresent = true;
        } catch (Throwable ignored) {
            this.punchyPresent = false;
        }
    }

    @Override public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (this.punchyPresent && mixinClassName.equals("com.soradotwav.mixin.client.ItemInHandRendererMixin")) {
            return false;
        }
        return true;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
