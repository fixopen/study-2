package com.baremind;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.sql.Time;

/**
 * Created by User on 2016/10/21.
 */
public class TimeTypeAdapter implements JsonSerializer<java.sql.Time>{
    //private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public JsonElement serialize(Time time, Type type, JsonSerializationContext jsonSerializationContext) {
        System.out.println("时间：" + time);
        String TimeFormatAsString = time.toString();
        /*String TimeFormatAsString = "";*/
        //new JsonPrimitive(TimeFormatAsString)
        return new JsonPrimitive(TimeFormatAsString);
    }
}