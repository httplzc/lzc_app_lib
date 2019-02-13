package pers.lizechao.android_lib.support.aop.aspect;

import pers.lizechao.android_lib.support.log.LogRecorder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created by Lzc on 2018/5/18 0018.
 */
@Aspect
public class LogAspect {
    @Before("newCall(* com.google.devtools.build.android.desugar.runtime.ThrowableExtension.printStackTrace(..))")
    public void catchException(JoinPoint joinPoint) {
        if (joinPoint.getArgs().length != 1)
            return;
        Exception exception = (Exception) joinPoint.getArgs()[0];
        LogRecorder.getInstance().writeNormalException(exception);
    }


    @After("execution(* android.support.v4.app.FragmentActivity.onCreate(..))")
    public void activityCreate(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onCreate");
    }

    @After("execution(* android.support.v4.app.FragmentActivity.onResume(..))")
    public void activityStart(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onResume");

    }

    @After("execution(* android.support.v4.app.FragmentActivity.onPause(..))")
    public void activityStop(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onPause");

    }

    @After("execution(* android.support.v4.app.FragmentActivity.onDestroy(..))")
    public void activityDestroy(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onDestroy");
        LogRecorder.getInstance().endOperationHistory(joinPoint.getTarget());
    }


    @After("execution(* *..*Fragment.onCreateView(..))")
    public void fragmentCreateView(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onCreateView");

    }

    @After("execution(* android.support.v4.app.Fragment.onDestroyView(..))")
    public void fragmentDestroyView(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onDestroyView");

    }

    @After("execution(* android.support.v4.app.Fragment.onCreate(..))")
    public void fragmentCreate(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onCreate");

    }

    @After("execution(* android.support.v4.app.Fragment.onDestroy(..))")
    public void fragmentDestroy(JoinPoint joinPoint) {
        LogRecorder.getInstance().addOperationHistory(joinPoint.getTarget(), "onDestroy");
        LogRecorder.getInstance().endOperationHistory(joinPoint.getTarget());
    }

}



