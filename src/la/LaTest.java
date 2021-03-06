package la;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class LaTest {
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

		Scanner sc = new Scanner(s);
		List<Line> lines = new ArrayList<Line>();
		int i = 0;
		while (sc.hasNext())
			lines.add(new Line(sc.nextLine(), i++));

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

		for (Line line : lines) {
//			if (line.size > 0) {
//				line.nlerr_ratio = ((float) line.nlerrno) / line.size;
//			}
//
//			if (line.size > 0) {
//				// System.out.println(line.id);
//				System.out.println(line.content);
//				System.out.println(line.nlerrno + "/" + line.content.length());
//				System.out.println(line.nlerr_ratio);
//				AnalyzedSentence sentence = null;
//				try {
//					sentence = langTool.getAnalyzedSentence(line.content);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				AnalyzedTokenReadings[] words = sentence.getTokensWithoutWhitespace();				
//				System.out.println(line.nlerrno + "/" + (words.length-1));
//				System.out.println(line.nlerrno/(float)(words.length-1));
//				System.out.println("~~~~~~~~~~~~~~~~");
//			}
		}

		
//		while (sc.hasNext()) {
//			Line line = new Line(sc.nextLine(), i++);
//			lines.add(line);
//
//			if (line.size > 0) {
//				List<RuleMatch> matches = null;
//				try {
//					matches = langTool.check(line.content);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				line.nlerrno = matches.size();
//				line.nlerr_ratio = ((float) line.nlerrno) / line.size;
//				
//				System.out.println(line.content);
//				System.out.println(line.nlerrno + "/" + line.size);
//				System.out.println(line.nlerr_ratio);
//				
//				 try {
//					 AnalyzedSentence sentence = langTool.getAnalyzedSentence(line.content);
//					AnalyzedTokenReadings[] words = sentence.getTokensWithoutWhitespace();
//					
//					System.out.println(line.nlerrno + "/" + (words.length-1));
//					System.out.println(line.nlerrno/(float)(words.length-1));
//
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				System.out.println("~~~~~~~~~~~~~~~~");
//			}
//		}
	}

}
