package max.dirscan;

import max.dirscan.config.ApplicationConfig;
import max.dirscan.config.DefaultApplicationConfig;

public class Main {

    public static void main(String[] args) {

        String[] testArgs = {
                "/home/maxim/",
                "-",
                "/home/maxim/.gradle/",
                "/home/maxim/.cache/"
        };

        String[] testArgs2 = {"/home/maxim/"};

        Application application = Application.getApplication();
        ApplicationConfig config = new DefaultApplicationConfig();
        application.init(config);
        application.start(testArgs);
    }

}