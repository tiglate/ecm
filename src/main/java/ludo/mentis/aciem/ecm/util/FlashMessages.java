package ludo.mentis.aciem.ecm.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SuppressWarnings("unused")
public class FlashMessages {

    public static final String MSG_SUCCESS = "MSG_SUCCESS";
    public static final String MSG_INFO = "MSG_INFO";
    public static final String MSG_ERROR = "MSG_ERROR";

    private FlashMessages() {
    }

    public static void referencedWarning(RedirectAttributes redirectAttributes, String referencedBy, Integer id) {
        redirectAttributes.addFlashAttribute(MSG_ERROR, "This entity is still referenced by %s %d via field Software.".formatted(referencedBy, id));
    }

    public static void createSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, "%s was created successfully".formatted(entityName));
    }

    public static void updateSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, "%s was updated successfully.".formatted(entityName));
    }

    public static void deleteSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, "%s was removed successfully.".formatted(entityName));
    }

    public static void error(RedirectAttributes redirectAttributes, Exception exception) {
        error(redirectAttributes, exception.getMessage());
    }

    public static void error(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(MSG_ERROR, errorMessage);
    }

    public static void info(RedirectAttributes redirectAttributes, String infoMessage) {
        redirectAttributes.addFlashAttribute(MSG_INFO, infoMessage);
    }

    public static void success(RedirectAttributes redirectAttributes, String successMessage) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, successMessage);
    }
}