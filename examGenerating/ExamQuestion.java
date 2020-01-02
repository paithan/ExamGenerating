/**
 * This represents a question in an exam.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */
 
public abstract class ExamQuestion {

    //instance variables
    /**
     * The text of the question.
     */
    protected String text;

    /**
     * The solution to this question.
     */
    protected String answer;

    /**
     * The Chapter this question is from.
     */
    protected int chapter;

    /**
     * The section of the chapter this question is most relevant to.
     */
    protected int section;

    //constructors
    protected ExamQuestion() {
        this.text = "dummy";
        this.answer = "Unknown!";
        this.chapter = -1;
        this.section = -1;
    }

    //public methods

    /**
     * Determines whether this already has the given choice.
     * @param choice    The choice to look for, as a string.
     * @return          Whether this question has the choice specified as one of its answers.
     *
     */
    public abstract boolean hasChoice(String choice);

	/**
	 * Returns the entire LaTeX text for this question.
	 */
	public abstract String getLatexString(String indent);

    /**
     * Sets the text of this question.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the answer to this question.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Sets the answer to this question.
     */
    public void addChoice(String answer) {
        //Does nothing!
    }

    /**
     * Sets the answer to this question.
     */
    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    /**
     * Sets the answer to this question.
     */
    public void setSection(int section) {
        this.section = section;
    }

    /**
     * Gets the answer to this question.
     */
    public int getChapter() {
        return this.chapter;
    }

    /**
     * Gets the section of this question.
     */
    public int getSection() {
        return this.section;
    }

    /**
     * Gets the answer to this question.
     */
    public String getAnswer() {
        return this.answer;
    }
 

} //end of class ExamQuestion
