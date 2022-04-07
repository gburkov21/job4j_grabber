package ru.job4j.cache;

public class Emulator {
    public static void main(String[] args) {
        DirFileCache dirFileCache = new DirFileCache("src/main/resources/cache_files/");

        String pathToNamesFile = dirFileCache.getCachingDir() + "Names.txt";
        String pathToAddressesFile = dirFileCache.getCachingDir() + "Address.txt";

        String namesFromFile = dirFileCache.get(pathToNamesFile);
        System.out.println(namesFromFile);
        String namesFromCache = dirFileCache.get(pathToNamesFile);
        System.out.println(namesFromCache);

        String addressesFromFile = dirFileCache.get(pathToAddressesFile);
        System.out.println(addressesFromFile);
        String addressesFromCache = dirFileCache.get(pathToAddressesFile);
        System.out.println(addressesFromCache);
    }
}
