/**
 * A group of multiple exam questions.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */

import java.util.*;

public class ExamQuestionGroup {

    //constants

	//instance variables
    
    //the questions in this group
    private List<ExamQuestion> questions;

    //the text before all questions
    private String header;

    //the chapter number
    private int chapter;

    //the section number
    private int section;

    
	//constructors
	/**
	 * Creates a new group of exam questions.
	 */
	public ExamQuestionGroup() {
        this.questions = new ArrayList<ExamQuestion>();
        this.chapter = -1;
        this.section = -1;
	}

	//public methods

    /**
     * Sets the header suffix.  This will always be proceeded by "The next X questions refer to the following:".
     */
    public void setHeaderSuffix(String header) {
        this.header = header;
    }

    /**
     * Adds a question to this group
     * @param question  Question to add.
     */
    public void addQuestion(ExamQuestion question) {
        this.questions.add(question);
        if (this.chapter < question.getChapter()) {
            this.chapter = question.getChapter();
            this.section = question.getSection();
        }
        if (this.chapter == question.getChapter() && this.section < question.getSection()) {
            this.section = question.getSection();
        }
    }

    /**
     * Gets the number of questions in this group.
     * @return  Number of questions in this group.
     *
     */
    public int size() {
        return this.questions.size();
    }

    /**
     * Gets the chapter needed to answer this question.
     * @return  The chapter this question is from.
     */
    public int getChapter() {
        return this.chapter;
    }

    /**
     * Gets the section of the chapter needed to answer this question.
     * @return  The section this question is from.
     */
    public int getSection() {
        return this.section;
    }

    /**
     * Sets the chapter of these questions.
     */
    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    /**
     * Sets the section of these questions.
     */
    public void setSection(int section) {
        this.section = section;
    }
    
    /**
     * Gets the answers of the questions in this group.
     */
    public Vector<String> getAnswers() {
        Vector<String> answers = new Vector<String>();
        for (ExamQuestion question: this.questions) {
            answers.add(question.getAnswer());
        }
        return answers;
    }

    /**
     * Gets the LaTeX String representing this question group.
     * @param indent    Amount all questions should be indented
     * @return  String representing this question group for use in a LaTeX exam document.
     */
    public String getLatexString(String indent) {
		StringBuilder builder = new StringBuilder();
        if (this.size() > 1) {
		    builder.append(indent + "\\begin{minipage}{\\textwidth}\n");
		    builder.append(indent + "  \\fbox{The next " + this.size() + " questions refer to the following:}\n\\vspace{.4cm}\n\n");
		    builder.append(indent + "  " + this.header + "\n\\end{minipage}\\\\\n\n");
        }
        for (ExamQuestion question : this.questions) {
            builder.append(question.getLatexString(indent) + "\n");
        }
		return builder.toString();
    }

	//private methods

	//main method for testing
	public static void main(String[] args) {

	}
    
} //end of ExamQuestionGroup.java
