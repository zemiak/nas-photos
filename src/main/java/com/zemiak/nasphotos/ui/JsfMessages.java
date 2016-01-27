package com.zemiak.nasphotos.ui;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public final class JsfMessages {
    public static boolean isValidationFailed() {
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    public static void addErrorMessage(Exception ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(ex.getClass().getName());
        }
    }

    public static void addErrorMessages(List<String> messages) {
        messages.stream().forEach((message) -> {
            addErrorMessage(message);
        });
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("jsf_infoMessages", facesMsg);
    }
}
