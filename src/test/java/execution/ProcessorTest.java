package execution;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessorTest {

    @Test
    public void everythingFineTest() {
        Processor processor = new Processor(Mode.EXECUTE, "number x = 1;");
        assertFalse(processor.isExceptionThrown());
        assertFalse(processor.isErrorThrown());

        assertTrue(processor.isLexCheckSuccessful());
        assertTrue(processor.isParseCheckSuccessful());
        assertTrue(processor.isValidationCheckSuccessful());
        assertTrue(processor.isRuntimeCheckSuccessful());
    }

    @Test
    public void scanExceptionTest() {
        Processor processor = new Processor(Mode.EXECUTE, "string ab = 'ä';");
        assertFalse(processor.isExceptionThrown());
        assertTrue(processor.isErrorThrown());

        assertFalse(processor.isLexCheckSuccessful());
        assertFalse(processor.isParseCheckSuccessful());
        assertFalse(processor.isValidationCheckSuccessful());
        assertFalse(processor.isRuntimeCheckSuccessful());
    }

    @Test
    public void parseExceptionTest() {
        Processor processor = new Processor(Mode.EXECUTE, "string ab = 1");
        assertTrue(processor.isExceptionThrown());
        assertFalse(processor.isErrorThrown());

        assertTrue(processor.isLexCheckSuccessful());
        assertFalse(processor.isParseCheckSuccessful());
        assertFalse(processor.isValidationCheckSuccessful());
        assertFalse(processor.isRuntimeCheckSuccessful());
    }

    @Test
    public void validateExceptionTest() {
        Processor processor = new Processor(Mode.EXECUTE, "def number fun() { return fun(); } fun1();");
        assertTrue(processor.isExceptionThrown());
        assertFalse(processor.isErrorThrown());

        assertTrue(processor.isLexCheckSuccessful());
        assertTrue(processor.isParseCheckSuccessful());
        assertFalse(processor.isValidationCheckSuccessful());
        assertFalse(processor.isRuntimeCheckSuccessful());
    }

    @Test
    public void recursionTest() {
        Processor processor = new Processor(Mode.EXECUTE, "def number fun() { return fun(); } fun();");
        assertFalse(processor.isExceptionThrown());
        assertTrue(processor.isErrorThrown());

        assertTrue(processor.isLexCheckSuccessful());
        assertTrue(processor.isParseCheckSuccessful());
        assertTrue(processor.isValidationCheckSuccessful());
        assertFalse(processor.isRuntimeCheckSuccessful());
    }

    @Test
    public void infinityTest() {
        Processor processor = new Processor(Mode.EXECUTE, "number x = 1/0;");
        assertTrue(processor.isExceptionThrown());
        assertFalse(processor.isErrorThrown());

        assertTrue(processor.isLexCheckSuccessful());
        assertTrue(processor.isParseCheckSuccessful());
        assertTrue(processor.isValidationCheckSuccessful());
        assertFalse(processor.isRuntimeCheckSuccessful());
    }

}
