package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.verify;

class FlashMessagesTest {

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReferencedWarning() {
        FlashMessages.referencedWarning(redirectAttributes, "Entity", 1);
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_ERROR, "This entity is still referenced by Entity 1 via field Software.");
    }

    @Test
    void testCreateSuccess() {
        FlashMessages.createSuccess(redirectAttributes, "Entity");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_SUCCESS, "Entity was created successfully");
    }

    @Test
    void testUpdateSuccess() {
        FlashMessages.updateSuccess(redirectAttributes, "Entity");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_SUCCESS, "Entity was updated successfully.");
    }

    @Test
    void testDeleteSuccess() {
        FlashMessages.deleteSuccess(redirectAttributes, "Entity");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_SUCCESS, "Entity was removed successfully.");
    }

    @Test
    void testErrorWithException() {
        Exception exception = new Exception("Error message");
        FlashMessages.error(redirectAttributes, exception);
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_ERROR, "Error message");
    }

    @Test
    void testErrorWithString() {
        FlashMessages.error(redirectAttributes, "Error message");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_ERROR, "Error message");
    }

    @Test
    void testInfo() {
        FlashMessages.info(redirectAttributes, "Info message");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_INFO, "Info message");
    }

    @Test
    void testSuccess() {
        FlashMessages.success(redirectAttributes, "Success message");
        verify(redirectAttributes).addFlashAttribute(FlashMessages.MSG_SUCCESS, "Success message");
    }
}