package com.benection.babymoment.api.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.benection.babymoment.api.util.GsonUtils.toJson;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
@Aspect
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private Map<Object, Object> params(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<Object, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            if (args[i] == null) {
                continue;
            }
//            System.out.println(args[i].getClass());
//            System.out.println(args[i].getClass().getName());
            if (Objects.equals(args[i].getClass().getName(), "org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile")) { // multipart일 경우 toString()으로 처리한다.
                params.put(parameterNames[i], args[i].toString());
            } else {
                params.put(parameterNames[i], args[i]);
            }
        }

        return params;
    }

    private String getRequestPath(JoinPoint joinPoint, Class<?> clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping.value()[0];
        return Stream.of(GetMapping.class, PutMapping.class, PostMapping.class,
                        PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(method::isAnnotationPresent)
                .map(mappingClass -> getPath(method, mappingClass, baseUrl))
                .findFirst().orElse(null);
    }

    /**
     * method, url
     */
    private String getPath(Method method, Class<? extends Annotation> annotationClass, String baseUri) {
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod;
        try {
            value = (String[]) annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            value = new String[]{"?"};
            httpMethod = "?";
        }

        return String.format("%s %s%s", httpMethod, baseUri, value.length > 0 ? value[0] : "");
    }

    @Pointcut("within(com.benection.babymoment.api.controller..*)")
    public void onRequest() {
    }

    @Around("onRequest()")
    public Object requestLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = joinPoint.getTarget().getClass();
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Exception e) {
            logger.error("[requestLogging] Request path: " + getRequestPath(joinPoint, clazz));
            logger.error("[requestLogging] Request body: " + toJson(params(joinPoint)));
            throw e;
        }
    }
}
