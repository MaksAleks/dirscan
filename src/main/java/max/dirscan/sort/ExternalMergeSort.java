package max.dirscan.sort;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Класс, реализующий алгоритм слияния для расширенной сортировки слянием
 * Главный момент в том, что все входные файлы отсортированы
 */
public class ExternalMergeSort {

    /**
     * Слияние отсортированных файлов
     * @param files - отсортированные файлы
     * @param outputFile - выходной файл
     * @param comparator - компаратор строк (для сортировки)
     * @param cs - кодировка выходного и входных фалов
     * @param append
     * @return - возвращается количество строчек, записанных в выходной файл
     * @throws IOException
     */
    public long mergeSortedFiles(List<File> files, File outputFile,
                                        final Comparator<String> comparator,
                                        Charset cs,
                                        boolean append) throws IOException {
        ArrayList<BinaryFileBuffer> bfbs = new ArrayList<>();
        // Для каждого файла создаем обертку BinaryFileBuffer
        for (File f : files) {
            InputStream in = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, cs));

            BinaryFileBuffer bfb = new BinaryFileBuffer(br);
            bfbs.add(bfb);
        }
        // Создаем writer, который будет записывать строчки в выходной
        // файл в отсортированном порядке
        BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile, append), cs));
        long rowcounter = mergeSortedFiles(fbw, comparator, bfbs);
        for (File f : files) {
            f.delete();
        }
        return rowcounter;
    }

    /**
     * Реализация механизма слияния с помощью очереди с приоритетом
     * @param fbw - writer для выходного файла
     * @param comparator - компаратор строк для сортировки
     * @param buffers - обертки ридеров для входных отсортированных файлов
     * @return - возвращается количество строчек записанных в выходной файл
     * @throws IOException
     */
    private long mergeSortedFiles(BufferedWriter fbw,
                                        final Comparator<String> comparator,
                                        List<BinaryFileBuffer> buffers) throws IOException {

        // Создается очередь оберток для ридеров с приоритетом
        // Если текующая строка в обертке лексиграфически меньше
        // то она имеет более высокий приоритет
        PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<>(
                11, new Comparator<BinaryFileBuffer>() {
            @Override
            public int compare(BinaryFileBuffer i,
                               BinaryFileBuffer j) {
                return comparator.compare(i.peek(), j.peek());
            }
        });
        // добавляем все ридеры в очередь
        for (BinaryFileBuffer bfb : buffers) {
            if (!bfb.empty()) {
                pq.add(bfb);
            }
        }
        long rowcounter = 0;
        try {
            // основной цикл слияния
            while (pq.size() > 0) {
                // достаем самый приоритетный ридер (т.е. с самой маленькой текущей строчкой)
                BinaryFileBuffer bfb = pq.poll();
                // Получаем из ридера текущую строку
                // и читаем в него следующую из файла
                // (следующая становится текущей)
                String r = bfb.pop(); // кладем ридер обратно в очередь (его приоритет поменяется, т.к. текущая строчка уже другая)
                // Мы достали самую маленькую строчку из всех строк во всех файлах - пишем её в выходной файл
                // и т.д.
                fbw.write(r);
                fbw.newLine();
                ++rowcounter;
                if (bfb.empty()) {
                    bfb.fbr.close();
                } else {
                    pq.add(bfb); // add it back
                }
            }

        } finally {
            fbw.close();
            for (BinaryFileBuffer bfb : pq) {
                bfb.close();
            }
        }
        return rowcounter;

    }
}
