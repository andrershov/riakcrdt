package me.ershov.joker2017.entities;

import com.basho.riak.client.api.commands.datatypes.Context;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by andrershov on 21/02/2017.
 */
public class ItemsResponse {
    public byte[] ctx;
    public List<Item> items;

    public ItemsResponse(Context ctx, List<Item> items) {

        this.ctx = ctx.getValue().getValue();
        this.items = items;
    }

    public ItemsResponse(List<Item> items){
        this.items = items;
    }


    public ItemsResponse() {
    }
}
