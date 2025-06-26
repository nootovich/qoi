package nootovich.common;

import java.util.Stack;

public class BitAccumulator {

    public Stack<Boolean> data = new Stack<>();

    public void push(boolean n) {
        data.push(n);
    }

    public void push(boolean[] n) {
        for (int i = 0; i < n.length; i++) data.push(n[i]);
    }

    public void push(byte n) {
        data.push((n >> 7 & 1) == 1);
        data.push((n >> 6 & 1) == 1);
        data.push((n >> 5 & 1) == 1);
        data.push((n >> 4 & 1) == 1);
        data.push((n >> 3 & 1) == 1);
        data.push((n >> 2 & 1) == 1);
        data.push((n >> 1 & 1) == 1);
        data.push((n & 1) == 1);
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

    public void push(String n) {
        for (int i = 0; i < n.length(); i++) push((byte) n.charAt(i));
    }

    public byte[] getData() {
        while (data.size() % 8 != 0) data.push(false);
        byte[] result = new byte[data.size() / 8];
        for (int i = result.length - 1; i >= 0; i--) {
            for (int j = 0; j < 8; j++) result[i] |= (data.pop() ? 1 : 0) << j;//(byte) ((result[i] << 1) + (data.pop() ? 1 : 0));
        }
        return result;
    }
}
