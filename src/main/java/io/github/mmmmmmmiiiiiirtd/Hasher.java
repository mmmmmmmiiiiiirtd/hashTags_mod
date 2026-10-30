package io.github.mmmmmmmiiiiiirtd;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.*;

import java.util.*;

public class Hasher {
    static Long2ObjectOpenHashMap<CompoundTag> hashTag = new Long2ObjectOpenHashMap<>();

    private static Map<String, Object> toMap(CompoundTag compound) {
        Map<String, Object> map = new HashMap<>();
        for (String key : compound.getAllKeys()) {
            Tag tag = compound.get(key);
            map.put(key, convertTag(tag));
        }
        return map;
    }

    private static Object convertTag(Tag tag) {
        if (tag instanceof CompoundTag) {
            return toMap((CompoundTag) tag);
        } else if (tag instanceof ListTag) {
            List<Object> list = new ArrayList<>();
            for (Tag listElement : (ListTag) tag) {
                list.add(convertTag(listElement));
            }
            return list;
        } else if (tag instanceof NumericTag) { // Byte, Short, Int, Long, Float, Double
            return ((NumericTag) tag).getAsNumber();
        } else if (tag instanceof StringTag) {
            return tag.getAsString();
        } else if (tag instanceof ByteArrayTag) {
            return ((ByteArrayTag) tag).getAsByteArray();
        } else if (tag instanceof IntArrayTag) {
            return ((IntArrayTag) tag).getAsIntArray();
        } else if (tag instanceof LongArrayTag) {
            return ((LongArrayTag) tag).getAsLongArray();
        }
        return tag.toString(); // На крайний случай
    }

    public static long tagToHash(CompoundTag tag){
        long hash = calculateMapHash(toMap(tag));
        if (!hashTag.containsKey(hash)){
            hashTag.put(hash, tag);
        }
        return hash;
    }

    public static CompoundTag hashToTag(long hash){
        return hashTag.get(hash);
    }

    private static final long calculateMapHash(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return 0L;
        }

        long totalHash = 1125899906842597L;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            long entryHash = java.util.Objects.hash(key, value);

            totalHash = totalHash * 31L + entryHash;
        }

        return totalHash;
    }
}
