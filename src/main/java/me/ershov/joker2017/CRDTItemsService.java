package me.ershov.joker2017;

import com.basho.riak.client.api.cap.BasicVClock;
import com.basho.riak.client.api.commands.datatypes.*;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.util.BinaryValue;
import me.ershov.joker2017.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static me.ershov.joker2017.RiakClientHolder.client;

/**
 * Created by andrershov on 21/02/2017.
 */
public class CRDTItemsService implements ItemsService {

    private final Namespace itemsNamespace = new Namespace("maps", "items");



    @Override
    public void updateItems(String id, ItemsRequest itemsRequest) throws ExecutionException, InterruptedException {
        if (itemsRequest.items.isEmpty()) {
            System.out.println("Nothing to store");
            return;
        }
        System.out.println("Storing CRDTs");

        MapUpdate mapUpdate = new MapUpdate();

        for (ItemRequest request: itemsRequest.items){
            if (request.text != null) storeItemText(id, request.id, request.text);
            MapUpdate attributesMapUpdate = new MapUpdate();
            if (request.done != null) attributesMapUpdate.update("done", new FlagUpdate(request.done));
            final SetUpdate tagsUpdate = new SetUpdate();
            if (request.tagsToAdd != null) request.tagsToAdd.forEach(tag -> tagsUpdate.add(BinaryValue.create(tag)));
            if (request.tagsToRemove != null) request.tagsToRemove.forEach(tag -> tagsUpdate.remove(BinaryValue.create(tag)));
            attributesMapUpdate.update("tags", tagsUpdate);
            mapUpdate.update(request.id, attributesMapUpdate);
        }
        Location location = new Location(itemsNamespace, id);

        UpdateMap.Builder builder = new UpdateMap.Builder(location, mapUpdate);

        UpdateMap um;

        if (itemsRequest.ctx != null) {
            um = builder.withContext(new Context(BinaryValue.create(itemsRequest.ctx))).build();
        } else {
            um = builder.build();
        }

        client.execute(um);
        System.out.println("Finished storing CRDTs");


    }

    private void storeItemText(String itemsId, String id, ItemRequestText text) throws ExecutionException, InterruptedException {
        Namespace textNamespace = new Namespace("siblings_allowed", itemsId);
        RiakObject obj = new RiakObject()
                .setContentType("text/plain")
                .setValue(BinaryValue.create(text.text));
        StoreValue.Builder builder =  new StoreValue.Builder(obj)
                .withLocation(new Location(textNamespace, id));

        StoreValue sv;

        if (text.vclock!=null) {
            sv = builder
                    .withVectorClock(new BasicVClock(text.vclock))
                    .build();
        } else  {
            sv = builder.build();
        }

        client.execute(sv);
    }


    @Override
    public ItemsResponse getItems(String id) throws ExecutionException, InterruptedException {
        Location location = new Location(itemsNamespace, id);
        FetchMap fetch = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(fetch);
        Context ctx = response.getContext();
        if (ctx == null) return new ItemsResponse();
        RiakMap itemsMap = response.getDatatype();
        Map<BinaryValue, List<RiakDatatype>> itemsMapView = itemsMap.view();
        List<Item> items = new ArrayList<>();
        for (BinaryValue itemId: itemsMapView.keySet()) {
            ItemText text = getItemText(id, itemId);
            if (text == null) continue;
            RiakMap attributes = itemsMapView.get(itemId).get(0).getAsMap();
            boolean done = attributes.getFlag("done").getEnabled();
            Set<String> tags = attributes.getSet("tags").view().stream().map(BinaryValue::toString).collect(Collectors.toSet());
            Item item = new Item(itemId.toString(), done, text, tags);
            items.add(item);
        }
        return new ItemsResponse(ctx, items);
    }

    private ItemText getItemText(String itemsId, BinaryValue itemId) throws ExecutionException, InterruptedException {
        Namespace textNamespace = new Namespace("siblings_allowed", itemsId);
        FetchValue fetch = new FetchValue.Builder(new Location(textNamespace, itemId)).build();
        FetchValue.Response response = client.execute(fetch);

        if (!response.hasValues()) {
            System.out.println("There is a reference in the map, but text item does not exist. Seems that there is network problem");
            return null;
        }
        List<RiakObject> objs = response.getValues(RiakObject.class);
        Set<String> textSet = objs.stream().map(RiakObject::getValue).map(BinaryValue::toString).collect(Collectors.toSet());
        return new ItemText(response.getVectorClock(), textSet);
    }
}
