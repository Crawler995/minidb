package com.bit.exception;

/**
 * @author aerfafish
 * @date 2020/9/15 7:05 下午
 */
public class SameNameDatabaseException extends Exception {

    public SameNameDatabaseException(String message) {
        super(message);
    }
}
