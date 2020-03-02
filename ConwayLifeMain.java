package conwayLife;
import java.util.Arrays;

/**
 * A program to simulate Conway's game of life. Expects:
 * 1) a rectangular array of 0s and 1s representing the cells (dead or alive), given in the form [01]...[01]R[01]...[01]R......R[01]...[01]
 * 2) an int representing the number of generations to simulate
 * The array for each generation will then be printed to the console, according to the rules found on: https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 */
public class ConwayLifeMain 
{
	private static final String ARGUMENT_ROW_DELIMITER = "R";
	
	public static void main(String[] args)
	{
		String cellsStr = args[0];
		int[][] startArray = parseAndPrintStartArray(cellsStr);
		
		String generationsStr = args[1];
		int generations = Integer.parseInt(generationsStr);
		
		getGeneration(startArray, generations);
	}

	/**
	 * Parses the program argument as a 2D int array and prints to the console
	 */
	private static int[][] parseAndPrintStartArray(String cellsStr)
	{
		String[] rows = cellsStr.split(ARGUMENT_ROW_DELIMITER);
		int numRows = rows.length;
		int numCols = rows[0].length();
		
		int[][] startArray = new int[numRows][numCols];
		for (int i=0; i < numRows; i++)
		{
			String rowStr = rows[i];
			for (int j=0; j < numCols; j++)
			{
				String cellijStr = rowStr.substring(j, j+1);
				int cellij = Integer.parseInt(cellijStr);
				startArray[i][j] = cellij;
			}
		}
		
		printCellsArray(startArray);
		
		return startArray;
	}
	
	private static int[][] getGeneration(int[][] cells, int generations) 
	{
		for (int i = 0; i < generations; i++)
		{
			//nothing to do if all the cells are dead, just stop
			if (cells.length == 0) return cells;

			//cells might be born outside the existing array, so extend it on all sides
			int[][] paddedCells = padEdges(cells);

			//apply Conway's rules
			int[][] nextGenCells = nextGen(paddedCells);

			//get rid of any unnecessary edge rows or columns (which contain only dead cells)
			cells = trimPadding(nextGenCells);
			
			//print to the console
			printCellsArray(cells);
		}

		return cells;
	}

	/**
	 * Prints an array of cells to the console, with a new line after the array for legibility
	 */
	private static void printCellsArray(int[][] cells)
	{
		for (int[] cellsRow : cells) System.out.println(Arrays.toString(cellsRow));
		System.out.println("");
	}

	/**
	 * Removes rows or columns from the edge which only contain dead cells
	 */
	private static int [][] trimPadding(int[][] cells) 
	{
		int[][] cellsTopAndBottomTrimmed = trimTopAndBottomPadding(cells);

		return trimLeftAndRightPadding(cellsTopAndBottomTrimmed);
	}

	/**
	 * Removes rows or columns from the left/right edges which only contain dead cells
	 */
	private static int[][] trimLeftAndRightPadding(int[][] cells) 
	{
		int numRows = cells.length;
		int[] firstRow = cells[0];
		int numCols = firstRow.length;
		
		//index of left-most populated column
		int firstPopulatedColumnIndex = findFirstPopulatedColumnIndex(cells, numRows, numCols);
		
		//array is empty if there is no such index
		if (firstPopulatedColumnIndex == -1) return new int[][] {{}};
		
		//index of right-most populated column
		int lastPopulatedColumnIndex = findLastPopulatedColumnIndex(cells, numRows, numCols);
		
		//copy out the populated section
		int[][] retval = new int[numRows][lastPopulatedColumnIndex + 1 - firstPopulatedColumnIndex];
		for (int i = 0; i < numRows; i++)
		{
			retval[i] = Arrays.copyOfRange(cells[i], firstPopulatedColumnIndex, lastPopulatedColumnIndex + 1);
		}

		return retval;
	}

	/**
	 * Returns index of right-most populated column in cells
	 */
	private static int findLastPopulatedColumnIndex(int[][] cells, int numRows, int numCols) 
	{
		for (int j = numCols -1; j >= 0; j--)
		{
			for (int i = 0; i < numRows; i++)
			{
				int[] row = cells[i];
				if (row[j] == 1) return j;
			}
		}

		return -1;
	}

	/**
	 * Returns index of left-most populated column in cells
	 */
	private static int findFirstPopulatedColumnIndex(int[][] cells, int numRows, int numCols) 
	{
		for (int j = 0; j < numCols; j++)
		{
			for (int i = 0; i < numRows; i++)
			{
				int[] row = cells[i];
				if (row[j] == 1) return j;
			}
		}

		return -1;
	}

