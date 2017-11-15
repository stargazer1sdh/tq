package la;

import java.io.IOException;
import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class LaTest {

	public static void main(String[] args) {
		JLanguageTool langTool = null;
		langTool = new JLanguageTool(new AmericanEnglish());
		// comment in to use statistical ngram data:
		//langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
		List<RuleMatch> matches = null;
		try {
			matches = langTool.check("A sentence with a error in the Hitchhiker's Guide tot he Galaxy");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (RuleMatch match : matches) {
		  System.out.println("Potential error at characters " +
		      match.getFromPos() + "-" + match.getToPos() + ": " +
		      match.getMessage());
		  System.out.println("Suggested correction(s): " +
		      match.getSuggestedReplacements());
		}
	}

}
