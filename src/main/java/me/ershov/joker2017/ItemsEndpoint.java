package me.ershov.joker2017;

import me.ershov.joker2017.entities.ItemsRequest;
import me.ershov.joker2017.entities.ItemsResponse;

import javax.ws.rs.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by andrershov on 21/02/2017.
 */
@Path("/items")
public class ItemsEndpoint {
    private ItemsService service;

    public ItemsEndpoint(){
        service = System.getenv("riakmode").equals("crdt") ? new CRDTItemsService() : new SimpleItemsService();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public ItemsResponse getItems(@PathParam("id") String id) throws Exception {
        return service.getItems(id);
    }

    @POST
    @Path("{id}")
    @Consumes("application/json")
    public void updateItems(@PathParam("id") String id, ItemsRequest request) throws ExecutionException, InterruptedException {
        service.updateItems(id, request);
    }

}
