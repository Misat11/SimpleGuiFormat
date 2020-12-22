package org.screamingsandals.simpleinventories;

import org.screamingsandals.simpleinventories.builder.InventoryBuilder;
import org.screamingsandals.simpleinventories.events.EventManager;
import org.screamingsandals.simpleinventories.inventory.Inventory;
import org.screamingsandals.simpleinventories.inventory.SubInventory;
import org.screamingsandals.simpleinventories.material.builder.ItemFactory;
import org.screamingsandals.simpleinventories.render.InventoryRenderer;
import org.screamingsandals.simpleinventories.utils.BidirectionalConverter;
import org.screamingsandals.simpleinventories.wrapper.PlayerWrapper;

import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class SimpleInventoriesCore {

    protected final BidirectionalConverter<PlayerWrapper> playerConverter = BidirectionalConverter.build();
    protected final EventManager eventManager = new EventManager(null);
    protected Logger logger;

    protected static SimpleInventoriesCore core;

    public static void init(Supplier<SimpleInventoriesCore> supplier) {
        if (core != null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore is already initialized.");
        }

        core = supplier.get();

        assert ItemFactory.isInitialized();
        assert core.logger != null;

        core.playerConverter.finish();
    }

    public static <T> T convertPlayerWrapper(PlayerWrapper player, Class<T> type) {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        return core.playerConverter.convert(player, type);
    }

    public static <T> PlayerWrapper wrapPlayer(T player) {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        return core.playerConverter.convert(player);
    }

    public static InventoryRenderer openInventory(PlayerWrapper playerWrapper, SubInventory subInventory) {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        var renderer = core.openInventory0(playerWrapper, subInventory);
        renderer.render();
        return renderer;
    }

    public static void closeInventory(PlayerWrapper playerWrapper) {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        core.closeInventory0(playerWrapper);
    }

    protected abstract InventoryRenderer openInventory0(PlayerWrapper playerWrapper, SubInventory subInventory);

    protected abstract void closeInventory0(PlayerWrapper playerWrapper);

    public static Logger getLogger() {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        return core.logger;
    }

    public static EventManager getEventManager() {
        if (core == null) {
            throw new UnsupportedOperationException("SimpleInventoriesCore isn't initialized yet.");
        }
        return core.eventManager;
    }

    public static InventoryBuilder builder() {
        return new InventoryBuilder(new Inventory());
    }
}
