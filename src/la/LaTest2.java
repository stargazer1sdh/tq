package la;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.en.MorfologikAmericanSpellerRule;

public class LaTest2 {
	public static void main(String[] args) {
		// float f = 0;
		// System.out.println(f==0);
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
//		langTool.disableRule(MorfologikAmericanSpellerRule.RULE_ID);

		Scanner sc = new Scanner(s);
		List<Line> lines = new ArrayList<Line>();
		int i = 0;
		while (sc.hasNext())
			lines.add(new Line(sc.nextLine(), i++));

//		List<String> sentences = langTool.sentenceTokenize(s);
//		for (String se : sentences) {
//			System.out.println("\t~~~~~~~~~~~~" + se);
//		}

		List<RuleMatch> matches = null;
		try {
			matches = langTool.check(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (RuleMatch match : matches) {
			int id = match.getLine();
			lines.get(id).nlerrno++;
		}

//		for (Line line : lines) {
//			if (line.content.length() > 0) {
//				line.nlerr_ratio = ((float) line.nlerrno) / line.size;
//			}
//
//			if (line.content.length() > 0) {
//				System.out.println(line.id);
//				System.out.println(line.content);
//				System.out.println(line.nlerrno + "/" + line.content.length());
//				System.out.println(line.nlerr_ratio);
//				System.out.println("~~~~~~~~~~~~~~~~");
//			}
//		}

		for (RuleMatch match : matches) {
			System.out.println(match.getLine() + "L\t");
			// System.out.println(s.substring(match.getl, match.getToPos()));
			System.out.println(s.substring(match.getFromPos(), match.getToPos()));
			System.out.println("Potential error at characters " + match.getFromPos() + "-" + match.getToPos() + ": "
					+ match.getMessage());
			System.out.println("Suggested correction(s): " + match.getSuggestedReplacements());
			System.out.println();
		}
	}

}
