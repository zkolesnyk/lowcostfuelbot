import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

class Config {
    private static final String CONFIG_BOT_FILE = "./config/bot.properties";
    private static final String CONFIG_DB_FILE = "./config/db.properties";

    private static String BOT_NAME;
    private static String BOT_TOKEN;

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASS;

    static void load() {
        Properties botSettings = new Properties();

        try (InputStream is = new FileInputStream(new File(CONFIG_BOT_FILE))) {
            botSettings.load(is);
            is.close();
            System.out.println("Настройки бота успешно загружены");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки настроек бота");
        }

        Properties dbSettings = new Properties();

        try (InputStream is = new FileInputStream(new File(CONFIG_DB_FILE))) {
            dbSettings.load(is);
            is.close();
            System.out.println("Настройки базы данных успешно загружены");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки настроек базы данных");
        }

        BOT_NAME = botSettings.getProperty("botName", "Meow..meeow..");
        BOT_TOKEN = botSettings.getProperty("botToken", "Ha-ha-ha");

        DB_URL = dbSettings.getProperty("dbUrl", "url");
        DB_USER = dbSettings.getProperty("dbUser", "user");
        DB_PASS = dbSettings.getProperty("dbPass", "pass");
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPass() {
        return DB_PASS;
    }

    public static String getBotName() {
        return BOT_NAME;
    }

    public static String getBotToken() {
        return BOT_TOKEN;
    }
}
