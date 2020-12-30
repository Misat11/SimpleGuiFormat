package org.screamingsandals.simpleinventories.inventory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.loaders.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Include implements Queueable {
    public static final Pattern INCLUDE_PATTERN = Pattern.compile("^((?<loader>[A-Za-z0-9]*)(:(?<section>[A-Za-z0-9.]*))?@)?(?<path>.+)$");
    public static final Map<String, Supplier<? extends ILoader>> LOADERS = Map.ofEntries(
            Map.entry("yml", YamlLoader::new),
            Map.entry("yaml", YamlLoader::new),
            Map.entry("json", JsonLoader::new),
            Map.entry("csv", CsvLoader::new),
            Map.entry("txt", CsvLoader::new),
            Map.entry("xml", XmlLoader::new),
            Map.entry("conf", HoconLoader::new),
            Map.entry("hocon", HoconLoader::new),
            Map.entry("groovy", GroovyLoader::new),
            Map.entry("gvy", GroovyLoader::new),
            Map.entry("gy", GroovyLoader::new),
            Map.entry("gsh", GroovyLoader::new)
    );

    private final Path path;
    private final ILoader loader;
    private final String section;

    public static Include of(String link) {
        var matcher = INCLUDE_PATTERN.matcher(link.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("String " + link + " doesn't match pattern " + INCLUDE_PATTERN.pattern());
        }

        var loader = matcher.group("loader");
        var section = matcher.group("section");
        var pathString = matcher.group("path");

        var path = Path.of(pathString);

        if (loader == null) {
            var filename = path.getFileName().toString();
            loader = filename.substring(filename.lastIndexOf('.') + 1);
        }

        if (section == null) {
            section = "data";
        }

        return of(path, loader, section);
    }

    public static Include of(File file) {
        return of(file.toPath());
    }

    public static Include of(Path path) {
        var filename = path.getFileName().toString();
        var loader = filename.substring(filename.lastIndexOf('.') + 1);

        return of(path, loader);
    }

    public static Include of(String fileName, String loader) {
        return of(Path.of(fileName), loader);
    }

    public static Include of(String fileName, ILoader loader) {
        return of(Path.of(fileName), loader);
    }

    public static Include of(File file, String loader) {
        return of(file.toPath(), loader);
    }

    public static Include of(File file, ILoader loader) {
        return of(file.toPath(), loader);
    }

    public static Include of(Path path, String loader) {
        return of(path, loader, "data");
    }

    public static Include of(Path path, ILoader loader) {
        return of(path, loader, "data");
    }

    public static Include of(String fileName, String loader, String section) {
        return of(Path.of(fileName), loader, section);
    }

    public static Include of(String fileName, ILoader loader, String section) {
        return of(Path.of(fileName), loader, section);
    }

    public static Include of(File file, String loader, String section) {
        return of(file.toPath(), loader, section);
    }

    public static Include of(File file, ILoader loader, String section) {
        return of(file.toPath(), loader, section);
    }

    public static Include of(Path path, String loader, String section) {
        return of(path, LOADERS.getOrDefault(loader.toLowerCase(), YamlLoader::new).get(), section);
    }

    public static Include of(Path path, ILoader loader, String section) {
        if (!path.isAbsolute()) {
            path = SimpleInventoriesCore.getRootPath().resolve(path);
        }
        return new Include(path, loader, section);
    }

    public void apply(SubInventory subInventory) {
        try {
            this.loader.loadPathInto(subInventory, path, section);
        } catch (Exception e) {
            SimpleInventoriesCore.getLogger().severe("An error occurred while including file to inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}