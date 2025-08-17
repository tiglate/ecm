package ludo.mentis.aciem.ecm.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalizationUtilsTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //noinspection InstantiationOfUtilityClass
        new GlobalizationUtils(messageSource, localeResolver);
    }

    @Test
    void testGetMessage_WithArguments() {
        when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(eq("test.code"), any(), eq("test.code"), eq(Locale.ENGLISH)))
                .thenReturn("Test Message with args: arg1, arg2");

        String message = GlobalizationUtils.getMessage("test.code", "arg1", "arg2");
        assertEquals("Test Message with args: arg1, arg2", message);
    }

    @Test
    void testGetMessage_NoArguments() {
        when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(eq("test.code"), any(), eq("test.code"), eq(Locale.ENGLISH)))
                .thenReturn("Test Message");

        String message = GlobalizationUtils.getMessage("test.code");
        assertEquals("Test Message", message);
    }

    @Test
    void testGetMessage_DefaultMessage() {
        when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage(eq("non.existent.code"), any(), eq("non.existent.code"), eq(Locale.ENGLISH)))
                .thenReturn("non.existent.code");

        String message = GlobalizationUtils.getMessage("non.existent.code");
        assertEquals("non.existent.code", message);
    }
}