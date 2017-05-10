package rpn;

/**
 * Represents a token from the user. Contains implementations: NumberToken,
 * OperationToken, and QuitToken.
 */
abstract class Token {

    private static final String NUM = "Number", OP = "Operation";

    /**
     * Indicates that a value was requested of a token that doesn't support that
     * value.
     */
    static class TokenMismatchException extends RuntimeException {

        /**
         * Constructs a TokenMismatchException for the requested value.
         * @param valRequested the requested value
         */
        TokenMismatchException(String valRequested) {
            super("Value requested: " + valRequested);
        }
    }

    /**
     * Returns the number value of this token.
     * @throws TokenMismatchException if this token type doesn't support numbers
     */
    Double getNumber() throws TokenMismatchException {
        throw new TokenMismatchException(NUM);
    }

    /**
     * Returns the operation value of this token.
     * @throws TokenMismatchException if this token type doesn't support
     * operations
     */
    String getOperation() throws TokenMismatchException {
        throw new TokenMismatchException(OP);
    }

    static class NumberToken extends Token {

        private Double num;

        /**
         * Creates a NumberToken with the given number value.
         */
        NumberToken(Double num) {
            this.num = num;
        }

        /**
         * Returns the number value of this token.
         */
        @Override
        Double getNumber() {
            return num;
        }
    }

    static class OperationToken extends Token {

        private String op;

        /**
         * Creates a NumberToken with the given operation value.
         */
        OperationToken(String op) {
            this.op = op;
        }

        /**
         * Returns the operation value of this token.
         */
        @Override
        String getOperation() {
            return op;
        }
    }

    static class QuitToken extends Token {
    }
}
