package pers.lizechao.android_lib.support.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.FieldSignature;

import pers.lizechao.android_lib.storage.db.Storage;
import pers.lizechao.android_lib.support.aop.annotation.AutoSave;
import pers.lizechao.android_lib.utils.StrUtils;

/**
 * Created by Lzc on 2018/5/22 0022.
 */
@Aspect
public class AutoSaveAspect {
    private static final String AUTO_SAVE_TAG = "AUTO_SAVE_TAG";

    @Around("get(@pers.lizechao.android_lib.support.aop.annotation.AutoSave *)")
    public Object beanGet(ProceedingJoinPoint joinPoint) {
        FieldSignature fieldSignature = (FieldSignature) joinPoint.getSignature();
        AutoSave autoSave = fieldSignature.getField().getAnnotation(AutoSave.class);
        if (autoSave == null || StrUtils.CheckNull(autoSave.key())) {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        } else {
            return Storage.getStaticInstance().load(fieldSignature.getField().getType(), AUTO_SAVE_TAG + autoSave.key());
        }
    }

    @Around("set(@pers.lizechao.android_lib.support.aop.annotation.AutoSave *) && !withincode(*.new(..))")
    public void beanSet(ProceedingJoinPoint joinPoint) {
        FieldSignature fieldSignature = (FieldSignature) joinPoint.getSignature();
        AutoSave autoSave = fieldSignature.getField().getAnnotation(AutoSave.class);
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (autoSave == null || StrUtils.CheckNull(autoSave.key())) {
            return;
        }
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length != 0) {
            Object data = joinPoint.getArgs()[0];
            if (data == null)
                return;
            Storage.getStaticInstance().store(data, AUTO_SAVE_TAG + autoSave.key());
        }

    }
}
