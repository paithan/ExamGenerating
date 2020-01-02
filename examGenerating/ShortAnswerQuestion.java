import java.util.*;

/**
 * Models a single short-answer question.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
public class ShortAnswerQuestion extends ExamQuestion {
	//constructors
	/**
	 * Creates a dummy Question.
	 */
	public ShortAnswerQuestion() {
		super();
	}
    
	/**
	 * Creates a new ShortAnswerQuestion.
	 */
	public ShortAnswerQuestion(String text, String answer, int chapter, int section) {
        super();
		this.text = text;
        this.answer = answer;
        this.chapter = chapter;
        this.section = section;
	}
	
	@Override
    public boolean hasChoice(String choice) {
        return false;  //there are no choices here.
    }

    @Override
    public String getLatexString(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent + "\\begin{minipage}{\\textwidth}\n\\begin{sloppypar}\n\\question \n");
		builder.append(indent + "  " + this.text + "\\vspace{1.5cm}\n\n");
        builder.append(indent + "\\end{sloppypar}\n\\end{minipage}\n\n");
		return builder.toString();
    }
}
