package br.com.kitchen.indexation.classification;

import java.text.Normalizer;
import java.util.Locale;

public class TextNormalizer {

    public static String normalize(String input) {
        if (input == null) return "";
        String lower = input.toLowerCase(Locale.ROOT);
        String noAccents = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noAccents.replaceAll("[^a-z0-9]+", " ").trim();
    }
}
