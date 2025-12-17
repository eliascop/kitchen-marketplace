package br.com.kitchen.indexation.classification;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CategoryClassifier {

    private static final double MIN_SCORE = 5.0;

    private final Map<String, List<KeywordRule>> rulesByCategory;
    private final PortugueseStemmer stemmer = new PortugueseStemmer();

    public CategoryClassifier() {
        this.rulesByCategory = buildRules();
    }

    public Optional<CategoryMatch> classify(String name, String description) {

        String text = normalize(
                (name == null ? "" : name) + " " +
                        (description == null ? "" : description)
        );

        double bestScore = 0;
        String bestCategory = null;

        for (Map.Entry<String, List<KeywordRule>> entry : rulesByCategory.entrySet()) {
            double score = scoreCategory(text, entry.getValue());

            if (score > bestScore) {
                bestScore = score;
                bestCategory = entry.getKey();
            }
        }

        if (bestCategory != null && bestScore >= MIN_SCORE) {
            log.info("Categoria classificada por regras ⇒ {} (score={})", bestCategory, bestScore);
            return Optional.of(new CategoryMatch(bestCategory, bestScore));
        }

        String[] words = text.split("[^a-zA-ZÀ-ÿ]+");

        for (String w : words) {
            if (w.length() < 4) continue;

            String stem = stemmer.stem(w);

            for (String category : rulesByCategory.keySet()) {
                if (normalize(category).contains(stem)) {
                    log.info("Categoria classificada via STEM fallback ⇒ {} (stem={})", category, stem);
                    return Optional.of(new CategoryMatch(category, MIN_SCORE));
                }
            }
        }

        log.info("Nenhuma categoria classificada pelo classifier");
        return Optional.empty();
    }
    private double scoreCategory(String text, List<KeywordRule> rules) {
        double total = 0;
        for (KeywordRule rule : rules) {
            String term = normalize(rule.term());
            if (text.contains(term)) {
                total += rule.weight();
            }
        }
        return total;
    }

    private String normalize(String input) {
        return TextNormalizer.normalize(input);
    }

    private Map<String, List<KeywordRule>> buildRules() {
        return Map.of(
                "Facas e Cutelaria", List.of(
                        kw("faca", 5),
                        kw("facas", 5),
                        kw("cutelo", 4),
                        kw("cutelaria", 6),
                        kw("chaira", 3),
                        kw("afiador", 3),
                        kw("canivete", 2)
                ),
                "Eletroportáteis", List.of(
                        kw("liquidificador", 8),
                        kw("processador", 5),
                        kw("batedeira", 7),
                        kw("mixer", 6),
                        kw("air fryer", 9),
                        kw("cafeteira eletrica", 7),
                        kw("sandwicheira", 6)
                ),
                "Organização e Armazenamento", List.of(
                        kw("pote", 4),
                        kw("potes", 4),
                        kw("organizador", 6),
                        kw("porta mantimentos", 7),
                        kw("porta tempero", 5),
                        kw("divisoria de gaveta", 6)
                ),
                "Panelas e Frigideiras", List.of(
                        kw("panela", 7),
                        kw("panelas", 7),
                        kw("frigideira", 7),
                        kw("caçarola", 7),
                        kw("wok", 5)
                ),
                "Pratos e Talheres", List.of(
                        kw("prato", 7),
                        kw("pratos", 7),
                        kw("aparelho de jantar", 9),
                        kw("jogo de jantar", 8),
                        kw("talheres", 6)
                ),
                "Utensílios de Cozinha", List.of(
                        kw("concha", 5),
                        kw("espatula", 5),
                        kw("pegador", 5),
                        kw("ralador", 5),
                        kw("peneira", 4),
                        kw("abridor", 4)
                ),
                "Térmicos e Conservação", List.of(
                        kw("garrafa termica", 8),
                        kw("isotermico", 5),
                        kw("cooler", 6),
                        kw("lancheira termica", 7)
                ),
                "Assadeiras e Formas", List.of(
                        kw("assadeira", 8),
                        kw("forma", 7),
                        kw("forma de bolo", 9),
                        kw("forma de pudim", 8)
                ),
                "Café e Chá", List.of(
                        kw("cafeteira", 7),
                        kw("coador", 7),
                        kw("bule", 6),
                        kw("prensa francesa", 7),
                        kw("moka", 6)
                ),
                "Medidores e Bowls", List.of(
                        kw("medidor", 7),
                        kw("xicara medidora", 8),
                        kw("colher medida", 7),
                        kw("bowl", 4),
                        kw("tigela", 4)
                )
        );
    }

    private KeywordRule kw(String term, double weight) {
        return new KeywordRule(term, weight);
    }
}