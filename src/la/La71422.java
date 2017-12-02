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

public class La71422 {
	public static void main(String[] args) {
		String s = "Shouldn't gcc loudly fail when it cannot lay static objects out without wrapping around the end of the address space?\n" + 
				"Otherwise the resulting binary can, for example, crash. Or silently give wrong answer like in the example below.\n" + 
				"\n" + 
				"Source code:\n" + 
				"\n" + 
				"----------------------------------------------------------------------\n" + 
				"#include <stdint.h>\n" + 
				"#include <stdio.h>\n" + 
				"\n" + 
				"char a[SIZE_MAX / 2];\n" + 
				"char b[SIZE_MAX / 2];\n" + 
				"char c[SIZE_MAX / 2];\n" + 
				"\n" + 
				"int main(int argc, char **argv)\n" + 
				"{\n" + 
				"  (void)argv;\n" + 
				"  \n" + 
				"  a[argc] = 123;\n" + 
				"  printf(\"%d\\n\", b[argc]);\n" + 
				"\n" + 
				"  printf(\"&a = %p\\n\", (void *)&a);\n" + 
				"  printf(\"&b = %p\\n\", (void *)&b);\n" + 
				"  printf(\"&c = %p\\n\", (void *)&c);\n" + 
				"}\n" + 
				"----------------------------------------------------------------------\n" + 
				"\n" + 
				"Results:\n" + 
				"\n" + 
				"----------------------------------------------------------------------\n" + 
				"$ gcc -std=c11 -pedantic -Wall -Wextra -O3 -m32 test.c && ./a.out\n" + 
				"123\n" + 
				"&a = 0x8049740\n" + 
				"&b = 0x8049740\n" + 
				"&c = 0x88049740\n" + 
				"----------------------------------------------------------------------\n" + 
				"\n" + 
				"gcc version: gcc (GCC) 7.0.0 20160604 (experimental)";
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
				if(l.id <= 5 || l.id >= 24)  //text
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
				if(l.id <= 5 || l.id >= 24)  //text
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
				if(l.id <= 5 || l.id >= 24)  //text
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
