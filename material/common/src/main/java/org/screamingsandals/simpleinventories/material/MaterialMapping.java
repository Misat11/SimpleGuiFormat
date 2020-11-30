package org.screamingsandals.simpleinventories.material;

import lombok.Getter;
import lombok.SneakyThrows;
import org.screamingsandals.simpleinventories.utils.OneWayTypeConverter;
import org.screamingsandals.simpleinventories.utils.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MaterialMapping {

    @Getter
    protected Platform platform;
    protected final Map<String, MaterialHolder> materialMapping = new HashMap<>();
    protected OneWayTypeConverter<MaterialHolder> materialHolderConverter;

    private static MaterialMapping mapping = null;
    private static final Pattern RESOLUTION_PATTERN = Pattern.compile("^(((?:(?<namespace>[A-Za-z][A-Za-z0-9_.\\-]*):)?(?<material>[A-Za-z][A-Za-z0-9_.\\-/ ]*)(?::)?(?<durability>\\d+)?)|((?<id>\\d+)(?::)?(?<data>\\d+)?))$");

    public static Optional<MaterialHolder> resolve(String material) {
        if (mapping == null) {
            throw new UnsupportedOperationException("Material mapping is not initialized yet.");
        }
        material = material.trim();

        Matcher matcher = RESOLUTION_PATTERN.matcher(material);

        if (matcher.group("material") != null) {

            /*String namespace = matcher.group("namespace");*/ // namespace is currently useless on vanilla

            String name = matcher.group("material").toUpperCase();
            String durability = matcher.group("durability");

            if (durability != null && !durability.isEmpty()) {
                if (mapping.materialMapping.containsKey(name.toUpperCase() + ":" + durability)) {
                    MaterialHolder holder = mapping.materialMapping.get(name.toUpperCase() + ":" + durability);
                    return Optional.of(holder.newDurability(Short.parseShort(durability)));
                } else if (mapping.materialMapping.containsKey(name.toUpperCase())) {
                    MaterialHolder holder = mapping.materialMapping.get(name.toUpperCase());
                    return Optional.of(holder.newDurability(Short.parseShort(durability)));
                }
            } else if (mapping.materialMapping.containsKey(name)) {
                return Optional.of(mapping.materialMapping.get(name));
            }
        } else if (matcher.group("id") != null) {
            String id = matcher.group("id").toUpperCase();
            String data = matcher.group("data");

            if (data != null && !data.isEmpty()) {
                if (mapping.materialMapping.containsKey(id + ":" + data)) {
                    return Optional.of(mapping.materialMapping.get(id + ":" + data));
                } else if (mapping.materialMapping.containsKey(id)) {
                    MaterialHolder holder = mapping.materialMapping.get(id);
                    return Optional.of(holder.newDurability(Short.parseShort(data)));
                }
            } else if (mapping.materialMapping.containsKey(id)) {
                return Optional.of(mapping.materialMapping.get(id));
            }
        }
        return Optional.empty();
    }

    public static <T> T convertMaterialHolder(MaterialHolder holder, Class<T> newType) {
        if (mapping == null) {
            throw new UnsupportedOperationException("Material mapping is not initialized yet.");
        }
        return mapping.materialHolderConverter.convert(holder, newType);
    }

    @SneakyThrows
    public static void init(Class<? extends MaterialMapping> materialMapping) {
        if (mapping != null) {
            throw new UnsupportedOperationException("Material mapping is already initialized.");
        }

        MaterialMapping mapping = materialMapping.getConstructor().newInstance();
        mapping.applyBaseMapping();
    }

    private void applyBaseMapping() {
        // Legacy remapping
        f2l("AIR", 0);

        f2l("STONE", 1);
        f2l("GRANITE", "STONE", 1, (byte) 1);
        f2l("POLISHED_GRANITE", "STONE", 1, (byte) 2);
        f2l("DIORITE", "STONE", 1, (byte) 3);
        f2l("POLISHED_DIORITE", "STONE", 1, (byte) 4);
        f2l("ANDESITE", "STONE", 1, (byte) 5);
        f2l("POLISHED_ANDESITE", "STONE", 1, (byte) 6);

        f2l("GRASS_BLOCK", "GRASS", 2); // collision

        f2l("DIRT", 3);
        f2l("COARSE_DIRT", "DIRT", 3, (byte) 1);
        f2l("PODZOL", "DIRT", 3, (byte) 2);

        f2l("COBBLESTONE", 4);

        f2l("OAK_PLANKS", "WOOD", 5, "PLANKS");
        f2l("SPRUCE_PLANKS", "WOOD", 5, (byte) 1, "PLANKS");
        f2l("BIRCH_PLANKS", "WOOD", 5, (byte) 2, "PLANKS");
        f2l("JUNGLE_PLANKS", "WOOD", 5, (byte) 3, "PLANKS");
        f2l("ACACIA_PLANKS", "WOOD", 5, (byte) 4, "PLANKS");
        f2l("DARK_OAK_PLANKS", "WOOD", 5, (byte) 5, "PLANKS");

        f2l("OAK_SAPLING", "SAPLING", 6);
        f2l("SPRUCE_SAPLING", "SAPLING", 6, (byte) 1);
        f2l("BIRCH_SAPLING", "SAPLING", 6, (byte) 2);
        f2l("JUNGLE_SAPLING", "SAPLING", 6, (byte) 3);
        f2l("ACACIA_SAPLING", "SAPLING", 6, (byte) 4);
        f2l("DARK_OAK_SAPLING", "SAPLING", 6, (byte) 5);

        f2l("BEDROCK", 7);

        // flowing_water and water can't be properly translated. same thing is for lava
        //f2l("WATER", 9); - only block (blocks will be supported later), bukkit mapping collision
        //f2l("LAVA", 11); - only block (blocks will be supported later), bukkit mapping collision

        f2l("SAND", 12);
        f2l("RED_SAND", "SAND", 12, (byte) 1);

        f2l("GRAVEL", 13);
        f2l("GOLD_ORE", 14);
        f2l("IRON_ORE", 15);
        f2l("COAL_ORE", 16);

        f2l("OAK_LOG", "LOG", 17, (byte) 0);
        f2l("SPRUCE_LOG", "LOG", 17, (byte) 1);
        f2l("BIRCH_LOG", "LOG", 17, (byte) 2);
        f2l("JUNGLE_LOG", "LOG", 17, (byte) 3);

        // is this correct?
        f2l("OAK_WOOD", "LOG", 17, (byte) 12);
        f2l("SPRUCE_WOOD", "LOG", 17, (byte) 13);
        f2l("BIRCH_WOOD", "LOG", 17, (byte) 14);
        f2l("JUNGLE_WOOD", "LOG", 17, (byte) 15);

        f2l("OAK_LEAVES", "LEAVES", 18, (byte) 0);
        f2l("SPRUCE_LEAVES", "LEAVES", 18, (byte) 1);
        f2l("BIRCH_LEAVES", "LEAVES", 18, (byte) 2);
        f2l("JUNGLE_LEAVES", "LEAVES", 18, (byte) 3);

        f2l("SPONGE", 19);
        f2l("WET_SPONGE", "SPONGE", 19, (byte) 1);

        f2l("GLASS", 20);
        f2l("LAPIS_ORE", 21);
        f2l("LAPIS_BLOCK", 22);
        f2l("DISPENSER", 23);
        f2l("SANDSTONE", 24);
        f2l("CHISELED_SANDSTONE", "SANDSTONE", 24, (byte) 1);
        f2l("CUT_SANDSTONE", "SANDSTONE", 24, (byte) 2);

        f2l("NOTE_BLOCK", 25, "NOTEBLOCK");
        //f2l("", "BED_BLOCK", 26, (byte) 0); - only block (blocks will be supported later), bukkit mapping collision
        f2l("POWERED_RAIL", 27, "GOLDEN_RAIL");
        f2l("DETECTOR_RAIL", 28);
        f2l("STICKY_PISTON", 29, "PISTON_STICKY_BASE");
        f2l("COBWEB", "WEB", 30);
        f2l("GRASS", "LONG_GRASS", 31, "TALLGRASS"); // collision
        f2l("DEAD_BUSH", 32, "DEADBUSH");
        f2l("PISTON", 33, "PISTON_BASE");
        //f2l("PISTON_HEAD", 34, "PISTON_EXTENSION"); - only block (blocks will be supported later), bukkit mapping collision
        f2lcolored("WOOL", 35);
        //f2l("MOVING_PISTON", "PISTON_EXTENSION", 36, (byte) 0, "PISTON_MOVING_PIECE"); - only block (blocks will be supported later), bukkit mapping collision
        f2l("DANDELION", "YELLOW_FLOWER", 37, (byte) 0);

        f2l("POPPY", "RED_FLOWER", 38, "RED_ROSE");
        f2l("BLUE_ORCHID", "RED_FLOWER", 38, (byte) 1,"RED_ROSE");
        f2l("ALLIUM", "RED_FLOWER", 38, (byte) 2,"RED_ROSE");
        f2l("AZURE_BLUET", "RED_FLOWER", 38, (byte) 3,"RED_ROSE");
        f2l("RED_TULIP", "RED_FLOWER", 38, (byte) 4,"RED_ROSE");
        f2l("ORANGE_TULIP", "RED_FLOWER", 38, (byte) 5,"RED_ROSE");
        f2l("WHITE_TULIP", "RED_FLOWER", 38, (byte) 6,"RED_ROSE");
        f2l("PINK_TULIP", "RED_FLOWER", 38, (byte) 7,"RED_ROSE");
        f2l("OXEYE_DAISY", "RED_FLOWER", 38, (byte) 8,"RED_ROSE");

        f2l("BROWN_MUSHROOM", 39);
        f2l("RED_MUSHROOM", 40);
        f2l("GOLD_BLOCK", 41);
        f2l("IRON_BLOCK", 42);

        //f2l("", "DOUBLE_STONE_SLAB", 43, (byte) 0, "DOUBLE_STEP"); - only block (blocks will be supported later)

        f2l("SMOOTH_STONE_SLAB", "STONE_SLAB", 44, (byte) 0, "STEP");
        f2l("SANDSTONE_SLAB", "STONE_SLAB", 44, (byte) 1, "STEP");
        f2l("PETRIFIED_OAK_SLAB", "STONE_SLAB", 44, (byte) 2, "STEP");
        f2l("COBBLESTONE_SLAB", "STONE_SLAB", 44, (byte) 3, "STEP");
        f2l("BRICK_SLAB", "STONE_SLAB", 44, (byte) 4, "STEP");
        f2l("STONE_BRICK_SLAB", "STONE_SLAB", 44, (byte) 5, "STEP");
        f2l("NETHER_BRICK_SLAB", "STONE_SLAB", 44, (byte) 6, "STEP");
        f2l("QUARTZ_SLAB", "STONE_SLAB", 44, (byte) 7, "STEP");

        f2l("BRICKS", "BRICK_BLOCK", 45, "BRICK");
        f2l("TNT", 46);
        f2l("BOOKSHELF", 47);
        f2l("MOSSY_COBBLESTONE", 48);
        f2l( "OBSIDIAN", 49);
        f2l("TORCH", 50);
        // flattening WALL_TORCH - only block (blocks will be supported later)
        //f2l( "FIRE", 51); - only block (blocks will be supported later)
        f2l("SPAWNER", "MOB_SPAWNER", 52);
        f2l("OAK_STAIRS", "WOOD_STAIRS", 53);
        f2l( "CHEST", 54);
        //f2l("", "REDSTONE_WIRE", 55, (byte) 0); - block only
        f2l("DIAMOND_ORE", 56);
        f2l("DIAMOND_BLOCK", 57);
        f2l("CRAFTING_TABLE", 58, "WORKBENCH");
        //f2l("WHEAT", 59, "CROPS");
        f2l("FARMLAND", 60, "SOIL");
        f2l("FURNACE", 61);
        //f2l("", "LIT_FURNACE", 62, (byte) 0, "BURNING_FURNACE"); - block only
        //f2l("", "STANDING_SIGN", 63, "SIGN_POST"); - block only
        //f2l("", "WOODEN_DOOR", 64, (byte) 0); - block only
        f2l("LADDER", 65);
        f2l("RAIL", 66, "RAILS");
        f2l("COBBLESTONE_STAIRS", 67, "STONE_STAIRS");
        // actually official legacy name STONE_STAIRS is colliding with flattening name of another item
        //f2l("", "WALL_SIGN", 68, (byte) 0); - block
        f2l( "LEVER", 69);
        f2l("STONE_PRESSURE_PLATE", 70, "STONE_PLATE");
        //f2l("", "IRON_DOOR_BLOCK", 71, (byte) 0); - block
        f2l("OAK_PRESSURE_PLATE", "WOODEN_PRESSURE_PLATE", 72,"WOOD_PLATE");
        f2l("REDSTONE_ORE", 73);
        //f2l("", "GLOWING_REDSTONE_ORE", 74, (byte) 0); - block
        //f2l("", "REDSTONE_TORCH_OFF", 75, (byte) 0); - block
        // Flattening WALL_REDSTONE_TORCH - block
        f2l("REDSTONE_TORCH", "REDSTONE_TORCH_ON", 76);
        f2l("STONE_BUTTON", 77);
        f2l("SNOW",78, "SNOW_LAYER"); // name snow collision with legacy snow_block namespace
        f2l("ICE", 79);
        f2l("SNOW_BLOCK", 80, "SNOW"); // name snow collision with flattening snow_layer namespace
        f2l("CACTUS", 81);
        f2l( "CLAY", 82);
        //f2l("", "REEDS", 83, (byte) 0", SUGAR_CANE_BLOCK"); - block
        f2l("JUKEBOX", 84);
        f2l("OAK_FENCE", "FENCE", 85);
        f2l("CARVED_PUMPKIN", "PUMPKIN", 86); // colliding with flattening carved_pumpkin
        f2l("NETHERRACK", 87);
        f2l("SOUL_SAND", 88);
        f2l("GLOWSTONE", 89);
        //f2l("NETHER_PORTAL", "PORTAL", 90); - block
        f2l("JACK_O_LANTERN", 91, "LIT_PUMPKIN");
        //f2l("", "CAKE_BLOCK", 92, (byte) 0); - block
        // f2l("", "DIODE_BLOCK_OFF", 93, (byte) 0); - block
        //f2l("", "DIODE_BLOCK_ON", 94, (byte) 0); - block
        f2lcolored("STAINED_GLASS", 95);
        f2l("OAK_TRAPDOOR", "TRAPDOOR", 96, "TRAP_DOOR");

        f2l("INFESTED_STONE", "MONSTER_EGG", 97, (byte) 0, "MONSTER_EGGS");
        f2l("INFESTED_COBBLESTONE", "MONSTER_EGG", 97, (byte) 1, "MONSTER_EGGS");
        f2l("INFESTED_STONE_BRICKS", "MONSTER_EGG", 97, (byte) 2, "MONSTER_EGGS");
        f2l("INFESTED_MOSSY_STONE_BRICKS", "MONSTER_EGG", 97, (byte) 3, "MONSTER_EGGS");
        f2l("INFESTED_CRACKED_STONE_BRICKS", "MONSTER_EGG", 97, (byte) 4, "MONSTER_EGGS");
        f2l("INFESTED_CHISELED_STONE_BRICKS", "MONSTER_EGG", 97, (byte) 5, "MONSTER_EGGS");

        f2l("STONE_BRICKS", "STONEBRICK", 98, (byte) 0, "SMOOTH_BRICK");
        f2l("MOSSY_STONE_BRICKS", "STONEBRICK", 98, (byte) 1, "SMOOTH_BRICK");
        f2l("CRACKED_STONE_BRICKS", "STONEBRICK", 98, (byte) 2, "SMOOTH_BRICK");
        f2l("CHISELED_STONE_BRICKS", "STONEBRICK", 98, (byte) 3, "SMOOTH_BRICK");

        f2l("BROWN_MUSHROOM_BLOCK", 99, "HUGE_MUSHROOM_1");
        f2l("RED_MUSHROOM_BLOCK", 100, "HUGE_MUSHROOM_2");
        f2l("IRON_BARS", 101, "IRON_FENCE");
        f2l("GLASS_PANE", 102, "THIN_GLASS");
        f2l("MELON", "MELON_BLOCK", 103); // colliding with melon_slice?
        //f2l("", "PUMPKIN_STEM", 104, (byte) 0); - block
        //f2l("", "MELON_STEM", 105, (byte) 0); - block
        f2l("VINE", 106);
        f2l("OAK_FENCE_GATE", "FENCE_GATE", 107);
        f2l("BRICK_STAIRS", 108);
        f2l("STONE_BRICK_STAIRS", 109, "SMOOTH_STAIRS");
        f2l("MYCELIUM", 110, "MYCEL");
        f2l("LILY_PAD", "WATERLILY", 111, "WATER_LILY");
        f2l("NETHER_BRICKS", "NETHER_BRICK", 112);
        f2l("NETHER_BRICK_FENCE", 113, "NETHER_FENCE");
        f2l("NETHER_BRICK_STAIRS", 114);
        //f2l("NETHER_WART", 115, "NETHER_WARTS"); - block
        f2l("ENCHANTING_TABLE", 116, "ENCHANTMENT_TABLE");
       // f2l("BREWING_STAND", 117);- block
        //f2l("CAULDRON", 118); - block
        //f2l("END_PORTAL", 119, "ENDER_PORTAL"); - block
        //f2l("END_PORTAL_FRAME", 120, "ENDER_PORTAL_FRAME"); - block
        f2l("END_STONE_BRICKS", "END_BRICKS", 121, "ENDER_STONE");
        f2l("DRAGON_EGG", 122);
        f2l("REDSTONE_LAMP", 123, "REDSTONE_LAMP_OFF");
        //f2l("", "REDSTONE_LAMP_ON", 124, (byte) 0); - block
        //f2l("", "WOOD_DOUBLE_STEP", 125, (byte) 0); - block

        f2l("OAK_SLAB", "WOODEN_SLAB", 126, (byte) 0, "WOOD_STEP");
        f2l("SPRUCE_SLAB", "WOODEN_SLAB", 126, (byte) 1, "WOOD_STEP");
        f2l("BIRCH_SLAB", "WOODEN_SLAB", 126, (byte) 2, "WOOD_STEP");
        f2l("JUNGLE_SLAB", "WOODEN_SLAB", 126, (byte) 3, "WOOD_STEP");
        f2l("ACACIA_SLAB", "WOODEN_SLAB", 126, (byte) 4, "WOOD_STEP");
        f2l("DARK_OAK_SLAB", "WOODEN_SLAB", 126, (byte) 5, "WOOD_STEP");

        //f2l("", "COCOA", 127, (byte) 0); - block
        f2l("SANDSTONE_STAIRS", 128);
        f2l("EMERALD_ORE", 129);
        f2l("ENDER_CHEST", 130);
        f2l("TRIPWIRE_HOOK", 131);
        //f2l("TRIPWIRE", 132); - block
        f2l("EMERALD_BLOCK", 133);
        f2l("SPRUCE_STAIRS", 134, "SPRUCE_WOOD_STAIRS");
        f2l("BIRCH_STAIRS", 135, "BIRCH_WOOD_STAIRS");
        f2l("JUNGLE_STAIRS", 136, "JUNGLE_WOOD_STAIRS");
        f2l("COMMAND_BLOCK", 137, "COMMAND");
        f2l("BEACON", 138);
        f2l("COBBLESTONE_WALL", 139, "COBBLE_WALL");
        //f2l("", "FLOWER_POT", 140, (byte) 0); - block
        //f2l("", "CARROTS", 141, "CARROT"); - block
        //f2l("", "POTATOES", 142, "POTATO"); - block
        f2l("OAK_BUTTON", "WOODEN_BUTTON", 143, "WOOD_BUTTON");
        //f2l("", "SKULL", 144, (byte) 0); - block
        f2l("ANVIL", 145);
        f2l("CHIPPED_ANVIL", "ANVIL", 145, (byte) 1);
        f2l("DAMAGED_ANVIL", "ANVIL", 145, (byte) 2);
        f2l("TRAPPED_CHEST", 146);
        f2l("LIGHT_WEIGHTED_PRESSURE_PLATE", 147, "GOLD_PLATE");
        f2l("heavy_weighted_pressure_plate", 148, "IRON_PLATE");
        //f2l("", "REDSTONE_COMPARATOR_OFF", 149, (byte) 0); - block
        //f2l("", "REDSTONE_COMPARATOR_ON", 150, (byte) 0); - block
        f2l("DAYLIGHT_DETECTOR", 151);
        f2l("REDSTONE_BLOCK", 152);
        f2l("NETHER_QUARTZ_ORE", 153, "QUARTZ_ORE");
        f2l("HOPPER", 154);

        f2l("QUARTZ_BLOCK", 155);
        f2l("CHISELED_QUARTZ_BLOCK", "QUARTZ_BLOCK", 155, (byte) 1);
        f2l("QUARTZ_PILLAR", "QUARTZ_BLOCK", 155, (byte) 2);

        f2l("QUARTZ_STAIRS", 156);
        f2l("ACTIVATOR_RAIL", 157);
        f2l("DROPPER", 158);
        f2lcolored("TERRACOTTA", "STAINED_HARDENED_CLAY", 159, "STAINED_CLAY");
        f2lcolored("STAINED_GLASS_PANE", 160);

        f2l("ACACIA_LEAVES", "LEAVES2", 161, (byte) 0, "LEAVES_2");
        f2l("DARK_OAK_LEAVES", "LEAVES2", 161, (byte) 1, "LEAVES_2");

        f2l("ACACIA_LOG", "LOG2", 162, (byte) 0, "LOG_2");
        f2l("DARK_OAK_LOG", "LOG2", 162, (byte) 1, "LOG_2");
        f2l("ACACIA_WOOD", "LOG2", 162, (byte) 12, "LOG_2");
        f2l("DARK_OAK_WOOD", "LOG2", 162, (byte) 13, "LOG_2");

        f2l("ACACIA_STAIRS", 163);
        f2l("DARK_OAK_STAIRS", 164);

        f2l("SLIME_BLOCK",  165, "SLIME");
        f2l("BARRIER", 166);
        f2l("IRON_TRAPDOOR", 167);

        f2l("PRISMARINE", 168);
        f2l("PRISMARINE_BRICKS", "PRISMARINE", 168, (byte) 1);
        f2l("DARK_PRISMARINE", "PRISMARINE", 168, (byte) 1);

        f2l("SEA_LANTERN", 169);
        f2l("HAY_BLOCK", 170);
        f2lcolored("CARPET", 171);
        f2l("TERRACOTTA", "hardened_clay", 172, "HARD_CLAY");
        f2l("COAL_BLOCK", 173);
        f2l("PACKED_ICE", 174);

        f2l("SUNFLOWER", "DOUBLE_PLANT", 175, (byte) 0);
        f2l("LILAC", "DOUBLE_PLANT", 175, (byte) 1);
        f2l("TALL_GRASS", "DOUBLE_PLANT", 175, (byte) 2);
        f2l("LARGE_FERN", "DOUBLE_PLANT", 175, (byte) 3);
        f2l("ROSE_BUSH", "DOUBLE_PLANT", 175, (byte) 4);
        f2l("PEONY", "DOUBLE_PLANT", 175, (byte) 5);

        //f2l("", "STANDING_BANNER", 176, (byte) 0); - block
        //f2l("", "WALL_BANNER", 177, (byte) 0); - block

        //f2l("", "DAYLIGHT_DETECTOR_INVERTED", 178, (byte) 0); - block
        /*f2l("", "RED_SANDSTONE", 179, (byte) 0);
        f2l("", "RED_SANDSTONE_STAIRS", 180, (byte) 0);
        f2l("", "DOUBLE_STONE_SLAB2", 181, (byte) 0);
        f2l("", "STONE_SLAB2", 182, (byte) 0);
        f2l("", "SPRUCE_FENCE_GATE", 183, (byte) 0);
        f2l("", "BIRCH_FENCE_GATE", 184, (byte) 0);
        f2l("", "JUNGLE_FENCE_GATE", 185, (byte) 0);
        f2l("", "DARK_OAK_FENCE_GATE", 186, (byte) 0);
        f2l("", "ACACIA_FENCE_GATE", 187, (byte) 0);
        f2l("", "SPRUCE_FENCE", 188, (byte) 0);
        f2l("", "BIRCH_FENCE", 189, (byte) 0);
        f2l("", "JUNGLE_FENCE", 190, (byte) 0);
        f2l("", "DARK_OAK_FENCE", 191, (byte) 0);
        f2l("", "ACACIA_FENCE", 192, (byte) 0);
        f2l("", "SPRUCE_DOOR", 193, (byte) 0);
        f2l("", "BIRCH_DOOR", 194, (byte) 0);
        f2l("", "JUNGLE_DOOR", 195, (byte) 0);
        f2l("", "ACACIA_DOOR", 196, (byte) 0);
        f2l("", "DARK_OAK_DOOR", 197, (byte) 0);
        f2l("", "END_ROD", 198, (byte) 0);
        f2l("", "CHORUS_PLANT", 199, (byte) 0);
        f2l("", "CHORUS_FLOWER", 200, (byte) 0);
        f2l("", "PURPUR_BLOCK", 201, (byte) 0);
        f2l("", "PURPUR_PILLAR", 202, (byte) 0);
        f2l("", "PURPUR_STAIRS", 203, (byte) 0);
        f2l("", "PURPUR_DOUBLE_SLAB", 204, (byte) 0);
        f2l("", "PURPUR_SLAB", 205, (byte) 0);
        f2l("", "END_BRICKS", 206, (byte) 0);
        f2l("", "BEETROOT_BLOCK", 207, (byte) 0);
        f2l("", "GRASS_PATH", 208, (byte) 0);
        f2l("", "END_GATEWAY", 209, (byte) 0);
        f2l("", "COMMAND_REPEATING", 210, (byte) 0);
        f2l("", "COMMAND_CHAIN", 211, (byte) 0);
        f2l("", "FROSTED_ICE", 212, (byte) 0);
        f2l("", "MAGMA", 213, (byte) 0);
        f2l("", "NETHER_WART_BLOCK", 214, (byte) 0);
        f2l("", "RED_NETHER_BRICK", 215, (byte) 0);
        f2l("", "BONE_BLOCK", 216, (byte) 0);
        f2l("", "STRUCTURE_VOID", 217, (byte) 0);
        f2l("", "OBSERVER", 218, (byte) 0);
        f2l("", "WHITE_SHULKER_BOX", 219, (byte) 0);
        f2l("", "ORANGE_SHULKER_BOX", 220, (byte) 0);
        f2l("", "MAGENTA_SHULKER_BOX", 221, (byte) 0);
        f2l("", "LIGHT_BLUE_SHULKER_BOX", 222, (byte) 0);
        f2l("", "YELLOW_SHULKER_BOX", 223, (byte) 0);
        f2l("", "LIME_SHULKER_BOX", 224, (byte) 0);
        f2l("", "PINK_SHULKER_BOX", 225, (byte) 0);
        f2l("", "GRAY_SHULKER_BOX", 226, (byte) 0);
        f2l("", "SILVER_SHULKER_BOX", 227, (byte) 0);
        f2l("", "CYAN_SHULKER_BOX", 228, (byte) 0);
        f2l("", "PURPLE_SHULKER_BOX", 229, (byte) 0);
        f2l("", "BLUE_SHULKER_BOX", 230, (byte) 0);
        f2l("", "BROWN_SHULKER_BOX", 231, (byte) 0);
        f2l("", "GREEN_SHULKER_BOX", 232, (byte) 0);
        f2l("", "RED_SHULKER_BOX", 233, (byte) 0);
        f2l("", "BLACK_SHULKER_BOX", 234, (byte) 0);
        f2l("", "WHITE_GLAZED_TERRACOTTA", 235, (byte) 0);
        f2l("", "ORANGE_GLAZED_TERRACOTTA", 236, (byte) 0);
        f2l("", "MAGENTA_GLAZED_TERRACOTTA", 237, (byte) 0);
        f2l("", "LIGHT_BLUE_GLAZED_TERRACOTTA", 238, (byte) 0);
        f2l("", "YELLOW_GLAZED_TERRACOTTA", 239, (byte) 0);
        f2l("", "LIME_GLAZED_TERRACOTTA", 240, (byte) 0);
        f2l("", "PINK_GLAZED_TERRACOTTA", 241, (byte) 0);
        f2l("", "GRAY_GLAZED_TERRACOTTA", 242, (byte) 0);
        f2l("", "SILVER_GLAZED_TERRACOTTA", 243, (byte) 0);
        f2l("", "CYAN_GLAZED_TERRACOTTA", 244, (byte) 0);
        f2l("", "PURPLE_GLAZED_TERRACOTTA", 245, (byte) 0);
        f2l("", "BLUE_GLAZED_TERRACOTTA", 246, (byte) 0);
        f2l("", "BROWN_GLAZED_TERRACOTTA", 247, (byte) 0);
        f2l("", "GREEN_GLAZED_TERRACOTTA", 248, (byte) 0);
        f2l("", "RED_GLAZED_TERRACOTTA", 249, (byte) 0);
        f2l("", "BLACK_GLAZED_TERRACOTTA", 250, (byte) 0);
        f2l("", "CONCRETE", 251, (byte) 0);
        f2l("", "CONCRETE_POWDER", 252, (byte) 0);
        f2l("", "STRUCTURE_BLOCK", 255, (byte) 0);
        f2l("", "IRON_SPADE", 256, (byte) 0);
        f2l("", "IRON_PICKAXE", 257, (byte) 0);
        f2l("", "IRON_AXE", 258, (byte) 0);
        f2l("", "FLINT_AND_STEEL", 259, (byte) 0);
        f2l("", "APPLE", 260, (byte) 0);
        f2l("", "BOW", 261, (byte) 0);
        f2l("", "ARROW", 262, (byte) 0);
        f2l("", "COAL", 263, (byte) 0);
        f2l("", "DIAMOND", 264, (byte) 0);
        f2l("", "IRON_INGOT", 265, (byte) 0);
        f2l("", "GOLD_INGOT", 266, (byte) 0);
        f2l("", "IRON_SWORD", 267, (byte) 0);
        f2l("", "WOOD_SWORD", 268, (byte) 0);
        f2l("", "WOOD_SPADE", 269, (byte) 0);
        f2l("", "WOOD_PICKAXE", 270, (byte) 0);
        f2l("", "WOOD_AXE", 271, (byte) 0);
        f2l("", "STONE_SWORD", 272, (byte) 0);
        f2l("", "STONE_SPADE", 273, (byte) 0);
        f2l("", "STONE_PICKAXE", 274, (byte) 0);
        f2l("", "STONE_AXE", 275, (byte) 0);
        f2l("", "DIAMOND_SWORD", 276, (byte) 0);
        f2l("", "DIAMOND_SPADE", 277, (byte) 0);
        f2l("", "DIAMOND_PICKAXE", 278, (byte) 0);
        f2l("", "DIAMOND_AXE", 279, (byte) 0);
        f2l("", "STICK", 280, (byte) 0);
        f2l("", "BOWL", 281, (byte) 0);
        f2l("", "MUSHROOM_SOUP", 282, (byte) 0);
        f2l("", "GOLD_SWORD", 283, (byte) 0);
        f2l("", "GOLD_SPADE", 284, (byte) 0);
        f2l("", "GOLD_PICKAXE", 285, (byte) 0);
        f2l("", "GOLD_AXE", 286, (byte) 0);
        f2l("", "STRING", 287, (byte) 0);
        f2l("", "FEATHER", 288, (byte) 0);
        f2l("", "SULPHUR", 289, (byte) 0);
        f2l("", "WOOD_HOE", 290, (byte) 0);
        f2l("", "STONE_HOE", 291, (byte) 0);
        f2l("", "IRON_HOE", 292, (byte) 0);
        f2l("", "DIAMOND_HOE", 293, (byte) 0);
        f2l("", "GOLD_HOE", 294, (byte) 0);
        f2l("", "SEEDS", 295, (byte) 0);
        f2l("", "WHEAT", 296, (byte) 0);
        f2l("", "BREAD", 297, (byte) 0);
        f2l("", "LEATHER_HELMET", 298, (byte) 0);
        f2l("", "LEATHER_CHESTPLATE", 299, (byte) 0);
        f2l("", "LEATHER_LEGGINGS", 300, (byte) 0);
        f2l("", "LEATHER_BOOTS", 301, (byte) 0);
        f2l("", "CHAINMAIL_HELMET", 302, (byte) 0);
        f2l("", "CHAINMAIL_CHESTPLATE", 303, (byte) 0);
        f2l("", "CHAINMAIL_LEGGINGS", 304, (byte) 0);
        f2l("", "CHAINMAIL_BOOTS", 305, (byte) 0);
        f2l("", "IRON_HELMET", 306, (byte) 0);
        f2l("", "IRON_CHESTPLATE", 307, (byte) 0);
        f2l("", "IRON_LEGGINGS", 308, (byte) 0);
        f2l("", "IRON_BOOTS", 309, (byte) 0);
        f2l("", "DIAMOND_HELMET", 310, (byte) 0);
        f2l("", "DIAMOND_CHESTPLATE", 311, (byte) 0);
        f2l("", "DIAMOND_LEGGINGS", 312, (byte) 0);
        f2l("", "DIAMOND_BOOTS", 313, (byte) 0);
        f2l("", "GOLD_HELMET", 314, (byte) 0);
        f2l("", "GOLD_CHESTPLATE", 315, (byte) 0);
        f2l("", "GOLD_LEGGINGS", 316, (byte) 0);
        f2l("", "GOLD_BOOTS", 317, (byte) 0);
        f2l("", "FLINT", 318, (byte) 0);
        f2l("", "PORK", 319, (byte) 0);
        f2l("", "GRILLED_PORK", 320, (byte) 0);
        f2l("", "PAINTING", 321, (byte) 0);
        f2l("", "GOLDEN_APPLE", 322, (byte) 0);
        f2l("", "SIGN", 323, (byte) 0);
        f2l("", "WOOD_DOOR", 324, (byte) 0);
        f2l("", "BUCKET", 325, (byte) 0);
        f2l("", "WATER_BUCKET", 326, (byte) 0);
        f2l("", "LAVA_BUCKET", 327, (byte) 0);
        f2l("", "MINECART", 328, (byte) 0);
        f2l("", "SADDLE", 329, (byte) 0);
        f2l("", "IRON_DOOR", 330, (byte) 0);
        f2l("", "REDSTONE", 331, (byte) 0);
        f2l("", "SNOW_BALL", 332, (byte) 0);
        f2l("", "BOAT", 333, (byte) 0);
        f2l("", "LEATHER", 334, (byte) 0);
        f2l("", "MILK_BUCKET", 335, (byte) 0);
        f2l("", "CLAY_BRICK", 336, (byte) 0);
        f2l("", "CLAY_BALL", 337, (byte) 0);
        f2l("", "SUGAR_CANE", 338, (byte) 0);
        f2l("", "PAPER", 339, (byte) 0);
        f2l("", "BOOK", 340, (byte) 0);
        f2l("", "SLIME_BALL", 341, (byte) 0);
        f2l("", "STORAGE_MINECART", 342, (byte) 0);
        f2l("", "POWERED_MINECART", 343, (byte) 0);
        f2l("", "EGG", 344, (byte) 0);
        f2l("", "COMPASS", 345, (byte) 0);
        f2l("", "FISHING_ROD", 346, (byte) 0);
        f2l("", "WATCH", 347, (byte) 0);
        f2l("", "GLOWSTONE_DUST", 348, (byte) 0);
        f2l("", "RAW_FISH", 349, (byte) 0);
        f2l("", "COOKED_FISH", 350, (byte) 0);
        f2l("", "INK_SACK", 351, (byte) 0);
        f2l("", "BONE", 352, (byte) 0);
        f2l("", "SUGAR", 353, (byte) 0);
        f2l("", "CAKE", 354, (byte) 0);
        f2l("", "BED", 355, (byte) 0);
        f2l("", "DIODE", 356, (byte) 0);
        f2l("", "COOKIE", 357, (byte) 0);
        f2l("", "MAP", 358, (byte) 0);
        f2l("", "SHEARS", 359, (byte) 0);
        f2l("", "MELON", 360, (byte) 0);
        f2l("", "PUMPKIN_SEEDS", 361, (byte) 0);
        f2l("", "MELON_SEEDS", 362, (byte) 0);
        f2l("", "RAW_BEEF", 363, (byte) 0);
        f2l("", "COOKED_BEEF", 364, (byte) 0);
        f2l("", "RAW_CHICKEN", 365, (byte) 0);
        f2l("", "COOKED_CHICKEN", 366, (byte) 0);
        f2l("", "ROTTEN_FLESH", 367, (byte) 0);
        f2l("", "ENDER_PEARL", 368, (byte) 0);
        f2l("", "BLAZE_ROD", 369, (byte) 0);
        f2l("", "GHAST_TEAR", 370, (byte) 0);
        f2l("", "GOLD_NUGGET", 371, (byte) 0);
        f2l("", "NETHER_STALK", 372, (byte) 0);
        f2l("", "POTION", 373, (byte) 0);
        f2l("", "GLASS_BOTTLE", 374, (byte) 0);
        f2l("", "SPIDER_EYE", 375, (byte) 0);
        f2l("", "FERMENTED_SPIDER_EYE", 376, (byte) 0);
        f2l("", "BLAZE_POWDER", 377, (byte) 0);
        f2l("", "MAGMA_CREAM", 378, (byte) 0);
        f2l("", "BREWING_STAND_ITEM", 379, (byte) 0);
        f2l("", "CAULDRON_ITEM", 380, (byte) 0);
        f2l("", "EYE_OF_ENDER", 381, (byte) 0);
        f2l("", "SPECKLED_MELON", 382, (byte) 0);
        f2l("", "MONSTER_EGG", 383, (byte) 0);
        f2l("", "EXP_BOTTLE", 384, (byte) 0);
        f2l("", "FIREBALL", 385, (byte) 0);
        f2l("", "BOOK_AND_QUILL", 386, (byte) 0);
        f2l("", "WRITTEN_BOOK", 387, (byte) 0);
        f2l("", "EMERALD", 388, (byte) 0);
        f2l("", "ITEM_FRAME", 389, (byte) 0);
        f2l("", "FLOWER_POT_ITEM", 390, (byte) 0);
        f2l("", "CARROT_ITEM", 391, (byte) 0);
        f2l("", "POTATO_ITEM", 392, (byte) 0);
        f2l("", "BAKED_POTATO", 393, (byte) 0);
        f2l("", "POISONOUS_POTATO", 394, (byte) 0);
        f2l("", "EMPTY_MAP", 395, (byte) 0);
        f2l("", "GOLDEN_CARROT", 396, (byte) 0);
        f2l("", "SKULL_ITEM", 397, (byte) 0);
        f2l("", "CARROT_STICK", 398, (byte) 0);
        f2l("", "NETHER_STAR", 399, (byte) 0);
        f2l("", "PUMPKIN_PIE", 400, (byte) 0);
        f2l("", "FIREWORK", 401, (byte) 0);
        f2l("", "FIREWORK_CHARGE", 402, (byte) 0);
        f2l("", "ENCHANTED_BOOK", 403, (byte) 0);
        f2l("", "REDSTONE_COMPARATOR", 404, (byte) 0);
        f2l("", "NETHER_BRICK_ITEM", 405, (byte) 0);
        f2l("", "QUARTZ", 406, (byte) 0);
        f2l("", "EXPLOSIVE_MINECART", 407, (byte) 0);
        f2l("", "HOPPER_MINECART", 408, (byte) 0);
        f2l("", "PRISMARINE_SHARD", 409, (byte) 0);
        f2l("", "PRISMARINE_CRYSTALS", 410, (byte) 0);
        f2l("", "RABBIT", 411, (byte) 0);
        f2l("", "COOKED_RABBIT", 412, (byte) 0);
        f2l("", "RABBIT_STEW", 413, (byte) 0);
        f2l("", "RABBIT_FOOT", 414, (byte) 0);
        f2l("", "RABBIT_HIDE", 415, (byte) 0);
        f2l("", "ARMOR_STAND", 416, (byte) 0);
        f2l("", "IRON_BARDING", 417, (byte) 0);
        f2l("", "GOLD_BARDING", 418, (byte) 0);
        f2l("", "DIAMOND_BARDING", 419, (byte) 0);
        f2l("", "LEASH", 420, (byte) 0);
        f2l("", "NAME_TAG", 421, (byte) 0);
        f2l("", "COMMAND_MINECART", 422, (byte) 0);
        f2l("", "MUTTON", 423, (byte) 0);
        f2l("", "COOKED_MUTTON", 424, (byte) 0);
        f2l("", "BANNER", 425, (byte) 0);
        f2l("", "END_CRYSTAL", 426, (byte) 0);
        f2l("", "SPRUCE_DOOR_ITEM", 427, (byte) 0);
        f2l("", "BIRCH_DOOR_ITEM", 428, (byte) 0);
        f2l("", "JUNGLE_DOOR_ITEM", 429, (byte) 0);
        f2l("", "ACACIA_DOOR_ITEM", 430, (byte) 0);
        f2l("", "DARK_OAK_DOOR_ITEM", 431, (byte) 0);
        f2l("", "CHORUS_FRUIT", 432, (byte) 0);
        f2l("", "CHORUS_FRUIT_POPPED", 433, (byte) 0);
        f2l("", "BEETROOT", 434, (byte) 0);
        f2l("", "BEETROOT_SEEDS", 435, (byte) 0);
        f2l("", "BEETROOT_SOUP", 436, (byte) 0);
        f2l("", "DRAGONS_BREATH", 437, (byte) 0);
        f2l("", "SPLASH_POTION", 438, (byte) 0);
        f2l("", "SPECTRAL_ARROW", 439, (byte) 0);
        f2l("", "TIPPED_ARROW", 440, (byte) 0);
        f2l("", "LINGERING_POTION", 441, (byte) 0);
        f2l("", "SHIELD", 442, (byte) 0);
        f2l("", "ELYTRA", 443, (byte) 0);
        f2l("", "BOAT_SPRUCE", 444, (byte) 0);
        f2l("", "BOAT_BIRCH", 445, (byte) 0);
        f2l("", "BOAT_JUNGLE", 446, (byte) 0);
        f2l("", "BOAT_ACACIA", 447, (byte) 0);
        f2l("", "BOAT_DARK_OAK", 448, (byte) 0);
        f2l("", "TOTEM", 449, (byte) 0);
        f2l("", "SHULKER_SHELL", 450, (byte) 0);
        f2l("", "IRON_NUGGET", 452, (byte) 0);
        f2l("", "KNOWLEDGE_BOOK", 453, (byte) 0);
        f2l("", "GOLD_RECORD", 2256, (byte) 0);
        f2l("", "GREEN_RECORD", 2257, (byte) 0);
        f2l("", "RECORD_3", 2258, (byte) 0);
        f2l("", "RECORD_4", 2259, (byte) 0);
        f2l("", "RECORD_5", 2260, (byte) 0);
        f2l("", "RECORD_6", 2261, (byte) 0);
        f2l("", "RECORD_7", 2262, (byte) 0);
        f2l("", "RECORD_8", 2263, (byte) 0);
        f2l("", "RECORD_9", 2264, (byte) 0);
        f2l("", "RECORD_10", 2265, (byte) 0);
        f2l("", "RECORD_11", 2266, (byte) 0);
        f2l("", "RECORD_12", 2267, (byte) 0);*/


        // Flattening remapping
        f2f("ZOMBIFIED_PIGLIN_SPAWN_EGG", "ZOMBIE_PIGMAN_SPAWN_EGG");
        f2f("SMOOTH_STONE_SLAB", "STONE_SLAB");
        f2f("GREEN_DYE", "CACTUS_GREEN");
        f2f("YELLOW_DYE", "DANDELION_YELLOW");
        f2f("RED_DYE", "ROSE_RED");
        f2f("OAK_SIGN", "SIGN");
        f2f("BIRCH_SIGN", "SIGN");
        f2f("DARK_OAK_SIGN", "SIGN");
        f2f("JUNGLE_SIGN", "SIGN");
        f2f("SPRUCE_SIGN", "SIGN");
        f2f("ACACIA_SIGN", "SIGN");
        f2f("OAK_WALL_SIGN", "SIGN");
        f2f("BIRCH_WALL_SIGN", "SIGN");
        f2f("BIRCH_WALL_SIGN", "SIGN");
        f2f("DARK_OAK_WALL_SIGN", "SIGN");
        f2f("JUNGLE_WALL_SIGN", "SIGN");
        f2f("SPRUCE_WALL_SIGN", "SIGN");
        f2f("ACACIA_WALL_SIGN", "SIGN");
    }

    private void f2f(String material, String material2) {
        if (material == null || material2 == null) {
            throw new IllegalArgumentException("Both materials mustn't be null!");
        }
        if (materialMapping.containsKey(material.toUpperCase()) && !materialMapping.containsKey(material2.toUpperCase())) {
            materialMapping.put(material2.toUpperCase(), materialMapping.get(material.toUpperCase()));
        } else if (materialMapping.containsKey(material2.toUpperCase()) && !materialMapping.containsKey(material.toUpperCase())) {
            materialMapping.put(material.toUpperCase(), materialMapping.get(material2.toUpperCase()));
        }
    }

    private void f2lcolored(String material, int legacyId) {
        f2lcolored(material, material, legacyId);
    }
    private void f2lcolored(String flatteningMaterialSuffix, String legacyMaterial, int legacyId) {
        f2lcolored(flatteningMaterialSuffix, legacyMaterial, legacyId, null);
    }

    private void f2lcolored(String flatteningMaterialSuffix, String legacyMaterial, int legacyId, String alternativeLegacyName) {
        if (flatteningMaterialSuffix == null || legacyMaterial == null) {
            throw new IllegalArgumentException("Both materials mustn't be null!");
        }
        f2l("WHITE_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 0, alternativeLegacyName);
        f2l("ORANGE_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 1, alternativeLegacyName);
        f2l("MAGENTA_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 2, alternativeLegacyName);
        f2l("LIGHT_BLUE_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 3, alternativeLegacyName);
        f2l("YELLOW_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 4, alternativeLegacyName);
        f2l("LIME_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 5, alternativeLegacyName);
        f2l("PINK_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 6, alternativeLegacyName);
        f2l("GRAY_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 7, alternativeLegacyName);
        f2l("LIGHT_GRAY_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 8, alternativeLegacyName);
        f2l("CYAN_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 9, alternativeLegacyName);
        f2l("PURPLE_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 10, alternativeLegacyName);
        f2l("BLUE_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 11, alternativeLegacyName);
        f2l("BROWN_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 12, alternativeLegacyName);
        f2l("GREEN_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 13, alternativeLegacyName);
        f2l("RED_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 14, alternativeLegacyName);
        f2l("BLACK_" + flatteningMaterialSuffix, legacyMaterial, legacyId, (byte) 15, alternativeLegacyName);
    }

    /* For Materials where the name is same */
    private void f2l(String material, int legacyId) {
        f2l(material, material, legacyId);
    }

    private void f2l(String material, int legacyId, String alternativeLegacyName) {
        f2l(material, material, legacyId, alternativeLegacyName);
    }

    /* For Materials where the name is changed */
    private void f2l(String flatteningMaterial, String legacyMaterial, int legacyId) {
        f2l(flatteningMaterial, legacyMaterial, legacyId, (byte) 0);
    }

    private void f2l(String flatteningMaterial, String legacyMaterial, int legacyId, String alternativeLegacyName) {
        f2l(flatteningMaterial, legacyMaterial, legacyId, (byte) 0, alternativeLegacyName);
    }

    /* For Materials where the name is changed and data is not zero*/
    private void f2l(String flatteningMaterial, String legacyMaterial, int legacyId, byte data) {
        f2l(flatteningMaterial, legacyMaterial, legacyId, data, null);
    }

    private void f2l(String flatteningMaterial, String legacyMaterial, int legacyId, byte data, String alternativeLegacyName) {
        if (flatteningMaterial == null || legacyMaterial == null) {
            throw new IllegalArgumentException("Both flattening and legacy materials mustn't be null!");
        }
        flatteningMaterial = flatteningMaterial.toUpperCase();
        legacyMaterial = legacyMaterial.toUpperCase();
        if (alternativeLegacyName != null) {
            alternativeLegacyName = alternativeLegacyName.toUpperCase();
            if (alternativeLegacyName.equals(legacyMaterial)) {
                alternativeLegacyName = null;
            }
        }

        MaterialHolder holder = null;
        if (platform.isUsingLegacyNames() && (materialMapping.containsKey(legacyMaterial) || (alternativeLegacyName != null && materialMapping.containsKey(alternativeLegacyName)))) {
            if (materialMapping.containsKey(legacyMaterial)) {
                holder = materialMapping.get(legacyMaterial).newDurability(data);
                if (!materialMapping.containsKey(flatteningMaterial) && !flatteningMaterial.equals(legacyMaterial)) {
                    materialMapping.put(flatteningMaterial, holder);
                }
                if (data == 0 && alternativeLegacyName != null && !materialMapping.containsKey(alternativeLegacyName)) {
                    materialMapping.put(alternativeLegacyName, holder);
                }
                if (alternativeLegacyName != null && !materialMapping.containsKey(alternativeLegacyName + ":" + data)) {
                    materialMapping.put(alternativeLegacyName + ":" + data, holder);
                }
            } else if (alternativeLegacyName != null && materialMapping.containsKey(alternativeLegacyName)) {
                holder = materialMapping.get(alternativeLegacyName).newDurability(data);
                if (!materialMapping.containsKey(flatteningMaterial) && !flatteningMaterial.equals(legacyMaterial)) {
                    materialMapping.put(flatteningMaterial, holder);
                }
                if (data == 0 && !materialMapping.containsKey(legacyMaterial)) {
                    materialMapping.put(legacyMaterial, holder);
                }
                if (!materialMapping.containsKey(legacyMaterial + ":" + data)) {
                    materialMapping.put(legacyMaterial + ":" + data, holder);
                }
            }
        } else if (!platform.isUsingLegacyNames() && materialMapping.containsKey(flatteningMaterial)) {
            holder = materialMapping.get(flatteningMaterial);
            if (data == 0 && !materialMapping.containsKey(legacyMaterial) && !flatteningMaterial.equals(legacyMaterial)) {
                materialMapping.put(legacyMaterial, holder);
            }
            if (!materialMapping.containsKey(legacyMaterial + ":" + data)) {
                materialMapping.put(legacyMaterial + ":" + data, holder);
            }
            if (data == 0 && alternativeLegacyName != null && !materialMapping.containsKey(alternativeLegacyName) && !flatteningMaterial.equals(alternativeLegacyName)) {
                materialMapping.put(alternativeLegacyName, holder);
            }
            if (alternativeLegacyName != null && !materialMapping.containsKey(alternativeLegacyName + ":" + data)) {
                materialMapping.put(alternativeLegacyName + ":" + data, holder);
            }
        }

        if (holder != null) {
            if (!materialMapping.containsKey(String.valueOf(legacyId)) && data == 0) {
                materialMapping.put(String.valueOf(legacyId), holder);
            }
            if (!materialMapping.containsKey(legacyId + ":" + data)) {
                materialMapping.put(legacyId + ":" + data, holder);
            }
        }
    }
}