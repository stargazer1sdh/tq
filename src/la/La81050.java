package la;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class La81050 {
	public static void main(String[] args) {
		String s = "Compiling\n" + 
				"\n" + 
				"     #include <stdio.h>\n" + 
				"     int main()\n" + 
				"     {\n" + 
				"       char a = 'a';\n" + 
				"       printf(\"Size of char : %d\\n\",sizeof(a));\n" + 
				"       printf(\"Size of char : %d\\n\",sizeof('a'));\n" + 
				"       return 0;\n" + 
				"     }\n" + 
				"\n" + 
				"with   gcc x.c -o x -fexec-charset=utf-16   results in\n" + 
				"\n" + 
				"x.c: In function ‘main’:\n" + 
				"x.c:4:16: warning: multi-character character constant [-Wmultichar]\n" + 
				"       char a = 'a';\n" + 
				"                ^~~\n" + 
				"x.c:4:16: warning: overflow in implicit constant conversion [-Woverflow]\n" + 
				"x.c:6:43: warning: multi-character character constant [-Wmultichar]\n" + 
				"       printf(\"Size of char : %d\\n\",sizeof('a'));\n" + 
				"                                           ^~~\n" + 
				"x.c:8:0: internal compiler error: character 0xa is not unibyte in execution character set\n" + 
				"     }\n" + 
				"\n" + 
				"This has two issues:\n" + 
				"\n" + 
				" 1. The ICE.\n" + 
				"\n" + 
				" 2. The trailing } on a line of its own.\n" + 
				"\n" + 
				"This is with GCC 7.1.1 on x86_64-suse-linux; similarly was reported with\n" + 
				"GCC 5.";
		System.out.println(s);
		System.out.println();
		System.out.println();
		System.out.println();
		JLanguageTool langTool = null;
		langTool = new JLanguageTool(new AmericanEnglish());

		Scanner sc = new Scanner(s);
		List<Line> lines = new ArrayList<Line>();
		int i = 0;
		System.out.println();
		while (sc.hasNext()) {
			Line line = new Line(sc.nextLine(), i++);
			lines.add(line);
			if (line.content.length() > 0) {
				AnalyzedSentence sentence = null;
				try {
					sentence = langTool.getAnalyzedSentence(line.content);
				} catch (IOException e) {
					e.printStackTrace();
				}
				AnalyzedTokenReadings[] words = sentence.getTokensWithoutWhitespace();

				int punctuationNo = 0;
				for (AnalyzedTokenReadings w : words) {
					if (Line.containsKeyPunc(w.getToken())) {
						punctuationNo++;
					}
				}
				
				line.puncno = punctuationNo;
				line.tokenno = words.length - 1;
				line.punc_ratio = ((float) line.puncno) / line.tokenno;
			}
		}

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
			if(line.content.length()==0) 
				System.out.println(line.id+"\t"+line.content);
			else
				System.out.println(line.id+"\t"+line.punc_ratio+"\t\t"+line.content);
		}
		for (Line line : lines) {
			if(line.content.length()>0) {
				line.nlerr_ratio = line.nlerrno/(float)line.tokenno;
				line.nlerr_ratio2 = line.nlerrno/(float)line.size;
				
				System.out.println(line.content);
				System.out.println(line.nlerrno + "/" + line.tokenno);
				System.out.println(line.nlerr_ratio);
				System.out.println(line.nlerrno + "/" + line.size);
				System.out.println(line.nlerr_ratio2);
				System.out.println(line.puncno + "/" + line.tokenno);
				System.out.println(line.punc_ratio);
				System.out.println("~~~~~~~~~~~~~~~~");
			}
		}
		
		//statistical
		print_nlerr(lines);
		print_nlerr2(lines);
		print_punc(lines);
	}
	
	private static void print_nlerr(List<Line> ls) {
		System.out.println();
		System.out.println("```````` statistical nlerr_ratio:");
		ls.sort(new Comparator<Line>() {
			@Override
			public int compare(Line l0, Line l1) {
//				return l0.nlerr_ratio - l1.nlerr_ratio>0?1:-1;
				return new Float(l0.nlerr_ratio).compareTo(l1.nlerr_ratio);
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 10)  //text
					fs.offer(l.nlerr_ratio);
				else                         //code
					System.out.print(l.nlerr_ratio+"\t");
			}
		}
		System.out.println();
		System.out.println("text:");
		while(!fs.isEmpty()) {
			System.out.print(fs.poll()+"\t");
		}
		System.out.println();
	}
	private static void print_nlerr2(List<Line> ls) {
		System.out.println();
		System.out.println("```````` statistical nlerr_ratio2:");
		ls.sort(new Comparator<Line>() {
			@Override
			public int compare(Line l0, Line l1) {
//				return l0.nlerr_ratio2 - l1.nlerr_ratio2>0?1:-1;
				return new Float(l0.nlerr_ratio2).compareTo(l1.nlerr_ratio2);
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 10)  //text
					fs.offer(l.nlerr_ratio2);
				else                         //code
					System.out.print(l.nlerr_ratio2+"\t");
			}
		}
		System.out.println();
		System.out.println("text:");
		while(!fs.isEmpty()) {
			System.out.print(fs.poll()+"\t");
		}
		System.out.println();
	}
	private static void print_punc(List<Line> ls) {
		System.out.println();
		System.out.println("```````` statistical punc_ratio:");
		ls.sort(new Comparator<Line>() {
			@Override
			public int compare(Line l0, Line l1) {
				return new Float(l0.punc_ratio).compareTo(l1.punc_ratio);
//				return l0.punc_ratio - l1.punc_ratio>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 10)  //text
					fs.offer(l.punc_ratio);
				else                         //code
					System.out.print(l.punc_ratio+"\t");
			}
		}
		System.out.println();
		System.out.println("text:");
		while(!fs.isEmpty()) {
			System.out.print(fs.poll()+"\t");
		}
		System.out.println();
	}
}
