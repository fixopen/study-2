package com.baremind;

import com.baremind.data.Device;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("devices")
public class Devices {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Device.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Device.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Device entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Device newData) {
         return Impl.updateById(sessionId, id, newData, Device.class, (exist, device) -> {
            Long userId = device.getUserId();
            if (userId != null) {
                exist.setUserId(userId);
            }

            String platform = device.getPlatform();
            if (platform != null) {
                exist.setPlatform(platform);
            }

            String platformIdentity = device.getPlatformIdentity();
            if (platformIdentity != null) {
                exist.setPlatformIdentity(platformIdentity);
            }

            String platformNotificationToken = device.getPlatformNotificationToken();
            if (platformNotificationToken != null) {
                exist.setPlatformNotificationToken(platformNotificationToken);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Device.class);
    }
}
