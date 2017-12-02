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

public class TextParser {
	static final String strdef = "#define";  static final String strdef2 = "#DEFINE";
	static final String strincl = "#include";  static final String strincl2 = "#INCLUDE";
	static final float codethreshold = 0.25f; 
	public static void main(String[] args) {
		String desc = "Created attachment 42167 [details]\n" + 
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
		System.out.println(desc);
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~");
		System.out.println();
		String block = getCode(desc);
		System.out.println(block);
	
	}
	public static String getCode(String desc) {
		List<Line> block = getCodelines(desc);
		StringBuilder sb = new StringBuilder();
		if(block != null) {
			for(Line l : block) {
				sb.append(l.content).append('\n');
			}
		}
		return sb.toString();
	}
	public static List<Line> getCodelines(String desc) {
		JLanguageTool langTool = null;
		langTool = new JLanguageTool(new AmericanEnglish());

		Scanner sc = new Scanner(desc);
		List<Line> lines = new ArrayList<Line>();
		int index = 0;
		System.out.println();
		while (sc.hasNext()) {
			Line line = new Line(sc.nextLine(), index++);
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
				
				if(line.content.startsWith(strdef) || line.content.startsWith(strdef2) || line.content.startsWith(strincl) || line.content.startsWith(strincl2))
					line.punc_ratio = 1;
				//===========
				boolean onlyequal = true;
				String trim = line.content.trim();
				for(int i=0;i<trim.length();i++) {
					if(trim.charAt(i) != '=') {
						onlyequal = false;
						break;
					}
				}
				if(onlyequal)
					line.punc_ratio = 0;
				//不以.结束
				if(trim.endsWith("."))
					line.punc_ratio = 0;
			}
		}

//		List<RuleMatch> matches = null;
//		try {
//			matches = langTool.check(desc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		for (RuleMatch match : matches) {
//			int id = match.getLine();
//			lines.get(id).nlerrno++;
//		}
//		for (Line line : lines) {
//			if(line.content.length()>0) {
//				line.nlerr_ratio = line.nlerrno/(float)line.tokenno;
//				line.nlerr_ratio2 = line.nlerrno/(float)line.size;
//			}
//		}

		List<List<Line>> blocks = new ArrayList<List<Line>>();
		boolean blockexist = false;
		List<Line> block = null;
		
		boolean skipped = false;
		Line skipline = null;
		for (Line line : lines) {
			if(line.content.length()==0)
				continue;
			if(line.punc_ratio >= codethreshold) {
				if(!blockexist) {   //new block
					block = new ArrayList<Line>();;
					block.add(line);
					blocks.add(block);
					blockexist = true;
				}else {
					if(skipped) {                 //has block, last low line
						block.add(skipline);
						block.add(line);
						blocks.add(block);
						skipped = false;
						skipline = null;
					}else {                       //has block, no last low line
						block.add(line);
						blocks.add(block);
					}
				}
			}else {
				if(blockexist && !skipped) {
					skipped = true;
					skipline = line;
				}else if(blockexist && skipped){                  //block ends
					skipped = false;
					skipline = null;
					
					blockexist = false;
					block = null;
				}
			}
		}
		
		if(blocks.isEmpty())
			return null;
		else {
			int maxl = 0;
			List<Line> longestblock = null;
			for(List<Line> codeblock : blocks) {
				int l = codeblock.size();
				if(maxl < l) {
					maxl = l;
					longestblock = codeblock;
				}
			}
			return longestblock;
		}

	}

}
