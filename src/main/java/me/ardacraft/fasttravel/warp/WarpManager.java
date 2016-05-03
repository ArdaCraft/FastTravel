/*
 * The MIT License (MIT)
 *
 * Copyright (c) dags <https://dags.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.ardacraft.fasttravel.warp;

import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.data.traveldata.MutableTravelData;
import me.ardacraft.fasttravel.destination.region.Region;
import me.ardacraft.fasttravel.utils.FileUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class WarpManager {

    private final Map<String, Entry> warps = new ConcurrentHashMap<>();
    private BookView bookView = new WarpBookBuilder().build();

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        String world = event.getTargetWorld().getName();
        Path dir = FastTravel.instance().configPath(world, "warps");
        FileUtil.loadAll(dir, Warp.class).forEach(this::quickAdd);
        update();
    }

    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        String world = event.getTargetWorld().getName();
        warps.values().stream()
                .map(e -> e.warp)
                .filter(w -> w.getWorld().equals(world)).forEach(this::quickRemove);
        update();
    }

    @Listener
    public void onInteract(InteractBlockEvent.Secondary event, @Root Player player) {
        player.getItemInHand().map(ItemStack::getItem).filter(item -> item == ItemTypes.BOOK).ifPresent(i -> {
            BookView view = player.get(MutableTravelData.class).filter(MutableTravelData::isExploring).map(MutableTravelData::listWarps).orElse(listWarps());
            player.sendBookView(view);
        });
    }

    public BookView listWarps() {
        return bookView;
    }

    public BookView listWarps(Predicate<Warp> filter) {
        return new WarpBookBuilder().add(entries().sorted(), filter).build();
    }

    public Optional<Warp> byName(String name) {
        Warp warp = get(name);
        return Optional.ofNullable(warp);
    }

    public Stream<Warp> byRegion(String world, Region region) {
        return warps.values().stream()
                .map(e -> e.warp)
                .filter(w -> w.getName().equals(world))
                .filter(w -> region.contains(w.blockX(), 0, w.blockZ()));
    }

    public Stream<Entry> entries() {
        return warps.values().stream();
    }

    public void add(Warp warp) {
        quickAdd(warp);
        update();
    }

    public void remove(Warp warp) {
        quickRemove(warp);
        update();
    }

    public void save(Warp warp) {
        Path dir = FastTravel.instance().configPath(warp.getWorld(), "warps");
        Path path = dir.resolve(warp.getName() + ".conf");
        FileUtil.toJson(warp, path);
    }

    public void delete(Warp warp) {
        Path dir = FastTravel.instance().configPath(warp.getWorld(), "warps");
        Path path = dir.resolve(warp.getName() + ".conf");
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Warp get(String name) {
        Entry e = warps.get(name.toLowerCase());
        return e == null ? null : e.warp;
    }

    private void quickAdd(Warp warp) {
        Warp remove = get(warp.getName());
        if (remove != null) {
            delete(remove);
        }
        warps.put(warp.getName().toLowerCase(), new Entry(warp));
    }

    private void quickRemove(Warp warp) {
        warps.remove(warp.getName().toLowerCase());
    }

    private void update() {
        bookView = new WarpBookBuilder().add(entries().sorted(), w -> true).build();
    }

    static class Entry implements Comparable<Entry> {

        private static final TextFormat FORMAT_FALSE = TextFormat.of(TextColors.DARK_GRAY, TextStyles.ITALIC);
        private static final TextFormat FORMAT_TRUE = TextFormat.of(TextColors.DARK_AQUA, TextStyles.BOLD.and(TextStyles.UNDERLINE));

        final Warp warp;
        final String line;
        final Text hover;

        private Entry(Warp warp) {
            this.warp = warp;
            this.line = trim(warp);
            this.hover = Text.builder(warp.getName()).color(TextColors.YELLOW).build();
        }

        Text getDisplayText(Predicate<Warp> filter) {
            if (filter.test(warp)) {
                return getLine(FORMAT_TRUE, TextActions.runCommand("/warp " + warp.getName()));
            }
            return getLine(FORMAT_FALSE, TextActions.executeCallback(s -> {}));
        }

        private Text getLine(TextFormat format, ClickAction<?> action) {
            return Text.builder("- ").color(format.getColor())
                    .append(Text.builder(line)
                            .onHover(TextActions.showText(hover))
                            .onClick(action)
                            .format(format)
                            .build())
                    .build();
        }

        private String trim(Warp warp) {
            String s = warp.getName();
            if (s.length() > 15) {
                return s.substring(0, 15);
            }
            return s;
        }

        @Override
        public int compareTo(Entry o) {
            return warp.getName().compareTo(o.warp.getName());
        }
    }
}
