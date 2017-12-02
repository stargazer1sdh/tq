package la;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class zLa_lm {
	public static void main(String[] args) {
		String s = "Created attachment 29742 [details]\n" + "gcc49-pr19449.patch\n" + "\n"
				+ "Untested patch. There is another case where we'd better fold __builtin_constant_p right away, for static/extern function-local array dimensions:\n"
				+ "int y;\n" + "static char a[__builtin_constant_p (y) ? -1 : 1];\n"
				+ "extern char b[__builtin_constant_p (y) ? -1 : 1];\n" + "char d[__builtin_constant_p (y) ? -1 : 1];\n"
				+ "\n" + "void\n" + "foo (int x)\n" + "{\n" + "static char e[__builtin_constant_p (x) ? -1 : 1];\n"
				+ "extern char f[__builtin_constant_p (x) ? -1 : 1];\n"
				+ "auto char g[__builtin_constant_p (x) ? -1 : 1];\n" + "char h[__builtin_constant_p (x) ? -1 : 1];\n"
				+ "}\n" + "\n"
				+ "Right now this compiles fine for -O0, but for -O1 and above it errors on e and f (twice on the latter actually). When cfun == NULL, we always fold __builtin_constant_p right away, but when cfun is NULL, we don't consider static/extern. Not sure how to fix this issue though, because I think the declspecs aren't passed down to declarator parsing.";
		System.out.println(s);
		System.out.println();
		System.out.println();
		System.out.println();
		JLanguageTool langTool = null;
		langTool = new JLanguageTool(new AmericanEnglish());

		float code_nlerr_ratio_low = 0;
		float code_nlerr_ratio_high = 0;
		float code_punc_ratio_low = 0;
		float code_punc_ratio_high = 0;
		boolean codeinit = false;
		float text_nlerr_ratio_low = 0;
		float text_nlerr_ratio_high = 0;
		float text_punc_ratio_low = 0;
		float text_punc_ratio_high = 0;
		boolean textinit = false;

		Scanner sc = new Scanner(s);
		List<Line> lines = new ArrayList<Line>();
		int i = 0;
		while (sc.hasNext()) {
			Line line = new Line(sc.nextLine(), i++);
			lines.add(line);

			if (line.content.length() > 0) {
				List<RuleMatch> matches = null;
				AnalyzedSentence sentence = null;
				try {
					matches = langTool.check(line.content);
					sentence = langTool.getAnalyzedSentence(line.content);
				} catch (IOException e) {
					e.printStackTrace();
				}
				AnalyzedTokenReadings[] words = sentence.getTokensWithoutWhitespace();

				int punctuationNo = 0;
				for (AnalyzedTokenReadings w : words) {
					if (w.getToken().equals(";") || w.getToken().equals("=") || w.getToken().equals("(")
							|| w.getToken().equals(")")) {
						// System.out.print(w.getToken()+"\t");
						punctuationNo++;
					}
				}
				line.nlerrno = matches.size();
				line.puncno = punctuationNo;
				line.tokenno = words.length - 1;

				line.nlerr_ratio = ((float) line.nlerrno) / line.tokenno;
				System.out.println(line.content);
				System.out.println(line.nlerrno + "/" + line.tokenno);
				System.out.println(line.nlerr_ratio);

				line.punc_ratio = ((float) line.puncno) / line.tokenno;
				System.out.println();
				System.out.println(line.puncno + "/" + line.tokenno);
				System.out.println(line.punc_ratio);

				if (line.tokenno > 1) {//去掉
					if(line.id==1)//去掉
						continue;
					if (line.id <= 3 || line.id >= 18) {  
						if (!textinit) {
							text_nlerr_ratio_low = line.nlerr_ratio;
							text_nlerr_ratio_high = line.nlerr_ratio;
							text_punc_ratio_low = line.punc_ratio;
							text_punc_ratio_high = line.punc_ratio;
							textinit = true;
						} else {
							if (line.nlerr_ratio < text_nlerr_ratio_low) {
								text_nlerr_ratio_low = line.nlerr_ratio;
							} else if (line.nlerr_ratio > text_nlerr_ratio_high) {
								text_nlerr_ratio_high = line.nlerr_ratio;
							}

							if (line.punc_ratio < text_punc_ratio_low) {
								text_punc_ratio_low = line.punc_ratio;
							} else if (line.punc_ratio > text_punc_ratio_high) {
								text_punc_ratio_high = line.punc_ratio;
							}
						}
					} else {
						if (!codeinit) {
							code_nlerr_ratio_low = line.nlerr_ratio;
							code_nlerr_ratio_high = line.nlerr_ratio;
							code_punc_ratio_low = line.punc_ratio;
							code_punc_ratio_high = line.punc_ratio;
							codeinit = true;
						} else {
							if (line.nlerr_ratio < code_nlerr_ratio_low) {
								code_nlerr_ratio_low = line.nlerr_ratio;
							} else if (line.nlerr_ratio > code_nlerr_ratio_high) {
								code_nlerr_ratio_high = line.nlerr_ratio;
							}

							if (line.punc_ratio < code_punc_ratio_low) {
								code_punc_ratio_low = line.punc_ratio;
							} else if (line.punc_ratio > code_punc_ratio_high) {
								code_punc_ratio_high = line.punc_ratio;
							}
						}
					}
				}

				System.out.println("~~~~~~~~~~~~~~~~");
			}
		}

		System.out.println("code_nlerr_ratio:" + code_nlerr_ratio_low + "~" + code_nlerr_ratio_high
				+ "\t code_punc_ratio:" + code_punc_ratio_low + "~" + code_punc_ratio_high);
		System.out.println("text_nlerr_ratio:" + text_nlerr_ratio_low + "~" + text_nlerr_ratio_high
				+ "\t text_punc_ratio:" + text_punc_ratio_low + "~" + text_punc_ratio_high);

		System.out.println();
		System.out.println();

		for (Line line : lines) {
			if (line.content.length() == 0)
				continue;
			if (line.id <= 3 || line.id >= 18) {
				if (line.nlerr_ratio >= code_nlerr_ratio_low || line.punc_ratio >= code_punc_ratio_low) {
					System.out.println(line.content);
				}
			} else {
				if (line.nlerr_ratio <= text_nlerr_ratio_high || line.punc_ratio <= text_punc_ratio_high) {
					System.out.println(line.content);
				}
			}
		}
	}

}
