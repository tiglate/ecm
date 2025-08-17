package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReferencedWarningTest {

    private ReferencedWarning referencedWarning;

    @BeforeEach
    void setUp() {
        referencedWarning = new ReferencedWarning();
    }

    @Test
    void testAddParam() {
        referencedWarning.addParam("param1");
        assertEquals(1, referencedWarning.getParams().size());
        assertEquals("param1", referencedWarning.getParams().get(0));
    }

    @Test
    void testToMessageWithoutParams() {
        referencedWarning.setKey("WarningKey");
        assertEquals("WarningKey", referencedWarning.toMessage());
    }

    @Test
    void testToMessageWithParams() {
        referencedWarning.setKey("WarningKey");
        referencedWarning.addParam("param1");
        referencedWarning.addParam("param2");
        assertEquals("WarningKey,param1,param2", referencedWarning.toMessage());
    }

    @Test
    void testGetAndSetKey() {
        referencedWarning.setKey("NewKey");
        assertEquals("NewKey", referencedWarning.getKey());
    }

    @Test
    void testGetAndSetParams() {
        List<Object> params = List.of("param1", "param2");
        referencedWarning.setParams(params);
        assertEquals(params, referencedWarning.getParams());
    }
}