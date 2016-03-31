/**
 * @author UCSD MOOC development team
 *
 * Grader for the SCC assignment.
 *
 */

package graph.grader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import util.GraphLoader;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import graph.CapGraph;
import graph.Graph;

public class SCCGraderNew extends Grader {
    private int totalTests;
    private int testsPassed;

    public SCCGraderNew() {
        setTotalTests(0);
        setTestsPassed(0);
    }
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
        SCCGraderNew grader = new SCCGraderNew();
        Thread thread = new Thread(grader);
        thread.start();
        long endTime = System.currentTimeMillis() + 30000;
        boolean infinite = false;
        while (thread.isAlive()) {
            if (System.currentTimeMillis() > endTime) {
                thread.stop();
                infinite = true;
                break;
            }
        }
        if (grader.getTestsPassed() < grader.getTotalTests()) {
        	grader.feedback = "Some tests failed. Please check the following and try again:\n" + grader.feedback;
        } else {
        	grader.feedback = "All tests passed. Congrats!\n" + grader.feedback;
        }
        if (infinite) {
            grader.setTestsPassed(0);
            grader.setTotalTests(1);
            grader.feedback += "Your program entered an infinite loop or took longer than 30 seconds to finish.";
        }
        System.out.println("Passed " + grader.getTestsPassed() + " out of " + grader.getTotalTests() + 
        		makeOutput((double)grader.getTestsPassed()/grader.getTotalTests(), grader.feedback));
        System.out.close();
    }

    public void run() {

        try {

            for(int i = 0; i < 10; i++) {
                Graph g = new CapGraph();
                Set<Integer> vertices;

                String answerFile = "data/scc_answers/scc_" + (i + 1) + ".txt";
                GraphLoader.loadGraph(g, "data/scc/test_" + (i +1)+ ".txt");
                BufferedReader br = new BufferedReader(new FileReader(answerFile));
                feedback += appendFeedback(i + 1, "\nGRAPH: T" + (i + 1));

                // build list from answer
                List<Set<Integer>> answer = new ArrayList<Set<Integer>>();
                String line;

                while((line = br.readLine()) != null) {
                    Scanner sc = new Scanner(line);
                    vertices = new TreeSet<Integer>();
                    while(sc.hasNextInt()) {
                        vertices.add(sc.nextInt());
                    }
                    answer.add(vertices);


                    sc.close();
                }



                // get student SCC result
                List<Graph> graphSCCs = g.getSCCs();

                List<Set<Integer>> sccs = new ArrayList<Set<Integer>>();

                for(Graph graph : graphSCCs) {
                    HashMap<Integer, HashSet<Integer>> curr = graph.exportGraph();
                    TreeSet<Integer> scc = new TreeSet<Integer>();
                    for (Map.Entry<Integer, HashSet<Integer>> entry : curr.entrySet()) {
                        scc.add(entry.getKey());
                    }
                    sccs.add(scc);
                }


                boolean testFailed = false;
                setTestsPassed(getTestsPassed() + answer.size() + sccs.size());
                setTotalTests(getTotalTests() + answer.size() + sccs.size());

                Set<Integer> answerSCC = null;
                Set<Integer> scc = null;

                // loop over SCCs
                int j = 0;
                for(; j < answer.size(); j++) {

                    answerSCC = answer.get(j);
                    scc = null;

                    if(j < sccs.size()) {
                        scc = sccs.get(j);
                    }


                    // check if learner result contains SCC from answer file
                    if(!sccs.contains(answerSCC)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result did not contain the scc on line "
                                     + (j+1) + " in \"" + answerFile + "\"";
                        feedback += "\n";
                        setTestsPassed(getTestsPassed() - 1);
                    }

                    // check if answer contains learners scc
                    if(scc != null && !answer.contains(scc)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result contained an extra SCC: ";
                        for(Integer id : scc) {
                            feedback += id + " ";
                        }
                        feedback += "\n";
                        setTestsPassed(getTestsPassed() - 1);
                    }


                }

                while(j < sccs.size()) {
                    // check if answer contains learners scc
                    if(scc != null && !answer.contains(scc)) {
                        if(!testFailed) {
                            testFailed = true;
                            feedback += "FAILED. ";
                        }
                        feedback += "Your result contained an extra SCC : ";
                        for(Integer id : scc) {
                            feedback += id + " ";
                        }
                        feedback += "\n";
                        setTestsPassed(getTestsPassed() - 1);
                    }

                    j++;
                }

                if(!testFailed) {
                    feedback += "PASSED.";
                }

                br.close();
            }
        } catch (Exception e) {
            feedback = "An error occurred during runtime.\n" + feedback + "\nError during runtime: " + e;
            setTestsPassed(0);
            setTotalTests(1);
        }
    }
	public int getTotalTests() {
		return totalTests;
	}
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}
	public int getTestsPassed() {
		return testsPassed;
	}
	public void setTestsPassed(int testsPassed) {
		this.testsPassed = testsPassed;
	}
}