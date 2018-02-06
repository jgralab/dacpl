package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class Token {
    /**
     * Key constant that has no literal counterpart. It is meant to indicate in
     * an AST that there is an array access operation.
     */
    public static final int KEY_ARRAY_ACCESS = -62;

    /**
     * <code>=</code>
     */
    final public static int KEY_ASSIGN = -5;

    /**
     * <code>&amp;</code>
     */
    final public static int KEY_BIT_AND = -30;

    /**
     * <code>&amp;=</code>
     */
    public static final int KEY_BIT_AND_ASSIGN = -54;

    /**
     * <code>~</code>
     */
    final public static int KEY_BIT_NEGATION = -29;

    /**
     * <code>~=</code>
     */
    public static final int KEY_BIT_NEGATION_ASSIGN = -49;

    /**
     * <code>|</code>
     */
    final public static int KEY_BIT_OR = -31;

    /**
     * <code>|=</code>
     */
    public static final int KEY_BIT_OR_ASSIGN = -55;

    /**
     * <code>&lt;&lt;</code>
     */
    final public static int KEY_BIT_SHIFT_LEFT = -47;

    /**
     * <code>&lt;&lt;=</code>
     */
    public static final int KEY_BIT_SHIFT_LEFT_ASSIGN = -47;

    /**
     * <code>&gt;&gt;</code>
     */
    final public static int KEY_BIT_SHIFT_RIGHT = -48;

    /**
     * <code>&gt;&gt;=</code>
     */
    public static final int KEY_BIT_SHIFT_RIGHT_ASSIGN = -48;

    /**
     * <code>^</code>
     */
    final public static int KEY_BIT_XOR = -32;

    /**
     * <code>^=</code>
     */
    public static final int KEY_BIT_XOR_ASSIGN = -63;

    /**
     * <code>&amp;&amp;</code>
     */
    final public static int KEY_BOOLEAN_AND = -26;

    /**
     * <code>!</code>
     */
    final public static int KEY_BOOLEAN_NOT = -25;

    /**
     * <code>||</code>
     */
    final public static int KEY_BOOLEAN_OR = -27;

    /**
     * There's no single operator available in C. This key value definition may
     * be used for parsing other languages.
     */
    final public static int KEY_BOOLEAN_XOR = -28;

    /**
     * <code>}</code>
     */
    final public static int KEY_BRACE_CLOSE = -24;

    /**
     * <code>{</code>
     */
    final public static int KEY_BRACE_OPEN = -23;

    /**
     * <code>break</code>
     */
    final public static int KEY_BREAK = -44;

    /**
     * <code>case</code>
     */
    final public static int KEY_CASE = -42;

    /**
     * <code>:</code>
     */
    final public static int KEY_COLON = -13;

    /**
     * <code>,</code>
     */
    final public static int KEY_COMMA = -15;

    /**
     * <code>continue</code>
     */
    final public static int KEY_CONTINUE = -45;

    /**
     * <code>--</code>
     */
    public static final int KEY_DECREMENT = -57;

    /**
     * <code>default</code>
     */
    final public static int KEY_DEFAULT = -43;

    /**
     * <code>/</code>
     */
    final public static int KEY_DIV = -19;

    /**
     * <code>/=</code>
     */
    public static final int KEY_DIV_ASSIGN = -53;

    /**
     * <code>do</code>
     */
    final public static int KEY_DO = -39;

    /**
     * Key constant for double precision floating point literals.
     */
    final public static int KEY_DOUBLE_FLOAT = -66;

    /**
     * <code>else</code>
     */
    final public static int KEY_ELSE = -37;

    final public static int KEY_END = 4;

    /**
     * <code>==</code>
     */
    final public static int KEY_EQUAL = -19;

    /**
     * Please note that this is Java- and JavaScript-centric. There are no
     * boolean literals in C.
     *
     * <code>false</code>
     */
    final public static int KEY_FALSE = -34;

    /**
     * Key constant for any kind of floating point literal.
     */
    final public static int KEY_FLOAT = -3;

    /**
     * <code>for</code>
     */
    final public static int KEY_FOR = -38;

    /**
     * Key constant for ASTs which indicates a function / method call operation.
     */
    final public static int KEY_FUNCTION_CALL = -71;

    /**
     * <code>&gt;=</code>
     */
    final public static int KEY_GREATER_EQUAL = -11;

    /**
     * <code>&gt;</code>
     */
    final public static int KEY_GREATER_THAN = -9;

    /**
     * Key constant for user-defined identifiers (such as type, method and
     * variable names). Identifiers have to match the grammar rules as defined
     * by the tokenizer.
     */
    final public static int KEY_IDENTIFIER = -1;

    /**
     * <code>if</code>
     */
    final public static int KEY_IF = -36;

    /**
     * <code>++</code>
     */
    public static final int KEY_INCREMENT = -56;

    /**
     * Key constant for a signed integer literal.
     */
    final public static int KEY_INTEGER = -2;

    /**
     * <code>&lt;=</code>
     */
    final public static int KEY_LESS_EQUAL = -10;

    /**
     * <code>&lt;</code>
     */
    final public static int KEY_LESS_THAN = -8;

    /**
     * Key constant for signed long integer literals.
     */
    final public static int KEY_LONG_INTEGER = -69;

    /**
     * <code>-</code>
     */
    final public static int KEY_MINUS = -17;

    /**
     * <code>-=</code>
     */
    public static final int KEY_MINUS_ASSIGN = -51;

    /**
     * <code>*</code>
     */
    final public static int KEY_MUL = -18;

    /**
     * <code>*=</code>
     */
    public static final int KEY_MUL_ASSIGN = -52;

    /**
     * This is an unary negative sign operator and not subtraction.
     *
     * <code>-</code>
     */
    public static final int KEY_NEGATIVE_SIGN = -58;

    /**
     * Only meaningful for Java and JavaScript. C doesn't have that literal.
     * <code>null</code>
     */
    final public static int KEY_NULL = -35;

    final public static int KEY_ON = 1;

    /**
     * <code>)</code>
     */
    final public static int KEY_PAREN_CLOSE = -7;

    /**
     * <code>(</code>
     */
    final public static int KEY_PAREN_OPEN = -6;

    /**
     * <code>+</code>
     */
    final public static int KEY_PLUS = -16;

    /**
     * <code>+=</code>
     */
    public static final int KEY_PLUS_ASSIGN = -50;

    /**
     * <code>.</code>
     */
    final public static int KEY_POINT = -14;

    /**
     * <code>--</code>
     */
    public static final int KEY_POSTFIX_DECREMENT = -61;

    /**
     * <code>++</code>
     */
    public static final int KEY_POSTFIX_INCREMENT = -60;

    final public static int KEY_QUESTIONMARK = -72;

    /**
     * <code>return</code>
     */
    final public static int KEY_RETURN = -46;

    /**
     * Special key for root nodes.
     */
    final public static int KEY_ROOT = Integer.MIN_VALUE + 1;

    final public static int KEY_RULE = 2;

    /**
     * <code>;</code>
     */
    final public static int KEY_SEMICOLON = -12;

    /**
     * Key constant for single precision floating point literals.
     */
    final public static int KEY_SINGLE_FLOAT = -67;

    /**
     * <code>]</code>
     */
    final public static int KEY_SQUARE_BRACKET_CLOSE = -22;

    /**
     * <code>[</code>
     */
    final public static int KEY_SQUARE_BRACKET_OPEN = -21;

    /**
     * Can be used in ASTs for chaining a list of statements.
     */
    public static final int KEY_STATEMENT = -59;

    /**
     * Key constant for string literals.
     */
    final public static int KEY_STRING = -4;

    /**
     * <code>switch</code>
     */
    final public static int KEY_SWITCH = -41;

    /**
     * Only meaningful for Java / JavaScript. C doesn't have boolean literals.
     * <code>true</code>
     */
    final public static int KEY_TRUE = -33;

    /**
     * <code>!=</code>
     */
    final public static int KEY_UNEQUAL = -20;

    /**
     * Key for unknown literals.
     */
    final public static int KEY_UNKNOWN = Integer.MIN_VALUE;

    /**
     * Java / JavaScript
     * <code>&gt;&gt;&gt;</code>
     */
    public static final int KEY_UNSIGNED_BIT_SHIFT_RIGHT = -64;

    /**
     * <code>&gt;&gt;&gt;=</code>
     */
    public static final int KEY_UNSIGNED_BIT_SHIFT_RIGHT_ASSIGN = -65;

    /**
     * Key constant for unsigned integer literals.
     */
    final public static int KEY_UNSIGNED_INTEGER = -68;
    /**
     * Key constant for unsigned long integer literals.
     */
    final public static int KEY_UNSIGNED_LONG_INTEGER = -70;
    final public static int KEY_WHEN = 3;
    /**
     * <code>while</code>
     */
    final public static int KEY_WHILE = -40;

    public int key;
    public int line;
    public int pos;
    public Object value;

    final private Tokenizer mOwner;

    public Token(Tokenizer owner) {
    	mOwner = owner;
    }

    public Token(Tokenizer owner, int key) {
    	this(owner);
        this.key = key;
    }

    public Token(Tokenizer owner, int key, int line, int pos) {
    	this(owner);
        this.key = key;
        this.line = line;
        this.pos = pos;
    }

    public Token(Tokenizer owner, int key, int line, int pos, Object value) {
    	this(owner);
        this.key = key;
        this.line = line;
        this.pos = pos;
        this.value = value;
    }

    public String getLiteral() {
		if(value != null) {
			return value.toString();
		} else {
			return mOwner.getLiteralForSymbol(key);
		}
	}

	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("[key=");
    	sb.append(key);
    	sb.append(", line=");
    	sb.append(line);
    	sb.append(", pos=");
    	sb.append(pos);
    	sb.append(", value=");
    	sb.append(value);
    	sb.append("]");

    	return sb.toString();
    }
}
