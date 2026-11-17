package com.auca.exceptions;

public class FormNavigationException extends RuntimeException {
    private final boolean cancel;

    public FormNavigationException(boolean cancel) {
        super(cancel ? "Cancelled" : "Back");
        this.cancel = cancel;
    }

    public boolean isCancel() { return cancel; }
}