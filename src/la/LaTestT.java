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

public class LaTestT {
	public static void main(String[] args) {
		String s = "This PR is to track the existence of issues that will need to be fixed\n" + 
				"for full C99 support, and the reason for some XFAILs.\n" + 
				"\n" + 
				"For printf and scanf format checking to properly support C99 %j\n" + 
				"formats, GCC needs an internal notion of the intmax_t and uintmax_t\n" + 
				"types, meaning intmax_type_node and uintmax_type_node tree nodes for\n" + 
				"appropriate types specified in the target-specific headers.\n" + 
				"\n" + 
				"It also needs to communicate this information somehow to the tests\n" + 
				"c99-printf-1.c (which presently has XFAILs for the %j tests) and\n" + 
				"c90-printf-2.c (which presently has a generally incorrect definition\n" + 
				"of intmax_t but no XFAILSs).\n" + 
				"\n" + 
				"In general, GCC needs to be able to provide a <stdint.h> header, since\n" + 
				"this header is required by C99 for conforming freestanding\n" + 
				"implementations.\n" + 
				"\n" + 
				"See the thread starting at\n" + 
				"<URL:http://gcc.gnu.org/ml/gcc/2000-07/msg00142.html> for discussion\n" + 
				"of the associated issues.\n" + 
				"\n" + 
				"If GCC provides a <stdint.h>, then its inclusion in the relevant\n" + 
				"testcases seems a reasonable way of providing the definition without\n" + 
				"needing more special macros or typedefs.\n" + 
				"\n" + 
				"The thread starting at\n" + 
				"<URL:http://gcc.gnu.org/ml/gcc/2000-07/msg00504.html> shows\n" + 
				"significant controversy over whether GCC should install the headers\n" + 
				"required of freestanding implementations on all targets; in particular\n" + 
				"some targets have their own <stddef.h> and <limits.h> which can cause\n" + 
				"problems, although glibc knows what headers GCC provides and expects\n" + 
				"them to be provided by GCC (but at present has its own <stdint.h>).\n" + 
				"The mutual recursion between GCC's <limits.h> and glibc's <limits.h>,\n" + 
				"and the more general problems of interaction between <limits.h>\n" + 
				"headers to provide where appropriate the additional constants\n" + 
				"specified by POSIX but not by the C standard is very confusing and\n" + 
				"fragile.\n" + 
				"\n" + 
				"On some targets (e.g. aix64) the ABI can be changed by compiler\n" + 
				"options and the headers need to follow.  The existing methods for this\n" + 
				"to work with <limits.h> are fragile; on the more unusual targets\n" + 
				"(e.g. c4x), GCC's <limits.h> is simply broken.\n" + 
				"\n" + 
				"Conclusion\n" + 
				"==========\n" + 
				"\n" + 
				"GCC needs the mechanisms to provide correct <limits.h> and <stdint.h>\n" + 
				"headers for use with embedded targets and targets lacking those\n" + 
				"headers (especially <stdint.h>) in their C libraries.  In some cases\n" + 
				"it might defer to the system headers if correct, but the ability to\n" + 
				"interact with system headers knowing what GCC provides on systems\n" + 
				"where GCC is the native compiler (e.g. glibc) also seems useful at\n" + 
				"present.\n" + 
				"\n" + 
				"Fragile conditionals in the headers to allow for ABI choices by\n" + 
				"compiler options seem a bad way to go ahead.  A more promising\n" + 
				"approach might be to generate the headers for each multilib.  The\n" + 
				"existing mechanisms could run a generation program for each set of\n" + 
				"multilib options used; I have some proof-of-concept shell scripts that\n" + 
				"will generate limits.h (treating the compiler plus options as a black\n" + 
				"box) and most of stdint.h could be created on the same basis (with\n" + 
				"hints possibly provided by the compiler, e.g. through preprocessor\n" + 
				"defines, where unusual choices of types are made and the types can't\n" + 
				"simply be deduced from existing system headers).\n" + 
				"\n" + 
				"Release:\n" + 
				"2.96\n" + 
				"\n" + 
				"Environment:\n" + 
				"System: Linux decomino 2.2.16 #1 Thu Jun 8 00:26:22 UTC 2000 i686 unknown\n" + 
				"Architecture: i686\n" + 
				"\n" + 
				"	\n" + 
				"host: i686-pc-linux-gnu\n" + 
				"build: i686-pc-linux-gnu\n" + 
				"target: i686-pc-linux-gnu";
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
//					if(line.id==1)//去掉
//						continue;
//					if (line.id <= 3 || line.id >= 18) {  
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
//					} 
//					else {
//						if (!codeinit) {
//							code_nlerr_ratio_low = line.nlerr_ratio;
//							code_nlerr_ratio_high = line.nlerr_ratio;
//							code_punc_ratio_low = line.punc_ratio;
//							code_punc_ratio_high = line.punc_ratio;
//							codeinit = true;
//						} else {
//							if (line.nlerr_ratio < code_nlerr_ratio_low) {
//								code_nlerr_ratio_low = line.nlerr_ratio;
//							} else if (line.nlerr_ratio > code_nlerr_ratio_high) {
//								code_nlerr_ratio_high = line.nlerr_ratio;
//							}
//
//							if (line.punc_ratio < code_punc_ratio_low) {
//								code_punc_ratio_low = line.punc_ratio;
//							} else if (line.punc_ratio > code_punc_ratio_high) {
//								code_punc_ratio_high = line.punc_ratio;
//							}
//						}
//					}
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
//			if (line.id <= 3 || line.id >= 18) {
//				if (line.nlerr_ratio >= code_nlerr_ratio_low || line.punc_ratio >= code_punc_ratio_low) {
//					System.out.println(line.content);
//				}
//			} else {
//				if (line.nlerr_ratio <= text_nlerr_ratio_high || line.punc_ratio <= text_punc_ratio_high) {
//					System.out.println(line.content);
//				}
//			}
			if (line.nlerr_ratio == text_nlerr_ratio_high || line.punc_ratio == text_punc_ratio_high) {
				System.out.println(line.content);
			}
		}
	}

}
