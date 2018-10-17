package max.dirscan.scan

import max.dirscan.exceptions.InitException
import spock.lang.Specification


class DirScannerTest extends Specification {

    def "InitException если DirScanner стартанул до инициализации"() {
        given: "Неинициализированный DirScanner"
        DirScanner scanner = DirScanner.getScanner();

        when: "Пытаемся начать сканирование"
        scanner.startScan()

        then: "Ошибка инициализации"
        InitException ex = thrown()
        println ex.getMessage()
    }
}
