package max.dirscan.scan

import max.dirscan.config.ApplicationConfig
import max.dirscan.config.TestApplicationConfig
import max.dirscan.exceptions.InitException
import max.dirscan.input.ParseResult
import max.dirscan.output.FilesProcessor
import max.dirscan.scan.filter.ExcludeFilter
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

class DirScannerTest extends Specification {

    def "InitException если DirScanner стартанул до инициализации"() {
        given: "Неинициализированный DirScanner"
        DirScanner scanner = DirScanner.getScanner();

        when: "Пытаемся начать сканирование"
        scanner.scan()

        then: "Ошибка инициализации"
        InitException ex = thrown()
        println ex.getMessage()
    }

    def "Если в DirScanner передать существующие директории для сканирования, то на выходе будет отсортированный файл с информацией о найденных файлах"() {
        given: "Тестовая конфигурация приложения"
        ApplicationConfig config = new TestApplicationConfig();

        and: "Выходной файл"
        Path outputFile = Paths.get(config.outputFilePath())

        and: "Директория для сканирования"
        Path dirToScan = Paths.get("./test/scan/")
        Files.createDirectories(dirToScan.getParent())
        Files.createDirectories(dirToScan)

        and: "Файлы для сканирования"
        Path fileA = Paths.get("./test/scan/fileA.txt")
        Path fileB = Paths.get("./test/scan/fileB.txt")
        Path fileC = Paths.get("./test/scan/fileC.txt")
        Files.createFile(fileA)
        Files.createFile(fileB)
        Files.createFile(fileC)

        // Объект, который мы получаем после парсинга входящих параметров
        // Содержит директории для сканирования и фильтры
        and: "ParseResult"
        ParseResult parseResult = new ParseResult(
                Collections.singletonList(dirToScan),
                Collections.singletonList(ExcludeFilter.emptyFilter()) // Ничего не будем фильтровать
        )

        and: "DirScanner в который помещаем наш ParseResult"
        DirScanner scanner = DirScanner.getScanner()
        scanner.init(parseResult)

        and: "FileProcessor который будет обрабатывать найденные файлы"
        FilesProcessor processor = FilesProcessor.getProcessor()
        processor.init(config)
        processor.start()


        when: "Когда сканер отсканирует все переданные ему директории"
        scanner.scan()

        then: "Получаем непустой результирующий файл"
        Files.exists(outputFile)
        BasicFileAttributes attributes = Files.readAttributes(outputFile, BasicFileAttributes.class)
        attributes.size() > 0

        and: "Который отсортирован в алфавитном порядке"
        AlphabeticalOrderValidator.validate(outputFile.toFile().getAbsolutePath())

        cleanup:
        Files.deleteIfExists(fileA)
        Files.deleteIfExists(fileB)
        Files.deleteIfExists(fileC)

        Files.deleteIfExists(outputFile)
        Files.deleteIfExists(dirToScan)
        Files.deleteIfExists(dirToScan.getParent())
    }
}
