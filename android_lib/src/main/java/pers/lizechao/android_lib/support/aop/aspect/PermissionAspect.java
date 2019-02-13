package pers.lizechao.android_lib.support.aop.aspect;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pers.lizechao.android_lib.support.aop.annotation.NeedPermission;
import pers.lizechao.android_lib.support.aop.manager.PermissionHelper;
import pers.lizechao.android_lib.utils.StrUtils;

/**
 * Created by Lzc on 2018/5/22 0022.
 */
@Aspect
public class PermissionAspect {
    @After("execution(* android.support.v4.app.FragmentActivity.onRequestPermissionsResult(..))")
    public void onRequestPermissionActivityBack(JoinPoint joinPoint) {
        Integer requestCode = (Integer) joinPoint.getArgs()[0];
        String[] permissions = (String[]) joinPoint.getArgs()[1];
        int[] grantResults = (int[]) joinPoint.getArgs()[2];
        PermissionHelper.getInstance().onPermissionBackDo(requestCode, permissions, grantResults);
    }

    //    //在带有DebugLog注解的方法
    @Around("execution(@pers.lizechao.android_lib.support.aop.annotation.NeedPermission * *(..))")
    public void needPermissionMethod(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        NeedPermission permission = method.getAnnotation(NeedPermission.class);
        AppCompatActivity activity = null;
        if (proceedingJoinPoint.getTarget() instanceof Activity)
            activity = (AppCompatActivity) proceedingJoinPoint.getTarget();
        else if (proceedingJoinPoint.getTarget() instanceof Fragment) {
            activity = (AppCompatActivity) ((Fragment) proceedingJoinPoint.getTarget()).getActivity();
        }
        if (permission != null && permission.permissions().length != 0 && permission.requestCode() != -1 && activity != null) {
            PermissionHelper.getInstance().request(activity, permission.requestCode(), permission.permissions(), new PermissionHelper.CallBack() {
                @Override
                public void succeed() {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void fail(String[] permissions) {
                    if (StrUtils.CheckNull(permission.failFunction()))
                        return;
                    try {
                        Method method = proceedingJoinPoint.getTarget().getClass().getMethod(permission.failFunction(), int.class, String[].class);
                        method.invoke(proceedingJoinPoint.getTarget(), permission.requestCode(), permissions);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
