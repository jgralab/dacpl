package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * General purpose programming language scanner / tokenizer.
 * <p>
 * Can be adapted for language-specific symbols and needs.
 *
 * @author tw
 */
public class Tokenizer {
	private static class LookAheadCompoundOperatorNode {
		private final HashMap<Character, LookAheadCompoundOperatorNode> children = new HashMap<Character, Tokenizer.LookAheadCompoundOperatorNode>();

		public LookAheadCompoundOperatorNode() {
			// This is root
		}

		public void addOperator(String operator) {
			char firstChar = operator.charAt(0);
			LookAheadCompoundOperatorNode childNode = getNodeForNextChar(firstChar);

			if (childNode == null) {
				childNode = new LookAheadCompoundOperatorNode();
				children.put(firstChar, childNode);
			}

			if (operator.length() > 1) {
				childNode.addOperator(operator.substring(1));
			}
		}

		public LookAheadCompoundOperatorNode getNodeForNextChar(char next) {
			return children.get(next);
		}

		public boolean hasNextChar(char next) {
			return children.containsKey(next);
		}
	}

	final private static Pattern IDENTIFIER_PATTERN = Pattern.compile(
			"^[_A-Z][_A-Z0-9]*$", Pattern.CASE_INSENSITIVE);
	private BufferedReader chars;
	private char currentChar;
	private boolean ignoreEscapeError;
	private int line;
	private boolean mAllowUnknown;
	private int maxOperatorLength;
	private char nextChar;
	private final AtomicReference<Object> numberValue;
	private int pos;
	private final LookAheadCompoundOperatorNode rootLookAhead;
	private final HashMap<String, Integer> symbolTable;
	private int tokenIdx;
	private final HashMap<String, Integer> tokenKeyMap;
	private final Stack<Token> tokens;
	private final StringBuilder tokenSB;
	private boolean useCStyleComments;
	private boolean useSymbolTable;

	public Tokenizer() {
		tokens = new Stack<Token>();
		tokenKeyMap = new HashMap<String, Integer>();
		symbolTable = new HashMap<String, Integer>();
		numberValue = new AtomicReference<Object>();
		tokenSB = new StringBuilder();
		rootLookAhead = new LookAheadCompoundOperatorNode();
		useSymbolTable = true;

		registerSymbols();
	}

	public String getLiteralForSymbol(int key) {
		if(key == Token.KEY_IDENTIFIER) return "<identifier>";
		Set<Entry<String, Integer>> entries = tokenKeyMap.entrySet();

		for(Entry<String, Integer> entry : entries) {
			if(entry.getValue() == key) return entry.getKey();
		}

		return null;
	}

	/**
	 * Returns true if a call of {@link #next()} or {@link #peek()} would be
	 * successful.
	 *
	 * @return True if there are more tokens to be read.
	 */
	public boolean hasNext() {
		return tokenIdx < tokens.size();
	}

