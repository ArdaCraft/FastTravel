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

package me.ardacraft.fasttravel.commands;

import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.data.traveldata.MutableTravelData;
import me.ardacraft.fasttravel.destination.Destination;
import me.ardacraft.fasttravel.utils.Messenger;
import me.ardacraft.fasttravel.warp.Warp;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.Join;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class WarpCommands {

    private final Messenger messenger = new Messenger();

    @Command(aliases = {"warp", "go"}, perm = "fasttravel.command.warp")
    public void warp(@Caller Player player, @Join String name) {
        Optional<Warp> optional = FastTravel.instance().warpManager().byName(name);
        if (!optional.isPresent()) {
            messenger.error("No such warp ").stress(name).error("!").tell(player);
            return;
        }
        Warp warp = optional.get();
        Destination destination = FastTravel.instance().destinationManager().get(warp.getWorld(), warp.blockX(), warp.blockZ());
        MutableTravelData data = player.get(MutableTravelData.class).orElse(new MutableTravelData());
        if (data.isExploring() && !data.hasVisited(destination.getName())) {
            messenger.error("You must visit the location once before you can warp there!").tell(player);
        } else {
            warp.teleport(player);
            messenger.info("Teleporting...").tell(player);
        }
    }

    @Command(aliases = {"setwarp"}, perm = "fasttravel.command.setwarp")
    public void setWarp(@Caller Player player, @Join String name) {
        Warp warp = new Warp(name, player.getLocation(), player.getRotation());
        FastTravel.instance().warpManager().add(warp);
        FastTravel.instance().warpManager().save(warp);
        messenger.info("Created warp ").stress(name).tell(player);
    }

    @Command(aliases = {"listwarps", "warps"}, perm = "fasttravel.command.listwarps")
    public void listWarps(@Caller Player player) {
        player.getInventory().offer(ItemStack.of(ItemTypes.BOOK, 1));
        messenger.info("Take this!").tell(player);
    }

    @Command(aliases = {"delwarp", "deletewarp"}, perm = "fasttravel.command.deletewarps")
    public void deleteWarp(@Caller CommandSource source, @Join String name) {
        Optional<Warp> warp = FastTravel.instance().warpManager().byName(name);
        if (warp.isPresent()) {
            FastTravel.instance().warpManager().remove(warp.get());
            FastTravel.instance().warpManager().delete(warp.get());
            messenger.info("Deleting warp ").stress(name).tell(source);
        } else {
            messenger.error("Could not find warp ").stress(name).tell(source);
        }
    }
}
