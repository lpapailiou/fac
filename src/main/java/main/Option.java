package main;

/**
 * This enum is used to define the available options of this program.
 * The user can choose if a code should be just scanned, parsed, validated or executed.
 * The execution can be done at once or step by step within the console.
 * The steps build on each other, every option will include all preceding options.
 */
public enum Option {

    /**
     * Will scan a code and omit further processing.
     * If no error or exception is thrown, processed code is lexically valid.
     */
    SCAN,
    /**
     * Will parse a code and omit further processing.
     * If no exception is thrown, processed code is syntactically valid.
     */
    PARSE,
    /**
     * Will validate a code and omit further processing.
     * If no exception is thrown, processed code is semantically valid.
     */
    VALIDATE,
    /**
     * Will execute a code at once.
     * If no exception is thrown, processed code is valid.
     */
    EXECUTE,
    /**
     * Will execute a code entry by entry within the console.
     * If no exception is thrown, processed code is valid.
     */
    CONSOLE;

}
