/**
 * A single Multiple-Choice question, including the different choices.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
import java.util.*;

public class MultipleChoiceQuestion extends ExamQuestion {

	//instance variables
    private List<String> choices;

	//constructors
	/**
	 * Creates a dummy True/False Question.
	 */
	public MultipleChoiceQuestion() {
		super();
        this.choices = new ArrayList<String>();
	}
    
	/**
	 * Creates a new True/False Question.
	 */
	public MultipleChoiceQuestion(String text, Vector<String> choices, int correctAnswer, int chapter, int section) {
        super();
		this.text = text;
        this.choices = (ArrayList<String>)choices.clone();
        this.answer = choices.elementAt(correctAnswer);
        this.chapter = chapter;
        this.section = section;
	}

    /**
     * Adds a choice to this question.
     * @param choice    The choice to add.
     *
     */
    public void addChoice(String choice) {
        this.choices.add(choice);
    }
    
    @Override
    public boolean hasChoice(String choice) {
        return this.choices.contains(choice);
    }

    /**
     * Sets the answer to this question.
     * @param answer    Answer to this question.
     *
     */
    public void setAnswer(String answer) {
        this.answer = answer;  //TODO: got rid of the case where it checks for an integer.  Make sure the xml files are cool with this!
    }

    @Override
    public String getLatexString(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent + "\\begin{minipage}{\\textwidth}\n\\question \n");
		builder.append(indent + "  " + this.text + "\\vspace{.3cm}\n\n");
		builder.append(indent + "\\begin{choices}\n");
        for (String choice : this.choices) {
            builder.append(indent + "  \\choice " + choice + "\n");
        }
        builder.append(indent + "\\end{choices}\n\\vspace{.4cm}\n\\end{minipage}\n\n");
		return builder.toString();
    }
}
