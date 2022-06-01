package com.taskmanager.exceptions;

public class SizeOverflowException extends RuntimeException{
    public SizeOverflowException() {
        super("Size overflow for task manager");
    }
}
