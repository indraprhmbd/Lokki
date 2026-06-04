package com.lokki.service;

public class PassphraseGenerator extends PasswordGenerator {

    private static final String[] WORDS = {
        "apple", "river", "lucky", "king", "forest", "moon", "star", "cloud", "rain", "snow",
        "ocean", "mountain", "tiger", "eagle", "lion", "bear", "wolf", "fox", "deer", "hawk",
        "blue", "red", "gold", "silver", "iron", "stone", "fire", "water", "wind", "thunder",
        "happy", "brave", "calm", "swift", "bright", "dark", "wise", "bold", "kind", "pure",
        "crystal", "dragon", "phoenix", "raven", "shadow", "light", "frost", "flame", "storm", "blade",
        "rocket", "orbit", "nova", "solar", "cosmos", "nebula", "comet", "eclipse", "zenith", "aurora",
        "castle", "knight", "sword", "shield", "crown", "throne", "jewel", "gem", "pearl", "ruby",
        "puzzle", "riddle", "secret", "golden", "silver", "copper", "bronze", "steel", "quartz", "amber",
        "desert", "garden", "meadow", "valley", "canyon", "island", "coral", "tide", "wave", "reef",
        "arrow", "falcon", "vortex", "fusion", "prism", "laser", "pixel", "robot", "cyber", "quantum",
        "anchor", "bridge", "compass", "candle", "lantern", "beacon", "signal", "echo", "sonic", "pulse"
    };

    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%&";

    private final int wordCount;
    private final String separator;
    private final boolean capitalize;
    private final boolean includeDigit;
    private final boolean includeSymbol;

    public PassphraseGenerator(int wordCount, String separator, boolean capitalize,
                               boolean includeDigit, boolean includeSymbol) {
        this.wordCount = wordCount;
        this.separator = separator;
        this.capitalize = capitalize;
        this.includeDigit = includeDigit;
        this.includeSymbol = includeSymbol;
    }

    @Override
    public String generate() {
        StringBuilder passphrase = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            if (i > 0) {
                passphrase.append(separator);
            }
            String word = WORDS[RANDOM.nextInt(WORDS.length)];
            if (capitalize) {
                word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            }
            passphrase.append(word);

            if (includeDigit && i == wordCount - 1) {
                passphrase.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
            }
            if (includeSymbol && i == wordCount - 1) {
                passphrase.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
            }
        }

        return passphrase.toString();
    }

}
