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
            for (int i = 0; i <= 125; ++i) {
                Character fullwidthChar = charFromCodePoint(i + fullwidthOffset);
                Character halfwidthChar = charFromCodePoint(i + halfwidthOffset);
                put(fullwidthChar, halfwidthChar);
            }
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
            put('ﾞ', '゛');
            put('ﾟ', '゜');
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
        }
    };
    Map<Character, Character> kana_maru_map = new HashMap<Character, Character>() {
        {
            put('ハ', 'パ');
            put('ヒ', 'ピ');
            put('フ', 'プ');
            put('ヘ', 'ペ');
            put('ホ', 'ポ');
        }
    };

    public NeologdNormalizer() {
        jpChars = buildJpCharsBlock();
        basicLatin = charsFromRange(0, 127);

        hiphens = arrayToHashSet(HYPHENS);
        choonpus = arrayToHashSet(CHOONPUS);
        tildes = arrayToHashSet(TILDES);
    }

    public String normalize(String sentence) {
        StringBuilder result = new StringBuilder();
        Character prev = ' ';
        boolean latinSpace = false;

        for (int i = 0; i < sentence.length(); ++i) {
            char current = sentence.charAt(i);

            if (current == ' ' || current == '　') {
                current = ' ';
                if (prev == ' ' || jpChars.contains(prev)) {
                    continue;
                } else if (prev != '*' && i > 0 && basicLatin.contains(prev)) {
                    latinSpace = true;
                    result.append(current);
                }
            } else {
                if (hiphens.contains(current)) {
                    if (prev == '-') {
                        continue;
                    } else {
                        current = '-';
                        result.append(current);
                    }
                } else if (choonpus.contains(current)) {
                    if (prev == 'ー') {
                        continue;
                    } else {
                        current = 'ー';
                        result.append(current);
                    }
                } else if (tildes.contains(current)) {
                    continue;
                } else {
                    if (current == 'ﾞ' && kana_ten_map.containsKey(prev)) {
                        result.deleteCharAt(result.length()-1);
                        current = kana_ten_map.get(prev);
                    } else if (current == 'ﾟ' && kana_maru_map.containsKey(prev)) {
                        result.deleteCharAt(result.length()-1);
                        current = kana_maru_map.get(prev);
                    } else if (conversion_map.containsKey(current)) {
                        current = conversion_map.get(current);
                    }
                    if (latinSpace && jpChars.contains(current)) {
                        result.deleteCharAt(result.length()-1);
                    }
                    latinSpace = false;
                    result.append(current);
                }
            }
            prev = current;
        }
        return result.toString();
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