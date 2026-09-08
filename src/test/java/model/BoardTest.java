package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires de la classe {@link Board} (TP2 - generation par agent IA).
 *
 * Les tests sont organises par fonctionnalite :
 *  - etat initial et restart()
 *  - alternance des tours
 *  - coups invalides (hors limites, case occupee, partie terminee)
 *  - detection des victoires (lignes, colonnes, diagonales, pour X et pour O)
 *  - match nul
 *  - accesseurs currentTurn / state
 */
public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    // ---------------------------------------------------------------
    // Etat initial / restart
    // ---------------------------------------------------------------

    @Test
    public void newBoardIsInProgressWithNoWinner() {
        assertTrue(board.isInProgressMode());
        assertFalse(board.isInFinishedMode());
        assertNull(board.getWinner());
    }

    @Test
    public void newBoardStartsWithPlayerX() {
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void restartResetsWinnerStateAndTurn() {
        // X gagne sur la premiere ligne
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(0, 2);
        assertEquals(Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());

        board.restart();

        assertNull(board.getWinner());
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void restartClearsCellsSoTheyCanBePlayedAgain() {
        board.mark(1, 1);
        assertEquals(Player.O, board.getCurrentTurn());

        board.restart();

        // si la case (1,1) a bien ete videe, le coup est accepte et le tour change
        board.mark(1, 1);
        assertEquals(Player.O, board.getCurrentTurn());
    }

    // ---------------------------------------------------------------
    // Alternance des tours
    // ---------------------------------------------------------------

    @Test
    public void validMarkFlipsTurnFromXToO() {
        board.mark(0, 0);
        assertEquals(Player.O, board.getCurrentTurn());
    }

    @Test
    public void validMarkFlipsTurnFromOToX() {
        board.mark(0, 0);
        board.mark(0, 1);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void validMarkKeepsGameInProgressWhenNoWin() {
        board.mark(0, 0);
        assertTrue(board.isInProgressMode());
        assertNull(board.getWinner());
    }

    // ---------------------------------------------------------------
    // Coups invalides (no-op : le tour ne change pas)
    // ---------------------------------------------------------------

    @Test
    public void markOnAlreadyPlayedCellIsIgnored() {
        board.mark(0, 0);          // X
        board.mark(0, 0);          // O tente la meme case -> ignore
        assertEquals(Player.O, board.getCurrentTurn());
    }

    @Test
    public void markWithNegativeRowIsIgnored() {
        board.mark(-1, 0);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void markWithNegativeColIsIgnored() {
        board.mark(0, -1);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void markWithRowTooLargeIsIgnored() {
        board.mark(3, 0);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void markWithColTooLargeIsIgnored() {
        board.mark(0, 3);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void markWithBothIndicesOutOfBoundsIsIgnored() {
        board.mark(5, -5);
        assertEquals(Player.X, board.getCurrentTurn());
        assertTrue(board.isInProgressMode());
    }

    @Test
    public void boundaryCellsAreValid() {
        board.mark(0, 0);
        board.mark(2, 2);
        board.mark(0, 2);
        board.mark(2, 0);
        // 4 coups valides -> on est revenu a X
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void markAfterGameFinishedIsIgnored() {
        // X gagne la premiere colonne
        board.mark(0, 0); board.mark(0, 1);
        board.mark(1, 0); board.mark(1, 1);
        board.mark(2, 0);
        assertTrue(board.isInFinishedMode());
        Player turnAtEnd = board.getCurrentTurn();

        board.mark(2, 2);          // case libre mais partie finie -> ignore

        assertEquals(turnAtEnd, board.getCurrentTurn());
        assertEquals(Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    // ---------------------------------------------------------------
    // Victoires de X
    // ---------------------------------------------------------------

    @Test
    public void xWinsOnFirstRow() {
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(0, 2);
        assertEquals(Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void xWinsOnSecondRow() {
        board.mark(1, 0); board.mark(0, 0);
        board.mark(1, 1); board.mark(0, 1);
        board.mark(1, 2);
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void xWinsOnThirdRow() {
        board.mark(2, 0); board.mark(0, 0);
        board.mark(2, 1); board.mark(0, 1);
        board.mark(2, 2);
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void xWinsOnFirstColumn() {
        board.mark(0, 0); board.mark(0, 1);
        board.mark(1, 0); board.mark(1, 1);
        board.mark(2, 0);
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void xWinsOnSecondColumn() {
        board.mark(0, 1); board.mark(0, 0);
        board.mark(1, 1); board.mark(1, 0);
        board.mark(2, 1);
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void xWinsOnThirdColumn() {
        board.mark(0, 2); board.mark(0, 0);
        board.mark(1, 2); board.mark(1, 0);
        board.mark(2, 2);
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void xWinsOnMainDiagonal() {
        board.mark(0, 0); board.mark(0, 1);
        board.mark(1, 1); board.mark(0, 2);
        board.mark(2, 2);
        assertEquals(Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void xWinsOnAntiDiagonal() {
        board.mark(0, 2); board.mark(0, 0);
        board.mark(1, 1); board.mark(0, 1);
        board.mark(2, 0);
        assertEquals(Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void winIsDetectedWhenLastMoveIsInMiddleOfLine() {
        // X joue les deux extremites puis le centre de la ligne 0
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 2); board.mark(1, 1);
        board.mark(0, 1);
        assertEquals(Player.X, board.getWinner());
    }

    // ---------------------------------------------------------------
    // Victoires de O
    // ---------------------------------------------------------------

    @Test
    public void oWinsOnRow() {
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(2, 2); board.mark(1, 2);
        assertEquals(Player.O, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void oWinsOnColumn() {
        board.mark(0, 0); board.mark(0, 2);
        board.mark(1, 0); board.mark(1, 2);
        board.mark(1, 1); board.mark(2, 2);
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    public void oWinsOnMainDiagonal() {
        board.mark(0, 1); board.mark(0, 0);
        board.mark(0, 2); board.mark(1, 1);
        board.mark(1, 0); board.mark(2, 2);
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    public void oWinsOnAntiDiagonal() {
        board.mark(0, 0); board.mark(0, 2);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(1, 0); board.mark(2, 0);
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    public void turnDoesNotFlipAfterWinningMove() {
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(0, 2);              // X gagne
        assertEquals(Player.X, board.getCurrentTurn());
    }

    // ---------------------------------------------------------------
    // Match nul
    // ---------------------------------------------------------------

    @Test
    public void fullBoardWithoutLineIsDrawFinishedWithNoWinner() {
        // X O X
        // X O O
        // O X X
        board.mark(0, 0); board.mark(0, 1);
        board.mark(0, 2); board.mark(1, 1);
        board.mark(1, 0); board.mark(1, 2);
        board.mark(2, 1); board.mark(2, 0);
        board.mark(2, 2);

        assertTrue(board.isInFinishedMode());
        assertFalse(board.isInProgressMode());
        assertNull(board.getWinner());
    }

    @Test
    public void gameIsStillInProgressWithOneEmptyCell() {
        board.mark(0, 0); board.mark(0, 1);
        board.mark(0, 2); board.mark(1, 1);
        board.mark(1, 0); board.mark(1, 2);
        board.mark(2, 1); board.mark(2, 0);
        // (2,2) reste libre
        assertTrue(board.isInProgressMode());
        assertNull(board.getWinner());
    }

    @Test
    public void winOnLastCellIsAWinNotADraw() {
        // X O X
        // O O X
        // O X X   -> X complete la colonne 2 avec le dernier coup
        board.mark(0, 0); board.mark(0, 1);
        board.mark(0, 2); board.mark(1, 0);
        board.mark(1, 2); board.mark(1, 1);
        board.mark(2, 1); board.mark(2, 0);
        board.mark(2, 2);

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    public void restartAfterDrawAllowsNewGame() {
        board.mark(0, 0); board.mark(0, 1);
        board.mark(0, 2); board.mark(1, 1);
        board.mark(1, 0); board.mark(1, 2);
        board.mark(2, 1); board.mark(2, 0);
        board.mark(2, 2);
        assertTrue(board.isInFinishedMode());

        board.restart();
        board.mark(1, 1);

        assertTrue(board.isInProgressMode());
        assertEquals(Player.O, board.getCurrentTurn());
    }

    // ---------------------------------------------------------------
    // Accesseurs
    // ---------------------------------------------------------------

    @Test
    public void setCurrentTurnChangesWhoPlaysNext() {
        board.setCurrentTurn(Player.O);
        assertEquals(Player.O, board.getCurrentTurn());

        board.mark(0, 0);              // c'est O qui joue
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    public void setCurrentTurnToOLetsOWin() {
        board.setCurrentTurn(Player.O);
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(0, 2);
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    public void getStateIsNeverNullAfterConstruction() {
        assertNotNull(board.getState());
    }

    @Test
    public void setStateWithCurrentStateKeepsModeConsistent() {
        board.setState(board.getState());
        assertTrue(board.isInProgressMode());
    }

    @Test
    public void inProgressAndFinishedAreMutuallyExclusive() {
        assertTrue(board.isInProgressMode() ^ board.isInFinishedMode());
        board.mark(0, 0); board.mark(1, 0);
        board.mark(0, 1); board.mark(1, 1);
        board.mark(0, 2);
        assertTrue(board.isInProgressMode() ^ board.isInFinishedMode());
    }

    // ---------------------------------------------------------------
    // Tests ajoutes apres analyse de mutation (PITest) - partie 3 du TP
    // Mutants survivants : isWinningMoveByPlayer, anti-diagonale
    //   - "currentRow + currentCol == 2" remplace par une soustraction
    //   - "cells[2][0].getValue() == player" nie
    // ---------------------------------------------------------------

    @Test
    public void xWinsOnAntiDiagonalFinishingAtCenter() {
        // X : (0,2) puis (2,0), coup gagnant au centre (1,1) : 1 - 1 != 2
        board.mark(0, 2); board.mark(0, 0);
        board.mark(2, 0); board.mark(0, 1);
        board.mark(1, 1);
        assertEquals("X doit gagner sur l'anti-diagonale en finissant au centre",
                Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void xWinsOnAntiDiagonalFinishingAtTopRightCorner() {
        // X : (1,1) puis (2,0), coup gagnant en (0,2) : 0 - 2 != 2
        board.mark(1, 1); board.mark(0, 0);
        board.mark(2, 0); board.mark(0, 1);
        board.mark(0, 2);
        assertEquals("X doit gagner sur l'anti-diagonale en finissant en (0,2)",
                Player.X, board.getWinner());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    public void noWinOnAntiDiagonalWhenBottomLeftCornerBelongsToOpponent() {
        // O occupe (2,0) ; X joue (0,2) puis (1,1) : pas de victoire
        board.mark(0, 2); board.mark(2, 0);
        board.mark(1, 1);
        assertNull("Pas de gagnant : (2,0) appartient a O", board.getWinner());
        assertTrue(board.isInProgressMode());
        assertEquals(Player.O, board.getCurrentTurn());
    }

    @Test
    public void noWinOnAntiDiagonalWhenCenterBelongsToOpponent() {
        // O occupe (1,1) ; X joue (0,2) puis (2,0) : pas de victoire
        board.mark(0, 2); board.mark(1, 1);
        board.mark(2, 0);
        assertNull("Pas de gagnant : le centre appartient a O", board.getWinner());
        assertTrue(board.isInProgressMode());
    }
}
