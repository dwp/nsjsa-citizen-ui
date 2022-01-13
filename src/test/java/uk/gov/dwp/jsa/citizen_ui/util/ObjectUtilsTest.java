package uk.gov.dwp.jsa.citizen_ui.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilsTest {

    @Test
    public void whenAnObjectIsNull_returnEmpty() {
        A nullObject = null;
        assertFalse(ObjectUtils.resolve(() -> nullObject.getB().getValue()).isPresent());
    }

    @Test
    public void whenInnerPropertyIsNull_returnEmpty() {
        A a = new A();
        assertFalse(ObjectUtils.resolve(() -> a.getB().getValue()).isPresent());
    }

    @Test
    public void whenPropertyIsNull_returnEmpty() {
        A a = new A();
        a.setB(new B());
        assertFalse(ObjectUtils.resolve(() -> a.getB().getValue()).isPresent());
    }

    @Test
    public void whenPropertyHasValue_returnOptionalContainingValue() {
        String expected = "test";
        B b = new B();
        b.setValue(expected);
        A a = new A();
        a.setB(b);
        assertTrue(ObjectUtils.resolve(() -> a.getB().getValue()).isPresent());
        assertEquals(expected, ObjectUtils.resolve(() -> a.getB().getValue()).get());
    }

    class A {
        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    class B {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
