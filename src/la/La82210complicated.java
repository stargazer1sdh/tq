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

public class La82210complicated {
	public static void main(String[] args) {
		String s = "Created attachment 42167 [details]\n" + 
				"Preprocessed source that can be used to reproduce the bug\n" + 
				"\n" + 
				"In the following program, writing elements of s.b causes overwrite of the last few elements of s.a:\n" + 
				"\n" + 
				"void fun(int size)\n" + 
				"{\n" + 
				"    int i;\n" + 
				"    struct {\n" + 
				"        _Alignas(16) struct {\n" + 
				"            short aa;\n" + 
				"        } a[size];\n" + 
				"        int b[size];\n" + 
				"    } s;\n" + 
				"\n" + 
				"    for (i = 0; i < size; i++) {\n" + 
				"        s.a[i].aa = 0x1234;\n" + 
				"    }\n" + 
				"\n" + 
				"    for (i = 0; i < size; i++) {\n" + 
				"        s.b[i] = 0;\n" + 
				"    }\n" + 
				"\n" + 
				"    for (i = 0; i < size; i++) {\n" + 
				"        printf(\"0x%04x \", s.a[i].aa);\n" + 
				"    }\n" + 
				"\n" + 
				"    printf(\"\\n\");\n" + 
				"}\n" + 
				"\n" + 
				"int main ()\n" + 
				"{\n" + 
				"    fun(15);\n" + 
				"}\n" + 
				"\n" + 
				"The above program produces the output:\n" + 
				"0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x0000 0x0000 0x0000 0x0000 0x0000 0x0000 0x0000 \n" + 
				"\n" + 
				"\n" + 
				"I have narrowed it down to the use of VLAIS in combination with the _Alignas specifier.\n" + 
				"\n" + 
				"Removing _Alignas causes the program to execute correctly and produce the output:\n" + 
				"\n" + 
				"0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 0x1234 \n" + 
				"\n" + 
				"Command line output:\n" + 
				"[gopal@localbuild ~]$gcc -v -save-temps -std=c11 -O0 -o test test.c\n" + 
				"Using built-in specs.\n" + 
				"COLLECT_GCC=gcc\n" + 
				"COLLECT_LTO_WRAPPER=/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/lto-wrapper\n" + 
				"Target: x86_64-pc-linux-gnu\n" + 
				"Configured with: /build/gcc/src/gcc/configure --prefix=/usr --libdir=/usr/lib --libexecdir=/usr/lib --mandir=/usr/share/man --infodir=/usr/share/info --with-bugurl=https://bugs.archlinux.org/ --enable-languages=c,c++,ada,fortran,go,lto,objc,obj-c++ --enable-shared --enable-threads=posix --enable-libmpx --with-system-zlib --with-isl --enable-__cxa_atexit --disable-libunwind-exceptions --enable-clocale=gnu --disable-libstdcxx-pch --disable-libssp --enable-gnu-unique-object --enable-linker-build-id --enable-lto --enable-plugin --enable-install-libiberty --with-linker-hash-style=gnu --enable-gnu-indirect-function --disable-multilib --disable-werror --enable-checking=release --enable-default-pie --enable-default-ssp\n" + 
				"Thread model: posix\n" + 
				"gcc version 7.2.0 (GCC) \n" + 
				"COLLECT_GCC_OPTIONS='-v' '-save-temps' '-std=c11' '-O0' '-o' 'test' '-mtune=generic' '-march=x86-64'\n" + 
				" /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/cc1 -E -quiet -v test.c -mtune=generic -march=x86-64 -std=c11 -O0 -fpch-preprocess -o test.i\n" + 
				"ignoring nonexistent directory \"/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../x86_64-pc-linux-gnu/include\"\n" + 
				"#include \"...\" search starts here:\n" + 
				"#include <...> search starts here:\n" + 
				" /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/include\n" + 
				" /usr/local/include\n" + 
				" /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/include-fixed\n" + 
				" /usr/include\n" + 
				"End of search list.\n" + 
				"COLLECT_GCC_OPTIONS='-v' '-save-temps' '-std=c11' '-O0' '-o' 'test' '-mtune=generic' '-march=x86-64'\n" + 
				" /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/cc1 -fpreprocessed test.i -quiet -dumpbase test.c -mtune=generic -march=x86-64 -auxbase test -O0 -std=c11 -version -o test.s\n" + 
				"GNU C11 (GCC) version 7.2.0 (x86_64-pc-linux-gnu)\n" + 
				"        compiled by GNU C version 7.2.0, GMP version 6.1.2, MPFR version 3.1.5-p2, MPC version 1.0.3, isl version isl-0.18-GMP\n" + 
				"\n" + 
				"GGC heuristics: --param ggc-min-expand=100 --param ggc-min-heapsize=131072\n" + 
				"GNU C11 (GCC) version 7.2.0 (x86_64-pc-linux-gnu)\n" + 
				"        compiled by GNU C version 7.2.0, GMP version 6.1.2, MPFR version 3.1.5-p2, MPC version 1.0.3, isl version isl-0.18-GMP\n" + 
				"\n" + 
				"GGC heuristics: --param ggc-min-expand=100 --param ggc-min-heapsize=131072\n" + 
				"Compiler executable checksum: 7c9818d05f48c6fe376de9e4c491b54f\n" + 
				"COLLECT_GCC_OPTIONS='-v' '-save-temps' '-std=c11' '-O0' '-o' 'test' '-mtune=generic' '-march=x86-64'\n" + 
				" as -v --64 -o test.o test.s\n" + 
				"GNU assembler version 2.29 (x86_64-pc-linux-gnu) using BFD version (GNU Binutils) 2.29\n" + 
				"COMPILER_PATH=/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/:/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/:/usr/lib/gcc/x86_64-pc-linux-gnu/:/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/:/usr/lib/gcc/x86_64-pc-linux-gnu/\n" + 
				"LIBRARY_PATH=/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/:/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../lib/:/lib/../lib/:/usr/lib/../lib/:/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../:/lib/:/usr/lib/\n" + 
				"COLLECT_GCC_OPTIONS='-v' '-save-temps' '-std=c11' '-O0' '-o' 'test' '-mtune=generic' '-march=x86-64'\n" + 
				" /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/collect2 -plugin /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/liblto_plugin.so -plugin-opt=/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/lto-wrapper -plugin-opt=-fresolution=test.res -plugin-opt=-pass-through=-lgcc -plugin-opt=-pass-through=-lgcc_s -plugin-opt=-pass-through=-lc -plugin-opt=-pass-through=-lgcc -plugin-opt=-pass-through=-lgcc_s --build-id --eh-frame-hdr --hash-style=gnu -m elf_x86_64 -dynamic-linker /lib64/ld-linux-x86-64.so.2 -pie -o test /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../lib/Scrt1.o /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../lib/crti.o /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/crtbeginS.o -L/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0 -L/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../lib -L/lib/../lib -L/usr/lib/../lib -L/usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../.. test.o -lgcc --as-needed -lgcc_s --no-as-needed -lc -lgcc --as-needed -lgcc_s --no-as-needed /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/crtendS.o /usr/lib/gcc/x86_64-pc-linux-gnu/7.2.0/../../../../lib/crtn.o\n" + 
				"COLLECT_GCC_OPTIONS='-v' '-save-temps' '-std=c11' '-O0' '-o' 'test' '-mtune=generic' '-march=x86-64'";
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
				return new Float(l0.nlerr_ratio).compareTo(l1.nlerr_ratio);
//				return l0.nlerr_ratio - l1.nlerr_ratio>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 4 || l.id >= 34)  //text
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
				return new Float(l0.nlerr_ratio2).compareTo(l1.nlerr_ratio2);
//				return l0.nlerr_ratio2 - l1.nlerr_ratio2>0?1:-1;
			}
		});
		
		Queue<Float> fs = new LinkedList<Float>();
		System.out.println("code:");
		for(Line l : ls) {
			if(l.content.length()>0) {
				if(l.id <= 4 || l.id >= 34)  //text
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
				if(l.id <= 4 || l.id >= 34)  //text
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
