package max.dirscan.output

import max.dirscan.config.ApplicationConfig
import max.dirscan.config.TestApplicationConfig
import max.dirscan.exceptions.InitException
import spock.lang.Specification

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

class FilesProcessorTest extends Specification {

    def "InitException если FilesProcessor стартанул до инициализации"() {
        given: "Неинициализированный FilesProcessor"
        FilesProcessor processor = FilesProcessor.getProcessor();

        when: "Пытаемся стартануть процессор"
        processor.start()

        then: "Ошибка инициализации"
        InitException ex = thrown()
        println ex.getMessage()
    }

    def "Успешный старт если FilesProcessor инциализирован"() {

        given: "Тестовая конфигурация приложения"
        ApplicationConfig config = new TestApplicationConfig();

        and: "FileProcessor с этой конфигурацией"
        FilesProcessor processor = FilesProcessor.getProcessor();

        processor.init(config)

        when: "Пытаемся стартануть процессор"
        processor.start()

        then: "Процессор успешно стартанул"
        processor.isStared()
    }

    def "Если в FileProcessor передать на обработку существующий файл, то информация о нём попадет в результирующий файл"() {
        given: "Тестовая конфигурация приложения"
        ApplicationConfig config = new TestApplicationConfig();

        and: "Выходной файл"
        Path outputFile = config.outputFilePath()

        and: "Существующий файл для обработки"
        Path file = Paths.get("./test/test.txt").toAbsolutePath().normalize()
        Files.createDirectories(file.getParent())
        Files.createFile(file)

        and: "FileProcessor с тестовой конфигурацией"
        FilesProcessor processor = FilesProcessor.getProcessor();
        processor.init(config)

        and: "который уже начал обрабатывать файлы"
        processor.start()

        and: "форматированная строка с инфой об обрабатываемом файле"
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class)
        String formattedLine = config.fileFormatter().formatEntry(file, attrs);

        when: "Когда передаем на обработку существующий файл"
        processor.process(file.toFile().getAbsolutePath())

        and: "и завершаем работу процессора"
        processor.finish()
        processor.waitForComplete()

        then: "Получаем непустой результирующий файл"
        Files.exists(outputFile)
        BasicFileAttributes attributes = Files.readAttributes(outputFile, BasicFileAttributes.class)
        attributes.size() > 0

        and: "В котором записана информация о поступившем входном файле"
        byte[] result = Files.readAllBytes(outputFile)
        String resultString = new String(result, config.outputFileCharset())
        formattedLine.equals(resultString)

        cleanup:
        file.toFile().deleteOnExit()
        outputFile.toFile().deleteOnExit()
        file.getParent().toFile().deleteOnExit()
    }
}
