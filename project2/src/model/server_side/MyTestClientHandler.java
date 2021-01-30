package model.server_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MyTestClientHandler<Problem,Solution> implements ClientHandler {
	// Data members.
	@SuppressWarnings("rawtypes")
	private Solver<Problem, Solution> solver;
	@SuppressWarnings("rawtypes")
	private CacheManager<Problem, Solution> cacheManager;
	
	// CTOR.
	public MyTestClientHandler(CacheManager<Problem, Solution> cacheManager , Solver<Problem, Solution> solver) {
		this.solver = solver;
		this.cacheManager = cacheManager;
	}
	
	@Override
	public void handleClient(InputStream in, OutputStream out)  {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out));
		
		try {
			Problem Line;
			Solution Solved;
			
			while(!(Line = (Problem) bufferedReader.readLine()).equals("end"))
			{
				if(cacheManager.Check(Line))
				{
					Solved= cacheManager.Extract(Line);
				}
				else {
					Solved= solver.Solve(Line);
					cacheManager.Save(Line, Solved);
				}
				
				printWriter.println(Solved);
				printWriter.flush();
			}
			
		} catch (IOException e) {e.printStackTrace();}
		
		try {
			bufferedReader.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		printWriter.close();
	}
}