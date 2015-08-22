/*
 * Author 	: Aditya Borde
 * File 	: MiniMaxGameBlack.java
 * Function : MiniMax algorithm for Mid game and end game phase - For Black Pieces 
 */

import java.io.IOException;
import java.util.ArrayList;

public class MiniMaxGameBlack {

	public static void main(String[] args) throws IOException {
		String inputFile, outputFile;
		int treeDepth;
		// Get the input parameters :
		inputFile = args[0];
		outputFile = args[1];
		treeDepth = Integer.parseInt(args[2]);

		// Get the input positions
		MorrisBoard inputPositions = FileInputOutput.GetInputPositions(inputFile);

		// MiniMax Algorithm - Midgame Endgame Phase FOR "BLACK"
		Estimations est = MiniMax(inputPositions, treeDepth, true);

		// Write the final output in the file.
		FileInputOutput.WriteOutputInFile(outputFile, est);

	}
	
	
	// MiniMax Method for Game Mid game-End game Phase - to identify Best Move:
	public static Estimations MiniMax(MorrisBoard mB, int depth, boolean isBlack) 
	{
		Estimations estFinal = new Estimations();
		// check for the termination of MiniMax Recursion
		if (depth != 0) 
		{
			ArrayList<MorrisBoard> possibleBoardPositions = null;
			Estimations currentEstimation = new Estimations();
			// for Black pieces
			if (isBlack) 
			{
				possibleBoardPositions = MorrisGame.GenerateMovesMidgameEndgameBlack(mB);
				estFinal.setEstimate(Integer.MIN_VALUE);
			} 
			else // for white Pieces
			{
				possibleBoardPositions = MorrisGame.GenerateMovesMidgameEndgame(mB);
				estFinal.setEstimate(Integer.MAX_VALUE);
			}

			for (MorrisBoard b : possibleBoardPositions) 
			{
				if (isBlack) // For Black - it is on MAX level
				{
					currentEstimation = MiniMax(b, depth - 1, false);
					estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + currentEstimation.getPositionsEvaluated());
					if (currentEstimation.getEstimate() > estFinal.getEstimate()) 
					{
						estFinal.setEstimate(currentEstimation.getEstimate());
						estFinal.setMorrisBoard(b);
					}
				} 
				else // For White - it is on MIN level
				{
					currentEstimation = MiniMax(b, depth - 1, true);
					estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + currentEstimation.getPositionsEvaluated());
					// estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + 1);
					if (currentEstimation.getEstimate() < estFinal.getEstimate()) 
					{
						estFinal.setEstimate(currentEstimation.getEstimate());
						estFinal.setMorrisBoard(b);
					}

				}
			}
			return estFinal;
		} 
		else // if depth becomes Zero
		{
			estFinal.setEstimate(MorrisGame.GetStaticEstimationMidgameEndgame(mB.GetFlippedBoard()));
			estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + 1);
			return estFinal;
		}

	}


}
