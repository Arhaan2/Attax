package ataxx;

import java.util.ArrayList;
import java.util.Random;

import static ataxx.PieceColor.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A Player that computes its own moves.
 *
 * @author Arhaan Aggarwal
 */
class AI extends Player {

    /**
     * Maximum minimax search depth before going to static evaluation.
     */
    private static final int MAX_DEPTH = 4;
    /**
     * A position magnitude indicating a win (for red if positive, blue
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI for GAME that will play MYCOLOR. SEED is used to initialize
     * a random-number generator for use in move computations.  Identical
     * seeds produce identical behaviour.
     */
    AI(Game game, PieceColor myColor, long seed) {
        super(game, myColor);
        _random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getMove() {
        if (!getBoard().canMove(myColor())) {
            game().reportMove(Move.pass(), myColor());
            return "-";
        }
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(move, myColor());
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(getBoard());
        _lastFoundMove = null;
        if (myColor() == RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to the findMove method
     * above.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _foundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _foundMove. If the game is over
     * on BOARD, does not set _foundMove.
     */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board, WINNING_VALUE + depth);
        }
        Move best;
        best = null;
        int bestScore;
        bestScore = (sense == 1) ? -INFTY : INFTY;
        ArrayList<Move> movesList = moveMover(board, board.whoseMove());
        if (board.legalMove(Move.pass())) {
            movesList.add(Move.PASS);
        }
        int response;
        if (sense == -1) {
            for (Move moveX : movesList) {
                Board b0 = new Board(board);
                b0.makeMove(moveX);
                response = minMax(b0, depth - 1, false,
                        sense * -1, alpha, beta);
                if (response < bestScore) {
                    best = moveX;
                    bestScore = response;
                    beta = min(bestScore, beta);
                    if (alpha >= beta) {
                        if (saveMove) {
                            _lastFoundMove = best;
                        }
                        return bestScore;
                    }
                }
            }
        } else {
            for (Move moveX : movesList) {
                Board b0 = new Board(board);
                b0.makeMove(moveX);
                response = minMax(b0, depth - 1, false,
                        sense * -1, alpha, beta);
                if (response > bestScore) {
                    best = moveX;
                    bestScore = response;
                    alpha = max(bestScore, alpha);
                    if (alpha >= beta) {
                        if (saveMove) {
                            _lastFoundMove = best;
                        }
                        return bestScore;
                    }
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestScore;
    }

    /**
     * Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     * won positions, and 0 for ties.
     */
    private int staticScore(Board board, int winningValue) {
        PieceColor winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
            case RED -> winningValue;
            case BLUE -> -winningValue;
            default -> 0;
            };
        }
        return board.redPieces() - board.bluePieces();
    }

    /**
     * Creates list of possible moves.
     *
     * @param board
     * @param color
     * @return ArrayList<Move>
     */
    public ArrayList<Move> moveMover(Board board, PieceColor color) {
        ArrayList<Move> movesList = new ArrayList<Move>();
        for (char c = 'a'; c <= 'g'; c++) {
            for (char r = '1'; r <= '7'; r++) {
                if (board.get(c, r).equals(color)) {
                    for (int dc = -2; dc <= 2; dc++) {
                        for (int dr = -2; dr <= 2; dr++) {
                            if ((board.get((char) (c + dc),
                                    (char) (r + dr)).equals(EMPTY))
                                    && (board.legalMove(c, r, (char) (c + dc),
                                    (char) (r + dr)))) {
                                movesList.add(Move.move(c, r, (char) (c + dc),
                                        (char) (r + dr)));
                            }
                        }
                    }
                }
            }
        }
        return movesList;
    }
    /**
     * Pseudo-random number generator for move computation.
     */
    private Random _random = new Random();
}
