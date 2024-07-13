package dev.toma.configuration.config.adapter;

public final class TypeMappers {

    public static TypeMapper<boolean[], Boolean[]> boolArrayRemapper() {
        return TypeMapper.of(arr -> {
            Boolean[] res = new Boolean[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            boolean[] res = new boolean[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<char[], Character[]> charArrayRemapper() {
        return TypeMapper.of(arr -> {
            Character[] res = new Character[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            char[] res = new char[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<byte[], Byte[]> byteArrayRemapper() {
        return TypeMapper.of(arr -> {
            Byte[] res = new Byte[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            byte[] res = new byte[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<short[], Short[]> shortArrayRemapper() {
        return TypeMapper.of(arr -> {
            Short[] res = new Short[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            short[] res = new short[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<int[], Integer[]> intArrayRemapper() {
        return TypeMapper.of(arr -> {
            Integer[] res = new Integer[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            int[] res = new int[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<long[], Long[]> longArrayRemapper() {
        return TypeMapper.of(arr -> {
            Long[] res = new Long[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            long[] res = new long[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<float[], Float[]> floatArrayRemapper() {
        return TypeMapper.of(arr -> {
            Float[] res = new Float[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            float[] res = new float[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }

    public static TypeMapper<double[], Double[]> doubleArrayRemapper() {
        return TypeMapper.of(arr -> {
            Double[] res = new Double[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        }, arr -> {
            double[] res = new double[arr.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = arr[i];
            }
            return res;
        });
    }
}
