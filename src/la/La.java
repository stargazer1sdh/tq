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

public class La {
	public static void main(String[] args) {
		String s = "I would like to see -Wdefault-bitfield-sign turn on warnings when bitfields\n" + 
				"are declared in ways that are likely bugs:\n" + 
				"\n" + 
				"struct oink {\n" + 
				"  int foo : 5;        // warning: the signedness of foo is implementation defined\n" + 
				"  signed int bar : 5; // no warning\n" + 
				"  int : 5;            // no warning (since no name)\n" + 
				"  int booze : 1;      // warning: dubious signed one-bit bitfield\n" + 
				"  signed int baz : 1; // no warning.\n" + 
				"};\n" + 
				"\n" + 
				"(A signed bitfield on width one can contain {-1;0} or {0} depending on integer\n" + 
				"representation.  The latter allows for impressive optimizations, but surely\n" + 
				"someone meant to use unsigned.)\n" + 
				"\n" + 
				"Adding \"signed\" or \"unsigned\" as shown would silence the warnings.  Further,\n" + 
				"I would assume that foo's warning above would be silenced by the use of one\n" + 
				"of\n" + 
				"\n" + 
				"`-fsigned-bitfields'\n" + 
				"`-funsigned-bitfields'\n" + 
				"`-fno-signed-bitfields'\n" + 
				"`-fno-unsigned-bitfields'\n" + 
				"\n" + 
				"booze's warning should remain, though.";
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
				return l0.nlerr_ratio - l1.nlerr_ratio>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 13)  //text
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
				return l0.nlerr_ratio2 - l1.nlerr_ratio2>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 13)  //text
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
				return l0.punc_ratio - l1.punc_ratio>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 1 || l.id >= 13)  //text
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
