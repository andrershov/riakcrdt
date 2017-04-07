package me.ershov.joker2017.entities;

import com.basho.riak.client.api.cap.VClock;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrershov on 21/02/2017.
 */
public class ItemText {
    public byte[] vclock;
    public Set<String> text;

    public ItemText(VClock vclock, Set<String> textSet) {
        if (vclock != null)
            this.vclock = vclock.getBytes();
        else
            System.out.println("vclock is null for "+textSet);
        this.text = textSet;
    }

    public ItemText(ItemRequestText that) {
        this.vclock = that.vclock;
        this.text = new HashSet<>();
        text.add(that.text);
    }

    public ItemText(){

    }
}
