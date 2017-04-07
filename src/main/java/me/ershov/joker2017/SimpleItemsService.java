package me.ershov.joker2017;

import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.commands.indexes.SecondaryIndexQuery;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ershov.joker2017.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static me.ershov.joker2017.RiakClientHolder.client;

/**
 * Created by andrershov on 21/02/2017.
 */
public class SimpleItemsService implements ItemsService {

    private ObjectMapper mapper = new ObjectMapper();
    private Namespace itemsNamespace = new Namespace("simple_todo", "simple_todo");

    @Override
    public void updateItems(String id, ItemsRequest itemsRequest) throws ExecutionException, InterruptedException {
        if (itemsRequest.items.isEmpty()) {
            System.out.println("Nothing to store");
            return;
        }
        System.out.println("Storing items");
        for (ItemRequest itemRequest: itemsRequest.items){
            Location location = new Location(itemsNamespace, itemRequest.id);
            Item item = getItem(location);
            if (item == null){
                item = new Item();
                item.id = itemRequest.id;
            }
            if (itemRequest.done!=null) item.done = itemRequest.done;
            if (itemRequest.text!=null) item.text = new ItemText(itemRequest.text);
            if (itemRequest.tagsToAdd != null) item.tags.addAll(itemRequest.tagsToAdd);
            if (itemRequest.tagsToRemove!=null) item.tags.removeAll(itemRequest.tagsToRemove);
            storeItem(id, location, item);
        }
        System.out.println("Store finished");
    }

    private void storeItem(String id, Location location, Item item) throws ExecutionException, InterruptedException {
        String str;
        try {
            str = mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        RiakObject obj = new RiakObject()
                .setContentType("text/plain")
                .setValue(BinaryValue.create(str));

        obj.getIndexes().getIndex(StringBinIndex.named("listId")).add(id);
        StoreValue sv =  new StoreValue.Builder(obj)
                .withLocation(location)
                .build();
        System.out.println(str);
        client.execute(sv);
    }

    private Item getItem(Location l) throws ExecutionException, InterruptedException {
        FetchValue fetch = new FetchValue.Builder(l).build();
        FetchValue.Response response = client.execute(fetch);
        if (!response.hasValues()) return null;
        String str = response.getValue(RiakObject.class).getValue().toString();
        System.out.println(str);
        try {
            return mapper.readValue(str, Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemsResponse getItems(String id) throws ExecutionException, InterruptedException {
        System.out.println("Fetching items");

        List<Item> items = new ArrayList<>();
        BinIndexQuery biq = new BinIndexQuery.Builder(itemsNamespace, "listId", id).build();
        BinIndexQuery.Response response = client.execute(biq);
        List<SecondaryIndexQuery.Response.Entry<String>> entries = response.getEntries();
        for (SecondaryIndexQuery.Response.Entry<String> entry:entries){
            Location l = entry.getRiakObjectLocation();
            items.add(getItem(l));
        }

        System.out.println("Items fetched");
        return new ItemsResponse(items);

    }
}
