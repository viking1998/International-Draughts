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
public class MyDraughtsPlayer  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public MyDraughtsPlayer(int maxSearchDepth) {
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
//        Move bestMove = node.getBestMove();
//        if (bestMove == null) {
//            if (node.getState().isWhiteToMove()) {
//                return alphaBetaMax(node, alpha, beta, depth);
//            } else  {
//                return alphaBetaMin(node, alpha, beta, depth);
//            }
//        } else {
//            if (stopped) { stopped = false; throw new AIStoppedException(); }
//            DraughtsState state = node.getState();
//            
//            if (state.isWhiteToMove()) {
//                alpha = bestValue;
//                
//                state.doMove(bestMove);
//                int newAlpha = Integer.max(alpha, alphaBetaMin(new DraughtsNode(state.clone()), 
//                                            alpha, beta, depth - 1));
//                
//                if (newAlpha > alpha){
//                    alpha = newAlpha;
//                }                
//                state.undoMove(bestMove);
//                
//                for (Move m : state.getMoves()){
//                    state.doMove(m);
//                    newAlpha = Integer.max(alpha, alphaBetaMin(new DraughtsNode(state.clone()), 
//                                                        alpha, beta, depth - 1));
//                    
//                    if (newAlpha > alpha){
//                        alpha = newAlpha;
//                        node.setBestMove(m);
//                    }
//                    state.undoMove(m);
//                }
//                return alpha;   
//            } else {
//                beta = bestValue;
//                
//                state.doMove(bestMove);
//                int newBeta = alphaBetaMin(new DraughtsNode(state.clone()), 
//                                            alpha, beta, depth - 1);
//                if (newBeta < beta){
//                    beta = newBeta;
//                }                
//                state.undoMove(bestMove);
//                for (Move m : state.getMoves()){
//                    state.doMove(m);
//                        
//                        newBeta = Integer.min(beta, 
//                                            alphaBetaMax(new DraughtsNode(state.clone()),
//                                                        alpha, beta, depth-1));
//                
//                    if (newBeta < beta){
//                        beta = newBeta;
//                        node.setBestMove(m);
//                    }
//                    state.undoMove(m);
//                }
//                return beta;
//            }
//        }
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
        // ToDo: write an alphabeta search to compute bestMove and value
        return beta;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
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
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) 
    {
        int[] pieces = state.getPieces();
        int num_whites = 0, num_blacks = 0;
        int num_kwhites = 0, num_kblacks = 0;
        for (int i = 1; i < pieces.length; i++)
        {
            if(pieces[i] == DraughtsState.BLACKPIECE)
                num_blacks ++;
            if(pieces[i] == DraughtsState.BLACKKING)
                num_kblacks ++;
            if(pieces[i] == DraughtsState.WHITEPIECE)
                num_whites ++;
            if(pieces[i] == DraughtsState.WHITEKING)
                num_kwhites ++;
        }
        
        return num_whites+3*num_kwhites-num_blacks-3*num_kblacks;
    }
    
}
