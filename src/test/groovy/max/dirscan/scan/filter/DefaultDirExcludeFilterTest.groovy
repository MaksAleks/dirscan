package max.dirscan.scan.filter

import max.dirscan.input.DirsValidator
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths


class DefaultDirExcludeFilterTest extends Specification {

    def "Если файл не является директорией, то он не фильтруется"() {

        given: "Файлы для фильтрации"
        String file = "/home/filter"
        Path filePath = Paths.get(file)

        and: "DefaultDirExcludeFilter"
        DirsValidator validator = Mock()
        List<Path> dirsToFilter = Mock()
        DirExcludeFilter filter = new DefaultDirExcludeFilter(dirsToFilter, validator)

        and: "Файл для фильтрации не является директорией"
        validator.isDirectory(filePath) >> false

        when: "Происходит попытка фильтрации входящего файла"
        boolean isFiltered = filter.filter(filePath)

        then: "Файл оказывается неотфильтрованным"
        !isFiltered
    }

    def "Если фильтр пустой, то директории не будут фильтроваться"() {
        given: "Директория для фильтрации"
        String dir = "/home/filter/"
        Path dirPath = Paths.get(dir)

        and: "пустой DefaultDirExcludeFilter"
        DirsValidator validator = Mock()
        List<Path> dirsToFilter = Collections.emptyList()
        DirExcludeFilter filter = new DefaultDirExcludeFilter(dirsToFilter, validator)

        when: "Происходит попытка фильтрации входящей директории"
        boolean isFiltered = filter.filterDir(dirPath)

        then: "Директория оказывается неотфильтрованной"
        !isFiltered
    }

    def "Если директория не содержится среди тех, которые нужно отфильтровать, то она не фильтруется"() {

        given: "Директории, которые должны фильтроваться"
        String dirToFilter = "/home/filter/"
        List<Path> dirsToFilter = Collections.singletonList(Paths.get(dirToFilter))


        and: "DefaultDirExcludeFilter c директориями для фильтрации"
        DirsValidator validator = Mock()
        DirExcludeFilter filter = new DefaultDirExcludeFilter(dirsToFilter, validator)

        and: "Входящая директория, которая не должна фильтроваться"
        String dir = "/home/dir/"
        Path dirPath = Paths.get(dir)

        when: "Происходит попытка фильтрации входяшей директории"
        boolean isFiltered = filter.filterDir(dirPath)

        then: "Директория оказываетя неотфильтрованной"
        !isFiltered


    }

    def "Если директория содержится среди тех, которые нужно отфильтровать, то она фильтруется"() {

        given: "Директории, которые должны фильтроваться"
        String dirToFilter = "/home/filter/"
        List<Path> dirsToFilter = Collections.singletonList(Paths.get(dirToFilter))


        and: "DefaultDirExcludeFilter c директориями для фильтрации"
        DirsValidator validator = Mock()
        DirExcludeFilter filter = new DefaultDirExcludeFilter(dirsToFilter, validator)

        and: "Входящая директория, которая должна отфильтроваться"
        String dir = dirToFilter
        Path dirPath = Paths.get(dir)

        when: "Происходит попытка фильтрации входяшей директории"
        boolean isFiltered = filter.filterDir(dirPath)

        then: "Директория оказываетя отфильтрованной"
        isFiltered


    }

    def "Если директория является дочерней для одной из тех, которые фильтруются, то она будет отфильтрована"() {

        given: "Директории, которые должны фильтроваться"
        String dirToFilter = "/home/filter/"
        List<Path> dirsToFilter = Collections.singletonList(Paths.get(dirToFilter))


        and: "DefaultDirExcludeFilter c директориями для фильтрации"
        DirsValidator validator = Mock()
        DirExcludeFilter filter = new DefaultDirExcludeFilter(dirsToFilter, validator)

        and: "Входящая директория, дочерняя для одной из тех, которые должны фильтроваться"
        String dir = dirToFilter + "child/"
        Path dirPath = Paths.get(dir)

        when: "Происходит попытка фильтрации входяшей директории"
        boolean isFiltered = filter.filterDir(dirPath)

        then: "Директория оказываетя отфильтрованной"
        isFiltered
    }
}
