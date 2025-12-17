package br.com.kitchen.indexation.classification;

public class PortugueseStemmer {

    public String stem(String word) {
        if (word == null || word.length() < 4) {
            return word;
        }

        word = word.toLowerCase();

        if (word.endsWith("s") && word.length() > 4) {
            word = word.substring(0, word.length() - 1);
        }

        if (word.endsWith("a") || word.endsWith("e") || word.endsWith("o")) {
            word = word.substring(0, word.length() - 1);
        }

        String[] suffixes = {
                "mente", "mente", "zinho", "zinha", "zinho", "adora",
                "ador", "mento", "amento", "imento", "dora", "douro",
                "sor", "dor", "eiro", "eira", "ável", "ivel"
        };

        for (String s : suffixes) {
            if (word.endsWith(s) && word.length() > s.length() + 2) {
                return word.substring(0, word.length() - s.length());
            }
        }

        return word;
    }
}