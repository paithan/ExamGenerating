import java.io.*;
import java.lang.*;

/**
 * Represents a True-False question.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */ 
public class TrueFalseQuestion extends ExamQuestion {

	//instance variables
	
	//constructors
	/**
	 * Creates a dummy True/False Question.
	 */
	public TrueFalseQuestion() {
		super();
	}
	/**
	 * Creates a new True/False Question.
	 */
	public TrueFalseQuestion(String text, boolean isTrue, int chapter, int section) {
        super();
		this.text = text;
		if (isTrue) {
			this.answer = "True";
		} else {
			this.answer = "False";
		}
        this.chapter = chapter;
        this.section = section;
	}
	
	//public methods

    @Override
    public boolean hasChoice(String choice) {
        return choice.equals("True") || choice.equals("False");
    }
	
	/**
	 * Returns the entire LaTeX text for this question.
	 */
	public String getLatexString(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent + "\\begin{minipage}{\\textwidth}\n\\question \n");
		builder.append(indent + "  " + this.text + "\\vspace{.3cm}\n\n");
		builder.append(indent + "\\begin{oneparchoices}\n");
		builder.append(indent + "  \\choice True\n");
		builder.append(indent + "  \\choice False\n");
		builder.append(indent + "\\end{oneparchoices}\n\\vspace{.4cm}\n\\end{minipage}\n\n");
		return builder.toString();
	}
	
	/**
	 * Main method for testing.
	 */
	public static void main(String[] args) throws IOException{
		TrueFalseQuestion q = new TrueFalseQuestion("The \\code{raw\\_input} function returns an integer.", false, 5, 11);
		String latexText = q.getLatexString("");
		System.out.println("LaTeX Text:");
		System.out.println(latexText);
		File output = new File("testExam.tex");
		FileWriter writer = new FileWriter(output);
		writer.write(latexText);
		writer.flush();
	}

} //end of class TrueFalseQuestion
