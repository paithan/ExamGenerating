import java.io.*;
import java.lang.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;

/**
 * Main class for Exam Generating
 * Much of the XML-SAX handling code translated from this page: http://java.sun.com/developer/technicalArticles/xml/mapping/
 *
 * Prior to running this, create the file XYZquestions.xml, where XYZ is the number of the course.  Store that file either in the same directory as this code or in the relative directory, ../XYZ/, again, where XYZ is the number of the course.
 *
 * @author Kyle Burke <paithanq@gmail.com>
 */ 
public class ExamGenerator extends DefaultHandler {

	//instance variables

    //when building a question from the XML, this stores that data
    private ExamQuestion currentQuestion;

    //when building a cohesive group of questions from the XML, this stores that
    private ExamQuestionGroup currentGroup;
    
    // Buffer for collecting data from // the "characters" SAX event.
    private CharArrayWriter contents;

    //list of True/False questions from the file.
    private ArrayList<ExamQuestionGroup> trueFalseQuestions;

    //list of Multiple Choice questions from the file.
    private ArrayList<ExamQuestionGroup> multipleChoiceQuestions;
    
    //list of short answer questions from the file.
    private ArrayList<ExamQuestionGroup> shortAnswerQuestions;

    //list of fill-in-the-blank questions from the file.
    private ArrayList<ExamQuestionGroup> fillInTheBlankQuestions;

    //the program code for the course.(e.g. "CS" or "MA" or "MATH") 
    private String programCode;
    
    //the number of the course this will generate exams for
    private String courseNumber;
    
    //whether there will be multiple questions in the current group
    private boolean inMultipleQuestionGroup;
	
	//constructors

    /**
     * Creates a new exam generator.
     *
     * @param courseNumber  The number of the class.  Will be used to get the questions file, as described in the header.
     * @param programCode  The (usually alphabetical) code for the program or discipline.  E.g. "CS" or "Math".
     */
	public ExamGenerator(String courseNumber, String programCode) {
		super();
        this.contents = new CharArrayWriter();
        this.trueFalseQuestions = new ArrayList<ExamQuestionGroup>();
        this.multipleChoiceQuestions = new ArrayList<ExamQuestionGroup>();
        this.shortAnswerQuestions = new ArrayList<ExamQuestionGroup>();
        this.fillInTheBlankQuestions = new ArrayList<ExamQuestionGroup>();
        this.currentQuestion = null;
        this.currentGroup = null;
        this.courseNumber = courseNumber;
        this.programCode = programCode;
        this.inMultipleQuestionGroup = false;
        
        /*
	    //get the questions from here.
	    String xmlFileName = this.courseNumber + "questions.xml";
	    
	    //first look for XYZquestions.xml in the class directory.
	    File teachingDirectory = new File("").getAbsoluteFile().getParentFile();
	    File courseDirectory = new File(teachingDirectory, ("" + courseNumber) + File.separator);
	    System.out.println("Course home directory: " + courseDirectory.getAbsolutePath());
	    File xmlFile = new File(courseDirectory, xmlFileName);
	    
	    //if it's not there, look in this directory
	    if (!xmlFile.exists()) {
	        xmlFile = new File(xmlFileName);
	    }
	    */
	    
	    
	    File xmlFile = this.getQuestionsFile();
	    
	    System.out.println("File found: " + xmlFile.getAbsolutePath());
	    
	    //fetch the questions from the xml file!
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(this);
			reader.setErrorHandler(this);
			reader.parse(new InputSource(new FileInputStream(xmlFile)));
 		} catch (SAXException e) {
   			System.err.println(e.getMessage());
   			System.err.println("*~*~*~*~*~There is something wrong with the XML file!*~*~*~*~*\n  *~*(Probably there is a '<'... try changing to use '>' instead.)  You should be able to find the place this is happening by the messages above to see which questions have been accepted.*~*");
 		} catch (FileNotFoundException fnfe) {
 		    System.err.println(fnfe.getMessage());
 		    System.err.println("Didn't find the input (.xml) file!");
 		} catch (IOException ioe) {
 		    System.err.println(ioe.getMessage());
 		    System.err.println("Didn't find the input (.xml) file!");
 		}
 		
 		//now calculate and display some stats about the XML file!
        
        int numberOfTrueFalseQuestionsInXML = 0;
        for (ExamQuestionGroup group : this.trueFalseQuestions) {
            numberOfTrueFalseQuestionsInXML += group.size();
        }
        
        int numberOfMultipleChoiceQuestionsInXML = 0;
        for (ExamQuestionGroup group : this.multipleChoiceQuestions) {
            numberOfMultipleChoiceQuestionsInXML += group.size();
        }
        
        int numberOfShortAnswerQuestionsInXML = 0;
        for (ExamQuestionGroup group : this.shortAnswerQuestions) {
            numberOfShortAnswerQuestionsInXML += group.size();
        }
        
        int numberOfFillInTheBlankQuestionsInXML = 0;
        for (ExamQuestionGroup group : this.fillInTheBlankQuestions) {
            numberOfFillInTheBlankQuestionsInXML += group.size();
        }
        
        System.out.println("Finished gathering questions from the XML file!");
        
