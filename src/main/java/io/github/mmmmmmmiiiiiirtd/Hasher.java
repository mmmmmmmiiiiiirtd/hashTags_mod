package io.github.mmmmmmmiiiiiirtd;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.*;

import java.lang.ref.ReferenceQueue;
import java.util.*;

public class Hasher {
    private static final Long2ObjectOpenHashMap<TagEntry> hashTag = new Long2ObjectOpenHashMap<>();

    private final ReferenceQueue<CompoundTag> queue = new ReferenceQueue<>();

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

    public static long registerTag(CompoundTag tag) {
        long hash = calculateMapHash(toMap(tag));

        TagEntry entry = hashTag.get(hash);
        if (entry != null) {
            entry.refCount++;
        } else {
            hashTag.put(hash, new TagEntry(tag));
        }
        return hash;
    }

    public static long tagToHash(CompoundTag tag) {
        return calculateMapHash(toMap(tag));
    }

    public static CompoundTag hashToTag(long hash){
        TagEntry entry = hashTag.get(hash);
        return entry != null ? entry.tag : null;
    }

    public static void releaseTag(long hash) {
        TagEntry entry = hashTag.get(hash);
        if (entry != null) {
            entry.refCount--;
            if (entry.refCount <= 0) {
                hashTag.remove(hash);
            }
        }
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

    private static class TagEntry {
        final CompoundTag tag;
        int refCount;

        TagEntry(CompoundTag tag) {
            this.tag = tag;
            this.refCount = 1;
        }
    }
}