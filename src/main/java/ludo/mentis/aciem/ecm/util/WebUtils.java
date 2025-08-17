package ludo.mentis.aciem.ecm.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;


/**
 * Utility class for web-related operations.
 * <p>
 * This class provides static methods to perform common web-related tasks,
 * such as retrieving the current HttpServletRequest object.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
public class WebUtils {

    /**
     * Private constructor to prevent instantiation
     */
    private WebUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Retrieves the current HttpServletRequest object from the RequestContextHolder.
     *
     * @return the current HttpServletRequest object
     * @throws IllegalStateException if the request attributes are not available
     */
    public static HttpServletRequest getRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("RequestContextHolder.getRequestAttributes() cannot be null");
        }
        return !(attributes instanceof ServletRequestAttributes)
                ? null
                : ((ServletRequestAttributes) attributes).getRequest();
    }
}
