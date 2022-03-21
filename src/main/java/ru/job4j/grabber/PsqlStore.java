package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Properties properties;
    private Connection cnn;

    public PsqlStore(Properties cfg) throws Exception {
        this.properties = cfg;
        this.cnn = getConnection();
    }

    private Connection getConnection() throws Exception {
        Class.forName(properties.getProperty("jdbc.driver"));
        String url = properties.getProperty("jdbc.url");
        String login = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        return DriverManager.getConnection(url, login, password);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream resource = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            properties.load(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement("insert into post (name, text, link, created) values (?, ?, ?, ?);")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> resultList = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post;")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                resultList.add(getPostFromResultSet(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("text"),
                resultSet.getString("link"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPostFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws Exception {
        PsqlStore psqlStore = new PsqlStore(loadProperties());
        Post post = new Post(
                "Java - программист",
                "www.test-career.ru/12345",
                "Ищем java-программиста",
                LocalDateTime.now());
        Post secondPost = new Post(
                "Python - программист",
                "www.test-career.ru/54321",
                "Ищем python-программиста",
                LocalDateTime.now());
        psqlStore.save(post);
        psqlStore.save(secondPost);
        System.out.println(psqlStore.findById(1));
        List<Post> list = psqlStore.getAll();
        System.out.println(list);
    }
}
