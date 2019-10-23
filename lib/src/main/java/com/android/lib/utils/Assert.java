package com.android.lib.utils;

/**
 * @Description:
 * @Author: zhupeng
 * @Copyright: 浙江集商优选电子商务有限公司
 * @CreateDate: 2019/10/23 11:01
 * @Version: 1.0.0
 */
public class Assert {

    public static void assertNull(Object obj) {
        if (null == obj) {
            throw new AssertionError("The passed argument cannot be null");
        }
    }
}
