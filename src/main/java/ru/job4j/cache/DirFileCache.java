package ru.job4j.cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DirFileCache extends AbstractCache<String, String> {

    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    public String getCachingDir() {
        return cachingDir;
    }

    @Override
    protected String load(String key) {
        String result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(key))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            result = String.join("; ", list);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
