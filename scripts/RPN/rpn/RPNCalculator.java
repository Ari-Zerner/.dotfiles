package rpn;

/**
 * Holds and manipulates the data of the RPN calculator.
 */
public class RPNCalculator implements Cloneable {

    private static final PersistentStack<Double> EMPTY_STACK
            = new PersistentStack<>(0.0);
    private PersistentStack<Double> stack = EMPTY_STACK, altStack = EMPTY_STACK;

    public RPNCalculator clone() {
        RPNCalculator clone = new RPNCalculator();
        clone.stack = stack;
        clone.altStack = altStack;
        return clone;
    }

    /**
     * Pushes to the stack.
     */
    private void push(Double n) {
        stack = stack.push(n);
    }

    /**
     * Pops from the stack. Returns 0 if empty.
     */
    private Double pop() {
        Double val = stack.peek();
        stack = stack.pop();
        return val;
    }

    /**
     * Peeks at the stack. Returns 0 if empty.
     */
    private Double peek() {
        return stack.peek();
    }

    /**
     * Returns the Double at the top of the stack. Returns 0 if the stack is
     * empty.
     */
    public Double getResult() {
        return peek();
    }

    /**
     * Returns the stack.
     */
    public PersistentStack<Double> getStack() {
        return stack;
    }

    /**
     * Enters a numeric value. The value is converted to Long or Double and
     * placed on top of the stack.
     * @param n the value
     */
    public void enter(Double n) {
        push(n);
    }

    /**
     * Removes the top of the stack.
     */
    public void remove() {
        pop();
    }

    /**
     * Copies the top of the stack.
     */
    public void copy() {
        push(peek());
    }

    /**
     * Swaps the top two numbers on the stack.
     */
    public void swap() {
        Double oldTop = pop();
        Double newTop = pop();
        push(oldTop);
        push(newTop);
    }

    /**
     * Clears the stack.
     */
    public void clear() {
        stack = EMPTY_STACK;
    }

    /**
     * Moves the top of the current stack to the top of the alternate stack.
     */
    public void toAlt() {
        altStack = altStack.push(pop());
    }

    /**
     * Swaps the current stack and the alternate stack.
     */
    public void swapAlt() {
        PersistentStack<Double> temp = stack;
        stack = altStack;
        altStack = temp;
    }

    /**
     * Performs the addition operation.
     */
    public void add() {
        Double addend2 = pop();
        Double addend1 = pop();
        push(addend1 + addend2);
    }

    /**
     * Performs the subtraction operation.
     */
    public void subtract() {
        Double subtrahend = pop();
        Double minuend = pop();
        push(minuend - subtrahend);
    }

    /**
     * Performs the multiplication operation.
     */
    public void multiply() {
        Double factor2 = pop();
        Double factor1 = pop();
        push(factor1 * factor2);
    }

    /**
     * Performs the division operation.
     */
    public void divide() {
        Double divisor = pop();
        Double dividend = pop();
        push(dividend / divisor);
    }

    /**
     * Performs the exponentiation operation.
     */
    public void power() {
        Double exponent = pop();
        Double base = pop();
        push(Math.pow(base, exponent));
    }

    /**
     * Performs the absolute value operation.
     */
    public void abs() {
        push(Math.abs(pop()));
    }

    /**
     * Performs the log operation using the top number as the logarithmand and
     * the second number as the base.
     */
    public void log() {
        Double logarithmand = pop();
        Double base = pop();
        push(Math.log10(logarithmand) / Math.log10(base));
    }

    /*
     * Performs the sine operation on the top number.
     */
    public void sin() {
        push(Math.sin(pop()));
    }

    /*
     * Performs the inverse sine operation on the top number.
     */
    public void arcsin() {
        push(Math.asin(pop()));
    }

    /**
     * Replaces the top three numbers with the second if the third is nonzero
     * and the top if the third is zero.
     */
    public void ifThenElse() {
        Double ifTrue = pop();
        Double ifFalse = pop();
        Double condition = pop();
        push(condition != 0 ? ifTrue : ifFalse);
    }
}
