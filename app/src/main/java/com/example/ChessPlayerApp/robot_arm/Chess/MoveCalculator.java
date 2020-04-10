package com.example.ChessPlayerApp.robot_arm.Chess;

import java.util.ArrayList;
import static com.example.ChessPlayerApp.robot_arm.Chess.TheEngine.theBoard;

public class MoveCalculator {
    // can use theBoard to calculate the first matrix

    // from/to are 2-D array showing occupancy of piece on board
    // 1:white 0:empty -2:black
    public static int[][] calculate(int[][] from, int[][] to){
        // moveMat
        // 0 : constant position
        // 1 : from empty to white
        // -1 : from white to empty
        // 2 : from black to empty
        // -2 : from empty to black
        // 3 : from black to white
        // -3 : from white to black
        int[][] moveMat = new int[8][8];
        for (int i = 0; i < 8; i ++)
            for (int j = 0; j < 8; j ++){
                moveMat[i][j] = to[i][j] - from[i][j];
            }

        return moveMat;
    }

    public static String getMove(int[][] to){
        /* Calculate the previous matrix from theBoard */
        int[][] from = new int[8][8];
        for (int i = 0; i < 64; i ++){
            if (theBoard[i] > 'A' && theBoard[i] < 'Z')
                from[i/8][i%8] = 1;
            else if (theBoard[i] == '*')
                from[i/8][i%8] = 0;
            else
                from[i/8][i%8] = -2;
        }

        // only consider legal move
        // special move:
        // k-0-0r : 4 non-zero
        // en passant capture : 3 non-zero
        // normal move & normal eat : 2 non-zero
        String move;
        int[][] moveMat = calculate(from, to);

        // aim to find first click and second click which combine to form a movement
        // 1. find types of movement
        ArrayList<int[]> points = new ArrayList<>();
        for (int i = 0; i < 8; i ++) {
            for (int j = 0; j < 8; j++) {
                if (moveMat[i][j] != 0)
                    points.add(new int[]{i, j});
            }
        }

        int[] firstCli = new int[2];
        int[] secondCli = new int[2];

        switch (points.size()){
            case 2:
                for (int[] pos : points){
                    if (to[pos[0]][pos[1]] == 0){
                        // record first click
                        firstCli = pos;
                        break;
                    }
                }
                points.remove(firstCli);
                secondCli = points.get(0);

                char piece = theBoard[firstCli[0] * 8 + firstCli[1]];

                int fstPos, secPos;
                fstPos = firstCli[0] * 8 + firstCli[1];
                secPos = secondCli[0] * 8 + secondCli[1];

                // pown promotion
                if(piece == 'P' && secondCli[0] == 7){
                    if (secondCli[1] > firstCli[1])
                        return "Pr-" +  secPos;
                    else if (secondCli[1] < firstCli[1])
                        return "Pl-" +  secPos;
                    else
                        return "Pu-" +  secPos;
                }


                move = "" + piece + (fstPos < 10? "0" + fstPos:fstPos) + (secPos < 10? "0" + secPos: secPos);
                return move;

            case 3:
                // only consider en passant capture on human's side
                for (int[] pos : points){
                    if (from[pos[0]][pos[1]] == 0){
                        secondCli = pos;
                        break;
                    }
                }
                points.remove(secondCli);
                for (int[] pos : points){
                    if (pos[1] != secondCli[1]) {
                        firstCli = pos;
                        break;
                    }
                }
                if (secondCli[1] > firstCli[1]){
                    return "PER" + (secondCli[0]*8 + secondCli[1]);
                }else{
                    return "PEL" + (secondCli[0]*8 + secondCli[1]);
                }

            case 4:
                // white side
                if (points.get(0)[0] == 0) {
                    for (int[] pos : points) {
                        if (pos[1] == 0) {
                            //secondCli = new int[]{0, 2};
                            return "K0-0-0";
                        }
                        if (pos[1] == 7) {
                            //secondCli = new int[]{0, 6};
                            return "K-0-0R";
                        }
                    }
                    //firstCli = new int[]{0, 4};
                }else{
                    for (int[] pos : points) {
                        if (pos[1] == 0) {
                            //secondCli = new int[]{7, 2};

                            return "k0-0-0";
                        }
                        if (pos[1] == 7) {
                            //secondCli = new int[]{7, 6};

                            return "k-0-0r";
                        }
                    }
                    //firstCli = new int[]{7, 4};


                }
                break;
        }

        return "";
    }

}