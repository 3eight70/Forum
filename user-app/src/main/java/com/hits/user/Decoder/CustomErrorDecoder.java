//package com.hits.user.Decoder;
//
//import com.hits.user.Exceptions.InvalidTokenException;
//import com.hits.common.Exceptions.UnknownException;
//import feign.Response;
//import feign.codec.ErrorDecoder;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.naming.ServiceUnavailableException;
//
//@Component
//@Slf4j
//public class CustomErrorDecoder implements ErrorDecoder {
//    @Override
//    public Exception decode(String methodKey, Response response){
//        log.info("Error Decoder: {} {}", methodKey, response);
//
//        if (methodKey.contains("validateToken")){
//            return new InvalidTokenException("Токен не валиден");
//        }
//
//        return new ErrorDecoder.Default().decode(methodKey, response);
//    }
//}
