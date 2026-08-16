package com.project.paperjackpot.util;

import org.bukkit.Material;

import java.util.*;

/**
 * WeightedRandom - Chọn vật phẩm ngẫu nhiên theo trọng số (weight).
 */
public class WeightedRandom {

    private final List<Entry> entries = new ArrayList<>();
    private double totalWeight = 0;
    private final Random random = new Random();

    public record Entry(Material material, double weight) {}

    public void addEntry(Material material, double weight) {
        entries.add(new Entry(material, weight));
        totalWeight += weight;
    }

    public Material roll() {
        double value = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (Entry entry : entries) {
            cumulative += entry.weight();
            if (value <= cumulative) {
                return entry.material();
            }
        }
        return entries.get(entries.size() - 1).material();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
}
