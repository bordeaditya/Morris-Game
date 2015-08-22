/*
 * Author 	: Aditya Borde
 * File 	: MorrisBoard.java
 * Function : Nine Men's Morris Game : Classes and Functions Required for MiniMax and Alpha-Beta Pruning
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// PreDefined constants used over all the project
class Constant {
	public static final int BOARD_SIZE = 23;
	public static final int END_GAME_PIECES = 3;
	public static final String BOARD_POSITION = "BoardPosition: ";
	public static final String POSITION_EVALUATED = "Positions Evaluated By Static Estimation: ";
	public static final String MINIMAX_ESTIMATE = "MINIMAX Estimate: ";
}

// It shows the possible values of particular board position.
enum PositionValue {
	X('x'), W('W'), B('B');
	char position;
	PositionValue(char tempPosition) {
		position = tempPosition;
	}
}

// Morris Board - To represent the state of the board using "PositionValue"
class MorrisBoard {
	ArrayList<PositionValue> boardPositionValues;
	int i;

	// Default Constructor to initialize the board positions
	public MorrisBoard() 
	{
		boardPositionValues = new ArrayList<PositionValue>();
		// Fill the Board Positions with value "X"
		for (i = 0; i < Constant.BOARD_SIZE; i++)
			boardPositionValues.add(PositionValue.X);
	}

	// Setting the board positions from input
	public MorrisBoard(char[] boardPositionVal) 
	{
		boardPositionValues = new ArrayList<PositionValue>();
		// for valid Input Size
		if (boardPositionVal.length == Constant.BOARD_SIZE) {
			for (char pos : boardPositionVal) {
				if (pos == 'W' || pos == 'w')
					boardPositionValues.add(PositionValue.W);
				else if (pos == 'B' || pos == 'b')
					boardPositionValues.add(PositionValue.B);
				else
					boardPositionValues.add(PositionValue.X);

			}
		}
		else
		{
			System.out.println("Invalid Input String");
		}
	}

	// Return Board Position Array in String format
	public String toString() 
	{
		char[] output = new char[boardPositionValues.size()];
		for (i = 0; i < boardPositionValues.size(); i++) {
			output[i] = boardPositionValues.get(i).position;
		}
		String outputPositions = new String(output);
		return outputPositions;
	}

	// To get the Position value:
	public PositionValue GetPositionValue(int index) {
		PositionValue p = boardPositionValues.get(index);
		return p;
	}

	// Get the copy of the Morris Board
	public MorrisBoard GetMorrisBoardCopy() {
		MorrisBoard morrisBoard = null;
		char[] positionValues = new char[boardPositionValues.size()];
		for (i = 0; i < boardPositionValues.size(); i++)
			positionValues[i] = boardPositionValues.get(i).position;

		morrisBoard = new MorrisBoard(positionValues);
		return morrisBoard;
	}

	// Get the number of Pieces
	public int GetNumberOfPieces(PositionValue p) {
		int numPieceCount = 0;
		for (PositionValue temp : boardPositionValues) {
			if (temp == p) 
			{
				// if position Value is same as input
				numPieceCount++;
			}
		}
		return numPieceCount;
	}

	// Set Board position values
	public void SetPositionValue(int index, PositionValue p) {
		boardPositionValues.set(index, p);
	}

	// Get flipped copy of the Board
	public MorrisBoard GetFlippedBoard() {
		MorrisBoard morrisBoard = new MorrisBoard();
		for (i = 0; i < boardPositionValues.size(); i++) {
			PositionValue boardPV = boardPositionValues.get(i);
			if (boardPV == PositionValue.W) {
				morrisBoard.SetPositionValue(i, PositionValue.B);
			} else if (boardPV == PositionValue.B) {
				morrisBoard.SetPositionValue(i, PositionValue.W);
			} else {
				morrisBoard.SetPositionValue(i, PositionValue.X);
			}
		}
		return morrisBoard;
	}

}

// Class containing the functions related to 9 Men's Morris Game:
class MorrisGame {
	// Generate Moves in Game Opening
	public static ArrayList<MorrisBoard> GenerateMovesOpening(MorrisBoard mb) 
	{
		return GenerateAdd(mb);
	}

	// GenerateAdd : to return List of Board Positions possible
	public static ArrayList<MorrisBoard> GenerateAdd(MorrisBoard mb) 
	{
		int i;
		// List of Board Positions
		ArrayList<MorrisBoard> L = new ArrayList<MorrisBoard>();
		for (i = 0; i < mb.boardPositionValues.size(); i++) 
		{
			// if board position value is 'X'- location is empty
			if (mb.GetPositionValue(i) == PositionValue.X) 
			{
				MorrisBoard morrisBoardCopy = mb.GetMorrisBoardCopy();
				morrisBoardCopy.SetPositionValue(i, PositionValue.W);
				if (closeMill(i, morrisBoardCopy)) 
				{
					L = GenerateRemove(morrisBoardCopy, L);
				} 
				else 
				{
					L.add(morrisBoardCopy);
				}
			}
		}
		return L;
	}

	// Moves for Midgame-Endgame - for White
	public static ArrayList<MorrisBoard> GenerateMovesMidgameEndgame(MorrisBoard mb) 
	{
		// if game comes into End-Game phase
		if (mb.GetNumberOfPieces(PositionValue.W) == Constant.END_GAME_PIECES) 
		{
			return GenerateHopping(mb);
		} 
		else // Game is in Mid-Game phase
		{
			return GenerateMove(mb);
		}
	}

	// Moves for Mid-game phase
	public static ArrayList<MorrisBoard> GenerateMove(MorrisBoard mb) 
	{
		int i;
		ArrayList<MorrisBoard> L = new ArrayList<MorrisBoard>();
		// for every location-i in the board
		for (i = 0; i < mb.boardPositionValues.size(); i++) 
		{
			if (mb.GetPositionValue(i) == PositionValue.W) 
			{
				// Get list of neighbors
				ArrayList<Integer> n = neighbors(i);
				for (Integer j : n) 
				{
					// empty value
					if (mb.GetPositionValue(j) == PositionValue.X)
					{
						MorrisBoard b = mb.GetMorrisBoardCopy();
						b.SetPositionValue(i, PositionValue.X);
						b.SetPositionValue(j, PositionValue.W);
						if (closeMill(j, b)) 
						{
							L = GenerateRemove(b, L);
						} 
						else 
						{
							L.add(b);
						}
					}
				}
			}
		}
		return L;
	}

	// Return list of neighbors for given board position
	public static ArrayList<Integer> neighbors(int j) 
	{
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		switch (j) 
		{
			case 0:neighbors.addAll(Arrays.asList(1, 3, 8));break;
			case 1:neighbors.addAll(Arrays.asList(0, 2, 4));break;
			case 2:neighbors.addAll(Arrays.asList(1, 5, 13));break;
			case 3:neighbors.addAll(Arrays.asList(0, 4, 6, 9));break;
			case 4:neighbors.addAll(Arrays.asList(1, 3, 5));break;
			case 5:neighbors.addAll(Arrays.asList(2, 4, 7, 12));break;
			case 6:neighbors.addAll(Arrays.asList(3, 7, 10));break;
			case 7:neighbors.addAll(Arrays.asList(5, 6, 11));break;
			case 8:neighbors.addAll(Arrays.asList(0, 9, 20));break;
			case 9:neighbors.addAll(Arrays.asList(3, 8, 10, 17));break;
			case 10:neighbors.addAll(Arrays.asList(6, 9, 14));break;
			case 11:neighbors.addAll(Arrays.asList(7, 12, 16));break;
			case 12:neighbors.addAll(Arrays.asList(5, 11, 13, 19));break;
			case 13:neighbors.addAll(Arrays.asList(2, 12, 22));break;
			case 14:neighbors.addAll(Arrays.asList(10, 15, 17));break;
			case 15:neighbors.addAll(Arrays.asList(14, 16, 18));break;
			case 16:neighbors.addAll(Arrays.asList(11, 15, 19));break;
			case 17:neighbors.addAll(Arrays.asList(9, 14, 18, 20));break;
			case 18:neighbors.addAll(Arrays.asList(15, 17, 19, 21));break;
			case 19:neighbors.addAll(Arrays.asList(12, 16, 18, 22));break;
			case 20:neighbors.addAll(Arrays.asList(8, 17, 21));break;
			case 21:neighbors.addAll(Arrays.asList(18, 20, 22));break;
			case 22:neighbors.addAll(Arrays.asList(13, 19, 21));break;
			default:break;
		}
		return neighbors;
	}

	// Moves for end-game phase
	public static ArrayList<MorrisBoard> GenerateHopping(MorrisBoard mb) 
	{
		int i, j;
		ArrayList<MorrisBoard> L = new ArrayList<MorrisBoard>();

		// for every location (i) - alpha in the board
		for (i = 0; i < mb.boardPositionValues.size(); i++) 
		{
			// if board[alpha] - W
			if (mb.GetPositionValue(i) == PositionValue.W) 
			{
				// for every location (j)- beta in the board
				for (j = 0; j < mb.boardPositionValues.size(); j++) 
				{
					// if board[beta] - X
					if (mb.GetPositionValue(j) == PositionValue.X) 
					{
						MorrisBoard b = mb.GetMorrisBoardCopy();
						b.SetPositionValue(i, PositionValue.X);
						b.SetPositionValue(j, PositionValue.W);
						// if closeMill - beta,b
						if (closeMill(j, b)) 
						{
							L = GenerateRemove(b, L);
						} 
						else 
						{
							L.add(b);
						}
					}
				}
			}
		}
		return L;
	}

	// Method Generate Remove : to remove Piece after Mill formation
	public static ArrayList<MorrisBoard> GenerateRemove(MorrisBoard morrisBoardCopy, ArrayList<MorrisBoard> l) 
	{
		int i;
		for (i = 0; i < morrisBoardCopy.boardPositionValues.size(); i++) 
		{
			if (morrisBoardCopy.GetPositionValue(i) == PositionValue.B) 
			{
				// if not a close Mill
				if (!closeMill(i, morrisBoardCopy)) 
				{
					MorrisBoard b = morrisBoardCopy.GetMorrisBoardCopy();
					b.SetPositionValue(i, PositionValue.X);
					l.add(b);
				}
			}
		}
		return l;
	}

	// check if the Mill is formed on the board.
	public static boolean closeMill(int j, MorrisBoard morrisBoard) {

		PositionValue c = morrisBoard.GetPositionValue(j);
		if (c != PositionValue.X) 
		{
			// Check for all possible Mill formation positions
			return VerifyMills(j,morrisBoard,c);
		} 
		else 
		{
			return false;
		}
	}
	
	
	//Check for the Mill formation from the given positions:
	public static boolean VerifyMills(int j, MorrisBoard morrisBoard,PositionValue c)
	{
		switch (j) 
		{
			case 0:return (isMill(c, morrisBoard, 1, 2) || isMill(c, morrisBoard, 3, 6) || isMill(c, morrisBoard, 8, 20));
			case 1:return isMill(c, morrisBoard, 0, 2);
			case 2:return (isMill(c, morrisBoard, 0, 1) || isMill(c, morrisBoard, 5, 7) || isMill(c, morrisBoard, 13, 22));
			case 3:return (isMill(c, morrisBoard, 4, 5) || isMill(c, morrisBoard, 0, 6) || isMill(c, morrisBoard, 9, 17));
			case 4:return isMill(c, morrisBoard, 3, 5);
			case 5:return (isMill(c, morrisBoard, 2, 7) || isMill(c, morrisBoard, 3, 4) || isMill(c, morrisBoard, 12, 19));
			case 6:return (isMill(c, morrisBoard, 0, 3) || isMill(c, morrisBoard, 10, 14));
			case 7:return (isMill(c, morrisBoard, 11, 16) || isMill(c, morrisBoard, 2, 5));
			case 8:return (isMill(c, morrisBoard, 9, 10) || isMill(c, morrisBoard, 0, 20));
			case 9:return (isMill(c, morrisBoard, 3, 17) || isMill(c, morrisBoard, 8, 10));
			case 10:return (isMill(c, morrisBoard, 6, 14) || isMill(c, morrisBoard, 8, 9));
			case 11:return (isMill(c, morrisBoard, 7, 16) || isMill(c, morrisBoard, 12, 13));
			case 12:return (isMill(c, morrisBoard, 5, 19) || isMill(c, morrisBoard, 11, 13));
			case 13:return (isMill(c, morrisBoard, 11, 12) || isMill(c, morrisBoard, 2, 22));
			case 14:return (isMill(c, morrisBoard, 6, 10) || isMill(c, morrisBoard, 15, 16) || isMill(c, morrisBoard, 17, 20));
			case 15:return (isMill(c, morrisBoard, 14, 16) || isMill(c, morrisBoard, 18, 21));
			case 16:return (isMill(c, morrisBoard, 7, 11) || isMill(c, morrisBoard, 14, 15) || isMill(c, morrisBoard, 19, 22));
			case 17:return (isMill(c, morrisBoard, 3, 9) || isMill(c, morrisBoard, 14, 20) || isMill(c, morrisBoard, 18, 19));
			case 18:return (isMill(c, morrisBoard, 15, 21) || isMill(c, morrisBoard, 17, 19));
			case 19:return (isMill(c, morrisBoard, 5, 12) || isMill(c, morrisBoard, 16, 22) || isMill(c, morrisBoard, 17, 18));
			case 20:return (isMill(c, morrisBoard, 0, 8) || isMill(c, morrisBoard, 14, 17) || isMill(c, morrisBoard, 21, 22));
			case 21:return (isMill(c, morrisBoard, 15, 18) || isMill(c, morrisBoard, 20, 22));
			case 22:return (isMill(c, morrisBoard, 2, 13) || isMill(c, morrisBoard, 16, 19) || isMill(c, morrisBoard, 20, 21));
			default:return false;
		}
	}

	// Verify if the Mill condition satisfy
	public static boolean isMill(PositionValue c, MorrisBoard morrisBoard, int j, int k) 
	{
		if ((morrisBoard.GetPositionValue(j) == c) && (morrisBoard.GetPositionValue(k) == c))
			return true;

		return false;
	}

	// Generate Moves for Black
	public static ArrayList<MorrisBoard> GenerateMovesOpeningBlack(MorrisBoard mB) 
	{
		// Get flip Copy of the board for Black pieces
		MorrisBoard morrisBoardBlack = mB.GetFlippedBoard();
		ArrayList<MorrisBoard> listPositionsBlack = GenerateMovesOpening(morrisBoardBlack);
		// Possible moves for black - flipping the Pieces
		ArrayList<MorrisBoard> possibleMovesBlack = GenerateFlippedBoardList(listPositionsBlack);

		return possibleMovesBlack;
	}

	// Generate the flip board of each MorrisBoard in the list
	public static ArrayList<MorrisBoard> GenerateFlippedBoardList(ArrayList<MorrisBoard> listPositionsBlack) 
	{
		int i;
		// for every possible board positions in the list- flip the pieces
		for (i = 0; i < listPositionsBlack.size(); i++) {
			MorrisBoard tempBoard = listPositionsBlack.get(i).GetFlippedBoard();
			listPositionsBlack.set(i, tempBoard);
		}
		return listPositionsBlack;
	}

	// Generate Move - For Black
	public static ArrayList<MorrisBoard> GenerateMovesMidgameEndgameBlack(MorrisBoard mb) 
	{
		MorrisBoard flipBoardBlack = mb.GetFlippedBoard();
		// Return the flip Board List for Black Pieces
		return GenerateFlippedBoardList(GenerateMovesMidgameEndgame(flipBoardBlack));

	}
	
	
	/*
	 * Static estimation functions - Given in Hand out and Improved
	 */

	// Static estimation - Opening
	public static int GetStaticEstimationOpening(MorrisBoard morrisBoard) 
	{
		return (morrisBoard.GetNumberOfPieces(PositionValue.W) - morrisBoard.GetNumberOfPieces(PositionValue.B));
	}

	// Static estimation - Mid game AND End game
	public static int GetStaticEstimationMidgameEndgame(MorrisBoard morrisBoard) 
	{
		ArrayList<MorrisBoard> L = GenerateMovesMidgameEndgame(morrisBoard);
		int numBlackMoves = L.size();
		if (morrisBoard.GetNumberOfPieces(PositionValue.B) <= 2)
			return (10000);
		else if (morrisBoard.GetNumberOfPieces(PositionValue.W) <= 2)
			return (-10000);
		else if (numBlackMoves == 0)
			return (10000);
		else
			return (1000 * (morrisBoard.GetNumberOfPieces(PositionValue.W) - morrisBoard.GetNumberOfPieces(PositionValue.B))- numBlackMoves);
	}

	// Static Estimation Improved : Opening Phase
	public static int GetStaticEstimationImprovedOpening(MorrisBoard morrisBoard)
	{
		int numWhitePieces = morrisBoard.GetNumberOfPieces(PositionValue.W); 
		int numBlackPieces = morrisBoard.GetNumberOfPieces(PositionValue.B);
		int numPossibleMills = GetPossibleMillCount(PositionValue.W,morrisBoard);
		return (numWhitePieces + numPossibleMills - numBlackPieces);
	}
	
	
	// Static Estimation Improved : Mid-game and End-game Phase
	public static int GetStaticEstimationImprovedMidgameEndgame(MorrisBoard morrisBoard) 
	{

		int numWhitePieces = morrisBoard.GetNumberOfPieces(PositionValue.W); 
		int numBlackPieces = morrisBoard.GetNumberOfPieces(PositionValue.B);
		int numPossibleMills = GetPossibleMillCount(PositionValue.W,morrisBoard);
		
		ArrayList<MorrisBoard> L = GenerateMovesMidgameEndgame(morrisBoard);
		int numBlackMoves = L.size();
		if (numBlackPieces <= 2)
			return (10000);
		else if (numWhitePieces <= 2)
			return (-10000);
		else if (numBlackMoves == 0)
			return (10000);
		else
			return (1000 * (numWhitePieces + numPossibleMills - numBlackPieces) - numBlackMoves);
	}
	
	
	// Get possible Mills for White considering empty positions - X
	public static int GetPossibleMillCount(PositionValue positionVal, MorrisBoard morrisBoard) {
		int i, possibleMillCount = 0;
		
		for(i=0;i<morrisBoard.boardPositionValues.size();i++)
		{
			// For particular given position Type:
			if(morrisBoard.GetPositionValue(i) == PositionValue.X)
			{
				if(VerifyMills(i, morrisBoard, positionVal))
					possibleMillCount++;
			}
		}
		return possibleMillCount;
	}

	
}

