

import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

// IFF: If and only If
public class Coyote {
    private int[][] board;
    private boolean gameOver;
    private boolean turnEval;
    private int mdepth;  //Piles
    private int depth; // current pile
    private String moveBlk;// black player move
    private  String moveWh;
    private boolean turn; // true for Human, false for computer

    private int[][] blackPieces; // keep track of black player pieces
    private int[][] blackMoves; // keep track of black player possible moves


    private int[][] whitePieces;  // keep track of white player pieces
    private int[][] whiteMoves;   // keep track of white player possible moves

    private int minScore = 9999;
    private int maxScore;
    private long startTimer;
    private long endTimer;
    private Stack<int []> posMoves;

    public Coyote() {
        setup();
        gameOver = false;
        endTimer = TimeUnit.SECONDS.toNanos(5);
        mdepth = 5; // 6 depth
        depth=0;
        minScore= -9999;
        posMoves = new Stack <int []> ();
    }

    private void setup() {
        board = new int[][]
                {
                        {0, 0, 0, 1, 0, 0, 0},
                        {2, 2, 2, 0, 3, 3, 3},
                        {5, 5, 5, 0, 4, 4, 4},
                        {0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0},
                        {9, 9, 9, 0, 10, 10, 10},
                        {8, 8, 8, 0, 7, 7, 7},
                        {0, 0, 0, 6, 0, 0, 0},
                };

    }

    public void Start() {

        //gameOver = false;
        printBoard();
        String first = firstPlayer();
        if (first.equals("H") || first.equals("h")) {
            turn = true;
        } else if (first.equals("C") || first.equals("c")) {
            turn = false;
        }
        while (gameOver == false) {
            if (turn) {
                moveBlk = getBlkPlayerMove();
                while (checkMove(moveBlk) == false) {

                    moveBlk = getBlkPlayerMove();

                }
                makeMoveB(moveBlk);
                printBoard();
                turn = false;
            }
            if (!turn) {
                getWhPlayerMove();
               /*
               moveWh= getWhPlayerMoveNoAI();
              while(checkMoveW(moveWh) == false) {
                   moveWh = getWhPlayerMoveNoAI();
                }
               makeMoveW(moveWh);
                printBoard();

               */
               turn =true;
            }
        }
    }
    private int[] MiniMax() {
        Best best = new Best();
        best.mv = null;
        int moveScore= 0;
        best.score = -9999;
        getBlackMoves();
        getWhiteMoves();
        int[][] moves = whiteMoves;
        int i = 0;
        while ( moves[i][0] != 0 || moves[i][1] != 0 || moves[i][2] != 0 || moves[i][3] != 0) {
            storeMove(moves[i]);
            moveScore = min();
         if(moveScore > best.score) {
                 best.mv = moves[i];
             best.score = moveScore;
         }
            retractMove();
            i++;
        }
        return best.mv;
    }

    private int min() {
        if (gameOver) {
            return 9999;
        } else if (depth == mdepth) {
           turnEval=true;
            return eval();
        }
        else {
            Best best = new Best();
            best.score = 9999;
            best.mv=null;
            int moveScore;
            getBlackMoves();
            int[][] moves = blackMoves;
            int i = 0;
            depth++;
            while (moves[i][0] != 0 || moves[i][1] != 0
                    || moves[i][2] != 0 ||
                    moves[i][3] != 0) {
                storeMove(moves[i]);
                moveScore = max();
                if (moveScore < best.score) {
                    best.mv = moves[i];
                    best.score =moveScore;
                }

                retractMove();
                i++;
            }
            return best.score;
        }
    }

    private int max() {

        if (gameOver) {
            return -9999;
        } else if (depth == mdepth) {
           turnEval=false;
            return eval();
        }
        else {
            Best best = new Best();
            best.score = -9999;
            int moveScore;
            best.mv=null;

            int i = 0;
            getWhiteMoves();
            int[][] moves = whiteMoves;
            depth++;
             while(moves[i][0] != 0 || moves[i][1] != 0 ||
                     moves[i][2] != 0 ||
                    moves[i][3] != 0) {
                storeMove(moves[i]);
                moveScore = min();
                if (moveScore > best.score) {
                    best.mv = moves[i];
                    best.score = moveScore;
                }
                retractMove();
                i++;
            }
            return best.score;
        }
    }

