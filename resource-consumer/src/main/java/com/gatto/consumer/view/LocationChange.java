package com.gatto.consumer.view;

public record LocationChange(boolean changed, Double oldLat, Double oldLng, Double newLat, Double newLng) {}
