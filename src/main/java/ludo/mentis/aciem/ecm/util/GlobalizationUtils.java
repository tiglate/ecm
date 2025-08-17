package ludo.mentis.aciem.ecm.util;


import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;


/**
 * Utility class for handling globalization and localization tasks.
 * <p>
 * This class provides static methods to retrieve localized messages based on the current locale.
 * It uses a {@link MessageSource} to fetch messages and a {@link LocaleResolver} to determine the current locale.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
@Component
public class GlobalizationUtils {

    private static MessageSource messageSource;
    private static LocaleResolver localeResolver;

    GlobalizationUtils(final MessageSource messageSource, final LocaleResolver localeResolver) {
        GlobalizationUtils.messageSource = messageSource;
        GlobalizationUtils.localeResolver = localeResolver;
    }

    /**
     * Retrieves a localized message based on the provided message code and arguments.
     * <p>
     * This method uses the configured {@link MessageSource} to fetch the message corresponding
     * to the given code and formats it with the provided arguments. The current locale is determined
     * using the {@link LocaleResolver} and the {@link WebUtils#getRequest()} method.
     * </p>
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to format the message with
     * @return the localized message as a String
     */
    public static String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, code,
                localeResolver.resolveLocale(WebUtils.getRequest()));
    }
}