    public class Best {
        public int score;
        public int []mv;

    }


    private void storeMove(int[] moves) {
        int y1 = moves[0];
        int x1 = moves[1];
        int y2 =moves[2];
        int x2 =moves[3];
        posMoves.push(new int[]{y2, x2, y1, x1, board[x2][y2]});
        board[x2][y2] = board[x1][y1];
        board[x1][y1] =0;
    }


    private int eval() {
        int score = 0;
        int piecesAtWh = 0;
        int piecesAtBlk = 0;
        getWhiteMoves();
        getBlackMoves();
        if (whiteMoves[0][0] == 0 && whiteMoves[0][1] == 0 && whiteMoves[0][2] == 0
                && whiteMoves[0][3] == 0) {
            if (turnEval==true) {
                return -999;
            } else {
                return 999;
            }

    }

    for(int i =0; i <whitePieces.length; i++) {
        // King
        if (board[whitePieces[i][0]][whitePieces[i][1]] == 1) {
            score += 10;
            piecesAtWh++;
        }
        if (board[whitePieces[i][0]][whitePieces[i][1]] == 2) {
            score +=7;
            piecesAtWh++;
        }
        if (board[whitePieces[i][0]][whitePieces[i][1]] == 3) {
            score += 5;
            piecesAtWh++;
        }
        if (board[whitePieces[i][0]][whitePieces[i][1]] == 4) {
            score += 4;
            piecesAtWh++;
        }
        if (board[whitePieces[i][0]][whitePieces[i][1]] == 5) {
            score += 3;
            piecesAtWh++;
        }
    }
        for(int i =0; i <blackPieces.length; i++) {
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 6) {
                score -= 10;
                piecesAtBlk++;
            }
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 7) {
                score -=7;
                piecesAtBlk++;
            }
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 8) {
                score += 5;
                piecesAtBlk++;
            }
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 9) {
                score -= 4;
                piecesAtBlk++;
            }
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 10) {
                score -= 3;
                piecesAtBlk++;
            }
        }
        return score;

}
/*
private void printMoves(int [][] moves) {
        String list = null;
        for(int[] moveList: moves){
            if( moveList[0] !=0 || moveList[1]!=0 ||moveList[2] !=0
                    ||moveList[3]!=0) {
                for (int i=0;i < moveList.length;i++){
                    if(i==0||i==2) {
                         list += intToAxis(moveList[i]);
                    }

                    else{
                         list += moveList[i]+1+" ";
                    }
                }
                System.out.println("+list+");
            }
        }
}
*/

    private void retractMove() {
        int []bwrdMove = posMoves.pop();
        int y1 = bwrdMove[0];
        int x1 = bwrdMove[1];
        int y2 = bwrdMove[2];
        int x2 = bwrdMove[3];
        int piece = bwrdMove[4];
        board[x2][y2] = board[x1][y1];
        board[x1][y1] = piece;

    }
