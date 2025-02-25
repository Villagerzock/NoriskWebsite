package com.example.application.NoriskAPI;

import com.google.gson.JsonObject;

public interface ISerializer<T extends IAmSerializable<T>> {
    T read(JsonObject object, MinecraftPlayer player);
}
