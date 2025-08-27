package org.mantodea.more_attributes.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class MoreAttributesConfig {

    public static class Common {
        public ForgeConfigSpec.BooleanValue enableEquipLoad;

        public ForgeConfigSpec.DoubleValue lightPercent;

        public ForgeConfigSpec.DoubleValue normalPercent;

        public ForgeConfigSpec.DoubleValue heavyPercent;

        public ForgeConfigSpec.DoubleValue overweightPercent;

        public Common(ForgeConfigSpec.Builder builder) {

            builder.push("common");

            enableEquipLoad = builder.comment("是否计算负重 / enable equips' load attribute or not").define("enable_equip_load", true);

            lightPercent = builder.comment("负重上限百分比: 轻 / Max Load Percent: Light").defineInRange("light_percent", 0.4, 0, 1);

            normalPercent = builder.comment("负重上限百分比: 正常 / Max Load Percent: Normal").defineInRange("normal_percent", 0.6, 0, 1);

            heavyPercent = builder.comment("负重上限百分比: 重 / Max Load Percent: Heavy").defineInRange("heavy_percent", 0.8, 0, 1);

            overweightPercent = builder.comment("负重上限百分比: 超重 / Max Load Percent: Overweight").defineInRange("overweight_percent", 1d, 0, 1);

            builder.pop();
        }

        public static ForgeConfigSpec CommonSpec;

        public static Common Instance;

        static {
            var common = new ForgeConfigSpec.Builder().configure(Common::new);

            CommonSpec = common.getRight();

            Instance = common.getLeft();
        }
    }
}
