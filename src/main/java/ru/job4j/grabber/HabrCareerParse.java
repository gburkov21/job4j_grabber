package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws Exception {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(parser);
        List<Post> resultList = new ArrayList<>();
        for (int pageNumber = 1; pageNumber <= 5; pageNumber++) {
            List<Post> list = habrCareerParse.list(PAGE_LINK + pageNumber);
            resultList.addAll(list);
        }
    }

    private static String retrieveDescription(String link) {
        String resultDescription = "";
        Connection connection = Jsoup.connect(link);
        try {
            Document document = connection.get();
            resultDescription = document.select(".job_show_description__body").first().text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDescription;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new ArrayList<>();
        Connection connection = Jsoup.connect(link);
        try {
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                result.add(parsePageAndGetPost(row));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Post parsePageAndGetPost(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement = row.select(".vacancy-card__date").first();
        Element dateLinkElement = dateElement.child(0);
        String vacancyName = titleElement.text();
        String dateAttribute = dateLinkElement.attr("datetime");
        LocalDateTime dateTime = dateTimeParser.parse(dateAttribute);
        String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String description = retrieveDescription(vacancyLink);
        return new Post(vacancyName, vacancyLink, description, dateTime);
    }
}
