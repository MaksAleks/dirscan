package max.dirscan;

import max.dirscan.config.ApplicationConfig;
import max.dirscan.exceptions.InitException;
import max.dirscan.exceptions.ValidationParamsException;
import max.dirscan.input.InputParamsParser;
import max.dirscan.input.ParseResult;
import max.dirscan.output.FilesProcessor;
import max.dirscan.output.Timer;
import max.dirscan.scan.DirScanner;

import java.nio.file.Files;

//Singleton

/**
 * Главный класс приложения. С него начинается вся работа
 * Поведение приложения зависит от его конфигурации {@link max.dirscan.config.ApplicationConfig}
 *
 */
class Application {

    private boolean isInit = false;

    private ApplicationConfig config;

    private Timer timer = new Timer();

    private DirScanner scanner;

    private InputParamsParser paramsParser;

    private FilesProcessor processor;


    public void init(ApplicationConfig config) {
        this.config = config;

        paramsParser = config.inputParamsParser();
        paramsParser.registerExcluders(config.inputParamsExcluders());

        scanner = DirScanner.getScanner();

        processor = FilesProcessor.getProcessor();
        processor.init(config);

        isInit = true;
    }

    public void start(String... inputParams) {
        try {
            System.out.println("Application started");
            timer.start();
            if(!isInit) {
                throw new InitException("Cannot start application: application is not initialized");
            }
            Files.deleteIfExists(config.outputFilePath());
            processor.start();
            ParseResult result = paramsParser.parse(inputParams);
            scanner.init(result);
            scanner.scan();
            processor.waitForComplete();
        } catch (ValidationParamsException | InitException e) {
            System.out.println(e.getMessage());
            System.out.println("Shutting down the application");
        } catch (Exception e) {
            System.out.println("\nUnexpexpected error occured:");
            e.printStackTrace();
        } finally {
            long execTimeMs = timer.stop();
            System.out.println("\nApplication finished: execution time = " + execTimeMs + "ms");
            shutdown();
        }

    }

    private void shutdown() {
        processor.finish();
    }

    private Application() { }

    private static Application application = new Application();

    public static Application getApplication() {
        return application;
    }
}