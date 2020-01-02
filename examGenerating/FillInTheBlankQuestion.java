import java.util.*;

/**
 * Models a single Fill-In-The-Blank style question.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
public class FillInTheBlankQuestion extends ExamQuestion {
	//constructors
	/**
	 * Creates a dummy Question.
	 */
	public FillInTheBlankQuestion() {
		super();
	}
    
	/**
	 * Creates a new Fill-In-The-Blank Question.
	 */
	public FillInTheBlankQuestion(String text, String answer, int chapter, int section) {
        super();
		this.text = text;
        this.answer = answer;
        this.chapter = chapter;
        this.section = section;
	}
	
	@Override
    public boolean hasChoice(String choice) {
        return true;  //all options are choices here! :)
    }

    @Override
    public String getLatexString(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent + "\\begin{minipage}{\\textwidth}\n\\begin{sloppypar}\n\\question \n");
		builder.append(indent + "  " + this.text + "\\vspace{.3cm}\n\n");
        builder.append(indent + "\\vspace{.4cm}\n\\end{sloppypar}\n\\end{minipage}\n\n");
		return builder.toString();
    }
}
