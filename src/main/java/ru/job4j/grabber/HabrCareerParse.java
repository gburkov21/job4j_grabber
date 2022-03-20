package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.time.LocalDateTime;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    public static void main(String[] args) throws Exception {
        for (int pageNumber = 1; pageNumber <= 5; pageNumber++) {
            Connection connection = Jsoup.connect(PAGE_LINK + pageNumber);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateLinkElement = dateElement.child(0);
                String vacancyName = titleElement.text();
                String dateAttribute = dateLinkElement.attr("datetime");
                LocalDateTime dateTime = parser.parse(dateAttribute);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%tF: %s %s%n", dateTime, vacancyName, link);
            }
        }
    }
}
