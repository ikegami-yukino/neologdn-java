package io.github.ikegamiyukino.neologdn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NeologdNormalizer {
    private static final char[] HYPHENS = { '˗', '֊', '‐', '‑', '‒', '–', '⁃', '⁻', '₋', '−' };
    private static final char[] CHOONPUS = { '﹣', '－', 'ｰ', '—', '―', '─', '━', 'ー' };
    private static final char[] TILDES = { '~', '∼', '∾', '〜', '〰', '～' };

    Set<Character> jpChars, basicLatin, hiphens, choonpus, tildes;

    Map<Character, Character> conversion_map = new HashMap<Character, Character>() {
        {
            int halfwidthOffset = 33; // !
            int fullwidthOffset = 65281; // ！
            for (int i = 0; i <= 93; ++i) {
                Character fullwidthChar = charFromCodePoint(i + fullwidthOffset);
                Character halfwidthChar = charFromCodePoint(i + halfwidthOffset);
                put(fullwidthChar, halfwidthChar);
            }
            // Python uses these quotation/currency characters rather than
            // their fullwidth ASCII counterparts.
            remove('＂');
            remove('＇');
            remove('＼');
            remove('｀');
            put('“', '\"');
            put('”', '\"');
            put('’', '\'');
            put('‘', '`');
            put('¥', '\\');
            put('ｱ', 'ア');
            put('ｲ', 'イ');
            put('ｳ', 'ウ');
            put('ｴ', 'エ');
            put('ｵ', 'オ');
            put('ｶ', 'カ');
            put('ｷ', 'キ');
            put('ｸ', 'ク');
            put('ｹ', 'ケ');
            put('ｺ', 'コ');
            put('ｻ', 'サ');
            put('ｼ', 'シ');
            put('ｽ', 'ス');
            put('ｾ', 'セ');
            put('ｿ', 'ソ');
            put('ﾀ', 'タ');
            put('ﾁ', 'チ');
            put('ﾂ', 'ツ');
            put('ﾃ', 'テ');
            put('ﾄ', 'ト');
            put('ﾅ', 'ナ');
            put('ﾆ', 'ニ');
            put('ﾇ', 'ヌ');
            put('ﾈ', 'ネ');
            put('ﾉ', 'ノ');
            put('ﾊ', 'ハ');
            put('ﾋ', 'ヒ');
            put('ﾌ', 'フ');
            put('ﾍ', 'ヘ');
            put('ﾎ', 'ホ');
            put('ﾏ', 'マ');
            put('ﾐ', 'ミ');
            put('ﾑ', 'ム');
            put('ﾒ', 'メ');
            put('ﾓ', 'モ');
            put('ﾔ', 'ヤ');
            put('ﾕ', 'ユ');
            put('ﾖ', 'ヨ');
            put('ﾗ', 'ラ');
            put('ﾘ', 'リ');
            put('ﾙ', 'ル');
            put('ﾚ', 'レ');
            put('ﾛ', 'ロ');
            put('ﾜ', 'ワ');
            put('ｦ', 'ヲ');
            put('ﾝ', 'ン');
            put('ｧ', 'ァ');
            put('ｨ', 'ィ');
            put('ｩ', 'ゥ');
            put('ｪ', 'ェ');
            put('ｫ', 'ォ');
            put('ｯ', 'ッ');
            put('ｬ', 'ャ');
            put('ｭ', 'ュ');
            put('ｮ', 'ョ');
            put('｡', '。');
            put('､', '、');
            put('･', '・');
            put('゛', 'ﾞ');
            put('゜', 'ﾟ');
            put('｢', '「');
            put('｣', '」');
            put('ｰ', 'ー');
        }
    };
    Map<Character, Character> kana_ten_map = new HashMap<Character, Character>() {
        {
            put('カ', 'ガ');
            put('キ', 'ギ');
            put('ク', 'グ');
            put('ケ', 'ゲ');
            put('コ', 'ゴ');
            put('サ', 'ザ');
            put('シ', 'ジ');
            put('ス', 'ズ');
            put('セ', 'ゼ');
            put('ソ', 'ゾ');
            put('タ', 'ダ');
            put('チ', 'ヂ');
            put('ツ', 'ヅ');
            put('テ', 'デ');
            put('ト', 'ド');
            put('ハ', 'バ');
            put('ヒ', 'ビ');
            put('フ', 'ブ');
            put('ヘ', 'ベ');
            put('ホ', 'ボ');
            put('ウ', 'ヴ');
            put('う', 'ゔ');
        }
    };
    Map<Character, Character> kana_maru_map = new HashMap<Character, Character>() {
        {
            put('ハ', 'パ');
            put('ヒ', 'ピ');
            put('フ', 'プ');
            put('ヘ', 'ペ');
            put('ホ', 'ポ');
            put('は', 'ぱ');
            put('ひ', 'ぴ');
            put('ふ', 'ぷ');
            put('へ', 'ぺ');
            put('ほ', 'ぽ');
        }
    };

    public NeologdNormalizer() {
        jpChars = buildJpCharsBlock();
        basicLatin = charsFromRange(0, 127);

        hiphens = arrayToHashSet(HYPHENS);
        choonpus = arrayToHashSet(CHOONPUS);
        tildes = arrayToHashSet(TILDES);
    }

    /**
     * Normalizes text using the defaults of Python neologdn 0.5.6.
     * @param sentence input text (not null)
     * @return normalized text
     */
    public String normalize(String sentence) {
        return normalize(sentence, 0, true, 8, "remove");
    }

    /**
     * Normalizes text and shortens repeated substrings.
     * @param sentence input text
     * @param repeat maximum repetitions to retain; zero disables shortening
     * @return normalized text
     */
    public String normalize(String sentence, int repeat) {
        return normalize(sentence, repeat, true, 8, "remove");
    }

    /**
     * Normalizes text with configurable Japanese space removal.
     * @param sentence input text
     * @param removeSpace whether to remove spaces around Japanese characters
     * @return normalized text
     */
    public String normalize(String sentence, boolean removeSpace) {
        return normalize(sentence, 0, removeSpace, 8, "remove");
    }

    /**
     * Normalizes text with configurable tilde handling.
     * @param sentence input text
     * @param tilde "remove", "ignore", "normalize", or "normalize_zenkaku"
     * @return normalized text
     */
    public String normalize(String sentence, String tilde) {
        return normalize(sentence, 0, true, 8, tilde);
    }

    /**
     * Normalizes text with repetition and space options.
     * @param sentence input text
     * @param repeat maximum repetitions to retain; zero disables shortening
     * @param removeSpace whether to remove spaces around Japanese characters
     * @return normalized text
     */
    public String normalize(String sentence, int repeat, boolean removeSpace) {
        return normalize(sentence, repeat, removeSpace, 8, "remove");
    }

    /**
     * Normalizes text with a limit on repeated substring length.
     * @param sentence input text
     * @param repeat maximum repetitions to retain; zero disables shortening
     * @param removeSpace whether to remove spaces around Japanese characters
     * @param maxRepeatSubstringLength maximum substring length in code points; zero means unlimited
     * @return normalized text
     */
    public String normalize(String sentence, int repeat, boolean removeSpace, int maxRepeatSubstringLength) {
        return normalize(sentence, repeat, removeSpace, maxRepeatSubstringLength, "remove");
    }

    /**
     * Normalizes text with the options of Python neologdn 0.5.6, in the same order.
     * Fullwidth spaces become ASCII spaces even when removal is disabled.
     * As in Python, one trailing ASCII space is removed regardless of removeSpace.
     *
     * @param sentence input text (not null)
     * @param repeat maximum repetitions to retain; zero disables shortening
     * @param removeSpace whether to remove spaces around Japanese characters
     * @param maxRepeatSubstringLength maximum substring length in code points; zero means unlimited
     * @param tilde "ignore" preserves tildes, "normalize" maps to '~',
     *              "normalize_zenkaku" maps to '〜'; other values remove tildes
     * @return normalized text
     * @throws IllegalArgumentException if a repetition parameter is negative
     */
    public String normalize(String sentence, int repeat, boolean removeSpace,
                            int maxRepeatSubstringLength, String tilde) {
        checkRepeatParameters(repeat, maxRepeatSubstringLength);
        int[] result = new int[sentence.codePointCount(0, sentence.length())];
        int pos = 0;
        int prev = 0;
        boolean latinSpace = false;

        for (int i = 0; i < sentence.length();) {
            int current = sentence.codePointAt(i);
            i += Character.charCount(current);
            if (current == ' ' || current == '　') {
                current = ' ';
                if ((pos == 0 || prev == ' ' || contains(jpChars, prev)) && removeSpace) {
                    continue;
                } else if (prev != '*' && pos > 0 && contains(basicLatin, prev)) {
                    latinSpace = true;
                    result[pos] = current;
                } else if (removeSpace) {
                    // Match Python's space handling after non-Latin characters.
                    if (pos > 0) {
                        pos--;
                    }
                } else {
                    result[pos] = current;
                }
            } else {
                if (contains(hiphens, current)) {
                    if (prev == '-') {
                        continue;
                    }
                    result[pos] = current = '-';
                    latinSpace = false;
                } else if (contains(choonpus, current)) {
                    if (prev == 'ー') {
                        continue;
                    }
                    result[pos] = current = 'ー';
                    latinSpace = false;
                } else if (contains(tildes, current)) {
                    if ("ignore".equals(tilde)) {
                        result[pos] = current;
                    } else if ("normalize".equals(tilde)) {
                        result[pos] = current = '~';
                    } else if ("normalize_zenkaku".equals(tilde)) {
                        result[pos] = current = '〜';
                    } else {
                        continue;
                    }
                    latinSpace = false;
                } else {
                    if (current <= Character.MAX_VALUE && conversion_map.containsKey((char) current)) {
                        current = conversion_map.get((char) current);
                    }
                    if (current == 'ﾞ' && pos > 0 && prev <= Character.MAX_VALUE
                            && kana_ten_map.containsKey((char) prev)) {
                        pos--;
                        current = kana_ten_map.get((char) prev);
                    } else if (current == 'ﾟ' && pos > 0 && prev <= Character.MAX_VALUE
                            && kana_maru_map.containsKey((char) prev)) {
                        pos--;
                        current = kana_maru_map.get((char) prev);
                    }
                    if (latinSpace && pos > 0 && contains(jpChars, current) && removeSpace) {
                        pos--;
                    }
                    latinSpace = false;
                    result[pos] = current;
                }
            }
            prev = current;
            pos++;
        }
        if (pos > 0 && result[pos - 1] == ' ') {
            pos--;
        }
        // Python decodes its buffer as UTF-32, consuming one leading byte order mark.
        int start = pos > 0 && result[0] == 0xFEFF ? 1 : 0;
        String normalized = new String(result, start, pos - start);
        return repeat == 0 ? normalized : shortenRepeat(normalized, repeat, maxRepeatSubstringLength);
    }

    /**
     * Shortens repeated substrings of up to eight Unicode code points without normalization.
     * @param text input text (not null)
     * @param repeatThreshold maximum repetitions to retain
     * @return shortened text
     */
    public String shortenRepeat(String text, int repeatThreshold) {
        return shortenRepeat(text, repeatThreshold, 8);
    }

    /**
     * Shortens repeated substrings using Python neologdn 0.5.6's scan order and bounds.
     * In particular, very short repeats such as "aa", "aaa", and "abab" are unchanged
     * with a threshold of one. This method does not normalize the input first.
     *
     * @param text input text (not null)
     * @param repeatThreshold maximum repetitions to retain; zero removes scanned substrings
     * @param maxRepeatSubstringLength maximum substring length in code points; zero means unlimited
     * @return shortened text
     * @throws IllegalArgumentException if a repetition parameter is negative
     */
    public String shortenRepeat(String text, int repeatThreshold, int maxRepeatSubstringLength) {
        checkRepeatParameters(repeatThreshold, maxRepeatSubstringLength);
        int[] buffer = text.codePoints().toArray();
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            int upper = (length - i) / 2;
            if (maxRepeatSubstringLength != 0 && maxRepeatSubstringLength < upper) {
                upper = maxRepeatSubstringLength + 1;
            }
            // The exclusive upper bound and its calculation before the inner loop
            // intentionally match Python, including after a previous length shrinks text.
            for (int size = 1; size < upper && i < length; size++) {
                int count = 1;
                int right = i + size;
                while (size <= length - right && equalSubstring(buffer, i, right, size)) {
                    count++;
                    right += size;
                }
                if (count > repeatThreshold) {
                    int start = i + size * repeatThreshold;
                    int end = Math.min(right, length);
                    System.arraycopy(buffer, end, buffer, start, length - end);
                    length -= end - start;
                }
            }
        }
        return new String(buffer, 0, length);
    }

    private static boolean equalSubstring(int[] text, int left, int right, int length) {
        for (int j = 0; j < length; j++) {
            if (text[left + j] != text[right + j]) {
                return false;
            }
        }
        return true;
    }

    private static void checkRepeatParameters(int repeat, int maxRepeatSubstringLength) {
        if (repeat < 0 || maxRepeatSubstringLength < 0) {
            throw new IllegalArgumentException("Repetition parameters must be non-negative");
        }
    }

    private static boolean contains(Set<Character> chars, int codePoint) {
        return codePoint <= Character.MAX_VALUE && chars.contains((char) codePoint);
    }

    public static Set<Character> charsFromRange(int start, int end) {
        Set<Character> charSet = new HashSet<Character>();
        for (int i = start; i <= end; ++i) {
            charSet.add(charFromCodePoint(i));
        }
        return charSet;
    }

    public static Set<Character> buildJpCharsBlock() {
        Set<Character> jpChars = new HashSet<Character>();
        jpChars.addAll(charsFromRange(19968, 40959)); // CJK UNIFIED IDEOGRAPHS
        jpChars.addAll(charsFromRange(12352, 12447)); // HIRAGANA
        jpChars.addAll(charsFromRange(12448, 12543)); // KATAKANA
        jpChars.addAll(charsFromRange(12289, 12351)); // CJK SYMBOLS AND PUNCTUATION
        jpChars.addAll(charsFromRange(65280, 65519)); // HALFWIDTH AND FULLWIDTH FORMS
        return jpChars;
    }

    public static Set<Character> arrayToHashSet(char[] arr) {
        Set<Character> result = new HashSet<Character>();
        for (int i = 0; i < arr.length; ++i) {
            result.add(arr[i]);
        }
        return result;
    }

    public static Character charFromCodePoint(int codePoint) {
        String c = new String(new int[] { codePoint }, 0, 1);
        return c.charAt(0);
    }
}
