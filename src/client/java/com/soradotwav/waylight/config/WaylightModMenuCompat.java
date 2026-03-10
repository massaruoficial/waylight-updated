package com.soradotwav.waylight.config;

import com.soradotwav.WaylightClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

public final class WaylightModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			WaylightConfig config = WaylightClient.CONFIG_MANAGER.get();

			return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("waylight.config.title"))
				.category(ConfigCategory.createBuilder()
					.name(Component.translatable("waylight.config.category.general"))
					.option(Option.<LanternVariant>createBuilder()
						.name(Component.translatable("waylight.config.option.lantern_type"))
						.description(OptionDescription.of(Component.translatable("waylight.config.option.lantern_type.desc")))
						.binding(
							LanternVariant.fromConfig(config.lanternType),
							() -> LanternVariant.fromConfig(WaylightClient.CONFIG_MANAGER.get().lanternType),
							value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.lanternType = value.configValue)
						)
						.controller(option -> EnumControllerBuilder.create(option).enumClass(LanternVariant.class))
						.build())
					.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("waylight.config.option.extinguish_underwater"))
						.description(OptionDescription.of(Component.translatable("waylight.config.option.extinguish_underwater.desc")))
						.binding(
							config.extinguishUnderwater,
							() -> WaylightClient.CONFIG_MANAGER.get().extinguishUnderwater,
							value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.extinguishUnderwater = value)
						)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.build())
				.category(ConfigCategory.createBuilder()
					.name(Component.translatable("waylight.config.category.debug"))
					.option(Option.<Boolean>createBuilder()
						.name(Component.translatable("waylight.config.option.debug_anchor_gizmo"))
						.description(OptionDescription.of(Component.translatable("waylight.config.option.debug_anchor_gizmo.desc")))
						.binding(
							config.debugAnchorGizmo,
							() -> WaylightClient.CONFIG_MANAGER.get().debugAnchorGizmo,
							value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.debugAnchorGizmo = value)
						)
						.controller(TickBoxControllerBuilder::create)
						.build())
					.build())
				.save(WaylightClient.CONFIG_MANAGER::save)
				.build()
				.generateScreen(parent);
		};
	}

	private enum LanternVariant {
		NORMAL("normal", Component.translatable("waylight.config.value.lantern.normal")),
		SOUL("soul", Component.translatable("waylight.config.value.lantern.soul"));

		private final String configValue;
		private final Component label;

		LanternVariant(String configValue, Component label) {
			this.configValue = configValue;
			this.label = label;
		}

		public static LanternVariant fromConfig(String value) {
			return "soul".equals(value) ? SOUL : NORMAL;
		}

		@Override
		public String toString() {
			return label.getString();
		}
	}
}
