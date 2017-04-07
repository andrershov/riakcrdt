package me.ershov.joker2017.entities;

import com.basho.riak.client.api.cap.VClock;


/**
 * Created by andrershov on 21/02/2017.
 */
public class ItemRequestText {
    public byte[] vclock;
    public String text;
}
