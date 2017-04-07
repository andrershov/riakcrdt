package me.ershov.joker2017.entities;

import java.util.Set;

/**
 * Created by andrershov on 21/02/2017.
 */
public class ItemRequest {
    public String id;
    public Boolean done;
    public ItemRequestText text;
    public Set<String> tagsToAdd;
    public Set<String> tagsToRemove;
}
