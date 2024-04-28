package edu.ncsu.github.wordle;

/**
 * Represents a word in the Wordle game, consisting of an array of letters.
 * This class provides methods to manipulate and compare words.
 */
public class Word {

    private Letter[]  letters;    // Array to store the letters of the word
    private String    asString;   // String representation of the word

    public static int guesses = 0;

    /**
     * Constructor for creating a Word object with a specified length.
     *
     * @param wordLength
     *            The length of the word.
     */
    public Word ( final int wordLength ) {
        letters = new Letter[wordLength]; // Initialize the array to the
                                          // specified length

        final StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < wordLength; i++ ) {
            letters[i] = new Letter(); // Create a new Letter object for each
                                       // position
            sb.append( "A" ); // Initialize the string with 'A's
        }
        setLetters( sb.toString() );
    }

    /**
     * Constructor for creating a Word object with a specified word.
     *
     * @param newWord
     *            The word to initialize the Word object with.
     */
    public Word ( final String newWord ) {
        setLetters( newWord.toUpperCase() ); // Initialize the word with the
                                             // provided string
    }

    /**
     * Get the array of letters in the word.
     *
     * @return The array of letters.
     */
    public Letter[] getLetters () {
        return letters;
    }

    /**
     * Set the letters of the word.
     *
     * @param newWord
     *            The new word to set.
     */
    public void setLetters ( final String newWord ) {
        asString = newWord.toUpperCase(); // Update the string representation to
                                          // the uppercase of the provided word
        final int size = newWord.length();
        letters = new Letter[size]; // Resize the array to match the length of
                                    // the new word

        for ( int i = 0; i < size; i++ ) {
            letters[i] = new Letter( newWord.charAt( i ) ); // Initialize each
                                                            // Letter with the
                                                            // corresponding
                                                            // character in the
                                                            // new word
        }
    }

    /**
     * Get the Letter at a specific index.
     *
     * @param index
     *            The index of the letter.
     * @return The Letter at the specified index.
     */
    public Letter getLetterAt ( final int index ) {
        return letters[index];
    }

    /**
     * Get the length of the word.
     *
     * @return The length of the word.
     */
    public int getLength () {
        return letters.length;
    }

    /**
     * Set the Letter at a specific index.
     *
     * @param index
     *            The index of the Letter to set.
     * @param newChar
     *            The new character to set at the specified index.
     */
    public void setLetter ( final int index, char newChar ) {
        newChar = Character.toUpperCase( newChar );
        final Letter newLetter = new Letter( newChar );
        setLetter( index, newLetter );
    }

    /**
     * Set the Letter at a specific index.
     *
     * @param index
     *            The index of the Letter to set.
     * @param newLetter
     *            The new Letter to set at the specified index.
     */
    public void setLetter ( final int index, final Letter newLetter ) {
        letters[index] = newLetter;

        final StringBuilder sb = new StringBuilder();
        for ( final Letter letter : letters ) {
            sb.append( letter.getCharacter() );
        }
        asString = sb.toString();
    }

    /**
     * Compare this Word against a solution and update each Letter's status in
     * this word.
     *
     * @return True if the word matches the solution, false otherwise.
     * @throws WordLengthMismatchException
     *             If the word length is not equal to the solution length.
     */
    public boolean compareToSolution () throws WordLengthMismatchException {
        if ( null == Config.solution || this.getLength() != Config.solution.getLength() ) {
            throw new WordLengthMismatchException( "Word length must be equal to solution length" );
        }
        guesses++;
        // boolean wordIsSolution = true;

        for ( int i = 0; i < Config.solution.getLength(); i++ ) {
            if ( !compareLetterToSolution( i, -1 ) ) {
                // wordIsSolution = false;
            }
        }

        if ( Config.solution.toString().equals( asString ) ) {
            for ( int i = 0; i < Config.solution.getLength(); i++ ) {
                final Letter l = this.getLetterAt( i );
                l.setStatus( LetterStatus.GREEN_CORRECT );
                l.printInColor();
            }
        }
        else {
            for ( int i = 0; i < Config.solution.getLength(); i++ ) {
                final Letter l = this.getLetterAt( i );
                l.printInColor();
            }
        }

        System.out.println( "\u001B[0m" ); // Move to the next line after
                                           // printing the word
        return Config.solution.toString().equals( asString );
    }

    /**
     * Compare a Letter of this Word against the corresponding Letter of the
     * solution.
     *
     * @param letterIndex
     *            The index of the Letter to compare.
     * @param guessCount
     *            The count of guesses made so far, or -1 if only the Letter
     *            should be printed.
     * @return True if the Letter matches the solution, false otherwise.
     */
    public boolean compareLetterToSolution ( final int letterIndex, final int guessCount ) {
        if ( guessCount > 0 ) {
            // Print the right-aligned guess number
            final String formatted = String.format( "%5d", guessCount );
            System.out.print( formatted + ": " );

            for ( int i = 0; i < letterIndex; i++ ) {
                getLetterAt( i ).printInColor();
            }
        }

        final Letter guessLetter = this.getLetterAt( letterIndex );
        boolean letterIsCorrect = true;
        final String solution = Config.solution.toString();

        if ( Config.solution.getLetterAt( letterIndex ).getStatus() == LetterStatus.ORANGE_OBSCURED
                && Config.solution.getLetterAt( letterIndex ).getCharacter() == guessLetter.getCharacter() ) {
            guessLetter.setStatus( LetterStatus.ORANGE_OBSCURED );
            letterIsCorrect = true;
        }
        else if ( Config.solution.getLetterAt( letterIndex ).getStatus() == LetterStatus.ORANGE_OBSCURED ) {
            guessLetter.setStatus( LetterStatus.ORANGE_OBSCURED );
        }
        else if ( Config.solution.getLetterAt( letterIndex ).getCharacter() == guessLetter.getCharacter() ) {
            // Character is in the right position
            guessLetter.setStatus( LetterStatus.GREEN_CORRECT );
            // } else if
            // (Config.solution.toString().contains(Character.toString(guessLetter.getCharacter())))
            // {
            // // If the char is in the word but not in the right position
            // guessLetter.setStatus(LetterStatus.YELLOW_MISPLACED);
            // letterIsCorrect = false;
        }
        else if ( solution.contains( Character.toString( guessLetter.getCharacter() ) ) ) {
            // If the char is in the word but not in the right position
            final int totalLetter = countLetter( solution, guessLetter.getCharacter() );
            final int totalCorrect = countCorrect( guessLetter.getCharacter() );

            if ( totalLetter > totalCorrect ) {
                guessLetter.setStatus( LetterStatus.YELLOW_MISPLACED );
            }
            else {
                guessLetter.setStatus( LetterStatus.GRAY_NONEXISTENT );
            }
            letterIsCorrect = false;
        }
        else {
            // Character is not in word
            guessLetter.setStatus( LetterStatus.GRAY_NONEXISTENT );
            letterIsCorrect = false;
        }

        // guessLetter.printInColor();

        // if ( guessCount > 0 ) {
        // for ( int i = letterIndex + 1; i < this.getLength(); i++ ) {
        // getLetterAt( i ).printInColor();
        // }
        // System.out.println();
        // }

        return letterIsCorrect;
    }

    private int countLetter ( final String word, final char letter ) {
        int count = 0;
        for ( int i = 0; i < word.length(); i++ ) {
            if ( word.charAt( i ) == letter ) {
                count++;
            }
        }
        return count;
    }

    private int countCorrect ( final char letter ) {
        int count = 0;
        for ( int i = 0; i < this.getLength(); i++ ) {
            final Letter l = getLetterAt( i );
            if ( l.getCharacter() == letter && l.getStatus() == LetterStatus.GREEN_CORRECT ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Find the index of the first occurrence of a character in the Word.
     *
     * @param c
     *            The character to search for.
     * @return The index of the character if found, otherwise -1.
     */
    public int indexOf ( final char c ) {
        for ( int i = 0; i < letters.length; i++ ) {
            if ( letters[i].getCharacter() == c ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the Word contains a specific character.
     *
     * @param c
     *            The character to check for.
     * @return True if the character is found in the word, otherwise false.
     */
    public boolean contains ( final char c ) {
        return indexOf( c ) != -1;
    }

    @Override
    public String toString () {
        return asString;
    }

}