        System.out.println("Some stats about the XML file you're using:\n" 
            + numberOfTrueFalseQuestionsInXML + " True/False Questions total.\n" 
            + numberOfMultipleChoiceQuestionsInXML + " Multiple Choice Questions total.\n" 
            + numberOfShortAnswerQuestionsInXML + " Short Answer Questions total.\n" 
            + numberOfFillInTheBlankQuestionsInXML + " Fill-In-The-Blank Questions total.");
	}
	
	/** 
	 * Writes the stats of the database to a text file.
	 */
	public void writeDatabaseStats() throws IOException {
        File directory = this.getFileCreationFolder();
	    File reportFile = new File(directory, this.programCode + this.courseNumber + "QuestionsReport.txt");
	    FileWriter writer = new FileWriter(reportFile);
	    writer.write(this.getDatabaseStats());
	    writer.flush();
	}
	
	// Returns an array of possible XML file names. 
	private String[] getPossibleXmlFileNames() {
        return new String[] {
            this.courseNumber + "questions.xml", 
            this.courseNumber + "Questions.xml", 
            this.programCode + this.courseNumber + "questions.xml", 
            this.programCode + this.courseNumber + "Questions.xml", 
            "questions.xml", 
            "Questions.xml"
        }; 
    }
    
    // gets the directory where we will put files (the exam itself and the report)
    private File getFileCreationFolder() {
        File currentDirectory = new File("").getAbsoluteFile();
        File parentDirectory = currentDirectory.getParentFile();
        File externalDirectory = new File(parentDirectory, ((String) this.courseNumber) + File.separator);
        if (externalDirectory.exists()) {
            return externalDirectory;
        } else {
            File internalDirectory = new File(currentDirectory, ((String) this.courseNumber) + File.separator);
            if (!internalDirectory.exists()) {
                try {
                    internalDirectory.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("No external folder exists for this course and I could not create an internal folder!");
                }
            }
            return internalDirectory;
        }
    }
    
    //gets the questions file, from the appropriate folder.
    // It looks in an external folder first, then in an internal folder, then just in an internal file.
    private File getQuestionsFile() {
        if (this.hasExternalFolderWithQuestions()) {
            return this.getQuestionsFromExternalFolder();
        } else if (this.hasInternalFolderWithQuestions()) {
            return this.getQuestionsFromInternalFolder();
        } else if (this.hasLocalQuestions()) {
            return this.getLocalQuestions();
        } else {
            throw new RuntimeException("No questions file exists for this class!");
        }
    }
	
	// Returns whether there's an XML in a specific folder.
	private boolean hasQuestionsInFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
            //throw new RuntimeException("Looking for questions file in a directory that doesn't exist: " + folder);
        }
        String[] possibleXmlFileNames = this.getPossibleXmlFileNames();
        for (int i = 0; i < possibleXmlFileNames.length; i++) {
            String xmlFileName = possibleXmlFileNames[i];
            File xmlFile = new File(folder, xmlFileName);
            if (xmlFile.exists()) {
                return true;
            }
        }
        return false;
	}
	
	// Returns whether there's an XML file in an external folder for this course.
    private boolean hasExternalFolderWithQuestions() {
        File parentDirectory = new File("").getAbsoluteFile().getParentFile();
        File courseDirectory = new File(parentDirectory, ((String) this.courseNumber) + File.separator);
        return this.hasQuestionsInFolder(courseDirectory);
    }
    
    // Returns whether there's an XML file in an internal folder for this course.
    private boolean hasInternalFolderWithQuestions() {
        File currentDirectory = new File("").getAbsoluteFile();
        File courseDirectory = new File(currentDirectory, ((String) this.courseNumber) + File.separator);
        return this.hasQuestionsInFolder(courseDirectory);
    }
    
    // Returns whether there's an XML file right in this directory for this course.
    private boolean hasLocalQuestions() {
        return this.hasQuestionsInFolder(new File("").getAbsoluteFile());
    }
    
    // Gets the XML file in an external folder if one exists. 
    private File getQuestionsInFolder(File folder) {
        String[] possibleXmlFileNames = this.getPossibleXmlFileNames();
        if (this.hasQuestionsInFolder(folder)) {
            for (int i = 0; i < possibleXmlFileNames.length; i++) {
                String xmlFileName = possibleXmlFileNames[i];
                File xmlFile = new File(folder, xmlFileName);
                if (xmlFile.exists()) {
                    return xmlFile;
                }
            }
        } 
        throw new RuntimeException("No questions file exists in: " + folder);
    }
    
    // Gets the XML file in an external folder if one exists. 
    private File getQuestionsFromExternalFolder() {
        if (this.hasExternalFolderWithQuestions()) {
            File parentDirectory = new File("").getAbsoluteFile().getParentFile();
            File courseDirectory = new File(parentDirectory, ((String) this.courseNumber) + File.separator);
            return this.getQuestionsInFolder(courseDirectory);
        } else {
            throw new RuntimeException("No questions file exists in an external folder!");
        }
    }
    
    // Gets the XML file in an internal subfolder for this course
    private File getQuestionsFromInternalFolder() {
        if (this.hasInternalFolderWithQuestions()) {
            File currentDirectory = new File("").getAbsoluteFile();
            File courseDirectory = new File(currentDirectory, ((String) this.courseNumber) + File.separator);
            return this.getQuestionsInFolder(courseDirectory);
        } else {
            throw new RuntimeException("No questions file exists in an internal subfolder!");
        }
    }
    
    // Gets the XML file locally from right in here.
    private File getLocalQuestions() {
        if (this.hasLocalQuestions()) {
            return this.getQuestionsInFolder(new File("").getAbsoluteFile());
        } else {
            throw new RuntimeException("No questions file exists in this directory!");
        }
    }

    
    /** 
     * Fetches the stats for the question database for a course.
     */
    private String getDatabaseStats() throws IOException {
        StringBuilder builder = new StringBuilder();
        ArrayList<ExamQuestionGroup> allQuestions = new ArrayList<ExamQuestionGroup>();
        allQuestions.addAll(this.trueFalseQuestions);
        allQuestions.addAll(this.multipleChoiceQuestions);
        allQuestions.addAll(this.shortAnswerQuestions);
        //allQuestions.addAll(this.fillInTheBlankQuestions);  //who cares about these? :-P
        builder.append("Total questions: " + this.getNumberOfQuestions(allQuestions) + "    (not counting Fill In The Blanks)\n");
        int minChapter = this.getMinimumChapterFromQuestions(allQuestions);
        int maxChapter = this.getMaximumChapterFromQuestions(allQuestions);
        for (int chapter = minChapter; chapter < maxChapter + 1; chapter++) {
            int minSection = this.getMinimumSectionFromQuestionsInChapter(allQuestions, chapter);
            int maxSection = this.getMaximumSectionFromQuestionsInChapter(allQuestions, chapter);
            int numberTFQuestions = this.getNumberOfQuestions(this.trueFalseQuestions);
            int numberMCQuestions = this.getNumberOfQuestions(this.multipleChoiceQuestions);
            int numberSAQuestions = this.getNumberOfQuestions(this.shortAnswerQuestions);
            int numberFITBQuestions = this.getNumberOfQuestions(this.fillInTheBlankQuestions);
            ArrayList<ExamQuestionGroup> questionsInSection = new ArrayList<ExamQuestionGroup>();
            int numberOfQuestions = this.getNumberOfQuestionsInChapter(allQuestions, chapter);
            builder.append("\n##### Total in Chapter " + chapter + ": " + numberOfQuestions + "   (not Fill-In-The-Blanks) #####\n");
            builder.append("#####    T/F: " + this.getNumberOfQuestionsInChapter(this.trueFalseQuestions, chapter) 
                + "  | M-C: " + this.getNumberOfQuestionsInChapter(this.multipleChoiceQuestions, chapter) 
                + "  | S-A: " + this.getNumberOfQuestionsInChapter(this.shortAnswerQuestions, chapter) 
                + "  | F-i-t-B: " + this.getNumberOfQuestionsInChapter(this.fillInTheBlankQuestions, chapter) + "  ######\n");
            for (int section = minSection; section < maxSection + 1; section++) {
                numberOfQuestions = this.getNumberOfQuestionsInSection(allQuestions, chapter, section);
                if (numberOfQuestions > 0) {
                    builder.append("In Section " + chapter + "." + section + ": " + numberOfQuestions + "\n");
                    builder.append("T/F: " + this.getNumberOfQuestionsInSection(this.trueFalseQuestions, chapter, section) 
                        + "  | M-C: " + this.getNumberOfQuestionsInSection(this.multipleChoiceQuestions, chapter, section) 
                        + "  | S-A: " + this.getNumberOfQuestionsInSection(this.shortAnswerQuestions, chapter, section) 
                        + "  | F-i-t-B: " + this.getNumberOfQuestionsInSection(this.fillInTheBlankQuestions, chapter, section) + "\n");
                }
                
            }
        }
        return builder.toString();
    } 
        
	
	/**
	 * Creates an exam consisting of all the questions in the database.
	 */
	public void generateMasterExam() throws IOException {
	    this.generateExamFromQuestions(this.programCode + " " + this.courseNumber + " Master Exam", "../" + this.courseNumber + "/" + this.courseNumber + "MasterExam", this.trueFalseQuestions, 1, this.multipleChoiceQuestions, 1, this.shortAnswerQuestions, 1, this.fillInTheBlankQuestions, 1, false);
 	}

    /**
     * Generates the exam.
     *
     * @param courseSection    Section of the course.
     * @param semester    The season the semester is in (Fall or Spring)
     * @param year    The calendar year of this exam.
     * @param examTitle    The title of this exam.  Number or Final or Practice, etc.
     * @param numberTrueFalse    The number of T/F questions.
     * @param trueFalseValue    The value of one T/F question.
     * @param numberMultipleChoice    The number of M-C questions.
     * @param multipleChoiceValue    The value of one M-C question.
     * @param numberFillInTheBlank    The number of F-i-t-B questions.
     * @param fillInTheBlankValue    The value of one F-i-t-B question.
     * @param minChapter    The minimum chapter to use in this exam.
     * @param minSection    The minimum chapter in minSection to use in this exam.
     * @param maxChapter    The maximum chapter to use in this exam.
     * @param maxSection    The maximum section in maxChapter to use in this exam.
     */
    public void generateRandomExam(String examTitle, String fileNamePrefix, int numberTrueFalse, int trueFalseValue, int numberMultipleChoice, int multipleChoiceValue, int numberShortAnswer, int shortAnswerValue, int numberFillInTheBlank, int fillInTheBlankValue, int minChapter, int minSection, int maxChapter, int maxSection, boolean includeCoverAndBackPage) throws IOException {

        System.out.println("!!!!!!!!!!!! minChapter: " + minChapter + "!!!!!!!!!!!!!!!!!");
        
        System.out.println("!!!!!!!!!!!! minSection: " + minSection + "!!!!!!!!!!!!!!!!!");
        

        ArrayList<ExamQuestionGroup> trueFalseQuestionsForExam = this.getRandomQuestionsFrom(this.trueFalseQuestions, numberTrueFalse, minChapter, minSection, maxChapter, maxSection);
        
        System.out.println("Gathered True-False Questions.");

        ArrayList<ExamQuestionGroup> multipleChoiceQuestionsForExam = this.getRandomQuestionsFrom(this.multipleChoiceQuestions, numberMultipleChoice, minChapter, minSection, maxChapter, maxSection);
        
        System.out.println("Gathered Multiple-Choice Questions.");

        ArrayList<ExamQuestionGroup> shortAnswerQuestionsForExam = this.getRandomQuestionsFrom(this.shortAnswerQuestions, numberShortAnswer, minChapter, minSection, maxChapter, maxSection);
        
        System.out.println("Gathered Short Answer Questions.");

        ArrayList<ExamQuestionGroup> fillInTheBlankQuestionsForExam = this.getRandomQuestionsFrom(this.fillInTheBlankQuestions, numberFillInTheBlank, minChapter, minSection, maxChapter, maxSection);
        
        System.out.println("Gathered Fill-In-The-Blank Questions.");
        
        this.generateExamFromQuestions(examTitle, "../" + this.courseNumber + "/" + fileNamePrefix, trueFalseQuestionsForExam, trueFalseValue, multipleChoiceQuestionsForExam, multipleChoiceValue, shortAnswerQuestionsForExam, shortAnswerValue, fillInTheBlankQuestionsForExam, fillInTheBlankValue, includeCoverAndBackPage);
    }

    /**
     * Generates the exam, assuming we start with questions from Chapter 0, Section 0 (or anything higher if they exist).
     *
     * @param courseSection    Section of the course.
     * @param semester    The season the semester is in (Fall or Spring)
     * @param year    The calendar year of this exam.
     * @param examTitle    The title of this exam.  Number or Final or Practice, etc.
     * @param numberTrueFalse    The number of T/F questions.
     * @param trueFalseValue    The value of one T/F question.
     * @param numberMultipleChoice    The number of M-C questions.
     * @param multipleChoiceValue    The value of one M-C question.
     * @param numberFillInTheBlank    The number of F-i-t-B questions.
     * @param fillInTheBlankValue    The value of one F-i-t-B question.
     * @param maxChapter    The maximum chapter to use in this exam.
     * @param maxSection    The maximum section in maxChapter to use in this exam.
     */
    public void generateRandomExam(String examTitle, String fileNamePrefix, int numberTrueFalse, int trueFalseValue, int numberMultipleChoice, int multipleChoiceValue, int numberShortAnswer, int shortAnswerValue, int numberFillInTheBlank, int fillInTheBlankValue, int maxChapter, int maxSection, boolean includeCoverAndBackPage) throws IOException {
        this.generateRandomExam(examTitle, fileNamePrefix, numberTrueFalse, trueFalseValue, numberMultipleChoice, multipleChoiceValue, numberShortAnswer, shortAnswerValue, numberFillInTheBlank, fillInTheBlankValue, 0, 0, maxChapter, maxSection, includeCoverAndBackPage);
    }
    
    /**
     * Creates an exam from the given questions.
     *
     * @param trueFalseQuestions    The true-false questions to be used.
     * @param multipleChoiceQuestions   The multiple-choice questions to be used.
     * @param fillInTheBlankQuestions   The fill-in-the-blank questions to be used.
     * @param latexWriter    Writes the exam to the .tex output file.
     */
    private void generateExamFromQuestions(String examTitle, String fileNamePrefix, ArrayList<ExamQuestionGroup> trueFalseQuestionsForExam, int singleTrueFalseValue, ArrayList<ExamQuestionGroup> multipleChoiceQuestionsForExam, int singleMultipleChoiceValue, ArrayList<ExamQuestionGroup> shortAnswerQuestionsForExam, int singleShortAnswerValue, ArrayList<ExamQuestionGroup> fillInTheBlankQuestionsForExam, int singleFillInTheBlankValue, boolean includeCoverAndBackPage) throws IOException {
        
        File directory = this.getFileCreationFolder();
        File outputFile = new File(directory, fileNamePrefix + ".tex");
        //the LaTeX file to write to
        FileWriter latexWriter = new FileWriter(outputFile);

        //first, the preamble
        latexWriter.write("\\documentclass[12pt, addpoints]{exam}\n" +
                "\\usepackage[utf8x]{inputenc}\n" +
                "%\\usepackage{\\string~/GitHub/LaTeX-basics/paithan}\n" + //this one is for my latex macros that I use in a lot of my questions.
                "\\usepackage{multicol}\n" + 
                "\\usepackage{tikz}\n" +
                "\\usetikzlibrary{automata,positioning}\n" +
                "\n" +
                "\\newcommand{\\blankSpace}[0]{\\rule{30mm}{.4pt}}\n" + 
                "\\newcommand{\\trueFalseOptions}[0]{\n" + 
                "\\begin{oneparchoices}\n" + 
                "\\choice True\n" + 
                "\\choice False\n" + 
                "\\end{oneparchoices}}\n" + 
                "\\newcommand{\\blankToFillIn}[0]{\\underline{\\phantom{XXXXXXXXXXXXXXXXXXXX}}}\n" + 
                "\\newenvironment{answerPage}{ %TODO: figure out how to use a parameter to determine the number of columns.\n" + 
                "\\begin{flushleft}\n" + 
                "{\\large Name:\\rule{85mm}{.4pt} and Date:\\blankSpace}\n" + 
                "\\end{flushleft}\n" + 
                "\\newpage\n" + 
                "\\begin{multicols}{3}}{Go back and check!\\end{multicols}\\newpage}\n" + 
                "\\newcommand{\\answerSection}[1]{\\textbf{#1}\\newline}\n" + 
                "\\newcommand{\\answerSheetSpace}[2]{#1: \\fbox{\\phantom{\\large{#2}}}\n" + 
                "\\newline\n" + 
                "\\phantom{#1:}$\\downarrow$ \\phantom{\\fbox{\\phantom{#2}}}\n" + 
                "\\newline}\n" + 
                "\n" +
                "%opening\n" +
                "\n" +
                "\\begin{document}\n" +
                "\n" +
                "\\makebox[\\textwidth]{{\\Large " + examTitle + "}}\n" +
                "\n" +
                "\\vspace{.5in}\n" +
                "\n");
        if (includeCoverAndBackPage) {
            latexWriter.write("{\\large \\textbf{Instructions}: Hello!  Welcome to this exam!  Please write your name and date the bottom of this sheet.\n" +
                "\n" +
                "The next page (or pages) contains space for you to write your answers to the questions in this exam.  Please be sure to fill that sheet in; I won't look at the rest of the exam booklet.  Be very careful to put your answers in the space for the correct question.  I recommend double checking after you've gone through the questions once.  You are more than welcome to tear this page (or these pages) off.  \n" +
                "\n" +
                "Relax.  You have plenty of time to take this exam and will probably figure some stuff out just while taking it.  Thus, although there are a lot of problems, the best thing for many of them will be to work through them \\emph{twice}.  After you finish, please take the time to go back through your work and use the new knowledge you gained!  (It's not weird, I promise!)\n" +
                "\n" +
                "Relax.  If you need to during the exam, feel free to stand up and stretch or hop up and down to shake out the tension.  Please do keep your eyes on your own exam.\n" + 
                "\n" + 
                "Relax and ask me any questions you have during this exam.  (I can only answer those about something that I left unclear, sorry.  There are probably mistakes, so it's still safe to ask.)\n" +
                "\n" +
                "When you're finished, please bring your exam and this sheet up to me.  I don't need your scrap paper.\n" +
                "\n" +
                "If possible, enjoy!\n" +
                "\n" +
                "\\vspace{1in}");
            latexWriter.flush();
        
            //next, the answer page
            latexWriter.write("\\begin{answerPage}\n\n");
            latexWriter.flush();
        
            this.writeAnswerSection(latexWriter, "True-False", trueFalseQuestionsForExam);
        
            this.writeAnswerSection(latexWriter, "Multiple Choice", multipleChoiceQuestionsForExam);
        
            this.writeAnswerSection(latexWriter, "Short Answer", shortAnswerQuestionsForExam);
        
            this.writeAnswerSection(latexWriter, "Fill-In-The-Blanks", fillInTheBlankQuestionsForExam);
        
            latexWriter.write("\\end{answerPage}\n\n");
        
            //now add 
            latexWriter.write("\\ifodd\\thepage\n");
            latexWriter.write("\\else\n  {\\phantom{Monkey}\\pagebreak}\n");
            latexWriter.write("\\fi\n\n");
        
            //set up the extra Name line on page 3.
            latexWriter.write("\\begin{center}\n");
            latexWriter.write("  {\\large Name (yes, again, sorry!):\\rule{85mm}{.4pt}}\n");
            latexWriter.write("\\end{center}\n");
        } else {
            latexWriter.write("\\begin{center}\n");
            latexWriter.write("  {\\large Name:\\rule{85mm}{.4pt}}\n");
            latexWriter.write("\\end{center}\n");
        }
        latexWriter.flush();

        //now the questions!
        this.writeExamSection(latexWriter, "True-False", trueFalseQuestionsForExam, singleTrueFalseValue, false);
        
        System.out.println("Added True-False Questions.");

        this.writeExamSection(latexWriter, "Multiple Choice", multipleChoiceQuestionsForExam, singleMultipleChoiceValue, false);
        
        System.out.println("Added Multiple-Choice Questions.");

        this.writeExamSection(latexWriter, "Short Answer", shortAnswerQuestionsForExam, singleShortAnswerValue, false);
        
        System.out.println("Added Short Answer Questions.");

        
        this.writeExamSection(latexWriter, "Fill-In-The-Blanks", fillInTheBlankQuestionsForExam, singleFillInTheBlankValue, true);
        
        System.out.println("Added Fill-In-The-Blank Questions.");
        
        if (includeCoverAndBackPage) {
            latexWriter.write("\n\\ifodd\\thepage\n\t\\pagebreak\\phantom{Monkey} \n\\else\n\\fi\n\\pagebreak\n\\begin{center}After the exam begins, you may tear this sheet off to use as scrap paper.\\end{center}\n\n\\pagebreak\n\\begin{center}\\textbf{Do not} flip this over yet!  \\textbf{Before} you start the exam, please log off of the machine you're sitting at, turn off the monitor, and put away everything else aside from your cheet sheat and writing utensils.  Once you have done that, you may flip this over and read through the directions, \\textbf{but do not} flip past that cover sheet until the exam begins.  \\textbf{After} the exam begins, you may tear this sheet off to use as scrap paper.\\end{center}\n");
        }
        latexWriter.write("\\end{document}");
        latexWriter.flush();
        
        System.out.println("Success!  Wrote the exam to: \n    " + outputFile.getCanonicalPath());

    }
    
    //writes a section of answers for this group of questions.
    private void writeAnswerSection(FileWriter writer, String sectionName, ArrayList<ExamQuestionGroup> questions) throws IOException {
        int numberOfQuestions = this.getNumberOfQuestions(questions);
        if (questions.size() < 1) { return; }
        writer.write("\\answerSection{" + sectionName + "}\n");
        
        //TODO: this is a hack; change it!
        String sameLengthAsLongestAnswer = "WW";
        if (sectionName.equals("Fill-In-The-Blanks")) {
            sameLengthAsLongestAnswer = "encapsulation";
        }
        
        for (int i = 0; i < numberOfQuestions; i++) {
            writer.write("\\answerSheetSpace{" + (i+1) + "}{" + sameLengthAsLongestAnswer + "}\n");
        }
        writer.flush();
    }

    //writes a section of the exam for this group of questions.
    private void writeExamSection(FileWriter writer, String sectionName, ArrayList<ExamQuestionGroup> questions, int pointsApiece, boolean includeWordBox) throws IOException {
        int numberOfQuestions = this.getNumberOfQuestions(questions);
        if (questions.size() > 0) {
            writer.write("\\begin{minipage}{\\textwidth}\n");
            writer.write("\\section{" + sectionName + " (" + (numberOfQuestions * pointsApiece) + " points, " + pointsApiece + " point(s) apiece)}\n" +
                "\n");
             
            //print a box of solutions, if told to (used for fill-in-the-blank questions.  Should refactor to do this nicely! 
            if (includeWordBox) {
                TreeSet<String> answers = new TreeSet<String>();
                for (ExamQuestionGroup group: questions) {
                    answers.addAll(group.getAnswers());
                }
                writer.write("\\begin{center}\n \\textbf{\\large{Word Box}}\n\n \\small{(Use each term in this box once to fill in the blanks in this section.)}\n\n \\fbox{\\begin{tabular}{l c c c r}");
                int columnCounter = 0;
                for (String answer : answers) {
                    writer.write(answer);
                    columnCounter++;
                    if (columnCounter % 5 == 0) {
                        writer.write("\\\\ \n");
                    } else {
                        writer.write(" & ");
                    }
                }
                writer.write("\\end{tabular}}\n\n\\end{center}");
            }
            writer.write("\\end{minipage}");

            //now write the questions
            writer.write("\\begin{questions}\n");
            
            for (ExamQuestionGroup group: questions) {
                writer.write(group.getLatexString(""));
            }
            writer.write(" \n" +
                "\\end{questions}\n");
            writer.flush();
        }
    }
    
    //gets the maximum section from a set of questions in a chapter
    private int getMaximumSectionFromQuestionsInChapter(Iterable<ExamQuestionGroup> questionGroups, int chapter) {
        int maximumSection = -6000;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() == chapter && group.getSection() > maximumSection) {
                maximumSection = group.getSection();
            }
        }
        return maximumSection;
    }
    
    //gets the minimum section from a set of questions in a chapter
    private int getMinimumSectionFromQuestionsInChapter(Iterable<ExamQuestionGroup> questionGroups, int chapter) {
        int minimumSection = 6000;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() == chapter && group.getSection() < minimumSection) {
                minimumSection = group.getSection();
            }
        }
        return minimumSection;
    }
    
    //gets the maximum chapter from a set of questions
    private int getMaximumChapterFromQuestions(Iterable<ExamQuestionGroup> questionGroups) {
        int maximumChapter = -6000;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() > maximumChapter) {
                maximumChapter = group.getChapter();
            }
        }
        return maximumChapter;
    }
    
    //gets the minimum chapter from a set of questions
    private int getMinimumChapterFromQuestions(Iterable<ExamQuestionGroup> questionGroups) {
        int minimumChapter = 6000;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() < minimumChapter) {
                minimumChapter = group.getChapter();
            }
        }
        return minimumChapter;
    }
        
    
    //gets the number of questions in a given section of the given chapter.
    private int getNumberOfQuestionsInSection(Iterable<ExamQuestionGroup> questionGroups, int chapter, int section) {
        int numberOfQuestions = 0;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() == chapter && group.getSection() == section) {
                numberOfQuestions += group.size();
            }
        }
        return numberOfQuestions;
    }
    
    //gets the number of questions in a given chapter
    private int getNumberOfQuestionsInChapter(Iterable<ExamQuestionGroup> questionGroups, int chapter) {
        int numberOfQuestions = 0;
        for (ExamQuestionGroup group : questionGroups) {
            if (group.getChapter() == chapter) {
                numberOfQuestions += group.size();
            }
        }
        return numberOfQuestions;
    }
    
    //gets the number of questions in a collection of question groups.
    private int getNumberOfQuestions(Iterable<ExamQuestionGroup> questionGroups) {
        int numberOfQuestions = 0;
        for (ExamQuestionGroup group : questionGroups) {
            numberOfQuestions += group.size();
        }
        return numberOfQuestions;
    }

    //gets a random number of questions from the given source
    private ArrayList<ExamQuestionGroup> getRandomQuestionsFrom(ArrayList<ExamQuestionGroup> source, int numberOfQuestions, int minChapter, int minSection, int maxChapter, int maxSection) {
        Random random = new Random();
        ArrayList<ExamQuestionGroup> questions = new ArrayList<ExamQuestionGroup>();
        while (numberOfQuestions > 0) {
            if (source.size() == 0) {
                System.err.println("ERROR: not enough Questions!  Only " + questions.size() + " questions added so far.");
                return null;
            }
            // get a random element from the source questions
            int i = random.nextInt(source.size());
            ExamQuestionGroup selectedGroup = source.get(i);
            
            //see if we can add it.  If so, add it!
            if (selectedGroup.size() <= numberOfQuestions &&
                (selectedGroup.getChapter() < maxChapter ||
                 (selectedGroup.getChapter() == maxChapter &&
                  selectedGroup.getSection() <= maxSection)) &&
                (selectedGroup.getChapter() > minChapter ||
                 (selectedGroup.getChapter() == minChapter &&
                  selectedGroup.getSection() >= minSection))) {
                questions.add(selectedGroup);
                numberOfQuestions -= selectedGroup.size();
                System.out.println("Added " + selectedGroup.size() + " question(s) from chapter " + selectedGroup.getChapter() + ", section " + selectedGroup.getSection() + ".");
            } else {
                System.out.println("Failed adding " + selectedGroup.size() + " question(s) from chapter " + selectedGroup.getChapter() + ", section " + selectedGroup.getSection() + ".");
            }
            source.remove(i);
            System.out.println(numberOfQuestions + " question(s) left to go!");
        }
        return questions;

    }
	
	public void startDocument() {
		System.out.println("Start parsing the document!");
	}
	
	public void endDocument() {
		System.out.println("Finished parsing the document!");
	}    
	
	public void startElement (String uri, String name,
			      String qName, Attributes atts) {
        contents.reset();
        if (name == "TrueFalseQuestion" ||
                   name == "MultipleChoiceQuestion" ||
                   name == "ShortAnswerQuestion" ||
                   name == "FillInTheBlankQuestion") {
            if (!this.inMultipleQuestionGroup) {
                this.currentGroup = new ExamQuestionGroup();
            }
            if (name == "TrueFalseQuestion") {
                this.currentQuestion = new TrueFalseQuestion();
            } else if (name == "MultipleChoiceQuestion") {
                this.currentQuestion = new MultipleChoiceQuestion();
            } else if (name == "ShortAnswerQuestion") {
                this.currentQuestion = new ShortAnswerQuestion();
            } else {
                this.currentQuestion = new FillInTheBlankQuestion();
            }
        } else if (name == "ExamQuestionGroup") {
            this.currentGroup = new ExamQuestionGroup();
            this.inMultipleQuestionGroup = true;
        }
    }

    public void endElement (String uri, String name, String qName) {
        String contentString = contents.toString().trim();
        if (name == "text") {
            this.currentQuestion.setText(contentString);
            System.out.println("Setting text to: " + contentString);
        } else if (name == "answer") {
            this.currentQuestion.setAnswer(contentString);
            System.out.println("Setting answer to: " + contentString);
            if (!this.currentQuestion.hasChoice(contentString)) {
                this.currentQuestion.addChoice(contentString);
                System.out.println("  (Also added above as a choice.)");
            }
        } else if (name == "chapter") {
            this.currentGroup.setChapter(Integer.parseInt(contentString));
        } else if (name == "section") {
            this.currentGroup.setSection(Integer.parseInt(contentString));
        } else if (name == "header") {
            this.currentGroup.setHeaderSuffix(contentString);
        } else if (name == "choice") {
            this.currentQuestion.addChoice(contentString);
        } else if (name == "TrueFalseQuestion" ||
                   name == "MultipleChoiceQuestion" ||
                   name == "ShortAnswerQuestion" ||
                   name == "FillInTheBlankQuestion") {
            this.currentGroup.addQuestion(this.currentQuestion);
            if (!this.inMultipleQuestionGroup) {
                if (name == "TrueFalseQuestion") {
                    this.trueFalseQuestions.add(this.currentGroup);
                } else if (name == "MultipleChoiceQuestion") {
                    this.multipleChoiceQuestions.add(this.currentGroup);
                } else if (name == "ShortAnswerQuestion") {
                    this.multipleChoiceQuestions.add(this.currentGroup);
                } else {
                    this.fillInTheBlankQuestions.add(this.currentGroup);
                }
            }
        } else if (name == "ExamQuestionGroup") {
            //TODO: so far all multi-groups belong to the Multiple-Choice sections.  Not sure what to do if they're not.
            this.multipleChoiceQuestions.add(this.currentGroup);
            this.inMultipleQuestionGroup = false;
        }
    }
    
    public void characters (char ch[], int start, int length) {
        contents.write( ch, start, length );
    }
        /*
		System.out.print("Characters:    \"");
		for (int i = start; i < start + length; i++) {
			switch (ch[i]) {
			case '\\':
			System.out.print("\\\\");
			break;
			case '"':
			System.out.print("\\\"");
			break;
			case '\n':
			System.out.print("\\n");
			break;
			case '\r':
			System.out.print("\\r");
			break;
			case '\t':
			System.out.print("\\t");
			break;
			default:
			System.out.print(ch[i]);
			break;
			}
		}
		System.out.print("\"\n");
    }*/


	
	/**
	 * Main method for testing.
	 * Easy format for running: $ java ExamGenerator
	 * It will then prompt for info about the different types.  (I know this version works.)
	 *
	 * More complicated format for calling (I haven't tested this in a long while): 
	 * $ java ExamGenerator <programCode> <courseNumber> <section> <season> <year> <isPractice> <isFinal> <examNumber> <maxChapter> <maxSection> <numberTrueFalse> <valueOfEachTrueFalse> <numberMultipleChoice> <valueOfEachMultipleChoice> <numberFillInTheBlank> <valueOfEachFillInTheBlank>
	 * Example: java ExamGenerator Comp 150 A Fall 2012 False False 1 10 7 15 2 20 3 10 1
	 * TODO: include minChapter/minSection now too.
	 */
	public static void main(String args[]) throws IOException {
	    ExamGenerator generator;
	    String programCode;
	    String courseNumber;
	    String section = "";
	    String season;
	    String year;
	    String examTitle = "Exam";
	    int minChapter = 0;
	    int minSection = 0;
	    int maxChapter;
	    int maxSection;
	    int numberTrueFalse = 0;
	    int valuePerTrueFalse = 0;
	    int numberMultipleChoice = 0;
	    int valuePerMultipleChoice = 0;
	    int numberShortAnswer = 0;
	    int valuePerShortAnswer = 0;
	    int numberFillInTheBlank = 0;
	    int valuePerFillInTheBlank = 0;
	    boolean includeCoverAndBackPage = true;
	    //BufferedInputStream input = new BufferedInputStream(new InputStream());
	    Scanner input = new Scanner(System.in);
        System.out.println("Maybe someday I'll be smarter, but for now I have to ask you a bunch of questions to generate this exam.  If you make a mistake, just press Ctrl + C and then start over.\nLet's begin!");
        System.out.println("What is the program code? (Math, Comp, CS, Honr, ...)");
        programCode = input.nextLine();
        System.out.println("What is the course number?");
        courseNumber = input.nextLine();
	    generator = new ExamGenerator(courseNumber, programCode);
	    generator.writeDatabaseStats();
	    System.out.println("Do you want to generate the master exam for this class? (y/N)");
	    if (input.nextLine().toLowerCase().startsWith("y")) {
	        generator.generateMasterExam();
	        return;
	    }
	    System.out.println("Let's create a new exam!");
	    
	    
        System.out.println("What is the title of this?  E.g.: Practice Final Exam 2  (I'm expecting \"Exam\", feel free to press enter if that's the case.)");
        String examTitleInput = input.nextLine();
        if (!examTitleInput.equals("")) {
            examTitle = examTitleInput;
        }
        System.out.println("Which section is this (A, B, ...)?  If there is only one section of this class, just hit enter.");
        section = input.nextLine();
        //use this to auto-fill the season and year.
        Calendar today = Calendar.getInstance();
        int monthIndex = today.get(Calendar.MONTH);
        if ( monthIndex == 0) {
            //it's January
            season = "Winterim";
        } else if (monthIndex >= 1 && monthIndex <= 4) {
            //it's the Spring
            season = "Spring";
        } else if (monthIndex >= 8 && monthIndex <= 11) {
            //Fall semester
            season = "Fall";
        } else {
            season = "????";
        }   
        System.out.println("\nWhat is this semester's season?  (Fall, Spring, Summer, etc.  Just hit enter for " + season + ")");
        String inputString = input.nextLine();
        if (inputString.length() != 0) {
            //they didn't just hit enter; use the input string
            season = inputString;
        }
        int yearInt = today.get(Calendar.YEAR);
        System.out.println("Which year is it?  (Just hit enter for " + yearInt + ".)"); 
        try {
            yearInt = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException nfe) {
            //do nothing.
        }
        year = "" + yearInt;
        
        
        System.out.println("What is the lowest chapter to cover in this exam? (Hit enter to start from chapter 0, section 0.)");
        try {
            minChapter = Integer.parseInt(input.nextLine());
            System.out.println("What is the lowest section in chapter " + minChapter + "?  (Hit enter to start from section 0.)");
            try {
                minSection = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException nfe) {
                minSection = 0;
            }
        } catch (NumberFormatException nfe) {
            minChapter = 0;
            minSection = 0;
        }
        System.out.println("What is the highest chapter reached in class?  (Hit enter to go up through chapter 100, section 100.)");
        try {
            maxChapter = Integer.parseInt(input.nextLine());
            System.out.println("What is the highest section reached in that chapter? (Hit enter to end with section 100.)");
            try {
                maxSection = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException nfe) {
                maxSection = 100;
            }
        } catch (NumberFormatException nfe) {
            maxChapter = 100;
            maxSection = 100;
        }
        
        int totalPoints = 0;
        
        //get the numbers for true/false questions
        int maxNumTrueFalse = generator.getNumberOfQuestions(generator.trueFalseQuestions);
        numberTrueFalse = getIntInput("What is the number of True/False questions?  (You have " + maxNumTrueFalse + " available.)", input, 0, maxNumTrueFalse);
        if (numberTrueFalse > 0) {
            valuePerTrueFalse = getNonnegativeIntInput("How much is each T/F question worth?", input);
        }
        int trueFalsePoints = numberTrueFalse * valuePerTrueFalse;
        totalPoints += trueFalsePoints;
        System.out.println("Okay, that's " + trueFalsePoints + " for the True/False section.");
        System.out.println("Total points so far: " + totalPoints);
        
        
        //get the numbers for multiple choice questions
        int maxNumMultipleChoice = generator.getNumberOfQuestions(generator.multipleChoiceQuestions);
        numberMultipleChoice = getIntInput("What is the number of Multiple-Choice questions?  (You have " + maxNumMultipleChoice + " available.)", input, 0, maxNumMultipleChoice);
        if (numberMultipleChoice > 0) {
            valuePerMultipleChoice = getNonnegativeIntInput("How much is each MC question worth?", input);
        }
        int multipleChoicePoints = numberMultipleChoice * valuePerMultipleChoice;
        totalPoints += multipleChoicePoints;
        System.out.println("Okay, that's " + multipleChoicePoints + " for the Multiple Choice section.");
        System.out.println("Total points so far: " + totalPoints);
        
        
        //get numbers for short-answer questions
        int maxNumShortAnswer = generator.getNumberOfQuestions(generator.shortAnswerQuestions);
        numberShortAnswer = getIntInput("What is the number of Short-Answer questions?  (You have " + maxNumShortAnswer + " available.)", input, 0, maxNumShortAnswer);
        if (numberShortAnswer > 0) {
            valuePerShortAnswer = getNonnegativeIntInput("How much is each SA question worth?", input);
        }
        int shortAnswerPoints = numberShortAnswer * valuePerShortAnswer;
        totalPoints += shortAnswerPoints;
        System.out.println("Okay, that's " + shortAnswerPoints + " for the Short Answer section.");
        System.out.println("Total points so far: " + totalPoints);
        
        
        //get numbers for fill-in-the-blank questions
        int maxNumFillInTheBlank = generator.getNumberOfQuestions(generator.fillInTheBlankQuestions);
        numberFillInTheBlank = getIntInput("What is the number of Fill-In-The-Blank questions?  (You have " + maxNumFillInTheBlank + " available.)", input, 0, maxNumFillInTheBlank);
        if (numberFillInTheBlank > 0) {
            valuePerFillInTheBlank = getNonnegativeIntInput("How much is each FitB question worth?", input);
        }
        int fillInTheBlankPoints = numberFillInTheBlank * valuePerFillInTheBlank;
        totalPoints += fillInTheBlankPoints;
        System.out.println("Okay, that's " + fillInTheBlankPoints + " for the Fill in the Blank section.");
        System.out.println("Total points so far: " + totalPoints);
        
        
        System.out.println("Do you want to include a cover page with instructions and a place to put answers?  (Also decides whether to put a protective back page.) Y/n");
        includeCoverAndBackPage = !(input.nextLine().toLowerCase().startsWith("n"));
	        
	    
	    //System.out.println("~~~~~~~ examTitle: '" + examTitle + "'   ~~~~~~~~~~~~~~~~~~");
	    
        String examTitlePrefix = programCode + " " + courseNumber + " ";
        if (section.length() > 0) {
            examTitlePrefix += section + " ";
        }
        examTitlePrefix += season + " " + year + " ";
        //fileNamePrefix += season + year;
        //examTitle = programCode + " " + courseNumber + " "
        examTitle = examTitlePrefix + examTitle;
        String fileNamePrefix = examTitle.replace(" ", "");
        
        boolean acceptableExam = false;
        
        
        //generate the exam!
        generator.generateRandomExam(examTitle, fileNamePrefix, numberTrueFalse, valuePerTrueFalse, numberMultipleChoice, valuePerMultipleChoice, numberShortAnswer, valuePerShortAnswer, numberFillInTheBlank, valuePerFillInTheBlank, minChapter, minSection, maxChapter, maxSection, includeCoverAndBackPage);
        
        
        /*
        
        //TODO: this part is broken right now because it removes the questions from the collection, so when it tries to generate again, it's not regenerating from the full question set.  I need to make clones to make this work so it's always starting from the same place.
        while (!acceptableExam) {
            //generate the exam!
            generator.generateRandomExam(examTitle, fileNamePrefix, numberTrueFalse, valuePerTrueFalse, numberMultipleChoice, valuePerMultipleChoice, numberFillInTheBlank, valuePerFillInTheBlank, minChapter, minSection, maxChapter, maxSection, includeCoverAndBackPage);
            //generator.generateRandomExam(section, semester, examTitle, numberTrueFalse, valuePerTrueFalse, numberMultipleChoice, valuePerMultipleChoice, numberFillInTheBlank, valuePerFillInTheBlank, maxChapter, maxSection);
            
            //check to make sure everything's done
            String done = "";
            while (!(done.startsWith("y") || done.startsWith("n"))) {
                System.out.println("Please check the exam to see whether you like it.  Is it acceptable?  [y/N]  (If not, I will generate another one.)");
                done = input.nextLine().toLowerCase();
            }
            if (done.startsWith("y")) {
                acceptableExam = true;
            } else {
                System.out.println("Okay, I'll make another one.  One moment...");
            }
        }
        /* */
        
        
        System.out.println("Great!  Enjoy your exam!");
        
    }
    
    
    //prompts the user for an integer input
    private static int getNonnegativeIntInput(String queryString, Scanner input) {
        return getIntInput(queryString, input, 0, Integer.MAX_VALUE);
    }
    
    
    //prompts the user for an integer input
    private static int getIntInput(String queryString, Scanner input, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Can't have a bigger min than max!");
        } else if (min == max) {
            return min; //don't even bother asking for input!
        }
        int result = min;
        boolean success = false;
        while (!success) {
            System.out.println(queryString);
            try {
                result = Integer.parseInt(input.nextLine());
                if (result < min || result > max) {
                    System.out.println("Hmmm, that's not an acceptable number here.  Let's try again.");
                } else {
                    success = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Hmmm, I didn't understand that because it wasn't an integer.  Let's try again.");
            }
        }
        return result;
    }

} //end of class ExamGenerator
