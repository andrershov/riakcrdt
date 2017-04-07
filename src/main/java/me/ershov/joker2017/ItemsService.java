package me.ershov.joker2017;

import me.ershov.joker2017.entities.ItemsRequest;
import me.ershov.joker2017.entities.ItemsResponse;

import java.util.concurrent.ExecutionException;

/**
 * Created by andrershov on 21/02/2017.
 */
public interface ItemsService {
    void updateItems(String id, ItemsRequest itemsRequest) throws ExecutionException, InterruptedException;

    ItemsResponse getItems(String id) throws ExecutionException, InterruptedException;
}
