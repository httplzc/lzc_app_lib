package pers.lizechao.android_lib.support.protocol.base;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lzc on 2018/6/12 0012.
 */
public class StubUtil {
    public static List<Method> getMethodList(Class interfaceClass) {
        Method methods[] = interfaceClass.getMethods();
        Arrays.sort(methods, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return Arrays.asList(methods);
    }
}
