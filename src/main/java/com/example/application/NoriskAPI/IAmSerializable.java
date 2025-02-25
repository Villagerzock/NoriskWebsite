package com.example.application.NoriskAPI;

public interface IAmSerializable<T extends IAmSerializable<T>> {
    ISerializer<T> getSerializer();
}
