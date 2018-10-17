package max.dirscan;

import max.dirscan.config.ApplicationConfig;
import max.dirscan.config.DefaultApplicationConfig;

public class Main {

    public static void main(String[] args) {

        Application application = Application.getApplication();
        ApplicationConfig config = new DefaultApplicationConfig();
        application.init(config);
        application.start(args);
    }

}