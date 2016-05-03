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

package me.ardacraft.fasttravel.utils;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

@ConfigSerializable
public class Messenger {

    @Setting
    private TextColor textColor = TextColors.DARK_AQUA;
    @Setting
    private TextColor highlightColor = TextColors.DARK_PURPLE;
    @Setting
    private TextColor errorColor = TextColors.GRAY;
    @Setting
    private TextColor warnColor = TextColors.RED;

    private Text of(String messge, TextColor color) {
        return Text.builder(messge).color(color).build();
    }

    public Messenger textColor(TextColor color) {
        this.textColor = color;
        return this;
    }

    public Messenger stressColor(TextColor color) {
        this.highlightColor = color;
        return this;
    }

    public Messenger errorColor(TextColor color) {
        this.errorColor = color;
        return this;
    }

    public Messenger warnColor(TextColor color) {
        this.warnColor = color;
        return this;
    }

    public Builder info(Object input) {
        return build().info(input);
    }

    public Builder stress(Object input) {
        return build().stress(input);
    }

    public Builder error(Object input) {
        return build().error(input);
    }

    public Builder warn(Object input) {
        return build().warn(input);
    }

    public Builder append(Text input) {
        return build().append(input);
    }

    private Text infoText(String message) {
        return of(message, textColor);
    }

    private Text stressText(String message) {
        return of(message, highlightColor);
    }

    private Text errorText(String message) {
        return of(message, errorColor);
    }

    private Text warnText(String message) {
        return of(message, warnColor);
    }

    public Builder build() {
        return new Builder();
    }

    public class Builder {

        private final Text.Builder builder = Text.builder();

        public Builder info(Object input) {
            builder.append(Messenger.this.infoText(input.toString()));
            return this;
        }

        public Builder stress(Object input) {
            builder.append(Messenger.this.stressText(input.toString()));
            return this;
        }

        public Builder error(Object input) {
            builder.append(Messenger.this.errorText(input.toString()));
            return this;
        }

        public Builder warn(Object input) {
            builder.append(Messenger.this.warnText(input.toString()));
            return this;
        }

        public Builder append(Text text) {
            builder.append(text);
            return this;
        }

        public Text build() {
            return builder.build();
        }

        public Builder tell(MessageReceiver receiver) {
            receiver.sendMessage(build());
            return this;
        }

        public Builder tell(MessageReceiver... receivers) {
            for (MessageReceiver receiver : receivers) {
                tell(receiver);
            }
            return this;
        }
    }
}
