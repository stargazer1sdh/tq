package la;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * 未处理空行
 */
public class Line {
	public String content;
	int id;// 句子的顺序号
	public int size;
	
	int tokenno;// token的数
	int nlerrno = 0;// 自然语言的错误数
	float nlerr_ratio;
	float nlerr_ratio2;
	int puncno = 0;// 标点数
	float punc_ratio;

	public Line(String content, int i) {
		this.content = content.trim();
		this.id = i;
		size = content.length();
	}

	private static List<String> keypuncs = Arrays.asList(";","=","(",")" ,"[","]" ,"{","}"  ,"int","long","char","float","double","void","static");
	public static boolean containsKeyPunc(String token) {
		return keypuncs.contains(token);
	}
}
