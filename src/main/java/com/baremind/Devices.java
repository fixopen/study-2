package com.baremind;

import com.baremind.data.Device;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("devices")
public class Devices {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDevice(@CookieParam("sessionId") String sessionId, Device device) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            device.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(device);
            result = Response.ok(device).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Device> devices = JPAEntry.getList(Device.class, filterObject);
            if (!devices.isEmpty()) {
                result = Response.ok(new Gson().toJson(devices)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Device device = JPAEntry.getObject(Device.class, "id", id);
            if (device != null) {
                result = Response.ok(new Gson().toJson(device)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDevice(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Device device) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Device existdevice = JPAEntry.getObject(Device.class, "id", id);
            if (existdevice != null) {
                String platform = device.getPlatform();
                if (platform != null) {
                    existdevice.setPlatform(platform);
                }

                Long userId = device.getUserId();
                if (userId != null) {
                    existdevice.setUserId(userId);
                }

                String platformIdentity = device.getPlatformIdentity();
                if (platformIdentity != null) {
                    existdevice.setPlatformIdentity(platformIdentity);
                }

                String platformNotificationToken = device.getPlatformNotificationToken();
                if (platformNotificationToken != null) {
                    existdevice.setPlatformNotificationToken(platformNotificationToken);
                }
                JPAEntry.genericPut(existdevice);
                result = Response.ok(existdevice).build();
            }
        }
        return result;
    }
}
