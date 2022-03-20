package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);

            Properties properties = loadProperties();
            Connection connection = getConnection(properties);
            data.put("connection", connection);

            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            int rabbitInterval = Integer.parseInt(properties.getProperty("rabbit.interval"));
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(rabbitInterval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("jdbc.driver-class-name"));
        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        return DriverManager.getConnection(url, user, password);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream resource = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static class Rabbit implements Job {
        private int id;
        private LocalDateTime created = LocalDateTime.now();

        public Rabbit() {
            System.out.println(hashCode());
        }

        public Rabbit(int id, LocalDateTime created) {
            this.id = id;
            this.created = created;
            System.out.println(hashCode());
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            List<Long> store = (List<Long>) jobDataMap.get("store");
            store.add(System.currentTimeMillis());

            Connection connection = (Connection) jobDataMap.get("connection");
            try (PreparedStatement statement = connection.prepareStatement("insert into rabbit (created_date) values (?);")) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
