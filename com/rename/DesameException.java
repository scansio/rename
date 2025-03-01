package com.rename;

public class DesameException extends Exception{
    public DesameException() {
        super();
    }

    @Override
    public String getMessage() {
        return "Error says comparison is the same";
    }
}
