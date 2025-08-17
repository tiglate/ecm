package ludo.mentis.aciem.ecm.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Utility class for handling flash messages.
 * <p>
 * This class provides static methods to add success, info, and error messages to the redirect attributes.
 * The messages are displayed to the user after a redirect operation.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
public class FlashMessages {

    public static final String MSG_SUCCESS = "MSG_SUCCESS";
    public static final String MSG_INFO = "MSG_INFO";
    public static final String MSG_ERROR = "MSG_ERROR";

    /**
     * Private constructor to prevent instantiation
     */
    private FlashMessages() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Add a referenced warning message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param referencedBy the entity name that references the entity
     * @param id the id of the entity
     */
    public static void referencedWarning(RedirectAttributes redirectAttributes, String referencedBy, Integer id) {
        redirectAttributes.addFlashAttribute(MSG_ERROR, String.format("This entity is still referenced by %s %d via field Software.", referencedBy, id));
    }

    /**
     * Add a success message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param entityName the entity name
     */
    public static void createSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, String.format("%s was created successfully", entityName));
    }

    /**
     * Add a success message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param entityName the entity name
     */
    public static void updateSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, String.format("%s was updated successfully.", entityName));
    }

    /**
     * Add a success message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param entityName the entity name
     */
    public static void deleteSuccess(RedirectAttributes redirectAttributes, String entityName) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, String.format("%s was removed successfully.", entityName));
    }

    /**
     * Add an error message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param exception the exception
     */
    public static void error(RedirectAttributes redirectAttributes, Exception exception) {
        error(redirectAttributes, exception.getMessage());
    }

    /**
     * Add an error message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param errorMessage the error message
     */
    public static void error(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(MSG_ERROR, errorMessage);
    }

    /**
     * Add an info message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param infoMessage the info message
     */
    public static void info(RedirectAttributes redirectAttributes, String infoMessage) {
        redirectAttributes.addFlashAttribute(MSG_INFO, infoMessage);
    }

    /**
     * Add a success message to the redirect attributes
     *
     * @param redirectAttributes the redirect attributes
     * @param successMessage the success message
     */
    public static void success(RedirectAttributes redirectAttributes, String successMessage) {
        redirectAttributes.addFlashAttribute(MSG_SUCCESS, successMessage);
    }
}