// To read / write input/output into the file.
class FileInputOutput {
	// Get Input positions from file
	public static MorrisBoard GetInputPositions(String path) throws IOException {

		MorrisBoard morrisBoard = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String lineInput = "";

			// Read the input moves and place in character array
			if ((lineInput = br.readLine()) != null) 
			{
				// Insert the character positions into the Character List
				morrisBoard = new MorrisBoard(lineInput.toCharArray());
			} else 
			{
				morrisBoard = new MorrisBoard();
			}

			br.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return morrisBoard;
	}

	// Write the result into the file
	public static void WriteOutputInFile(String fileName, Estimations est) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);

			BufferedWriter bW = new BufferedWriter(fileWriter);

			// write output in the file
			bW.write(Constant.BOARD_POSITION + est.getMorrisBoard());
			bW.write("\n" + Constant.POSITION_EVALUATED + est.getPositionsEvaluated());
			bW.write("\n" + Constant.MINIMAX_ESTIMATE + est.getEstimate());
			// Always close files.
			bW.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
}

// To prepare final estimations
class Estimations {
	private int estimate = 0, positionsEvaluated = 0;
	private MorrisBoard morrisBoard;

	public int getEstimate() {
		return estimate;
	}

	public MorrisBoard getMorrisBoard() {
		return morrisBoard;
	}

	public void setMorrisBoard(MorrisBoard morrisBoard) {
		this.morrisBoard = morrisBoard;
	}

	public void setEstimate(int estimate) {
		this.estimate = estimate;
	}

	public int getPositionsEvaluated() {
		return positionsEvaluated;
	}

	public void setPositionsEvaluated(int positionsEvaluated) {
		this.positionsEvaluated = positionsEvaluated;
	}

}
