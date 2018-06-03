package Data.Helpers;

import java.util.Arrays;

public class ArrayHelpers {

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] combined = new byte[a.length + b.length];
        for (int i = 0; i < combined.length; ++i)
        {
            combined[i] = i < a.length ? a[i] : b[i - a.length];
        }
        return combined;
    }
}
