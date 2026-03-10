package com.soradotwav.waylight.config;

import com.soradotwav.WaylightClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.ChatFormatting;
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
					.group(OptionGroup.createBuilder()
						.name(Component.translatable("waylight.config.group.lantern"))
						.collapsed(false)
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
						.option(Option.<LanternSide>createBuilder()
							.name(Component.translatable("waylight.config.option.lantern_side"))
							.description(OptionDescription.of(Component.translatable("waylight.config.option.lantern_side.desc")))
							.binding(
								LanternSide.fromConfig(config.lanternSide),
								() -> LanternSide.fromConfig(WaylightClient.CONFIG_MANAGER.get().lanternSide),
								value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.lanternSide = value.configValue)
							)
							.controller(option -> EnumControllerBuilder.create(option).enumClass(LanternSide.class))
							.build())
						.option(Option.<PoseVariant>createBuilder()
							.name(Component.translatable("waylight.config.option.carry_mode"))
							.description(OptionDescription.of(Component.translatable("waylight.config.option.carry_mode.desc")))
							.binding(
								PoseVariant.fromConfig(config.poseMode),
								() -> PoseVariant.fromConfig(WaylightClient.CONFIG_MANAGER.get().poseMode),
								value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.poseMode = value.configValue)
							)
							.controller(option -> EnumControllerBuilder.create(option).enumClass(PoseVariant.class))
							.build())
						.build())
					.group(OptionGroup.createBuilder()
						.name(Component.translatable("waylight.config.group.behavior"))
						.collapsed(false)
						.option(Option.<Boolean>createBuilder()
							.name(Component.translatable("waylight.config.option.first_person_light"))
							.description(OptionDescription.of(Component.translatable("waylight.config.option.first_person_light.desc")))
							.binding(
								config.firstPersonLight,
								() -> WaylightClient.CONFIG_MANAGER.get().firstPersonLight,
								value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.firstPersonLight = value)
							)
							.controller(TickBoxControllerBuilder::create)
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
						.option(Option.<Integer>createBuilder()
							.name(Component.translatable("waylight.config.option.motion_intensity"))
							.description(OptionDescription.of(Component.translatable("waylight.config.option.motion_intensity.desc")))
							.binding(
								config.motionIntensity,
								() -> WaylightClient.CONFIG_MANAGER.get().motionIntensity,
								value -> WaylightClient.CONFIG_MANAGER.update(cfg -> cfg.motionIntensity = value)
							)
							.controller(option -> IntegerSliderControllerBuilder.create(option)
								.range(25, 200)
								.step(5)
								.formatValue(value -> Component.literal(value + "%").withStyle(value == 100 ? ChatFormatting.GREEN : ChatFormatting.WHITE)))
							.build())
						.build())
					.group(OptionGroup.createBuilder()
						.name(Component.translatable("waylight.config.group.debug"))
						.collapsed(true)
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

	private enum LanternSide {
		RIGHT("right", Component.translatable("waylight.config.value.side.right")),
		LEFT("left", Component.translatable("waylight.config.value.side.left"));

		private final String configValue;
		private final Component label;

		LanternSide(String configValue, Component label) {
			this.configValue = configValue;
			this.label = label;
		}

		public static LanternSide fromConfig(String value) {
			return "left".equals(value) ? LEFT : RIGHT;
		}

		@Override
		public String toString() {
			return label.getString();
		}
	}

	private enum PoseVariant {
		HIP("hip", Component.translatable("waylight.config.value.pose.hip")),
		HAND_LEFT("hand_left", Component.translatable("waylight.config.value.pose.hand_left"));

		private final String configValue;
		private final Component label;

		PoseVariant(String configValue, Component label) {
			this.configValue = configValue;
			this.label = label;
		}

		public static PoseVariant fromConfig(String value) {
			return "hand_left".equals(value) ? HAND_LEFT : HIP;
		}

		@Override
		public String toString() {
			return label.getString();
		}
	}
}
