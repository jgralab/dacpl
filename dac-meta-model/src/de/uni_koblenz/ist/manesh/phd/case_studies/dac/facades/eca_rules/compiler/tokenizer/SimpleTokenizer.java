package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

import java.util.NoSuchElementException;

public class SimpleTokenizer {
    private final String[] tokens;
    private int idx;

    public SimpleTokenizer(String str, String delimiters) {
        tokens = str.split(delimitersToRegex(delimiters));
    }

    public boolean hasNext() {
        return idx < tokens.length;
    }

    public String next() {
        if(idx < tokens.length) {
            return tokens[idx++];
        } else {
            throw new NoSuchElementException();
        }
    }

    public String peek() {
        if(idx < tokens.length) {
            return tokens[idx];
        } else {
            throw new NoSuchElementException();
        }
    }

    private String delimitersToRegex(String delimiters) {
        StringBuilder sb = new StringBuilder();
        int len = delimiters.length();
        sb.append('[');

        for(int i = 0; i < len; ++i) {
            char chr = delimiters.charAt(i);

            switch(chr) {
                case '\n':
                    sb.append("\\n");
                    break;

                case '\r':
                    sb.append("\\r");
                    break;

                case '-':
                    sb.append("\\-");
                    break;

                case '[':
                    sb.append("\\[");
                    break;

                case ']':
                    sb.append("\\]");
                    break;

                default:
                    sb.append(chr);
            }
        }

        sb.append(']');

        return sb.toString();
    }
}
