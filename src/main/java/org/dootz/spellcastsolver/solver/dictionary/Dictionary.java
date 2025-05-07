package org.dootz.spellcastsolver.solver.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {

    public static final int ALPHABET_CHARACTERS = 26;
    private final DictionaryNode root;

    public Dictionary() {
        this.root = new DictionaryNode();
    }

    public void insertNode(String key) {
        if (key == null || key.isBlank()) return;

        DictionaryNode node = root;
        for (char ch : key.toUpperCase().toCharArray()) {
            int index = ch - 'A';
            if (node.getChildren()[index] == null) {
                node.getChildren()[index] = new DictionaryNode();
            }
            node = node.getChildren()[index];
        }
        node.setWord(true);
    }

    public void importFromFile(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                insertNode(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Failed to read from stream: " + e.getMessage());
        }
    }

    public List<String> getAllWords() {
        return getAllWords(root, new StringBuilder());
    }

    private List<String> getAllWords(DictionaryNode node, StringBuilder prefix) {
        List<String> words = new ArrayList<>();

        if (node.isWord()) {
            words.add(prefix.toString());
        }

        for (int i = 0; i < ALPHABET_CHARACTERS; i++) {
            DictionaryNode child = node.getChildren()[i];
            if (child != null) {
                prefix.append((char) ('A' + i));
                words.addAll(getAllWords(child, prefix));
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        return words;
    }

    public DictionaryNode getRoot() {
        return root;
    }
}
