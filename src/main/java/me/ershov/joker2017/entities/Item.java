package me.ershov.joker2017.entities;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrershov on 21/02/2017.
 */
public class Item {
    public String id;
    public boolean done;
    public ItemText text;
    public Set<String> tags = new HashSet<>();

    public Item(String id, boolean done, ItemText text, Set<String> tags) {
        this.id = id;
        this.done = done;
        this.text = text;
        this.tags = tags;

    }

    public Item(){
    }
}
