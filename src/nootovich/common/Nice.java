package nootovich.common;

public class Nice {

    public static final byte COLOR      = 0;
    public static final byte RUN        = 1;
    public static final byte RUN_CHUNK  = 2;
    public static final byte DIFF_SMALL = 3;
    public static final byte DIFF_LARGE = 4;

    public static final int       TAG_LEN        = 3;
    public static final int       TAG_DATA_LEN   = 8 - TAG_LEN;
    public static final int       TAG_MAX_DATA   = (int) Math.pow(2, TAG_DATA_LEN);
    public static final boolean[] TAG_COLOR      = toBits(COLOR, TAG_LEN); // bits
    public static final boolean[] TAG_RUN        = toBits(RUN, TAG_LEN); // bits
    public static final boolean[] TAG_RUN_CHUNK  = toBits(RUN_CHUNK, TAG_LEN); // bits
    public static final boolean[] TAG_DIFF_SMALL = toBits(DIFF_SMALL, TAG_LEN); // bits
    public static final boolean[] TAG_DIFF_LARGE = toBits(DIFF_LARGE, TAG_LEN); // bits

    public static boolean[] toBits(int n, int len) {
        boolean[] result = new boolean[len];
        for (int i = 0; i < len; i++) {
            result[i] = ((n >> (len - i - 1)) & 1) == 1;
        }
        return result;
    }
}
