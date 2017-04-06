package com.javarush.task.task35.task3513;

import java.awt.*;

/**
 *  Плитка.
 *
 * Created by Shurik on 03.04.2017.
 */
public class Tile {
    int value;

    public Tile(int value) {
        this.value = value;
    }
    public Tile(){
        this(0);
    }

    public boolean isEmpty(){
        return value == 0;
    }

    public Color getFontColor(){
        return new Color(value < 16 ? 0x776e65 : 0xf9f6f2);
    }

    public Color getTileColor(){
        Color res = null;

        switch (value){
            case 0 :        res = new Color(0xcdc1b4); break;
            case 2 :        res = new Color(0xeee4da); break;
            case 4 :        res = new Color(0xede0c8); break;
            case 8 :        res = new Color(0xf2b179); break;
            case 16 :       res = new Color(0xf59563); break;
            case 32 :       res = new Color(0xf67c5f); break;
            case 64 :       res = new Color(0xf65e3b); break;
            case 128 :      res = new Color(0xedcf72); break;
            case 256 :      res = new Color(0xedcc61); break;
            case 512 :      res = new Color(0xedc850); break;
            case 1024 :     res = new Color(0xedc53f); break;
            case 2048 :     res = new Color(0xedc22e); break;
        }

        return res == null ? new Color(0xff0000) : res;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
