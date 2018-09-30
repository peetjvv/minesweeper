package ValueObjects;

import Enums.CellType;

/**
 *
 * @author Peet
 */
public final class Cell {

    private CellType _type;
    private boolean _flagged;
    private boolean _clicked;
    private int _number = 0;

    public Cell(int number, boolean flagged, boolean clicked) {
        this._number = number;
        if (_number == 0) {
            this._type = CellType.Blank;
        } else {
            this._type = CellType.Number;
        }
        this._flagged = flagged;
        this._clicked = clicked;
    }

    public Cell(CellType type, boolean flagged, boolean clicked) {
        this._type = type;
        this._flagged = flagged;
        this._clicked = clicked;
    }

    public Cell click() throws Exception {
        if (_flagged) {
            throw new Exception("Can't click a flagged cell");
        }
        return new Cell(_type, _flagged, true);
    }

    public Cell toggleFlagged() throws Exception {
        if (_clicked) {
            throw new Exception("Can't flag a clicked cell");
        }
        return new Cell(_type, !_flagged, false);
    }

    public CellType type() {
        return _type;
    }

    public boolean isFlagged() {
        return _flagged;
    }

    public boolean isClicked() {
        return _clicked;
    }

    public int number() {
        return _number;
    }

}
