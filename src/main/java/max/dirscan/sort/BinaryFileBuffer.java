package max.dirscan.sort;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Класс обертка для BufferedReader
 */
public final class BinaryFileBuffer {

    public BufferedReader fbr;

    private String cache;

    public BinaryFileBuffer(BufferedReader r) throws IOException {
        this.fbr = r;
        reload();
    }

    public void close() throws IOException {
        this.fbr.close();
    }

    public boolean empty() {
        return this.cache == null;
    }

    // Возвратить текущую строчку
    public String peek() {
        return this.cache;
    }

    // Вытянуть новую строчку и возвратить старую
    public String pop() throws IOException {
        String answer = peek();// make a copy
        reload();
        return answer;
    }

    // получаем новую строчку из буфера
    private void reload() throws IOException {
        this.cache = this.fbr.readLine();
    }
}