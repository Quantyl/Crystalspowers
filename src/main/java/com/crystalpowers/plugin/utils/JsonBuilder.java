package com.crystalpowers.plugin.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;

public class JsonBuilder {
    private final TextComponent component;
    
    private JsonBuilder(String text) {
        this.component = new TextComponent(text);
    }
    
    public static JsonBuilder create() {
        return new JsonBuilder("");
    }
    
    public static JsonBuilder create(String text) {
        return new JsonBuilder(text);
    }
    
    public JsonBuilder text(String text) {
        component.setText(text);
        return this;
    }
    
    public JsonBuilder color(ChatColor color) {
        component.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.getChar()));
        return this;
    }
    
    public JsonBuilder bold() {
        component.setBold(true);
        return this;
    }
    
    public JsonBuilder underline() {
        component.setUnderlined(true);
        return this;
    }
    
    public JsonBuilder italic() {
        component.setItalic(true);
        return this;
    }
    
    public JsonBuilder clickEvent(ClickEvent.Action action, String value) {
        component.setClickEvent(new ClickEvent(action, value));
        return this;
    }
    
    public JsonBuilder hoverEvent(String text) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(text).create()));
        return this;
    }
    
    @Override
    public String toString() {
        return ComponentSerializer.toString(component);
    }
}
