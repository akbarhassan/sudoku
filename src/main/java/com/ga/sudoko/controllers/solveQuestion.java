package com.ga.sudoko.controllers;

import com.ga.sudoko.service.SudokuSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solve")
public class solveQuestion {

    @Autowired
    private SudokuSolver sudokuSolver;

    @GetMapping("/{puzzleNumber}")
    public ResponseEntity<String> solvePuzzle(@PathVariable int puzzleNumber) {
        if (puzzleNumber < 1 || puzzleNumber > 4) {
            return ResponseEntity.badRequest().body("Puzzle number must be between 1 and 4");
        }
        
        String result = sudokuSolver.solveAndSave(puzzleNumber);
        return ResponseEntity.ok(result);
    }
}
