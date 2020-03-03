package nl.tue.s2id90.group92;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class VeselinTopalov  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public VeselinTopalov(int maxSearchDepth) {
        super("best.png");
        this.maxSearchDepth = maxSearchDepth;
    }
    
    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s.clone());    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            // do this with iterative deepening, i.e. start from depth 1 and go further
            for(int depth = 1; depth <= maxSearchDepth; depth++){
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, depth);
                
                // store the bestMove found uptill now
                // NB this is not done in case of an AIStoppedException in alphaBetа()
                bestMove  = node.getBestMove();
            }
            
            // print the results for debugging reasons
            System.err.format(
                "%s: depth= %2d, best move = %5s, value=%d\n", 
                this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
            );
        } catch (AIStoppedException ex) {  /* nothing to do */  }
        
        if (bestMove==null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth);
        } else  {
            return alphaBetaMin(node, alpha, beta, depth);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        
        if (depth == 0 || state.getMoves().isEmpty())
        {
            return evaluate(state);
        }

        for (Move m : state.getMoves())
        {
            state.doMove(m);
            
            int newBeta = Integer.min(beta,
                                    alphaBetaMax(new DraughtsNode(state.clone()),
                                                          alpha, beta, depth-1));
            
            if (newBeta <= alpha) { return alpha; }
            
            if (newBeta < beta)
            {
                beta = newBeta;
                node.setBestMove(m);
            }
            
            state.undoMove(m);
        }
        return beta;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();

        if (depth == 0 || state.getMoves().isEmpty())
        {
            return evaluate(state);
        }

        for (Move m : state.getMoves())
        {
            state.doMove(m);
            
            int newAlpha = Integer.max(alpha, 
                                    alphaBetaMin(new DraughtsNode(state.clone()),
                                                        alpha, beta, depth - 1));
            
            if(newAlpha >= beta){
                return beta;
            }
            
            if (newAlpha > alpha){
                alpha = newAlpha;
                node.setBestMove(m);
            }
            
            state.undoMove(m);
        }
        return alpha;
    }
    
    /** A method that evaluates the given state. */
    int evaluate(DraughtsState state) 
    {
        int[] pieces = state.getPieces();
        int num_pieces = 0;
        int score_black = 0;
        int score_white = 0;
        int[]blackscores ={0, 5, 4, 3, 4, 4,
                            4, 2, 2, 3, 2,
                              3, 4, 5, 4, 3,
                            6, 5, 5, 4, 4,
                              4, 5, 4, 4, 5,
                            7, 6, 5, 5, 6,
                              4, 5, 6, 6, 7,
                            7, 6, 7, 7, 7,
                              7, 7, 9, 8, 9,
                            10, 9, 10, 10, 10
        };
        int[]whitescores ={0, 10, 10, 10, 9, 10,
                            9, 8, 9, 7,  7,
                              7,  7, 7, 6, 7,
                            7, 6,  6, 5, 4,
                              6,  5, 5, 6, 7,
                            5, 4,  4, 5, 4,
                              4,  4, 5, 5, 6,
                            3, 4,  5, 4, 3,
                              2,  3, 2, 2, 4,
                            5,  4, 3, 4, 4
        };
        for (int i = 1; i < pieces.length; i++)
        {
            if(pieces[i] == DraughtsState.BLACKPIECE)
                score_black += blackscores[i];
            if(pieces[i] == DraughtsState.BLACKKING)
                score_black += blackscores[i]+20;
            if(pieces[i] == DraughtsState.WHITEPIECE)
                score_white += whitescores[i];
            if(pieces[i] == DraughtsState.WHITEKING)
                score_white += whitescores[i]+20;
        }
        return score_white - score_black;
    }
}
