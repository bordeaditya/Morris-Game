/*
 * Author 	: Aditya Borde
 * File 	: ABOpening.java
 * Function : Alpha Beta Pruning for Opening phase 
 */

import java.io.IOException;
import java.util.ArrayList;

public class ABOpening {

	public static void main(String[] args) throws IOException {
		
		String inputFile, outputFile;
		int treeDepth;
		
		// Get the input parameters :
		inputFile = args[0];
		outputFile = args[1];
		treeDepth = Integer.parseInt(args[2]);

		// Get the input positions
		MorrisBoard inputPositions = FileInputOutput.GetInputPositions(inputFile);

		// AlphaBeta Pruning Algorithm - Opening Phase FOR "WHITE"
		Estimations est = AlphaBetaPruning(inputPositions, treeDepth, true,Integer.MIN_VALUE,Integer.MAX_VALUE);

		// Write the final output in the file.
		FileInputOutput.WriteOutputInFile(outputFile, est);
	}
	
	
	// Alpha beta Pruning Algorithm : Returns Best Move for Opening phase for "WHITE"
	public static Estimations AlphaBetaPruning(MorrisBoard mB, int depth, boolean isWhite, int alpha,int beta) 
	{
		Estimations estFinal = new Estimations();
		// check for the termination of MiniMax Recursion
		if (depth != 0) 
		{
			ArrayList<MorrisBoard> possibleBoardPositions = null;
			Estimations currentEstimation = new Estimations();
			// for White pieces
			// for White pieces
			if (isWhite) 
			{
				possibleBoardPositions = MorrisGame.GenerateMovesOpening(mB);
			} 
			else // for Black Pieces
			{
				possibleBoardPositions = MorrisGame.GenerateMovesOpeningBlack(mB);
			}

			// For Every possible positions on the Board
			for (MorrisBoard b : possibleBoardPositions) 
			{
				if (isWhite) // For White - it is on MAX level
				{
					currentEstimation = AlphaBetaPruning(b, depth - 1, false, alpha, beta);
					estFinal.setPositionsEvaluated(
					estFinal.getPositionsEvaluated() + currentEstimation.getPositionsEvaluated());
					if (currentEstimation.getEstimate() > alpha) 
					{
						alpha = currentEstimation.getEstimate();
						estFinal.setMorrisBoard(b);
					}
				} 
				else // For Black - it is on MIN level
				{
					currentEstimation = AlphaBetaPruning(b, depth - 1, true, alpha, beta);
					estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + currentEstimation.getPositionsEvaluated());
					// estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + 1);
					if (currentEstimation.getEstimate() < beta) 
					{
						beta = currentEstimation.getEstimate();
						estFinal.setMorrisBoard(b);
					}

				}
				// Do not calculate if Nodes are no longer being affected in final calculation 
				if(alpha >= beta)
					break;
			}
			// set final estimate value 
			if(isWhite)
				estFinal.setEstimate(alpha);
			else
				estFinal.setEstimate(beta);
			
			return estFinal;
		} 
		else // if depth becomes Zero
		{
			estFinal.setEstimate(MorrisGame.GetStaticEstimationOpening(mB));
			estFinal.setPositionsEvaluated(estFinal.getPositionsEvaluated() + 1);
			return estFinal;
		}
	}

}
