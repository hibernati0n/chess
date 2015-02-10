package com.drewhannay.chesscrafter.files;

import com.drewhannay.chesscrafter.models.History;
import com.drewhannay.chesscrafter.models.PieceType;
import com.drewhannay.chesscrafter.utility.GsonUtility;
import com.drewhannay.chesscrafter.utility.JavaFxFileDialog;
import com.drewhannay.chesscrafter.utility.Log;
import com.google.common.base.Preconditions;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

public enum FileManager {
    INSTANCE;

    public static final FileChooser.ExtensionFilter IMAGE_EXTENSION_FILTER = new FileChooser.ExtensionFilter("PNG", "*.png");
    public static final FileChooser.ExtensionFilter HISTORY_EXTENSION_FILTER = new FileChooser.ExtensionFilter("Saved Chess Game", "*.chesscrafter");
    public static final FileChooser.ExtensionFilter CONFIG_EXTENSION_FILTER = new FileChooser.ExtensionFilter("Crafted Game", "*.craftconfig");
    public static final FileChooser.ExtensionFilter PIECE_EXTENSION_FILTER = new FileChooser.ExtensionFilter("Piece", "*.piece");

    private static final String TAG = "FileUtility";

    private static final String SAVED_GAME_EXTENSION = ".chesscrafter";
    private static final String GAME_CRAFTER_EXTENSION = ".craftconfig";
    private static final String PIECE_EXTENSION = ".piece";

    private File sImageDir;
    private File sGameConfigDir;
    private File sSavedGameDir;
    private File sPieceDir;

    private boolean mInitialized;

    public void init() throws IOException {
        String hiddenDir;

        if (System.getProperty("os.name").startsWith("Windows")) {
            hiddenDir = System.getProperty("user.home") + "\\chess";

            Runtime rt = Runtime.getRuntime();
            // try to make our folder hidden on Windows
            rt.exec("attrib +H " + System.getProperty("user.home") + "\\chess");
        } else {
            // if we're not on Windows, just add a period
            hiddenDir = System.getProperty("user.home") + "/.chess";
        }

        sSavedGameDir = new File(hiddenDir + File.separator + "SavedGames" + File.separator);
        sGameConfigDir = new File(hiddenDir + File.separator + "GameConfigs" + File.separator);
        sPieceDir = new File(hiddenDir + File.separator + "Pieces" + File.separator);
        sImageDir = new File(hiddenDir + File.separator + "Images" + File.separator);

        boolean allExist = Stream.of(sSavedGameDir, sGameConfigDir, sPieceDir, sImageDir)
                .allMatch(dir -> {
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                    return dir.exists();
                });

        if (!allExist) {
            throw new IOException("Failed to create directory");
        }

        mInitialized = true;
    }

    private void verifyInitialized() {
        Preconditions.checkState(mInitialized, "Must call FileUtility.init()");
    }

    public boolean writeHistory(History history, String fileName) {
        verifyInitialized();

        return writeToFile(history, new File(sSavedGameDir, fileName + SAVED_GAME_EXTENSION));
    }

    public boolean writePiece(PieceType pieceType) {
        verifyInitialized();

        return writeToFile(pieceType, new File(sPieceDir, pieceType.getInternalId() + PIECE_EXTENSION));
    }

    public boolean deletePiece(PieceType pieceType) {
        verifyInitialized();

        // we might not HAVE an image to delete
        if (!new File(sImageDir, pieceType.getInternalId()).delete()) {
            Log.e(TAG, "Could not delete image for PieceType:" + pieceType.getInternalId());
        }

        return new File(sPieceDir, pieceType.getInternalId()).delete();
    }

    private boolean writeToFile(Object object, File file) {
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            String json = GsonUtility.toJson(object);
            fileOut.write(json.getBytes());
            fileOut.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing file", e);
            return false;
        }
        return true;
    }

    public boolean writePieceImage(@NotNull String internalId, @NotNull BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File(sImageDir, internalId));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public File readPieceImage(String imageName) {
        return new File(sImageDir, imageName);
    }

    @Nullable
    public File chooseFile(FileChooser.ExtensionFilter filter) {
        return JavaFxFileDialog.chooseFile(filter, sSavedGameDir);
    }

    @Nullable
    public File chooseDirectory() {
        return JavaFxFileDialog.chooseDirectory(sSavedGameDir);
    }
}