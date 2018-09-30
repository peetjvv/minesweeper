package ValueObjects;

import Enums.BoardState;
import Enums.CellType;
import java.util.ArrayList;

/**
 *
 * @author Peet
 */
public final class Board {

    private BoardState _state;
    private Cell[][] _cells;

    public Board(BoardState state, Cell[][] cells) {
        this._state = state;
        this._cells = cells;
    }

    public static Board newBoard(int numMines) {
        int[] dimensions = getDimensionsFromNumMines(numMines);
        Cell[][] cells = new Cell[dimensions[0]][dimensions[1]];

        // lay the mines
        int numMinesSoFar = 0;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                CellType type = CellType.Blank;
                if (numMinesSoFar != numMines) {
                    int rand = (int) (Math.random() * 2);
                    if (rand == 1) {
                        type = CellType.Mine;
                    }
                }

                cells[x][y] = new Cell(type, false, false);
            }
        }

        // calculate the numbers
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y].type() == CellType.Mine) {
                    continue;
                }

                int numAdjacentMines = 0;

                boolean lookUp = y != 0;
                boolean lookRight = x != cells.length - 1;
                boolean lookDown = y != cells[x].length - 1;
                boolean lookLeft = x != 0;

                if (lookUp && cells[x][y - 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookUp && lookRight && cells[x + 1][y - 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookRight && cells[x + 1][y].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookRight && lookDown && cells[x + 1][y + 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookDown && cells[x][y + 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookLeft && lookDown && cells[x - 1][y + 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookLeft && cells[x - 1][y].type() == CellType.Mine) {
                    numAdjacentMines++;
                }
                if (lookLeft && lookUp && cells[x - 1][y - 1].type() == CellType.Mine) {
                    numAdjacentMines++;
                }

                cells[x][y] = new Cell(numAdjacentMines, false, false);
            }
        }

        return new Board(BoardState.New, cells);
    }

    public Board toggleFlagged(int x, int y) throws Exception {
        if (x >= _cells.length || y >= _cells[x].length) {
            throw new Exception("Out of bounds");
        }

        Board nextBoard = clone();
        nextBoard._cells[x][y] = nextBoard._cells[x][y].toggleFlagged();
        nextBoard._state = BoardState.InProgress;
        return nextBoard;
    }

    public Board click(int x, int y) throws Exception {
        if (x >= _cells.length || y >= _cells[x].length) {
            throw new Exception("Out of bounds");
        }

        Board nextBoard = clone();
        nextBoard._cells[x][y] = nextBoard._cells[x][y].click();
        if (nextBoard._cells[x][y].type() == CellType.Mine) {
            nextBoard._state = BoardState.Lost;
        } else if (_cells[x][y].type() == CellType.Blank) {
            nextBoard.recursivelyClickSurroundingCells(x, y);
        } else {
            ArrayList<Cell> openCells = openCells();
            ArrayList<Cell> remainingMines = remainingMines();
            if (openCells.isEmpty() && remainingMines.isEmpty()) {
                nextBoard._state = BoardState.Won;
            } else if (!remainingMines.isEmpty()) {
                nextBoard._state = BoardState.InProgress;
            } else {
                boolean containsNumbers = false;
                for (int i = 0; i < openCells.size(); i++) {
                    if (openCells.get(i).type() == CellType.Number) {
                        containsNumbers = true;
                        break;
                    }
                }
                if (containsNumbers) {
                    nextBoard._state = BoardState.InProgress;
                } else {
                    nextBoard._state = BoardState.Won;
                }
            }
        }
        return nextBoard;
    }

    public ArrayList<Cell> openCells() {
        ArrayList<Cell> result = new ArrayList<>();
        for (int x = 0; x < _cells.length; x++) {
            for (int y = 0; y < _cells[x].length; y++) {
                if (!_cells[x][y].isClicked() && !_cells[x][y].isFlagged()) {
                    result.add(_cells[x][y]);
                }
            }
        }
        return result;
    }

    public ArrayList<Cell> remainingMines() {
        ArrayList<Cell> result = new ArrayList<>();
        for (int x = 0; x < _cells.length; x++) {
            for (int y = 0; y < _cells[x].length; y++) {
                if (_cells[x][y].type() == CellType.Mine && !_cells[x][y].isFlagged()) {
                    result.add(_cells[x][y]);
                }
            }
        }
        return result;
    }

    // to keep immutibility, this method should be fired on the next board only
    private void recursivelyClickSurroundingCells(int x, int y) throws Exception {
        // out of bounds -> return
        if (x == -1 || x == _cells.length || y == -1 || y == _cells[x].length) {
            return;
        }

        Cell thisCell = _cells[x][y];

        // reached number or flagged cell -> click numbered cell and return
        if (thisCell.type() == CellType.Number || thisCell.isFlagged()) {
            if (thisCell.type() == CellType.Number && !thisCell.isFlagged()) {
                _cells[x][y] = thisCell.click();
            }
            return;
        }

        // for some reason this is a mine cell, this shouldn't happen!
        if (thisCell.type() == CellType.Mine) {
            throw new Exception("For some reason the recursive click reached a mine! Will need to investigate this as it shouldn't happen.");
        }

        _cells[x][y] = thisCell.click();
        recursivelyClickSurroundingCells(x, y - 1); // up
        recursivelyClickSurroundingCells(x + 1, y - 1); // up-right
        recursivelyClickSurroundingCells(x + 1, y); // right
        recursivelyClickSurroundingCells(x + 1, y + 1); // down-right
        recursivelyClickSurroundingCells(x, y + 1); // down
        recursivelyClickSurroundingCells(x - 1, y + 1); // down-left
        recursivelyClickSurroundingCells(x - 1, y); // left
        recursivelyClickSurroundingCells(x + 1, y + 1); // up-left        
    }

    private static int[] getDimensionsFromNumMines(int numMines) {
        int numSquares = numMines * 10;
        int x = numSquares / 9;
        int y = numSquares / x;
        return new int[]{x, y};
    }

    public Board clone() {
        return new Board(_state, _cells);
    }
}
