package com.project.sfoc.exception;

import java.util.List;

public record DiscordMessage(
        String content,
        List<Embed> embeds
) {

    public static DiscordMessage of(String content, List<Embed> embeds) {
        return new DiscordMessage(content, embeds);
    }

    public record Embed(
            String title,
            String description
    ) {
        public static Embed of(String title, String description) {
            return new Embed(title, description);
        }
    }

}
