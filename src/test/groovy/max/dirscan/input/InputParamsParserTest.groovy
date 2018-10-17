package max.dirscan.input

import max.dirscan.exceptions.ValidationParamsException
import max.dirscan.scan.filter.DefaultDirExcludeFilter
import max.dirscan.scan.filter.DirExcludeFilter
import max.dirscan.scan.filter.ExcludeFilter
import spock.lang.Specification

import java.nio.file.*


class InputParamsParserTest extends Specification {

    def "Ошибка валидации если среди входящих параметров присутствуют параметры с некорректным форматом"() {

        given: "DirExcluder"
        DirsValidator validator = Mock()
        Excluder excluder = new DirExcluder(validator)

        and: "InputParamsParser c зарегистрированным DirExcluder"
        InputParamsParser paramsParser = new InputParamsParser(validator)
        paramsParser.registerExcluder(excluder)

        and: "Входящие параметры, имеющие некорректный формат"
        String existingScanDir = "/home/scan/dir1/"
        String existingExcludeDir = "/home/exclude/dir/"

        validator.isExists(Paths.get(existingScanDir)) >> true
        validator.isExists(Paths.get(existingExcludeDir)) >> true

        String invalidParam = "/home/scan/dir2"

        String[] invalidParams = [existingScanDir, invalidParam, "-", existingExcludeDir]

        when: "Происходит попытка парсинга параметров"
        paramsParser.parse(invalidParams)

        then: "Получаем ошибку валидации - некорректный формат"
        ValidationParamsException ex = thrown()
        println ex.getMessage()
    }


    def "Ошибка валидации если среди входящих параметров присутствуют несуществующие директории"() {

        given: "DirExcluder"
        DirsValidator validator = Mock()
        Excluder excluder = new DirExcluder(validator)

        and: "InputParamsParser c зарегистрированным DirExcluder"
        InputParamsParser paramsParser = new InputParamsParser(validator)
        paramsParser.registerExcluder(excluder)

        and: "Входящие параметры, имеющие некорректный формат"
        String existingScanDir = "/home/scan/exists/"
        String existingExcludeDir = "/home/exclude/exists/"

        validator.isExists(Paths.get(existingScanDir)) >> true
        validator.isExists(Paths.get(existingExcludeDir)) >> true

        String nonExistingScanDir = "/home/scan/nonExists/"
        validator.isExists(Paths.get(nonExistingScanDir)) >> false


        String[] invalidParams = [existingScanDir, nonExistingScanDir, "-", existingExcludeDir]

        when: "Происходит попытка парсинга параметров"
        paramsParser.parse(invalidParams)

        then: "Получаем ошибку валидации - директория не существует"
        ValidationParamsException ex = thrown()
        println ex.getMessage()
    }

    def "Если все входящие параметры валидны, то после парсинга получаем соответствующий результат"() {
        given: "DirExcluder"
        DirsValidator validator = Mock()
        Excluder excluder = new DirExcluder(validator)

        and: "InputParamsParser c зарегистрированным DirExcluder"
        InputParamsParser paramsParser = new InputParamsParser(validator)
        paramsParser.registerExcluder(excluder)

        and: "Входящие параметры, имеющие корректный формат"
        String existingScanDir = "/home/scan/exists/"
        String existingExcludeDir = "/home/exclude/exists/"
        Path dirToScan = Paths.get(existingScanDir)
        Path dirToExclude = Paths.get(existingExcludeDir)
        validator.isExists(dirToScan) >> true
        validator.isExists(dirToExclude) >> true

        String[] validParams = [existingScanDir, "-", existingExcludeDir]

        when: "Происходит попытка парсинга параметров"
        ParseResult result = paramsParser.parse(validParams)

        then: "В результате получаем список директорий для сканирования"
        List<Path> dirsToScan = result.getDirsToScan();
        dirsToScan.contains(dirToScan)

        and: "и фильтр директорий, соответствующий данному DirExcluder"
        List<ExcludeFilter> filters = result.getFilters()
        filters.size() == 1 //Потому что у нас был только один Excluder. Каждому Excluder'у один фильтр
        ExcludeFilter filter = filters.get(0)
        filter instanceof DirExcludeFilter
        filter instanceof DefaultDirExcludeFilter

        and: "который содержит директории для фильтрации"
        DirExcludeFilter dirExcludeFilter = (DefaultDirExcludeFilter) filter
        dirExcludeFilter.getDirsToFilter().contains(dirToExclude)

    }
}