	/**
	 * Gets the current token from the list and increments the token pointer.
	 *
	 * @return The current token from the list.
	 * @throws NoSuchElementException
	 *             If the end of this list has been reached.
	 */
	public Token next() {
		if (tokenIdx < tokens.size()) {
			return tokens.get(tokenIdx++);
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Gets the current token from the list without incrementing the token
	 * pointer.
	 *
	 * @return The current token from the list.
	 * @throws NoSuchElementException
	 *             If the end of this list has been reached.
	 */
	public Token peek() {
		if (tokenIdx < tokens.size()) {
			return tokens.get(tokenIdx);
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Gets the next token from the list without incrementing the token pointer.
	 *
	 * @return The next token from the list.
	 * @throws NoSuchElementException
	 *             If the end of this list has been reached.
	 */
	public Token peekNext() {
		if (tokenIdx + 1 < tokens.size()) {
			return tokens.get(tokenIdx + 1);
		} else {
			throw new NoSuchElementException();
		}
	}

	public void setAllowUnknownSymbols(boolean allowUnknown) {
		mAllowUnknown = allowUnknown;
	}

	public void setIgnoreEscapeError(boolean ignoreEscapeError) {
		this.ignoreEscapeError = ignoreEscapeError;
	}

	public void setUseCStyleComments(boolean useComments) {
		useCStyleComments = useComments;
	}

	public void setUseSymbolTable(boolean useST) {
		useSymbolTable = useST;
	}

	public void tokenize(InputStream is, Charset charset) throws IOException,
			TokenizerException {
		tokens.clear();
		chars = new BufferedReader(new InputStreamReader(is, charset));
		pos = 0;
		line = 1;
		/*
		 * TODO currentChar must not be -1. Char is not signed use line = 0
		 * instead for checking first read!
		 */
		currentChar = (char) -1;
		readNextChar();
		symbolTable.clear();
		tokenIdx = 0;

		do {
			// Filter white space
			while (isWhitespaceChar(currentChar)) {
				readNextChar();
			}

			if (currentChar == '\n') {
				++line;
				pos = 0;
				readNextChar();
			} else if (isIdentfierFirstChar(currentChar)) {
				ruleIdentifier();
			} else if (isNumberFirstChar(currentChar)) {
				ruleNumber();
			} else if (currentChar == '"' || currentChar == '\'') {
				ruleString();
			} else if (useCStyleComments && currentChar == '/'
					&& (nextChar == '/' || nextChar == '*')) {
				if (nextChar == '/') {
					// Line comment

					// TODO crashes if comment is last line of file
					// without newline in end of file
					while (currentChar != '\n') {
						readNextChar();
					}

					continue;
				} else {
					// Block comment
					do {
						readNextChar();

						if (currentChar == '\n') {
							++line;
							pos = 0;
							continue;
						} else if (currentChar == '*' && nextChar == '/') {
							readNextChar();
							readNextChar();
							break;
						}
					} while (!isEndOfChars(currentChar));
				}
			} else {
				// Character must be a structural element or an operator
				// If not --> tokenizing exception will be thrown
				ruleStructuralElementOrOperator();
			}
		} while (!isEndOfChars(currentChar));
	}

	public void tokenize(InputStream is, String encoding) throws IOException,
			TokenizerException {
		tokenize(is, Charset.forName(encoding));
	}

	/**
	 * Adds an identifier literal to the symbol table and returns its freshly
	 * assigned symbol table ID.
	 *
	 * @param literal
	 *            Identifier literal to add to the symbol table.
	 * @return The identifier's new symbol table ID.
	 */
	protected int addToSymbolTable(String literal) {
		int key = symbolTable.size();
		symbolTable.put(literal, key);

		return key;
	}

	/**
	 * Registers a programming language symbol like keyword, structure element
	 * or operator with its according unique symbol key.
	 *
	 * @param symbol
	 *            The symbol to register as a literal.
	 * @param key
	 *            The according symbol key to associate with the symbol. This
	 *            must be a unique, positive number starting from 0.
	 */
	protected void registerSymbol(String symbol, int key) {
		assert (key >= 0);
		tokenKeyMap.put(symbol, key);

		// It's an operator or structure symbol like {} or ()
		if (!IDENTIFIER_PATTERN.matcher(symbol).matches()) {
			int symLen = symbol.length();

			if (symLen > maxOperatorLength) {
				maxOperatorLength = symLen;
			}

			rootLookAhead.addOperator(symbol);
		}
	}

	protected void registerSymbols() {
		registerSymbol("=", Token.KEY_ASSIGN);
		registerSymbol("==", Token.KEY_EQUAL);
		registerSymbol("!=", Token.KEY_UNEQUAL);
		registerSymbol("<", Token.KEY_LESS_THAN);
		registerSymbol("<=", Token.KEY_LESS_EQUAL);
		registerSymbol(">", Token.KEY_GREATER_THAN);
		registerSymbol(">=", Token.KEY_GREATER_EQUAL);
		registerSymbol("(", Token.KEY_PAREN_OPEN);
		registerSymbol(")", Token.KEY_PAREN_CLOSE);
		registerSymbol("[", Token.KEY_SQUARE_BRACKET_OPEN);
		registerSymbol("]", Token.KEY_SQUARE_BRACKET_CLOSE);
		registerSymbol("{", Token.KEY_BRACE_OPEN);
		registerSymbol("}", Token.KEY_BRACE_CLOSE);
		registerSymbol(":", Token.KEY_COLON);
		registerSymbol(";", Token.KEY_SEMICOLON);
		registerSymbol(",", Token.KEY_COMMA);
		registerSymbol(".", Token.KEY_POINT);
		registerSymbol("+", Token.KEY_PLUS);
		registerSymbol("+=", Token.KEY_PLUS_ASSIGN);
		registerSymbol("-", Token.KEY_MINUS);
		registerSymbol("-=", Token.KEY_MINUS_ASSIGN);
		registerSymbol("*", Token.KEY_MUL);
		registerSymbol("*=", Token.KEY_MUL_ASSIGN);
		registerSymbol("/", Token.KEY_DIV);
		registerSymbol("/=", Token.KEY_DIV_ASSIGN);
		registerSymbol("&&", Token.KEY_BOOLEAN_AND);
		registerSymbol("||", Token.KEY_BOOLEAN_OR);
		registerSymbol("!", Token.KEY_BOOLEAN_NOT);
		registerSymbol("&", Token.KEY_BIT_AND);
		registerSymbol("&=", Token.KEY_BIT_AND_ASSIGN);
		registerSymbol("|", Token.KEY_BIT_OR);
		registerSymbol("|=", Token.KEY_BIT_OR_ASSIGN);
		registerSymbol("<<", Token.KEY_BIT_SHIFT_LEFT);
		registerSymbol("<<=", Token.KEY_BIT_SHIFT_LEFT_ASSIGN);
		registerSymbol(">>", Token.KEY_BIT_SHIFT_RIGHT);
		registerSymbol(">>=", Token.KEY_BIT_SHIFT_RIGHT_ASSIGN);
		registerSymbol("~", Token.KEY_BIT_NEGATION);
		registerSymbol("~=", Token.KEY_BIT_NEGATION_ASSIGN);
		registerSymbol("^=", Token.KEY_BIT_XOR_ASSIGN);
		registerSymbol("++", Token.KEY_INCREMENT);
		registerSymbol("--", Token.KEY_DECREMENT);
		registerSymbol("?", Token.KEY_QUESTIONMARK);
		registerSymbol("true", Token.KEY_TRUE);
		registerSymbol("false", Token.KEY_FALSE);
		registerSymbol("null", Token.KEY_NULL);
		registerSymbol("if", Token.KEY_IF);
		registerSymbol("else", Token.KEY_ELSE);
		registerSymbol("for", Token.KEY_FOR);
		registerSymbol("do", Token.KEY_DO);
		registerSymbol("while", Token.KEY_WHILE);
		registerSymbol("switch", Token.KEY_SWITCH);
		registerSymbol("case", Token.KEY_CASE);
		registerSymbol("default", Token.KEY_DEFAULT);
		registerSymbol("break", Token.KEY_BREAK);
		registerSymbol("continue", Token.KEY_CONTINUE);
		registerSymbol("return", Token.KEY_RETURN);
	}

	protected Token tokenFactory() {
		return new Token(this);
	}

	private void addIdentifierToken(int symbolTableIdx, int line, int pos,
			String literal) {
		Token tk = tokenFactory();
		tk.key = Token.KEY_IDENTIFIER;
		tk.line = line;
		tk.pos = pos;
		tk.value = useSymbolTable ? symbolTableIdx : literal;
		tokens.add(tk);

		// if(log.isDebugAllowed()) {
		// log.debug("Identifier token added " + symbolTableIdx + " (line="
		// + line + ", pos=" + pos + ")");
		// }
	}

	private void addToken(int tokenKey, int line, int pos) {
		Token tk = tokenFactory();
		tk.key = tokenKey;
		tk.line = line;
		tk.pos = pos;
		tokens.add(tk);

		// if(log.isDebugAllowed()) {
		// log.debug("Token added " + tokenKey + " (line=" + line + ", pos="
		// + pos + ")");
		// }
	}

	private void addToken(int tokenKey, int line, int pos, Object value) {
		Token tk = tokenFactory();
		tk.key = tokenKey;
		tk.line = line;
		tk.pos = pos;
		tk.value = value;
		tokens.add(tk);

		// if(log.isDebugAllowed()) {
		// log.debug("Token added " + tokenKey + " (value=" + value
		// + ", line=" + line + ", pos=" + pos + ")");
		// }
	}

	private int getTokenKey(String token) {
		if (tokenKeyMap.containsKey(token)) {
			return tokenKeyMap.get(token);
		} else {
			return Token.KEY_IDENTIFIER;
		}
	}

	private StringBuilder getTokenStringBuilder() {
		tokenSB.setLength(0);

		return tokenSB;
	}

	private boolean isBinaryNumberChar(char chr) {
		return chr == '0' || chr == '1';
	}

	private boolean isEndOfChars(char chr) {
		return chr == (char) -1;
	}

	private boolean isHexNumberChar(char chr) {
		return isNumberChar(chr) || (chr >= 'a' && chr <= 'f')
				|| (chr >= 'A' && chr <= 'F');
	}

	private boolean isIdentfierFirstChar(char chr) {
		return (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z')
				|| chr == '_';
	}

	private boolean isIdentifierChar(char chr) {
		return isIdentfierFirstChar(chr) || isNumberChar(chr);
	}

	private boolean isKey(String token) {
		return tokenKeyMap.containsKey(token);
	}

	private boolean isNumberChar(char chr) {
		return chr >= '0' && chr <= '9';
	}

	private boolean isNumberFirstChar(char chr) {
		return isNumberChar(chr) || chr == '.';
	}

	private boolean isWhitespaceChar(char chr) {
		return chr == ' ' || chr == '\r' || chr == '\t';
	}

	private char readNextChar() throws IOException {
		if (currentChar == (char) -1) {
			/*
			 * TODO following fails on single character inputs!
			 */
			currentChar = (char) chars.read();
			nextChar = (char) chars.read();
		} else {
			currentChar = nextChar;
			nextChar = (char) chars.read();
		}

		++pos;

		return currentChar;
	}

	private void ruleIdentifier() throws IOException {
		// Extract identifier or keyword
		int startPos = pos;
		String literal = scanIdentifier();
		int key = getTokenKey(literal);

		if (key == Token.KEY_IDENTIFIER) {
			int symbolTableIdx;

			if (symbolTable.containsKey(literal)) {
				symbolTableIdx = symbolTable.get(literal);
			} else {
				symbolTableIdx = addToSymbolTable(literal);
			}

			addIdentifierToken(symbolTableIdx, line, startPos, literal);
		} else {
			// It's a registered key word
			addToken(key, line, startPos);
		}
	}

	private void ruleNumber() throws IOException, TokenizerException {
		// Extract number
		int startPos = pos;
		int key = scanNumber(numberValue);

		if (key != Token.KEY_UNKNOWN) {
			addToken(key, line, startPos, numberValue.get());
		} else {
			ruleStructuralElementOrOperator();
		}
	}

	private void ruleString() throws IOException, StringLiteralException {
		// Extract string
		int startPos = pos;
		String literal = scanStringLiteral();
		addToken(Token.KEY_STRING, line, startPos, literal);
	}

	private void ruleStructuralElementOrOperator() throws TokenizerException,
			IOException {
		StringBuilder token = getTokenStringBuilder();
		int startPos = pos;
		LookAheadCompoundOperatorNode curLookAhead = rootLookAhead;

		while (curLookAhead.hasNextChar(currentChar)
				&& token.length() <= maxOperatorLength) {
			curLookAhead = curLookAhead.getNodeForNextChar(currentChar);
			token.append(currentChar);
			readNextChar();
		}

		String literal = token.toString();

		if (isKey(literal)) {
			addToken(getTokenKey(literal), line, startPos);
		} else {
			if (mAllowUnknown) {
				addToken(Token.KEY_UNKNOWN, line, startPos, String.valueOf(currentChar));
				readNextChar();
			} else {
				throw new IllegalCharacterException(currentChar, line, pos);
			}
		}
	}

	private char scanEscapeCharacter() throws StringLiteralException,
			IOException {
		char chr = readNextChar();

		switch (chr) {
		case 'b':
			return '\b';

		case 't':
			return '\t';

		case 'n':
			return '\n';

		case 'f':
			return '\f';

		case 'r':
			return '\r';

		case '"':
			return '"';

		case '\'':
			return '\'';

		case '\\':
			return '\\';

		case 'u':
			String hexCode = new String(new char[] { readNextChar(),
					readNextChar(), readNextChar(), readNextChar() });
			int code = Integer.parseInt(hexCode, 16);

			return (char) code;

		default:
			if (ignoreEscapeError) {
				return chr;
			} else {
				throw new StringLiteralException("Illegal escape character '"
						+ chr + "'", line, pos);
			}
		}
	}

	private String scanIdentifier() throws IOException {
		char chr = currentChar;
		StringBuilder token = getTokenStringBuilder();

		while (isIdentifierChar(chr)) {
			token.append(chr);
			chr = readNextChar();
		}

		return token.toString();
	}

	private int scanNumber(AtomicReference<Object> numberValue)
			throws IOException, IllegalNumberFormatException {
		char chr = currentChar;
		StringBuilder token = getTokenStringBuilder();

		if (chr == '0') {
			if (nextChar == 'x') {
				// Hexadecimal number
				// Skip the x
				readNextChar();
				chr = readNextChar();

				while (isHexNumberChar(chr)) {
					token.append(chr);
					chr = readNextChar();
				}

				try {
					numberValue.set(Long.parseLong(token.toString(), 16));
					return Token.KEY_INTEGER;
				} catch (NumberFormatException e) {
					throw new IllegalNumberFormatException(line, pos);
				}
			} else if (nextChar == 'b') {
				// Binary number
				// Skip the b
				readNextChar();
				chr = readNextChar();

				while (isBinaryNumberChar(chr)) {
					token.append(chr);
					chr = readNextChar();
				}

				try {
					numberValue.set(Long.parseLong(token.toString(), 2));
					return Token.KEY_INTEGER;
				} catch (NumberFormatException e) {
					throw new IllegalNumberFormatException(line, pos);
				}
			}
		}

		boolean isFloat = false;

		if ((currentChar == '.' && isNumberChar(nextChar))
				|| isNumberChar(currentChar)) {
			while (isNumberChar(chr) || (chr == '.' && !isFloat)) {
				if (chr == '.') {
					isFloat = true;
				}

				token.append(chr);
				chr = readNextChar();
			}

			if (isFloat) {
				if (token.length() > 1) {
					numberValue.set(Double.parseDouble(token.toString()));

					return Token.KEY_FLOAT;
				} else {
					// Could be if there's only a '.' character and no following
					// digits.
					return Token.KEY_UNKNOWN;
				}
			} else {
				numberValue.set(Long.parseLong(token.toString()));

				return Token.KEY_INTEGER;
			}
		} else {
			return Token.KEY_UNKNOWN;
		}
	}

	private String scanStringLiteral() throws IOException,
			StringLiteralException {
		StringBuilder token = getTokenStringBuilder();
		char delim = currentChar;
		char chr = readNextChar();

		while (chr != delim) {
			if (chr == '\\') {
				chr = scanEscapeCharacter();
			}

			token.append(chr);
			chr = readNextChar();

			if (isEndOfChars(chr)) {
				throw new UnclosedStringLiteralException(line, pos);
			}
		}

		// consume closing quotation mark
		readNextChar();

		return token.toString();
	}
}
