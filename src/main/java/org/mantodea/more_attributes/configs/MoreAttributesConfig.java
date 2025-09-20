package org.mantodea.more_attributes.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class MoreAttributesConfig {

    public static class Common {
        public ForgeConfigSpec.BooleanValue enableEquipLoad;

        public Common(ForgeConfigSpec.Builder builder) {

            builder.push("common");

            enableEquipLoad = builder.comment("是否计算负重 / enable equips' load attribute or not").define("enable_equip_load", true);

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
