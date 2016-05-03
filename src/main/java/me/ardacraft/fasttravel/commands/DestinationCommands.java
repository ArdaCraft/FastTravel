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

import com.flowpowered.math.vector.Vector3i;
import me.ardacraft.fasttravel.FastTravel;
import me.ardacraft.fasttravel.destination.Destination;
import me.ardacraft.fasttravel.destination.region.Point;
import me.ardacraft.fasttravel.utils.Messenger;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.Join;
import me.dags.commandbus.annotation.One;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.Collections;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class DestinationCommands {

    private final Messenger messenger = new Messenger();

    @Command(aliases = "create", parent = "destination")
    public void create(@Caller Player player, @One("radius") int radius, @Join String name) {
        Vector3i pos = player.getLocation().getBlockPosition();
        Destination destination = Destination.builder()
                .name(name)
                .point(new Point(pos.add(radius, radius, radius)))
                .point(new Point(pos.sub(radius, radius, radius)))
                .world(player.getWorld().getName())
                .build();
        messenger.info("Created destination ").stress(name).tell(player);
        messenger.info(destination);
        FastTravel.instance().destinationManager().add(destination);
        FastTravel.instance().destinationManager().save(destination);
    }

    @Command(aliases = "show", parent = "destination")
    public void show(@Caller Player player, @Join String name) {
        Optional<Destination> optional = FastTravel.instance().destinationManager().find(Collections.singletonList(name)).findFirst();
        if (optional.isPresent()) {
            messenger.info("Displaying destination ").stress(name).tell(player);
            Destination destination = optional.get();

            BlockState state = BlockTypes.STAINED_GLASS.getDefaultState()
                    .with(Keys.DYE_COLOR, DyeColors.CYAN)
                    .orElse(BlockTypes.STAINED_GLASS.getDefaultState());

            int y = player.getLocation().getBlockY();
            for (int x = destination.getRegion().getMin().getX(); x <= destination.getRegion().getMax().getX(); x++) {
                for (int z = destination.getRegion().getMin().getZ(); z <= destination.getRegion().getMax().getZ(); z++) {
                    player.sendBlockChange(x, y, z, state);
                }
            }
        } else {
            messenger.error("Destination ").stress(name).error(" is not recognised!").tell(player);
        }
    }

    @Command(aliases = "explore")
    public void explore(@Caller Player player) {
        player.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
        FastTravel.instance().travellerManager().get(player).setExplorer(true);
        messenger.info("You are now exploring. You can only ")
                .stress("/warp").info(" to destinations you have discovered previously!").tell(player);
    }
}
