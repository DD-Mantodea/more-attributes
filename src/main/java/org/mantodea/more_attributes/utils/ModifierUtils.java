package org.mantodea.more_attributes.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.mantodea.more_attributes.configs.MoreAttributesConfig;
import org.mantodea.more_attributes.datas.*;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;

public class ModifierUtils {

    public static class DetailModifiers {

        public static class Equip {
            public static Map<String, UUID> AdditionModifiers = new HashMap<>();

            public static Map<String, UUID> BaseMultiplyModifiers = new HashMap<>();

            public static Map<String, UUID> TotalMultiplyModifiers = new HashMap<>();

            public static Map<String, Float> AdditionValues = new HashMap<>();

            public static Map<String, Float> BaseMultiplyValues = new HashMap<>();

            public static Map<String, Float> TotalMultiplyValues = new HashMap<>();

            private static boolean modifierAdded = false;

            public static void initialize() {
                for (var attr : AttributeUtils.AllAttributes.keySet()) {
                    AdditionModifiers.put(attr, UUID.randomUUID());

                    BaseMultiplyModifiers.put(attr, UUID.randomUUID());

                    TotalMultiplyModifiers.put(attr, UUID.randomUUID());

                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }

            public static void rebuildModifiers(Player player) {
                if(modifierAdded)
                    removeModifiers(player);

                resetValues();

                if(ModUtils.checkModLoaded("curios")) {
                    var optional = CuriosApi.getCuriosInventory(player).resolve();

                    var curios = optional.isPresent() ? optional.get().getCurios().entrySet() : new ArrayList<Map.Entry<String, ICurioStacksHandler>>();

                    for (var curio : curios) {
                        String slotName = curio.getKey();

                        ICurioStacksHandler handler = curio.getValue();

                        int slotCount = handler.getSlots();

                        for(int slot = 0; slot < slotCount; slot++) {
                            ItemStack stack = handler.getStacks().getStackInSlot(slot);

                            updateItemModifiers(stack, slotName);
                        }
                    }
                }

                for (var slot : EquipmentSlot.values()) {
                    ItemStack stack = player.getItemBySlot(slot);

                    updateItemModifiers(stack, slot.getName());
                }

                addModifiers(player);
            }

            private static void updateItemModifiers(ItemStack stack, String slotName) {
                ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());

                ItemModifierData data = ModifierUtils.getItemModifierData(location);

                if(data == null || !ModUtils.checkCondition(data.displayCondition))
                    return;

                for (var modifier : data.modifiers) {
                    String modifierSlot = modifier.slot;

                    if(!modifierSlot.equals(slotName)) continue;

                    DetailData detailData = AttributeUtils.getDetailData(modifier.attribute.split(":")[1], modifier.attribute.split(":")[0]);

                    if(detailData == null || !ModUtils.checkCondition(detailData.displayCondition)) continue;

                    String type = modifier.type;

                    String attributeName = detailData.mod.equals("more_attributes") ? detailData.name : detailData.mod + ":" + detailData.name;

                    switch (type) {
                        case "base":
                            addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE, modifier.value);
                            break;
                        case "total":
                            addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, modifier.value);
                            break;
                        case "addition":
                            addToValue(attributeName, AttributeModifier.Operation.ADDITION, modifier.value);
                            break;
                    }
                }
            }

            private static void addToValue(String attributeName, AttributeModifier.Operation operation, float value) {
                switch (operation) {
                    case ADDITION:
                        AdditionValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_BASE:
                        BaseMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_TOTAL:
                        TotalMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                }
            }

