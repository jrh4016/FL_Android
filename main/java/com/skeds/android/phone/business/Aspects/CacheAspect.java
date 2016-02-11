package com.skeds.android.phone.business.Aspects;

/**
 * Created by Mikhail on 31.07.2014.
 */
//@Aspect
public class CacheAspect {
//    private static final String TAG = "cacheAspect";
//
//    IResponseCache myResponseCache;
//    //@Pointcut("execution(@Cacheable * * *(..)")
//    @Pointcut("execution(public * *(..))")
//    public void cacheAnnotation() {
//    }
//
//    @Around("cacheAnnotation()")
//    public Object cacheAnnotation(JoinPoint joinPoint) {
//
//        Object rez = null;
//        Signature signature = joinPoint.getSignature();
//        Object[] args = joinPoint.getArgs();
//
//        StringBuilder key = new StringBuilder();
//        key.append(signature.getName());
//        for (Object arg : args) {
//            key.append(arg.toString());
//        }
//
//        try {
//            rez = myResponseCache.getObject(key.toString());
//        } catch (NotFoundValueExeption exeption) {
//            try {
//                Object proceed = ((ProceedingJoinPoint) joinPoint).proceed();
//                myResponseCache.putObject(key.toString(),proceed);
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//        }
//
//        Log.i(TAG, "AspectJ was here");
//        return rez;
//    }
}