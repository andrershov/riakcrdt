package me.ershov.joker2017;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by andrershov on 21/02/2017.
 */
/*

Bucket settings:
riak-admin bucket-type create maps '{"props":{"n_val":"3", "r":"2", "w":"2", "datatype":"map"}}'
riak-admin bucket-type activate maps
riak-admin bucket-type status maps

riak-admin bucket-type create siblings_allowed '{"props":{"n_val":3, "r":2, "w":2, "allow_mult":true}}'
riak-admin bucket-type activate siblings_allowed
riak-admin bucket-type status siblings_allowed

riak-admin bucket-type create simple_todo '{"props":{"n_val":3, "r":2, "w":2, "allow_mult":false, "last_write_wins":true}}'
riak-admin bucket-type activate simple_todo
riak-admin bucket-type status simple_todo

Network segmentation:
sudo iptables -A INPUT -p tcp -m multiport -s 192.168.2.36 ! --dport 22,8087 -j DROP
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(System.getenv("httpport"))).build();
        JettyHttpContainerFactory.createServer(baseUri, new MyApplication());
    }
}
