package com;


public final class Singleton {

    private Singleton() {
    }

    /** 方法1：饿汉式 */
    private static final Singleton instance3 = new Singleton();

    public static Singleton getInstance() {
        return instance3;
    }

    /** 方法2：使用时创建 双重检查锁 */
    private volatile static Singleton instance = null;

    public static Singleton getInstance2() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    /** 方法3：静态内部类 */
    private static class Inner {
        private static final Singleton instance = new Singleton();
    }

    public static Singleton getInstance3() {
        return Inner.instance;
    }

    /** 方法4：枚举 */
    private enum SingletonHolder {
        INSTANCE;

        private final Singleton instance;

        SingletonHolder() {
            instance = new Singleton();
        }

        public Singleton getInstance() {
            return instance;
        }
    }

    public static Singleton getInstance4() {
        return SingletonHolder.INSTANCE.getInstance();
    }

}
