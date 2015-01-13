package com.drewhannay.chesscrafter.models;

import com.google.common.base.Objects;

import java.util.List;

public final class Piece {
    public static final int TEAMLESS_PIECE = -1;

    private final long mId;
    private final PieceType mPieceType;
    private final ChessCoordinate mOriginalCoordinates;

    private int mMoveCount;
    private ChessCoordinate mCoordinates;

    public Piece(long id, PieceType pieceType, ChessCoordinate coordinates) {
        mId = id;
        mPieceType = pieceType;
        mOriginalCoordinates = coordinates;

        mCoordinates = coordinates;
    }

    public long getId() {
        return mId;
    }

    public PieceType getPieceType() {
        return mPieceType;
    }

    public ChessCoordinate getOriginalCoordinates() {
        return mOriginalCoordinates;
    }

    public ChessCoordinate getCoordinates() {
        return mCoordinates;
    }

    public int getMoveCount() {
        return mMoveCount;
    }

    public void setCoordinates(ChessCoordinate coordinates) {
        mCoordinates = coordinates;
    }

    public int getTeamId(Game game) {
        for (int teamIndex = 0; teamIndex < game.getTeams().length; teamIndex++) {
            if (game.getTeams()[teamIndex].getCapturedPieces().contains(this) || game.getTeams()[teamIndex].getPieces().contains(this))
                return teamIndex;
        }

        return TEAMLESS_PIECE;
    }

    public List<ChessCoordinate> getMovesFrom(ChessCoordinate coordinate, BoardSize boardSize) {
        // TODO:
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Piece))
            return false;

        Piece otherPiece = (Piece) other;

        // TODO: we shouldn't include mutable state in an equals method, but we still need to somehow check the other fields of the piece...
        return Objects.equal(mId, otherPiece.mId)
                && Objects.equal(mPieceType, otherPiece.mPieceType)
                && Objects.equal(mOriginalCoordinates, otherPiece.mOriginalCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mPieceType, mOriginalCoordinates);
    }

    @Override
    public String toString() {
        return getPieceType().toString();
    }
}