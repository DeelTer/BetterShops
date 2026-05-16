package ru.deelter.bettershops.shop;

import net.kyori.adventure.text.Component;
import ru.deelter.bettershops.shop.product.IProduct;

import java.util.List;

public record Shop(String id, Component title, List<IProduct> products) {
}