This is a command-line tool for generating random exams (in LaTeX) from an XML database of questions.  

Here's a [sample PDF](https://github.com/paithan/ExamGenerating/blob/master/101/CS101Winterim2020Exam.pdf) generated from the sample questions included in this repo.

To use this tool, it helps:

  * To have a basic understanding of XML.  This is where you will write your questions.  There are example files in this repo.  If you run in to trouble here later on, you are probably using some special characters in your questions.  You should either rewrite your questions to avoid those symbols, or search online for the correct way to replace them.
  
  * To be comfortable with using a command-line interface.  You'll need to be able to open a command prompt, run a .jar file there, then answer questions that are posed to you.
  
  * To have a basic understanding of LaTeX.  This program will spit out a .tex file, which can be compiled into a PDF.  I have used cocalc to generate my LaTeX files online, though there might be a better tool now.  There are lots of special characters in LaTeX and the error messages are unforgiving, so if you run into trouble here, you probably need to remove some special characters.
  

To try out the provided examples, download this repo, then run the executable jar in a command prompt.

e.g.:
$ java -jar ExamGenerator.jar

You will be asked a series of questions you'll type answers to (then hit enter/return).  There are lots of defaults, so you can just push Enter for most of them:

  * Type any subject code you like for the first question.

  * The sample question database is for a course numbered 101 or 102 (there are two separate XML files provided), so type either 101 or 102.  (To create your own course questions, you will have to create your own XML file, one for each course.)
  
  * Type n to choose not to generate the Master Exam.  (The Master Exam is just an exam with all the questions from the databse.  This is very useful when you need to see all the questions.) 
  
  * Type the name of your exam.
  
  * Type the section letter/number.
  
  * Type in the semester name.  (This is usually just the season.  The year number comes next, so you don't need to enter that yet.)
  
  * Type in the year number.
  
  * Type in the lowest chapter number to use.
  
  * Type in the lowest section number from that chapter. 
  
  * Type in the highest chapter number.
  
  * Type in the highest section number from that chapter.
  
  * Type in the number of True/False questions you want on the exam.
  
  * Type in the number of points you want each True/False question to be worth.  (If you chose 0 for the previous question, you won't be asked this.)
  
  * Do those same two steps for Multiple Choice, Short Answer, and Fill in the Blank.  (It will skip any category where there aren't any questions.)
  
  * Decide whether you want to include the answer pages for students to write their answers down in.  (The default for this is Yes.)
  
  * A .tex file will be generated.  If you are not familiar with LaTeX, you can create the exam PDF by using an online tool such as cocalc.com.  (You can log in to CoCalc using a Google account.)
  
  
FAQ:

  * Why are there two sample questions files included?
  
  These are to show (and test) that the code works with two different styles of placements for the input XML files.  They can either be in a sibling directory or in a child directory.
  
