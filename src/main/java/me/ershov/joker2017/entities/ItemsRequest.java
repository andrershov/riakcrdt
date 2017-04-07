package me.ershov.joker2017.entities;

import com.basho.riak.client.api.commands.datatypes.Context;

import java.util.List;

/**
 * Created by andrershov on 21/02/2017.
 */
public class ItemsRequest {
    public byte[] ctx;
    public List<ItemRequest> items;
}
