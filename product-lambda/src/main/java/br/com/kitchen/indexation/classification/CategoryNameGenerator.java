package br.com.kitchen.indexation.classification;

import java.util.*;
import java.util.regex.Pattern;

public class CategoryNameGenerator {

    private final PortugueseStemmer stemmer = new PortugueseStemmer();
    private static final Set<String> STOPWORDS = Set.of(
            "de", "da", "do", "para", "com", "sem", "em", "na", "no",
            "e", "ou", "por", "uma", "um", "uns", "umas", "kit", "jogo"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-ZÀ-ÿ]{4,}");

    public String generateCategoryName(String name, String description) {

        String text = (name + " " + description)
                .toLowerCase()
                .replaceAll("[^a-zA-ZÀ-ÿ ]", " "); // remove símbolos

        List<String> candidates = extractKeywords(text);

        if (candidates.isEmpty()) {
            return "Outros Produtos";
        }

        String keyword = candidates.get(0);
        String stem = stemmer.stem(keyword);

        return formatCategoryName(stem);
    }

    private List<String> extractKeywords(String text) {
        List<String> results = new ArrayList<>();
        String[] parts = text.split(" ");

        for (String w : parts) {
            if (w.length() < 4) continue;
            if (STOPWORDS.contains(w)) continue;
            if (!WORD_PATTERN.matcher(w).matches()) continue;

            results.add(w);
        }

        return results;
    }

    private String formatCategoryName(String stem) {
        if (stem == null || stem.isBlank()) {
            return "Outros Produtos";
        }

        String plural = stem + "s";
        return Character.toUpperCase(plural.charAt(0)) + plural.substring(1);
    }
}