package max.dirscan.sort;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalTwoWaySort {

    public static long mergeSortedFiles(List<File> files, File outputFile,
                                        final Comparator<String> comparator,
                                        Charset cs,
                                        boolean append) throws IOException {
        ArrayList<BinaryFileBuffer> bfbs = new ArrayList<>();
        for (File f : files) {
            InputStream in = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, cs));

            BinaryFileBuffer bfb = new BinaryFileBuffer(br);
            bfbs.add(bfb);
        }
        BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile, append), cs));
        long rowcounter = mergeSortedFiles(fbw, comparator, bfbs);
        for (File f : files) {
            f.delete();
        }
        return rowcounter;
    }

    public static long mergeSortedFiles(BufferedWriter fbw,
                                        final Comparator<String> comparator,
                                        List<BinaryFileBuffer> buffers) throws IOException {

        PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<>(
                11, new Comparator<BinaryFileBuffer>() {
            @Override
            public int compare(BinaryFileBuffer i,
                               BinaryFileBuffer j) {
                return comparator.compare(i.peek(), j.peek());
            }
        });
        for (BinaryFileBuffer bfb : buffers) {
            if (!bfb.empty()) {
                pq.add(bfb);
            }
        }
        long rowcounter = 0;
        try {
            while (pq.size() > 0) {
                BinaryFileBuffer bfb = pq.poll();
                String r = bfb.pop();
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