            private static float getValue(String attributeName, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionValues.get(attributeName);
                    case MULTIPLY_BASE -> BaseMultiplyValues.get(attributeName);
                    case MULTIPLY_TOTAL -> TotalMultiplyValues.get(attributeName);
                };
            }

            private static void removeModifiers(Player player) {
                for (var attr : AttributeUtils.MyModAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeModifier(player, attributeName, AttributeModifier.Operation.ADDITION);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL);
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            }

            private static void removeModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void removeCustomModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void addModifiers(Player player) {

                modifierAdded = true;

                for (var attr : AttributeUtils.MyModAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addModifier(player, attributeName, AttributeModifier.Operation.ADDITION, getValue(attributeName, AttributeModifier.Operation.ADDITION));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION, getValue(attr.getKey(), AttributeModifier.Operation.ADDITION));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }

            private static void addModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static void addCustomModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static AttributeModifier createModifier(String attribute, AttributeModifier.Operation operation, float val) {
                UUID uuid = getUUID(attribute, operation);

                return new AttributeModifier(uuid, attribute, val, operation);
            }

            private static UUID getUUID(String attribute, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionModifiers.get(attribute);
                    case MULTIPLY_BASE -> BaseMultiplyModifiers.get(attribute);
                    case MULTIPLY_TOTAL -> TotalMultiplyModifiers.get(attribute);
                };
            }

            private static void resetValues() {
                for (var attr : AttributeUtils.AllAttributes.keySet()) {
                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }
        }

        public static class Level {
            public static Map<String, UUID> AdditionModifiers = new HashMap<>();

            public static Map<String, UUID> BaseMultiplyModifiers = new HashMap<>();

            public static Map<String, UUID> TotalMultiplyModifiers = new HashMap<>();

            public static Map<String, Float> AdditionValues = new HashMap<>();

            public static Map<String, Float> BaseMultiplyValues = new HashMap<>();

            public static Map<String, Float> TotalMultiplyValues = new HashMap<>();

            private static boolean modifierAdded = false;

            public static void initialize() {

                for (var attr : AttributeUtils.AllDetailAttributes.keySet()) {

                    AdditionModifiers.put(attr, UUID.randomUUID());

                    BaseMultiplyModifiers.put(attr, UUID.randomUUID());

                    TotalMultiplyModifiers.put(attr, UUID.randomUUID());

                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }

            public static void rebuildModifiers(Player player) {
                if(modifierAdded)
                    removeModifiers(player);

                resetValues();

                var cap = player.getCapability(MoreAttributes.CLASS_CAPABILITY).resolve().orElse(null);

                if (cap == null) {
                    return;
                }

                for(var attr : cap.getAttributes().entrySet()) {
                    String name = attr.getKey();

                    AttributeData data = AttributeUtils.getAttributeData(name);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    int level = attr.getValue() - data.baseLevel;

                    for (var modifier : data.modifiers)
                    {
                        String detailName = modifier.attribute;

                        DetailData detailData = AttributeUtils.getDetailData(modifier.mod, detailName);

                        if(detailData == null || !ModUtils.checkCondition(detailData.displayCondition)) continue;

                        String type = modifier.type;

                        String attributeName = detailData.mod.equals("more_attributes") ? detailData.name : detailData.mod + ":" + detailData.name;

                        switch (type)
                        {
                            case "base":
                                addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE, level * modifier.value);
                                break;
                            case "total":
                                addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, level * modifier.value);
                                break;
                            case "addition":
                                addToValue(attributeName, AttributeModifier.Operation.ADDITION, level * modifier.value);
                                break;
                        }
                    }
                }

                addModifiers(player);
            }

            private static void addToValue(String attributeName, AttributeModifier.Operation operation, float value) {
                switch (operation) {
                    case ADDITION:
                        AdditionValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_BASE:
                        BaseMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_TOTAL:
                        TotalMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                }
            }

            private static float getValue(String attributeName, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionValues.get(attributeName);
                    case MULTIPLY_BASE -> BaseMultiplyValues.get(attributeName);
                    case MULTIPLY_TOTAL -> TotalMultiplyValues.get(attributeName);
                };
            }

            private static void removeModifiers(Player player) {
                for (var attr : AttributeUtils.MyDetailAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeModifier(player, attributeName, AttributeModifier.Operation.ADDITION);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL);
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            }

            private static void removeModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void removeCustomModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void addModifiers(Player player) {

                modifierAdded = true;

                for (var attr : AttributeUtils.MyDetailAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addModifier(player, attributeName, AttributeModifier.Operation.ADDITION, getValue(attributeName, AttributeModifier.Operation.ADDITION));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION, getValue(attr.getKey(), AttributeModifier.Operation.ADDITION));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }

            private static void addModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static void addCustomModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static AttributeModifier createModifier(String attribute, AttributeModifier.Operation operation, float val) {
                UUID uuid = getUUID(attribute, operation);

                return new AttributeModifier(uuid, attribute, val, operation);
            }

            private static UUID getUUID(String attribute, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionModifiers.get(attribute);
                    case MULTIPLY_BASE -> BaseMultiplyModifiers.get(attribute);
                    case MULTIPLY_TOTAL -> TotalMultiplyModifiers.get(attribute);
                };
            }

            private static void resetValues() {
                for (var attr : AttributeUtils.AllDetailAttributes.keySet()) {
                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }
        }

        public static class Hands {
            public static Map<String, UUID> AdditionModifiers = new HashMap<>();

            public static Map<String, UUID> BaseMultiplyModifiers = new HashMap<>();

            public static Map<String, UUID> TotalMultiplyModifiers = new HashMap<>();

            public static Map<String, Float> AdditionValues = new HashMap<>();

            public static Map<String, Float> BaseMultiplyValues = new HashMap<>();

            public static Map<String, Float> TotalMultiplyValues = new HashMap<>();

            private static boolean modifierAdded = false;

            public static void initialize() {
                for (var attr : AttributeUtils.AllAttributes.keySet()) {

                    AdditionModifiers.put(attr, UUID.randomUUID());

                    BaseMultiplyModifiers.put(attr, UUID.randomUUID());

                    TotalMultiplyModifiers.put(attr, UUID.randomUUID());

                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }

            public static void rebuildModifiers(Player player) {
                if(modifierAdded)
                    removeModifiers(player);

                resetValues();

                ItemStack mainHand = player.getMainHandItem();

                ItemStack offHand = player.getOffhandItem();

                updateItemModifiers(mainHand, "minecraft:mainhand");

                updateItemModifiers(offHand, "minecraft:offhand");

                addModifiers(player);
            }

            private static void addToValue(String attributeName, AttributeModifier.Operation operation, float value) {
                switch (operation) {
                    case ADDITION:
                        AdditionValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_BASE:
                        BaseMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                    case MULTIPLY_TOTAL:
                        TotalMultiplyValues.compute(attributeName, (k, val) -> val + value);
                        break;
                }
            }

            private static float getValue(String attributeName, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionValues.get(attributeName);
                    case MULTIPLY_BASE -> BaseMultiplyValues.get(attributeName);
                    case MULTIPLY_TOTAL -> TotalMultiplyValues.get(attributeName);
                };
            }

            private static void removeModifiers(Player player) {
                for (var attr : AttributeUtils.MyDetailAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeModifier(player, attributeName, AttributeModifier.Operation.ADDITION);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE);

                    removeModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL);
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE);

                    removeCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            }

            private static void removeModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void removeCustomModifier(Player player, String attribute, AttributeModifier.Operation operation) {
                UUID uuid = getUUID(attribute, operation);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).removeModifier(uuid);
            }

            private static void addModifiers(Player player) {

                modifierAdded = true;

                for (var attr : AttributeUtils.MyDetailAttributes.entrySet()) {
                    String attributeName = attr.getKey();

                    DetailData data = AttributeUtils.getDetailData("more_attributes", attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addModifier(player, attributeName, AttributeModifier.Operation.ADDITION, getValue(attributeName, AttributeModifier.Operation.ADDITION));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_BASE, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE));

                    addModifier(player, attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                for (var attr : AttributeUtils.CustomDetailAttributes.entrySet()) {
                    String[] attributeData = attr.getKey().split(":");

                    String attributeName = attributeData[1];

                    String attributeMod = attributeData[0];

                    DetailData data = AttributeUtils.getDetailData(attributeMod, attributeName);

                    if(data == null || !ModUtils.checkCondition(data.displayCondition)) continue;

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.ADDITION, getValue(attr.getKey(), AttributeModifier.Operation.ADDITION));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_BASE));

                    addCustomModifier(player, attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL, getValue(attr.getKey(), AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }

            private static void addModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.MyModAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static void addCustomModifier(Player player, String attribute, AttributeModifier.Operation operation, float val) {
                AttributeModifier modifier = createModifier(attribute, operation, val);

                Objects.requireNonNull(player.getAttribute(AttributeUtils.CustomDetailAttributes.get(attribute))).addTransientModifier(modifier);
            }

            private static AttributeModifier createModifier(String attribute, AttributeModifier.Operation operation, float val) {
                UUID uuid = getUUID(attribute, operation);

                return new AttributeModifier(uuid, attribute, val, operation);
            }

            private static UUID getUUID(String attribute, AttributeModifier.Operation operation) {
                return switch (operation) {
                    case ADDITION -> AdditionModifiers.get(attribute);
                    case MULTIPLY_BASE -> BaseMultiplyModifiers.get(attribute);
                    case MULTIPLY_TOTAL -> TotalMultiplyModifiers.get(attribute);
                };
            }

            private static void resetValues() {
                for (var attr : AttributeUtils.AllAttributes.keySet()) {
                    AdditionValues.put(attr, 0f);

                    BaseMultiplyValues.put(attr, 0f);

                    TotalMultiplyValues.put(attr, 0f);
                }
            }

            private static void updateItemModifiers(ItemStack stack, String slotName) {
                ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());

                ItemModifierData data = ModifierUtils.getItemModifierData(location);

                if(data == null || !ModUtils.checkCondition(data.displayCondition))
                    return;

                for (var modifier : data.modifiers) {
                    String modifierSlot = modifier.slot;

                    if(!modifierSlot.equals(slotName)) continue;

                    DetailData detailData = AttributeUtils.getDetailData(modifier.mod, modifier.attribute);

                    if(detailData == null || !ModUtils.checkCondition(detailData.displayCondition)) continue;

                    String type = modifier.type;

                    String attributeName = detailData.mod.equals("more_attributes") ? detailData.name : detailData.mod + ":" + detailData.name;

                    switch (type) {
                        case "base":
                            addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_BASE, modifier.value);
                            break;
                        case "total":
                            addToValue(attributeName, AttributeModifier.Operation.MULTIPLY_TOTAL, modifier.value);
                            break;
                        case "addition":
                            addToValue(attributeName, AttributeModifier.Operation.ADDITION, modifier.value);
                            break;
                    }
                }
            }
        }

        public static class EquipLoad {
            public static UUID speedModifier;

            public static boolean modifierAdded;

            public static void initialize() {
                speedModifier = UUID.randomUUID();

                modifierAdded = false;
            }

            public static void rebuildModifier(Player player) {

                AttributeInstance instance = player.getAttribute(Attributes.MOVEMENT_SPEED);

                if(instance == null) return;

                if (modifierAdded) {
                    instance.removeModifier(speedModifier);
                }

                calculateLoad(player);

                double speedMultiplier = 1f;

                double currentLoad = Objects.requireNonNull(player.getAttribute(DetailAttributes.EquipLoadCurrent)).getValue();

                double maxLoad = Objects.requireNonNull(player.getAttribute(DetailAttributes.EquipLoadMax)).getValue();

                double lightLoad = maxLoad * MoreAttributesConfig.Common.Instance.lightPercent.get();

                double normalLoad = maxLoad * MoreAttributesConfig.Common.Instance.normalPercent.get();

                double heavyLoad = maxLoad * MoreAttributesConfig.Common.Instance.heavyPercent.get();

                double overweightLoad = maxLoad * MoreAttributesConfig.Common.Instance.overweightPercent.get();

                if (currentLoad < lightLoad) {
                    speedMultiplier = 1f;
                }

                if (currentLoad >= lightLoad && currentLoad < normalLoad) {
                    speedMultiplier = 0.9f;
                }

                if (currentLoad >= normalLoad && currentLoad < heavyLoad) {
                    speedMultiplier = 0.8f;
                }

                if (currentLoad >= heavyLoad && currentLoad < overweightLoad) {
                    speedMultiplier = 0.7f;
                }

                if(heavyLoad >= overweightLoad) {
                    speedMultiplier = 0.5f;
                }

                instance.addTransientModifier(new AttributeModifier(speedModifier, "speed", speedMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));

                modifierAdded = true;
            }

            public static void calculateLoad(Player player) {
                if (ModUtils.checkModLoaded("tfc")) {
                    var currentLoad = player.getAttribute(DetailAttributes.EquipLoadCurrent);

                    if (currentLoad != null) {
                        currentLoad.setBaseValue(0);

                        var load = 0;

                        for (var itemStack : player.getInventory().items) {
                            load += ItemUtils.getWeight(itemStack);
                        }

                        for (var itemStack : player.getInventory().armor) {
                            load += ItemUtils.getWeight(itemStack);
                        }

                        for (var itemStack : player.getInventory().offhand) {
                            load += ItemUtils.getWeight(itemStack);
                        }

                        if (ModUtils.checkModLoaded("curios")) {
                            var optional = CuriosApi.getCuriosInventory(player).resolve();

                            var curios = optional.isPresent() ? optional.get().getCurios().entrySet() : new ArrayList<Map.Entry<String, ICurioStacksHandler>>();

                            for (var curio : curios) {
                                var handler = curio.getValue();

                                for (var i = 0; i < handler.getSlots(); i++) {
                                    var itemStack = handler.getStacks().getStackInSlot(i);

                                    load += ItemUtils.getWeight(itemStack);
                                }
                            }
                        }

                        currentLoad.setBaseValue(load);
                    }
                }
            }
        }

        public static void initialize() {
            Equip.initialize();

            Level.initialize();

            Hands.initialize();

            EquipLoad.initialize();
        }
    }

    public static ItemModifierData getItemModifierData(ResourceLocation item) {
        return ItemModifierLoader.Modifiers.stream().filter(d -> d.item.equals(item.toString())).findFirst().orElse(null);
    }

    public static List<Component> getComponentsForItem(List<ModifierData> modifiers) {

        modifiers = new ArrayList<>(modifiers);

        List<Component> components = new ArrayList<>();

        modifiers.sort(Comparator.comparing(c -> c.slot));

        String currentSlot = "";

        for (var modifier : modifiers)
        {
            if(modifier == null || !ModUtils.checkCondition(modifier.displayCondition()))
                continue;

            String name = modifier.attribute;

            ConditionalContent data = AttributeUtils.AllDetailAttributes.containsKey(name) ?
                AttributeUtils.getDetailData(modifier.mod, name) :
                AttributeUtils.getAttributeData(name);

            if(data == null || !ModUtils.checkCondition(data.displayCondition()) || !SlotUtils.Slots.contains(modifier.slot))
                continue;

            var location = modifier.slot.split(":");

            String slotMod = location[0];

            String slotName = location[1];

            if(!currentSlot.equals(slotName))
            {
                String key = (slotMod.equals("curios") && ModUtils.checkModLoaded("curios")) ?
                    LangUtils.getCuriosSlotKey(slotName) :
                    LangUtils.getVanillaSlotKey(slotName);

                components.add(Component.translatable(key).withStyle(Style.EMPTY.withColor(0x69E6FF)));

                currentSlot = slotName;
            }

            float val = modifier.value;

            addComponentToList(components, modifier, name, val);
        }

        return components;
    }

    public static List<Component> getComponentsForLevel(List<ModifierData> modifiers, int level) {

        List<Component> components = new ArrayList<>();

        for (var modifier : modifiers)
        {
            String name = modifier.attribute;

            DetailData detail = AttributeUtils.getDetailData(modifier.mod, name);

            if(detail == null || !ModUtils.checkCondition(detail.displayCondition))
                continue;

            float val = modifier.value * level;

            addComponentToList(components, modifier, name, val);
        }

        return components;
    }

    private static void addComponentToList(List<Component> components, ModifierData modifier, String name, float val) {

        String type = modifier.type;

        String mod = modifier.mod;

        DetailData detail = AttributeUtils.getDetailData(mod, name);

        if(type.equals("total") || type.equals("base") || (!name.contains("equip_load")) && detail.displayAsPercentage)
            val *= 100;

        String text = (val >= 0 ? "+" : "") + val;

        if(type.equals("total") || type.equals("base") || (!name.contains("equip_load")) && detail.displayAsPercentage)
            text += "%";

        components.add(Component.translatable(LangUtils.getDetailNameKey(modifier.mod, name, type), text));
    }
}