	/**
	 * Removes rows or columns from the top/bottom edges which only contain dead cells
	 */
	private static int[][] trimTopAndBottomPadding(int[][] cells) 
	{
		int numRows = cells.length;
		int[] firstRow = cells[0];
		int numCols = firstRow.length;
		
		//index of top-most populated row
		int firstPopulatedRowIndex = findFirstPopulatedRowIndex(cells, numRows, numCols);
		
		//array is empty if there is no such index
		if (firstPopulatedRowIndex == -1) return new int[][] {{}};
		
		//index of bottom-most populated row
		int lastPopulatedRowIndex = findLastPopulatedRowIndex(cells, numRows, numCols);

		//copy out the populated section
		int[][] retval = new int[lastPopulatedRowIndex + 1 - firstPopulatedRowIndex][numCols];
		for (int i = 0; i < lastPopulatedRowIndex + 1 - firstPopulatedRowIndex; i++)
		{
			retval[i] = cells[i + firstPopulatedRowIndex];
		}

		return retval;
	}

	/**
	 * Returns index of bottom-most populated row in cells
	 */
	private static int findLastPopulatedRowIndex(int[][] cells, int numRows, int numCols) 
	{
		int[] blankRow = new int[numCols];
		for (int i = numRows - 1; i >= 0; i--)
		{
			int[] row = cells[i];
			if (!Arrays.equals(row, blankRow)) return i;
		}

		return -1;
	}

	/**
	 * Returns index of top-most populated row in cells
	 */
	private static int findFirstPopulatedRowIndex(int[][] cells, int numRows, int numCols) 
	{
		int[] blankRow = new int[numCols];
		for (int i = 0; i < numRows; i++)
		{
			int[] row = cells[i];
			if (!Arrays.equals(row, blankRow)) return i;
		}

		return -1;
	}

	/**
	 * Applies Conway's rules to the paddedCells array
	 */
	private static int[][] nextGen(int[][] paddedCells) 
	{
		int numRows = paddedCells.length;
		int numCols = paddedCells[0].length;
		int[][] retval = new int[numRows][numCols]; 
		for (int i = 0; i < numRows; i++)
		{
			int[] row = paddedCells[i];
			for(int j = 0; j < numCols; j++)
			{
				int cellij = row[j];
				retval[i][j] = resolveCell(cellij, i, j, paddedCells, numRows, numCols);
			}
		}

		return retval;
	}

	/**
	 * Applies Conway's rules to a specific cell
	 */
	private static int resolveCell(int cellij, int i, int j, int[][] paddedCells, int numRows, int numCols) 
	{
		//count the number of living cells around the position i,j in the array
		int n = countNeighbouringLivingCells(i, j, paddedCells, numRows, numCols);

		//Live cells persist if there are 2 or 3 living neighbours. Otherwise they die
		if (cellij == 1) return n == 2 || n == 3 ? 1 : 0;
		
		//Dead cells become alive only if there are exactly 3 living neighbours
		if (cellij == 0) return n == 3 ? 1 : 0;

		return cellij;
	}

	/**
	 * Counts the number of living cells around the position i,j in paddedCells, taking into account that we might be at the
	 * edge of the array
	 */
	private static int countNeighbouringLivingCells(int i, int j, int[][] paddedCells, int numRows, int numCols) {

		int countOfLivingNeighbours = 0;
		countOfLivingNeighbours += i == 0 || j== 0 ? 0 : paddedCells[i-1][j-1];
		countOfLivingNeighbours += i == 0 ? 0 : paddedCells[i-1][j];
		countOfLivingNeighbours += i == 0 || j == numCols - 1 ? 0 : paddedCells[i-1][j+1];
		countOfLivingNeighbours += j == numCols - 1 ? 0 : paddedCells[i][j+1];
		countOfLivingNeighbours += i == numRows - 1 || j == numCols - 1? 0 : paddedCells[i+1][j+1];
		countOfLivingNeighbours += i == numRows - 1 ? 0 : paddedCells[i+1][j];
		countOfLivingNeighbours += i == numRows - 1 || j == 0 ? 0 : paddedCells[i+1][j-1];
		countOfLivingNeighbours += j == 0 ? 0 : paddedCells[i][j-1];

		return countOfLivingNeighbours;
	}

	/**
	 * Copies the array with a padding of dead cells around the edge (allowing for those cells to become alive in the next generation)
	 */
	private static int[][] padEdges(int[][] cells) 
	{
		int numRows = cells.length;
		int numCols = cells[0].length;

		int[][] retval = new int[numRows + 2][numCols + 2];

		for (int i = 0; i < numRows; i++)
		{
			retval[i+1][0] = 0;
			int[] originalRow = cells[i];
			for (int j = 0; j < numCols; j++)
			{
				retval[i+1][j+1] = originalRow[j];
			}
			retval[i+1][numCols + 1] = 0;
		}

		return retval;
	}
   
}


