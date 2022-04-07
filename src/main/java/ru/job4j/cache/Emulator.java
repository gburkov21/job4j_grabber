package ru.job4j.cache;

import java.nio.file.Path;
import java.util.Scanner;

public class Emulator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь до файла: ");
        String directory = scanner.nextLine();
        System.out.print("Введите имя файла и расширение: ");
        String fileName = scanner.nextLine();
        checkParameters(directory, fileName);
        DirFileCache dirFileCache = new DirFileCache(directory);

        String resultFromFile = dirFileCache.get(fileName);
        System.out.println(resultFromFile);
        String resultFromCache = dirFileCache.get(fileName);
        System.out.println(resultFromCache);
    }

    private static void checkParameters(String directory, String fileName) {
        if (!Path.of(directory).toFile().isDirectory()) {
            throw new IllegalArgumentException("Некорректный путь к директории: " + directory);
        }
        if (!Path.of(directory, fileName).toFile().isFile()) {
            throw new IllegalArgumentException("Отсутствует файл: " + fileName + " в директории: " + directory);
        }
    }
}
