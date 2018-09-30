
import ValueObjects.Board;


/**
 *
 * @author Peet
 */
public class Minesweeper {

    public static void main(String[] args) {
        Board board = new Board(20);
        System.out.println(board.toHiddenValuesString());
    }
}
