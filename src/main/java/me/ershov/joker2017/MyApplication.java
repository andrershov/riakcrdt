package me.ershov.joker2017;

import me.ershov.joker2017.entities.Item;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by andrershov on 21/02/2017.
 */
public class MyApplication extends ResourceConfig{
    public MyApplication(){
        register(ItemsEndpoint.class);
        register(JacksonFeature.class);
        register(MyObjectMapperProvider.class);
        register(CORSFilter.class);
    }
}
