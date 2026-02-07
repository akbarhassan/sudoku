package com.ga.sudoko.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SudokuSolver {

    private static final int SIZE = 9;
    private static final String PUZZLES_DIR = "src/main/java/com/ga/sudoko/puzzles/";

    public int[][] readPuzzle(int puzzleNumber) throws IOException {
        String filename = PUZZLES_DIR + "puzzle" + puzzleNumber + ".txt";
        File file = Paths.get(filename).toFile();
        
        if (!file.exists()) {
            throw new IOException("Puzzle file not found: " + filename);
        }

        int[][] board = new int[SIZE][SIZE];
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            
            while ((line = reader.readLine()) != null && row < SIZE) {
                if (line.contains("---")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                int col = 0;
                
                for (String part : parts) {
                    String[] numbers = part.trim().split("\\s+");
                    for (String numStr : numbers) {
                        if (!numStr.isEmpty() && col < SIZE) {
                            board[row][col] = Integer.parseInt(numStr);
                            col++;
                        }
                    }
                }
                row++;
            }
        }
        
        return board;
    }

    public boolean solvePuzzle(int[][] board) {
        int[] empty = findEmptyCell(board);
        if (empty == null) {
            return true;
        }
        
        int row = empty[0];
        int col = empty[1];
        
        for (int num = 1; num <= 9; num++) {
            if (isValidMove(board, row, col, num)) {
                board[row][col] = num;
                
                if (solvePuzzle(board)) {
                    return true;
                }
                
                board[row][col] = 0;
            }
        }
        
        return false;
    }

    private boolean isValidMove(int[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }
        
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private int[] findEmptyCell(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public void writeSolution(int[][] board, int puzzleNumber) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = PUZZLES_DIR + "question" + puzzleNumber + "-" + timestamp + "-solved.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    writer.write(board[i][j] + " ");
                    if ((j + 1) % 3 == 0 && j < SIZE - 1) {
                        writer.write("| ");
                    }
                }
                writer.newLine();
                if ((i + 1) % 3 == 0 && i < SIZE - 1) {
                    writer.write("----------------------------------");
                    writer.newLine();
                }
            }
        }
    }

    public String solveAndSave(int puzzleNumber) {
        try {
            int[][] board = readPuzzle(puzzleNumber);
            
            if (solvePuzzle(board)) {
                writeSolution(board, puzzleNumber);
                return "Puzzle " + puzzleNumber + " solved successfully!";
            } else {
                return "No solution exists for puzzle " + puzzleNumber;
            }
        } catch (IOException e) {
            return "Error solving puzzle " + puzzleNumber + ": " + e.getMessage();
        }
    }
}
