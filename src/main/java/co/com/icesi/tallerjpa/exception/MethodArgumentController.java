package co.com.icesi.tallerjpa.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class MethodArgumentController{

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ex.getAllErrors().stream()
                .collect(
                        HashMap::new,
                        (m, e) -> m.put(((DefaultMessageSourceResolvable) e.getArguments()[0]).getDefaultMessage(), e.getDefaultMessage()),
                        HashMap::putAll
                );
    }

}
