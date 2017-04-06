package com.javarush.task.task35.task3513;

import java.util.*;

/**
 * будет содержать игровую логику и хранить игровое поле.
 *
 * Created by Shurik on 03.04.2017.
 */
public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;

    protected int score;        // текущий счет
    protected int maxTile;      // максимальный вес плитки на игровом поле

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer>  previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        resetGameTiles();
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> list = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()){
                    list.add(gameTiles[i][j]);
                }
            }
        }

        return list;
    }

    private void addTile(){
       List<Tile> list = getEmptyTiles();

       if (list != null && !list.isEmpty()) {
           Tile tile = list.get((int) (list.size() * Math.random()));

           tile.value = Math.random() < 0.9 ? 2 : 4;
       }
    }

    public void resetGameTiles(){
        score = 0;
        maxTile = 2;

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();
    }

    /**
     * Сжатие плиток, таким образом, чтобы все пустые плитки были справа,
     * т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
     *
     * @param tiles - список плит
     */
    private boolean compressTiles(Tile[] tiles){
        boolean res = false;
        int length = tiles.length;

        for (int i = 0; i < length - 1; i++) {
            if (tiles[i].isEmpty()){
                // ищем следующую плитку не пустую
                for (int j = i + 1; j < length; j++) {
                    if (!tiles[j].isEmpty()) {
                        res = true;

                        tiles[i] = tiles[j];
                        tiles[j] = new Tile();
                        break;
                    }
                }
            }
        }

        return res;
    }

    /**
     * Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}.
     * Обрати внимание, что ряд {4, 4, 4, 4} превратится в {8, 8, 0, 0},
     * а {4, 4, 4, 0} в {8, 4, 0, 0}.
     *
     * @param tiles - список плит
     */
    private boolean mergeTiles(Tile[] tiles){
        boolean res = false;

        int length = tiles.length;

        for (int i = 0; i < length - 1; i++) {
            if (!tiles[i].isEmpty() && tiles[i].value == tiles[i+1].value){
                res = true;

                tiles[i].value += tiles[i].value;
                tiles[i + 1].value = 0;

                if (maxTile < tiles[i].value){
                    maxTile = tiles[i].value;
                }

                score += tiles[i].value;

                for (int j = i + 1; j < length - 1; j++) {
                    tiles[j].value = tiles[j + 1].value;
                }

                tiles[length - 1].value = 0;
            }
        }

        return res;
    }

    private void rotate(){
        for (int i = 0; i < FIELD_WIDTH / 2; i++) {
            for (int j = i; j < FIELD_WIDTH - 1 - i; j++) {
                Tile tmp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[j][FIELD_WIDTH - 1 - i];
                gameTiles[j][FIELD_WIDTH - 1 - i] = gameTiles[FIELD_WIDTH - 1 - i][FIELD_WIDTH - 1 - j];
                gameTiles[FIELD_WIDTH - 1 - i][FIELD_WIDTH - 1 - j] = gameTiles[FIELD_WIDTH - 1 - j][i];
                gameTiles[FIELD_WIDTH - 1 - j][i] = tmp;
            }
        }
    }

    public void left(){
        if (isSaveNeeded){
            saveState(gameTiles);
        }

        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) addTile();

        isSaveNeeded = true;
    }

    public void right(){
        saveState(gameTiles);
        rotate();rotate();
        left();
        rotate();rotate();
    }

    public void up(){
        saveState(gameTiles);
        rotate();
        left();
        rotate();rotate();rotate();
    }

    public void down(){
        saveState(gameTiles);
        rotate();rotate();rotate();
        left();
        rotate();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove(){
        if(!getEmptyTiles().isEmpty())
            return true;

        for(int i = 0; i < gameTiles.length; i++) {
            for(int j = 1; j < gameTiles.length; j++) {
                if ( (gameTiles[i][j].value == gameTiles[i][j-1].value) || ((gameTiles[j][i].value == gameTiles[j-1][i].value)))
                    return true;
            }
        }

        return false;
    }

    private void saveState(Tile[][] tiles){
        Tile[][] newTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                newTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }

        previousStates.push(newTiles);
        previousScores.push(score);

        isSaveNeeded = false;
    }

    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;

        switch (n){
            case 0 : left(); break;
            case 1 : right(); break;
            case 2 : up(); break;
            case 3 : down();break;
        }
    }

    public boolean hasBoardChanged(){
        int sum1 = 0;
        int sum2 = 0;
        if(!previousStates.isEmpty()) {
            Tile[][] prev = previousStates.peek();

            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    sum1 += gameTiles[i][j].value;
                    sum2 += prev[i][j].value;
                }
            }
        }
        return sum1 != sum2;
    }


    public MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency moveEfficiency;
        move.move();
        if(hasBoardChanged()){
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }else{
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        rollback();
        return moveEfficiency;
    }

    /*    public static void main(String[] args) {
        Model m = new Model();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                m.gameTiles[i][j].value = i * 10 + j;
            }
        }

        for (int i = 0; i < FIELD_WIDTH; i++) {
            System.out.println(Arrays.toString(m.gameTiles[i]));
        }
        m.rotate();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            System.out.println(Arrays.toString(m.gameTiles[i]));
        }
    }*/
}
