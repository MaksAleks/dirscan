package max.dirscan.input

import max.dirscan.exceptions.ValidationParamsException
import max.dirscan.scan.filter.DirExcludeFilter
import max.dirscan.scan.filter.ExcludeFilter
import spock.lang.Specification

import java.nio.file.Paths

class DirExcluderTest extends Specification {

    def "Ошибка валидации входящих параметров если параметры после ключа '-' имеют некорректный формат"() {

        given: "DirExcluder"
        Excluder excluder = new DirExcluder()

        and: "Один или несколько входящих параметров после ключа '-' невалидны"
        String[] invalidFormatParams = ["/home/user/", "-", "/home/dir"]


        when: "DirExcluder парсит параметры для исключения директорий из сканирования"
        excluder.exclude(invalidFormatParams);

        then: "Ошибка валидации входных параметров - неправильный формат"
        ValidationParamsException ex = thrown()
        print(ex.getMessage())
    }

    def "Ошибка валидации если один из параметров - несуществующая директория"() {

        given: "DirExcluder"
        DirsValidator mock = Mock()
        DirExcluder excluder = new DirExcluder(mock)

        and: "директория для исключения из сканирования"
        String dir = "/home/dir/"

        and: "Все входящие параметры после ключа '-' валидны"
        String[] invalidFormatParams = ["/home/user", "-", dir, "/home/exists/"]

        and: "Директория /home/dir/ не существует"
        mock.isNotExists(Paths.get(dir)) >> true

        when: "DirExcluder парсит параметры для исключения директорий из сканирования"
        excluder.exclude(invalidFormatParams);

        then: "Ошибка валидации параметров - директория не сущесвтует"
        ValidationParamsException ex = thrown()
        print(ex.getMessage())

    }

    def "При валидных параметрах в результате получаем соответствующий ExcludeFilter"() {

        given: "DirExcluder"
        DirsValidator mock = Mock()
        DirExcluder excluder = new DirExcluder(mock)

        and: "директория для исключения из сканирования"
        String dir = "/home/dir/"

        and: "Директория /home/dir/ существует"
        mock.isExist(Paths.get(dir)) >> true

        and: "Все входящие параметры после ключа '-' валидны"
        String[] validParams = ["/home/user/", "/opt/test/", "-", dir]

        when: "DirExcluder парсит параметры для исключения директорий из сканирования"
        ExcludeFilter filter = excluder.exclude(validParams);

        then: "Получаем ExcludeFilter"
        filter instanceof DirExcludeFilter

        and: "В котором присутствуют директории для фильтрации"
        !filter.isEmpty()

        and: "и среди них есть директория /home/dir/"
        DirExcludeFilter dirFilter = (DirExcludeFilter)filter
        dirFilter.getDirsToFilter().contains(Paths.get(dir))

    }
}
