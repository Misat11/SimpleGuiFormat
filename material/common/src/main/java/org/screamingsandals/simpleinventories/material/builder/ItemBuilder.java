package org.screamingsandals.simpleinventories.material.builder;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.simpleinventories.material.Item;
import org.screamingsandals.simpleinventories.material.MaterialMapping;
import org.screamingsandals.simpleinventories.material.meta.EnchantmentMapping;
import org.screamingsandals.simpleinventories.material.meta.PotionMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemBuilder {
    @NotNull
    private final Item item;

    public ItemBuilder type(@NotNull Object type) {
        // TODO: custom object resolving
        String typ = type.toString();
        MaterialMapping.resolve(typ).ifPresent(item::setMaterial);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(@Nullable String name) {
        item.setDisplayName(name);
        return this;
    }

    public ItemBuilder localizedName(@Nullable String name) {
        item.setLocalizedName(name);
        return this;
    }

    public ItemBuilder customModelData(int data) {
        item.setCustomModelData(data);
        return this;
    }

    public ItemBuilder repair(int repair) {
        item.setRepair(repair);
        return this;
    }

    public ItemBuilder flags(@Nullable List<Object> flags) {
        if (flags == null) {
            item.setItemFlags(null);
        } else {
            List<String> stringList = flags.stream().map(Object::toString).collect(Collectors.toList());
            item.setItemFlags(stringList);
        }
        return this;
    }
    public ItemBuilder unbreakable(boolean unbreakable) {
        item.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder lore(@Nullable List<String> lore) {
        item.setLore(lore);
        return this;
    }

    public ItemBuilder enchant(@NotNull Object enchant) {
        EnchantmentMapping.resolve(enchant.toString()).ifPresent(item.getEnchantments()::add); // TODO: custom object resolving
        return this;
    }

    public ItemBuilder enchant(@NotNull Object enchant, int level) {
        enchant(enchant + " " + level);
        return this;
    }

    public ItemBuilder enchant(@NotNull Map<Object, Integer> enchants) {
        enchants.forEach(this::enchant);
        return this;
    }

    public ItemBuilder enchant(@NotNull List<Object> enchants) {
        enchants.forEach(this::enchant);
        return this;
    }

    public ItemBuilder potion(@NotNull Object potion) {
        PotionMapping.resolve(potion.toString()).ifPresent(item::setPotion); // TODO: custom object resolving
        return this;
    }

    // For legacy versions
    @Deprecated
    public ItemBuilder damage(int damage) {
        return durability(damage);
    }
    // Or (durability is just alias for damage)
    public ItemBuilder durability(int durability) {
        item.setMaterial(item.getMaterial().newDurability(durability));
        return this;
    }

    public Optional<Item> build() {
        if (item.getMaterial() != null) {
            return Optional.of(item);
        }

        return Optional.empty();
    }
}
