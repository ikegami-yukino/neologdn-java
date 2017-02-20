package io.github.ikegamiyukino.neologdn;

import java.util.HashMap;
import java.util.HashSet;

public class NeologdNormalizer {
	private final char[] HIPHENS = { '˗', '֊', '‐', '‑', '‒', '–', '⁃', '⁻', '₋', '−' };
	private final char[] CHOONPUS = { '﹣', '－', 'ｰ', '—', '―', '─', '━', 'ー' };
	private final char[] TILDES = { '~', '∼', '∾', '〜', '〰', '～' };
	HashSet<Character> jpChars, basicLatin, hiphens, choonpus, tildes;

	HashMap<Character, Character> conversion_map = new HashMap<Character, Character>() {
		{
			int halfwidthOffset = 33; // !
			int fullwidthOffset = 65281; // ！
			for (int i = 0; i <= 125; ++i) {
				String fullwidthChar = new String(new int[] { i + fullwidthOffset }, 0, 1);
				String halfwidthChar = new String(new int[] { i + halfwidthOffset }, 0, 1);
				put(fullwidthChar.charAt(0), halfwidthChar.charAt(0));
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
	HashMap<Character, Character> kana_ten_map = new HashMap<Character, Character>() {
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
	HashMap<Character, Character> kana_maru_map = new HashMap<Character, Character>() {
		{
			put('ハ', 'パ');
			put('ヒ', 'ピ');
			put('フ', 'プ');
			put('ヘ', 'ペ');
			put('ホ', 'ポ');
		}
	};

	public String normalize(String sentence) {
		String result = "";
		Character prev = ' ';
		boolean lattinSpace = false;

		for (int i = 0; i < sentence.length(); ++i) {
			char current = sentence.charAt(i);

			if (current == ' ' | current == '　') {
				current = ' ';
				if (prev == ' ' | jpChars.contains(prev)) {
					continue;
				} else if (prev != '*' && i > 0 && basicLatin.contains(prev)) {
					lattinSpace = true;
					result += current;
				}
			} else {
				if (hiphens.contains(current)) {
					if (prev == '-') {
						continue;
					} else {
						current = '-';
						result += current;
					}
				} else if (choonpus.contains(current)) {
					if (prev == 'ー') {
						continue;
					} else {
						current = 'ー';
						result += current;
					}
				} else if (tildes.contains(current)) {
					continue;
				} else {
					if (current == 'ﾞ' && kana_ten_map.containsKey(prev)) {
						result = result.substring(0, result.length()-1);
						current = kana_ten_map.get(prev);
					} else if (current == 'ﾟ' && kana_maru_map.containsKey(prev)) {
						result = result.substring(0, result.length()-1);
						current = kana_maru_map.get(prev);
					} else if (conversion_map.containsKey(current)) {
						current = conversion_map.get(current);
					}
					if (lattinSpace && jpChars.contains(current)) {
						result = result.substring(0, result.length()-1);
					}
					lattinSpace = false;
					result += current;
				}
			}
			prev = current;
		}
		return result;
	}

	public HashSet<Character> addCharsFromRange(HashSet<Character> charSet, int start, int end) {
		for (int i = start; i <= end; ++i) {
			String c = new String(new int[] { i }, 0, 1);
			charSet.add(c.charAt(0));
		}
		return charSet;
	}

	public HashSet<Character> buildJpCharsBlock() {
		HashSet<Character> jpChars = new HashSet<Character>();
		jpChars = addCharsFromRange(jpChars, 19968, 40959); // CJK UNIFIED
														   // IDEOGRAPHS
		jpChars = addCharsFromRange(jpChars, 12352, 12447); // HIRAGANA
		jpChars = addCharsFromRange(jpChars, 12448, 12543); // KATAKANA
		jpChars = addCharsFromRange(jpChars, 12289, 12351); // CJK SYMBOLS AND
														   // PUNCTUATION
		jpChars = addCharsFromRange(jpChars, 65280, 65519); // HALFWIDTH AND
														   // FULLWIDTH FORMS
		return jpChars;
	}

	public HashSet<Character> arrayToHashSet(char[] arr) {
		HashSet<Character> result = new HashSet<Character>();
		for (int i = 0; i < arr.length; ++i) {
			result.add(arr[i]);
		}
		return result;
	}

	public NeologdNormalizer() {
		jpChars = buildJpCharsBlock();
		basicLatin = addCharsFromRange(new HashSet<Character>(), 0, 127);

		hiphens = arrayToHashSet(HIPHENS);
		choonpus = arrayToHashSet(CHOONPUS);
		tildes = arrayToHashSet(TILDES);
	}
}