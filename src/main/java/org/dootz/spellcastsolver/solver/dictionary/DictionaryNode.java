package org.dootz.spellcastsolver.solver.dictionary;

import static org.dootz.spellcastsolver.solver.dictionary.Dictionary.ALPHABET_CHARACTERS;

public class DictionaryNode {
    private final DictionaryNode[] children;
    private boolean isWord;
    private int childMask; // NEW

    public DictionaryNode() {
        this.children = new DictionaryNode[ALPHABET_CHARACTERS];
        this.isWord = false;
        this.childMask = 0;
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

    public DictionaryNode getChildByIndex(int index) { // fast path
        return children[index];
    }

    public void addChildByIndex(int index) {
        children[index] = new DictionaryNode();
    }

    public DictionaryNode[] getChildren() {
        return children;
    }

    public int getChildMask() {
        return childMask;
    }

    public void addChildMask(int index) {
        this.childMask |= 1 << index;
    }
}