/*
    private boolean checkMoveW(String move) {
        int y1 = charToInt(move.charAt(0));
        int x1 = toInt(move.charAt(1));
        int y2 = charToInt(move.charAt(2));
        int x2 = toInt((move.charAt(3)));


        getWhiteMoves();
        int[][] moves = whiteMoves;
        int i = 0;

        while (moves[i][0] != 0 || moves[i][1] != 0 || moves[i][2] != 0 || moves[i][3] != 0) {

            //System.out.println("ENTERS WhilE loop");
            if (moves[i][0] == y1 && moves[i][1] == x1 && moves[i][2] == y2 && moves[i][3] == x2) {
                //System.out.println("its legal");
                return true;
            }
            i++;
        }
        System.out.println("Illegal move: Try again");
        return false;
    }
*/
    private boolean checkMove(String move) {
        int y1 = charToInt(move.charAt(0));
        int x1 = toInt(move.charAt(1));
        int y2 = charToInt(move.charAt(2));
        int x2 = toInt((move.charAt(3)));

        getBlackMoves();
        int[][] moves = blackMoves;
        int i = 0;

        while (moves[i][0] != 0 || moves[i][1] != 0 || moves[i][2] != 0 || moves[i][3] != 0) {


            if (moves[i][0] == y1 && moves[i][1] == x1 && moves[i][2] == y2 && moves[i][3] == x2) {
                return true;
            }
            i++;
        }
        System.out.println("Illegal move: Try again");
        return false;
    }

    private void getBlackMoves() {
        int pieceLoc = 0;
        int moveLoc = 0;
        blackPieces = new int[20][2];
        blackMoves = new int[125][6];
        // get all pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j] == 6 || board[i][j] == 7 || board[i][j] == 8 || board[i][j] == 9 || board[i][j] == 10) {
                    blackPieces[pieceLoc][0] = i;
                    blackPieces[pieceLoc][1] = j;
                    pieceLoc++;
                }

            }
        }
        for (int i = 0; i < pieceLoc; i++) {
            // Ninja
            if (board[blackPieces[i][0]][blackPieces[i][1]] == 7) {
                // Ninja Up Left
                int x = blackPieces[i][0] - 1;
                int y = blackPieces[i][1] - 1;
                while (x >=0  && y >=0 && board[x][y] == 0) {
                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y--;
                }
                // Ninja Up Right
                x = blackPieces[i][0] - 1;
                y = blackPieces[i][1] + 1;
                while (x >= 0 && y <= 6 && board[x][y] == 0) {

                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y++;
                }
                // Ninja Down left Only iff to attack
                x = blackPieces[i][0] + 1;
                y = blackPieces[i][1] - 1;
                while (x <= 7 && y >= 0 && board[x][y] == 0) {
                    int attackDLX = blackPieces[i][0];
                    if (board[attackDLX][y] == 1 || board[attackDLX][y] == 2|| board[attackDLX][y] == 3|| board[attackDLX][y] == 4|| board[attackDLX][y] == 5) {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y--;

                }

                // Ninja Down  right iff to attack
                x = blackPieces[i][0] + 1;
                y = blackPieces[i][1] + 1;
                while (x <= 7 && y <=6 && (board[x][y] == 0)) {
                    int attackDRX = blackPieces[i][0];
                    if (board[attackDRX][y] == 1 || board[attackDRX][y] == 2|| board[attackDRX][y] == 3|| board[attackDRX][y] == 4|| board[attackDRX][y] == 5) {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y++;

                }

            }
            // Samurai
            else if (board[blackPieces[i][0]][blackPieces[i][1]] == 8) {
                // Move Vertical Forward
                int x = blackPieces[i][0] - 1;
                int y = blackPieces[i][1];
                while (x >= 0 && (board[x][y] == 0)) {
                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                }
                // Move to the right iff to attck
                x = blackPieces[i][0];
                y = blackPieces[i][1] + 1;
                while (y <= 6 && (board[x][y] == 0)) {
                    int attackSideways = blackPieces[i][0]-1;
                    if (board[attackSideways][y] == 1|| board[attackSideways][y] == 2||board[attackSideways][y] == 3|| board[attackSideways][y] == 4
                            || board[attackSideways][y] == 5) {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    y++;
                }
                // Move to the left iff to attack
                y = blackPieces[i][1] - 1;
                while (y >= 0 && (board[x][y] == 0)) {
                    int attackSideways = blackPieces[i][0]-1;
                    if (board[attackSideways][y] == 1|| board[attackSideways][y] == 2||board[attackSideways][y] == 3|| board[attackSideways][y] == 4
                            || board[attackSideways][y] == 5) {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    y--;
                }

            } else if (board[blackPieces[i][0]][blackPieces[i][1]] == 9) {
                // Mini Ninja Up Left
                int x = blackPieces[i][0]-1;
                int y = blackPieces[i][1]-1;
                if(x >=0  && y >= 0 && board[x][y] == 0) {

                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Mini Ninja Up Right
                x = blackPieces[i][0]-1;
                y = blackPieces[i][1]+1;
                if(x >= 0 && y <= 6 && board[x][y] == 0) {
                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Mini Ninja Down Left IFF to attack
                x = blackPieces[i][0]+1;
                y = blackPieces[i][1]-1;
                if(x >=0 && y >=0 && board[x][y] == 0) {
                    int attackDLX = blackPieces[i][0];
                       if(board[attackDLX][y] == 1 || board[attackDLX][y] == 2|| board[attackDLX][y] == 3|| board[attackDLX][y] == 4||
                            board[attackDLX][y] == 5) {
                            blackMoves[moveLoc][0] = blackPieces[i][1];
                            blackMoves[moveLoc][1] = blackPieces[i][0];
                            blackMoves[moveLoc][2] = y;
                            blackMoves[moveLoc][3] = x;
                            moveLoc++;
                        }
                    }
                // Mini Ninja Dow Right IFF to attack
                x = blackPieces[i][0]+1;
                y = blackPieces[i][1]+1;
                if (x <=7  && y <= 6 && board[x][y] == 0) {
                    int attackDLX = blackPieces[i][0];
                    if(board[attackDLX][y] == 1 || board[attackDLX][y] == 2|| board[attackDLX][y] == 3|| board[attackDLX][y] == 4||
                            board[attackDLX][y] == 5) {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    }
            } else if (board[blackPieces[i][0]][blackPieces[i][1]] == 10) {
                // Mini Samurai Vertical Forward
                int x = blackPieces[i][0]-1;
                int y = blackPieces[i][1];
                if(x > 0 && (board[x][y] == 0)) {
                    blackMoves[moveLoc][0] = blackPieces[i][1];
                    blackMoves[moveLoc][1] = blackPieces[i][0];
                    blackMoves[moveLoc][2] = y;
                    blackMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Move to the right iff to attack
                x = blackPieces[i][0];
                y = blackPieces[i][1]+1;
                if(y <=6 && (board[x][y] == 0)) {
                    int attackSideways = blackPieces[i][0]-1;
                    if (board[attackSideways][y] == 1 || board[attackSideways][y] == 2 || board[attackSideways][y] == 3
                            || board[attackSideways][y] == 4 || board[attackSideways][y] == 5)  {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                }
                // Move to the left iff to attack
                y = blackPieces[i][1]-1;
                if(y >=0 && (board[x][y] == 0)) {
                    int attackSideways = blackPieces[i][0]-1;
                    if (board[attackSideways][y] == 1 || board[attackSideways][y] == 2 || board[attackSideways][y] == 3
                            || board[attackSideways][y] == 4 || board[attackSideways][y] == 5)  {
                        blackMoves[moveLoc][0] = blackPieces[i][1];
                        blackMoves[moveLoc][1] = blackPieces[i][0];
                        blackMoves[moveLoc][2] = y;
                        blackMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                }

            }
        }
    }


    private void makeMoveW(String move) {

        int y1 = boardTransY(move.charAt(0));
        int x1 = boardTransX(move.charAt(1));
        int y2 = boardTransY(move.charAt(2));
        int x2 = boardTransX(move.charAt(3));
        int x3 = x2 + 1;


        board[x2][y2] = board[x1][y1];
        board[x1][y1] = 0;

        if (x3 <= 7) {
            board[x3][y2] = demote(board[x3][y2]);
        }
        checkGameOver();
    }

    private int demote(int piece) {
            switch(piece) {
                case 0:
                  return 0;
                case 1:
                    System.out.println("Hiya");
                    return 0;
                case  2:
                    System.out.println("Hiya");
                    return 4;
                case 3:
                    System.out.println("Hiya");
                    return 5;
                case 4:
                    System.out.println("Hiya");
                    return 0;
                case 5:
                    System.out.println("Hiya");
                    return 0;
                case 6:
                    System.out.println("Hiya");
                    return 0;
                case 7:
                    System.out.println("Hiya");
                    return 9;
                case 8:
                    System.out.println("Hiya");
                    return 10;
                case 9:
                    System.out.println("Hiya");
                    return 0;
                case 10:
                    System.out.println("Hiya");
                    return 0;
                default:
                    return -1;


            }
    }


    private void makeMoveB(String move) {
        int y1 = charToInt(move.charAt(0));
        int x1 = toInt(move.charAt(1));
        int y2 = charToInt(move.charAt(2));
        int x2 = toInt(move.charAt(3));
        int x3 = x2 + -1;

        board[x2][y2] = board[x1][y1];
        board[x1][y1] = 0;

        if (x3 >= 0) {
            board[x3][y2] = demote(board[x3][y2]);
        }
        checkGameOver();
    }

    private int toInt(char c) {

        switch (c) {
            case '1':
                return 7;
            case '2':
                return 6;
            case '3':
                return 5;
            case '4':
                return 4;
            case '5':
                return 3;
            case '6':
                return 2;
            case '7':
                return 1;
            case '8':
                return 0;
            default:
                return -1;
        }
    }
    private int boardTransX(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            default:
                return -1;

        }
    }
    private int boardTransY(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            default:
                return -1;

        }
    }
    private int charToInt(char c) {
        // Not case sensitive
        switch (c) {
            case 'a':
                return 0;
            case 'A':
                return 0;
            case 'b':
                return 1;
            case 'B':
                return 1;
            case 'c':
                return 2;
            case 'C':
                return 2;
            case 'd':
                return 3;
            case 'D':
                return 3;
            case 'e':
                return 4;
            case 'E':
                return 4;
            case 'f':
                return 5;
            case 'F':
                return 5;
            case 'g':
                return 6;
            case 'G':
                return 6;
            default:
                // error
                return -1;

        }
    }
    private char inToChar(int c) {
        switch (c) {
            case 0:
                return '7';
            case 1:
                return '6';
            case 2:
                return '5';
            case 3:
                return '4';
            case 4:
                return '2';
            case 5:
                return '1';
            case 6:
                return '0';
            default:
                return '0';
        }
    }


    private int chartoINT(int c) {
        switch (c) {
            case 0:
                return 7;
            case 1:
                return 6;
            case 2:
                return 5;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
                return 1;
            case 7:
                return  0;
            default:
                return -1;
        }
    }
    private int reverseBoardJ(int c) {
        switch (c) {
            case '0':
                return 8;
            case '1':
                return 7;
            case '2':
                return 6;
            case '3':
                return 5;
            case '4':
                return 4;
            case '5':
                return 3;
            case '6':
                return 2;
            case '7':
                return  1;
            default:
                return -1;
        }
    }
    private String getBlkPlayerMove() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your move HUMAN" + '\n');
        return input.next();

    }
    private String getWhPlayerMoveNoAI() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your move COMPUTER" + '\n');
        return input.next();
    }

    private String firstPlayer() {
        Scanner input = new Scanner(System.in);
        System.out.println(" Enter H for HUMAN and C for COMPUTER" + '\n');
        return input.next();

    }

    private void printBoard() {
        int lineNum = 8;
        System.out.println("-----------------------" + " " + "Computer");
        for (int i = 0; i < 8; i++) {

            System.out.print(lineNum + " ");
            lineNum--;
            String line = "";
            for (int j = 0; j < 7; j++) {
                line += intToChar(board[i][j]) + " ";
            }
            System.out.println(line + "\n");
        }
        System.out.println("-----------------------"+ " "+"Human");
        System.out.println(" A   B   C   D   E   F   G");

    }

    private String intToChar(int i) {
        /*
        0 = -
        Computer Pieces
        C_K = 1
        C_J =2
        C_S = 3
        C_MJ =4
        C_MS =5

        Human pieces
        H_K = 6
        H_J =7
        H_S = 8
        H_MJ = 9
        H_MS =10
         */
        switch (i) {
            case 0:
                return "-";
            case 1:
                return "wK";
            case 2:
                return "wJ";
            case 3:
                return "wS";
            case 4:
                return "wj";
            case 5:
                return "ws";
            case 6:
                return "bK";
            case 7:
                return "bJ";
            case 8:
                return "bS";
            case 9:
                return "bj";
            case 10:
                return "bs";
            default:
                return "-";
        }
    }

    public void getWhPlayerMove() {
        int []m;
        int i =0;
        startTimer = System.nanoTime();
        Best best = new Best();

        int[] mv = null;
            mv = MiniMax();

       System.out.println(mv[0]);
        System.out.println(mv[1]);
        System.out.println(mv[2]);
        System.out.println(mv[3]);
        StringBuilder temp = new StringBuilder();
        temp.append(mv[0]);
        temp.append(mv[1]);
        temp.append(mv[2]);
        temp.append(mv[3]);
        String bestmove = temp.toString();
        char y1 = intToAxis(bestmove.charAt(0));
        int x1 = reverseBoardJ(bestmove.charAt(1));
        char y2 = intToAxis(bestmove.charAt(2));
        int x2 = reverseBoardJ(bestmove.charAt(3));
       // System.out.println(y1);
       // System.out.println(x1);
       // System.out.println(y2);
       // System.out.println(x2);
        //System.out.println(bestmove);
          makeMoveW(bestmove);
          printBoard();
          System.out.println("Computer Moves: "+y1+x1+""+"-->" +y2+x2);

        }

    private void checkGameOver() {
        int[][] moves;
        int i = 0;
        boolean black = false;
        boolean white = false;
        if (board[0][3]  !=1 ) {
            gameOver = true;
            System.out.println("Human won");
        }
        if (board[7][3]  !=6) {
            black = false;
                gameOver = true;
            System.out.println(" Computer Won");
            }
                getBlackMoves();
                moves = blackMoves;
                if (moves[i][0] == 0 & moves[i][1] == 0 && moves[i][2] == 0
                        && moves[i][3] == 0) {
                    gameOver = true;
                    System.out.println("Human has no legal moves");
                }

                getWhiteMoves();
                moves = whiteMoves;
                if (moves[i][0] == 0 & moves[i][1] == 0 && moves[i][2] == 0
                        && moves[i][3] == 0) {
                    gameOver = true;
                    System.out.println("Computer has no legal moves");
                }
    }



    private char intToAxis(int i) {
        switch (i){
            case '0':
                return 'A';
            case '1':
                return 'B';
            case'2':
                return 'C';
            case '3':
                return 'D';
            case '4':
                return 'E';
            case '5':
                return 'F';
            case '6':
                return 'G';
            default: System.out.println("error");
            return  'x';
        }
    }

    private void getWhiteMoves() {

        int pieceLoc = 0;
        int moveLoc = 0;
        whitePieces = new int[20][2];
        whiteMoves = new int[125][4];
        // get all pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j] == 1 || board[i][j] == 2 || board[i][j] == 3 || board[i][j] == 4 || board[i][j] == 5) {
                    whitePieces[pieceLoc][0] = i;
                    whitePieces[pieceLoc][1] = j;
                    pieceLoc++;
                }

            }
        }
        for (int i = 0; i < pieceLoc; i++) {
            // Ninja
            if (board[whitePieces[i][0]][whitePieces[i][1]] == 2) {
                // Ninja Up Left
                int x = whitePieces[i][0] + 1;
                int y = whitePieces[i][1] + 1;
                while (x <= 7 && y <= 6 && board[x][y] == 0) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y++;
                }
                // Ninja Up Right
                x = whitePieces[i][0] + 1;
                y = whitePieces[i][1] - 1;
                while (x <= 7 && y >= 0 && board[x][y] == 0) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y--;
                }
                // Ninja Down left Only iff to attack
                x = whitePieces[i][0] - 1;
                y = whitePieces[i][1] + 1;
                while (x >=0 && y <=6 && board[x][y] == 0) {
                    int attackDLX = whitePieces[i][0];
                    if (board[attackDLX][y] == 6 || board[attackDLX][y] == 7|| board[attackDLX][y] == 8 ||
                            board[attackDLX][y] == 9 || board[attackDLX][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y++;

                }

                // Ninja Down  right iff to attack
                x = whitePieces[i][0] - 1;
                y = whitePieces[i][1] - 1;
                while (x >=0  && y >=0 && (board[x][y] == 0)) {
                    int attackDRX = whitePieces[i][0];
                    if (board[attackDRX][y] == 6 || board[attackDRX][y] == 7|| board[attackDRX][y] == 8 ||
                            board[attackDRX][y] == 9 || board[attackDRX][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y--;
                }
            }
            // Samurai
            else if (board[whitePieces[i][0]][whitePieces[i][1]] == 3) {
                // Move Vertical Forward
                int x = whitePieces[i][0] + 1;
                int y = whitePieces[i][1];
                while (x <= 7 && (board[x][y] == 0)) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                }
                // Move to the right iff to attack
                x = whitePieces[i][0];
                y = whitePieces[i][1] - 1;
                while (y > -1 && (board[x][y] == 0)) {
                    int attackSR = whitePieces[i][0] + 1;
                    if(attackSR <= 7) {
                        if (board[attackSR][y] == 6 || board[attackSR][y] == 7 || board[attackSR][y] == 8 ||
                                board[attackSR][y] == 9 || board[attackSR][y] == 10) {
                            whiteMoves[moveLoc][0] = whitePieces[i][1];
                            whiteMoves[moveLoc][1] = whitePieces[i][0];
                            whiteMoves[moveLoc][2] = y;
                            whiteMoves[moveLoc][3] = x;
                            moveLoc++;
                        }
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    y--;
                }
                // Move to the left iff to attack
                y = whitePieces[i][1]+1;
                while ( y <= 6  && (board[x][y] == 0)) {
                    int attackSL = blackPieces[i][0]+1;
                    if (board[attackSL][y] == 6 || board[attackSL][y] == 7|| board[attackSL][y] == 8 ||
                            board[attackSL][y] == 9 || board[attackSL][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    y++;
                }

            } else if (board[whitePieces[i][0]][whitePieces[i][1]] == 4) {
                // Mini Ninja Up Left
                int x = whitePieces[i][0]+1;
                int y = whitePieces[i][1]+1;
                 if(x <= 7 && y <=6  && board[x][y] == 0) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Mini Ninja Up Right
                x = whitePieces[i][0]+1;
                y = whitePieces[i][1]-1;
                if (x <= 7 && y >=0  && board[x][y] == 0) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Mini Ninja Down Left IFF to attack
                x = whitePieces[i][0]-1;
                y = whitePieces[i][1]+1;
                if(x >=0  && y <=6  && board[x][y] == 0) {
                    int attackDLX = whitePieces[i][0];
                    if (board[attackDLX][y] == 6 || board[attackDLX][y] == 7|| board[attackDLX][y] == 8 ||
                            board[attackDLX][y] == 9 || board[attackDLX][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                        }
                    }
                // Mini Ninja Dow Right IFF to attack
                x = whitePieces[i][0]-1;
                y = whitePieces[i][1]-1;
                if (x >=0  && y >=0 && board[x][y] == 0) {
                    int attackDLX = whitePieces[i][0];
                    if (board[attackDLX][y] == 6 || board[attackDLX][y] == 7|| board[attackDLX][y] == 8 ||
                            board[attackDLX][y] == 9 || board[attackDLX][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                }
            } else if (board[whitePieces[i][0]][whitePieces[i][1]] == 5) {
                // Mini Samurai Vertical Forward +1 block
                int x = whitePieces[i][0] + 1;
                int y = whitePieces[i][1];
                if (x <=7  && (board[x][y] == 0)) {
                    whiteMoves[moveLoc][0] = whitePieces[i][1];
                    whiteMoves[moveLoc][1] = whitePieces[i][0];
                    whiteMoves[moveLoc][2] = y;
                    whiteMoves[moveLoc][3] = x;
                    moveLoc++;
                }
                // Move to the right iff to attack
                x = whitePieces[i][0];
                y = whitePieces[i][1] - 1;
                if (y >= 0 && (board[x][y] == 0)) {
                    int attackSR = whitePieces[i][0] + 1;
                    if (board[attackSR][y] == 6 || board[attackSR][y] == 7|| board[attackSR][y] == 8 ||
                            board[attackSR][y] == 9 || board[attackSR][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }
                    }

                // Move to the left iff to attack
                y = whitePieces[i][1] + 1;
                if (y <= 6 && (board[x][y] == 0)) {
                    int attackSL = whitePieces[i][0] + 1;
                    if (board[attackSL][y] == 6 || board[attackSL][y] == 7|| board[attackSL][y] == 8 ||
                            board[attackSL][y] == 9 || board[attackSL][y] == 10) {
                        whiteMoves[moveLoc][0] = whitePieces[i][1];
                        whiteMoves[moveLoc][1] = whitePieces[i][0];
                        whiteMoves[moveLoc][2] = y;
                        whiteMoves[moveLoc][3] = x;
                        moveLoc++;
                    }

                }
            }
        }
    }
}

