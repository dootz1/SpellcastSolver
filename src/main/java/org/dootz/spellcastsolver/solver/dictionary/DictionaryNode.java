package org.dootz.spellcastsolver.solver.dictionary;

import static org.dootz.spellcastsolver.solver.dictionary.Dictionary.ALPHABET_CHARACTERS;

public class DictionaryNode {
    private final DictionaryNode[] children;
    private boolean isWord;

    public DictionaryNode() {
        this.children = new DictionaryNode[ALPHABET_CHARACTERS];
        this.isWord = false;
    }

    public boolean isWord() {
        return isWord;
    }

    public void setWord(boolean word) {
        isWord = word;
    }

    public DictionaryNode getChild(char letter) {
        return children[letter - 'A'];
    }

    public DictionaryNode[] getChildren() {
        return children;
    }

}