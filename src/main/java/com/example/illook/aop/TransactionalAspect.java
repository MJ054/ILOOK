package com.example.illook.aop;

import com.example.illook.model.Image;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Aspect
@Component
public class TransactionalAspect {


    @AfterThrowing("@annotation(org.springframework.transaction.annotation.Transactional)" +
            " && execution(* com.example.illook.service.PostService.createImage(..))")
    public void deleteImage(ProceedingJoinPoint joinPoint) throws Throwable {
           ArrayList<Image> images = (ArrayList<Image>) joinPoint.proceed();

           for(Image image : images){
               File file = new File(image.getPath());
               file.delete();
           }
    }

}
