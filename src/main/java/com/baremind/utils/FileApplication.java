package com.baremind.utils;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fixopen on 30/8/2016.
 */
public class FileApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register resources and features
        classes.add(MultiPartFeature.class);
        //classes.add(MultiPartResource.class);
        //classes.add(LoggingFilter.class);
        return classes;
    }
}
