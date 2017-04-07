package me.ershov.joker2017;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

/**
 * Created by andrershov on 21/02/2017.
 */
public class RiakClientHolder {

    public static RiakCluster setUpCluster() {
        RiakNode node = new RiakNode.Builder()
                .withRemoteAddress(System.getenv("riakhost"))
                .withRemotePort(8087)
                .build();

        RiakCluster cluster = new RiakCluster.Builder(node)
                .build();

        cluster.start();

        return cluster;
    }



    public static RiakClient client = new RiakClient(setUpCluster());

}
