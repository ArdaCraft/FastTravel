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

import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

class WarpBookBuilder {

    private final List<Text> pages = new ArrayList<>();
    private Text.Builder builder = Text.builder();
    private int lines = 0;

    WarpBookBuilder add(Stream<WarpManager.Entry> stream, Predicate<Warp> filter) {
        stream.forEach(e -> add(e.getDisplayText(filter)));
        return this;
    }

    BookView build() {
        if (lines > 0) {
            pages.add(builder.build());
        }
        return BookView.builder()
                .title(Text.builder("Warps").color(TextColors.DARK_AQUA).build())
                .author(Text.of("FastTravel"))
                .addPages(pages)
                .build();
    }

    private void add(Text text) {
        if (lines > 13) {
            lines = 0;
            pages.add(builder.build());
            builder = Text.builder();
        } else if (lines != 0) {
            builder.append(Text.NEW_LINE);
        }
        builder.append(text);
        lines++;
    }
}
