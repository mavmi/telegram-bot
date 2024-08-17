package mavmi.telegram_bot.hb.sheets.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class CellPosition {

    private List<Character> column;
    private int row;

    public void moveUp() {
        row--;
    }

    public void moveDown() {
        row++;
    }

    public void moveRight() {
        char lastChar = column.get(column.size() - 1);

        if (lastChar != 'Z') {
            column.set(column.size() - 1, (char) (((int) lastChar) + 1));
        } else {
            int positionToChange = column.size() - 1;
            while (positionToChange >= 0) {
                char sym = column.get(positionToChange);
                if (sym != 'Z') {
                    column.set(positionToChange, (char) (((int) sym) + 1));
                    break;
                } else {
                    column.set(positionToChange, 'A');
                    positionToChange--;
                }
            }
            if (positionToChange < 0) {
                column.add('A');
            }
        }
    }

    public void moveLeft() {
        char lastChar = column.get(column.size() - 1);

        if (lastChar != 'A') {
            column.set(column.size() - 1, (char) (((int) lastChar) - 1));
        } else {
            int positionToChange = column.size() - 1;
            while (positionToChange >= 0) {
                char sym = column.get(positionToChange);
                if (sym != 'A') {
                    column.set(positionToChange, (char) (((int) sym) - 1));
                    break;
                } else {
                    column.set(positionToChange, 'Z');
                    positionToChange--;
                }
                if (positionToChange < 0) {
                    column.remove(column.size() - 1);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : column) {
            stringBuilder.append(c);
        }

        return stringBuilder.append(row).toString();
    }
}
