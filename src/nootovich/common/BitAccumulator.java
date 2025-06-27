package nootovich.common;

import java.util.ArrayDeque;
import java.util.Deque;

public class BitAccumulator {

    public Deque<Boolean> data = new ArrayDeque<>();

    public void push(boolean n) {
        data.add(n);
    }

    public void push(boolean[] n) {
        for (int i = 0; i < n.length; i++) push(n[i]);
    }

    public void push(byte n) {
        push((n >> 7 & 1) == 1);
        push((n >> 6 & 1) == 1);
        push((n >> 5 & 1) == 1);
        push((n >> 4 & 1) == 1);
        push((n >> 3 & 1) == 1);
        push((n >> 2 & 1) == 1);
        push((n >> 1 & 1) == 1);
        push((n & 1) == 1);
    }

    public void push(byte[] n) {
        for (int i = 0; i < n.length; i++) push(n[i]);
    }

    public void push(char n) {
        push((byte) n);
    }

    public void push(int n) {
        push((byte) (n >> 24 & 0xFF));
        push((byte) (n >> 16 & 0xFF));
        push((byte) (n >> 8 & 0xFF));
        push((byte) (n & 0xFF));
    }

    public byte peekFirstByte() {
        Boolean[] a      = data.toArray(new Boolean[]{ });
        byte      result = 0;
        for (int i = 0; i < 8; i++) {
            result = (byte) ((result << 1) | (a[i] ? 1 : 0));
        }
        return result;
    }

    public void push(String n) {
        for (int i = 0; i < n.length(); i++) push((byte) n.charAt(i));
    }

    public byte pollByte() {
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            result = (byte) ((result << 1) | (data.poll() ? 1 : 0));
        }
        return result;
    }

    public char pollChar() {
        return (char) pollByte();
    }

    public String pollString(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(pollChar());
        return sb.toString();
    }

    public int poll(int len) {
        int result = 0;
        for (int i = 0; i < len; i++) result = result << 1 | (data.poll() ? 1 : 0);
        return result;
    }

    public byte[] getData() {
        while (data.size() % 8 != 0) push(false);
        byte[] result = new byte[data.size() / 8];
        for (int i = 0; i < result.length; i++) result[i] = pollByte();
        return result;
    }
}
