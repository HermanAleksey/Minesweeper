package com.bsuir.herman.saper

import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class Saper {
    val EMPTY_SPOT: Char = '.'
    val UNKNOWN_SPOT = '/'
    val FLAG: Char = '*'
    val MINE: Char = 'X'

    /*Generating mines and numbers on field.
    * if x and y equals -1 - generate casual field.
    * Other way generate field, where cell[x][y] will be not mine*/
    fun generateMinefield(
        width: Int, height: Int,
        minesCount: Int,
        x: Int = -1, y: Int = -1
    ): Array<Array<Char>> {
        val array: Array<Array<Char>> = Array(width) { Array(height) { EMPTY_SPOT } }

        /*Users can't create table with so small size*/
        if (width < 3 || height < 3) {
            throw Exception("Minimal width and height of field is 3")
        }

        /*If the number of mines exceeds the area of the field, such a field cannot be created.*/
        if (minesCount > width * height) {
            throw Exception("Can't place $minesCount mines in such a small field")
        }

        if (x == -1 && y == -1) {
            /*generating casual field*/
            if (minesCount == width * height) {
                fillAllFieldWithMines(array)
                return array
            }

            placeMinesOnField(array, minesCount)
            placeNumbersOnField(array)
        } else {
            /*generating field around some cell*/
            if (x > width || x < 0 || y > height || y < 0) {
                throw Exception("Cell is out of the minefield")
            }

            /*If the number of mines exceeds the area of the field, such a field cannot be created.*/
            if (minesCount + 1 > width * height) {
                throw Exception("Can't place $minesCount mines in such a small field")
            }

            placeMinesOnField(array, minesCount, x, y)
            placeNumbersOnField(array)

        }

        return array
    }

    /*Opens a cell on the player's board and vision if it's needed.
     If the open cell is empty (no mines around) or
        contains a digit - returns true.
     In case the cell contains a mine - returns false*/
    fun openCoordinate(
        x: Int, y: Int, hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>
    ): Boolean {
        //case the spot is mine
        if (hostField[x][y] == MINE) {
            return false
        }
        //case the spot is number
        if (hostField[x][y].isDigit()) {
            userField[x][y] = hostField[x][y]
            return true
        }
        //case the spot is empty
        openVision(x, y, hostField, userField)

        return true
    }

    /*Return true only when game is ended by winning of player
    * and false in other cases */
    fun useFlagOnSpot(
        x: Int, y: Int,
        hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>
    ): Boolean {
        when (userField[x][y]) {
            FLAG -> userField[x][y] = UNKNOWN_SPOT
            UNKNOWN_SPOT -> {
                userField[x][y] = FLAG
                if (checkWinCondition(hostField, userField)) { //win condition
                    return true
                }
            }
        }
        return false
    }

    /*Calculates how many cells on the player field is opened*/
    fun getNumberOfOpenCells(userField: Array<Array<Char>>): Int {
        var count: Int = 0

        for (x in userField) {
            for (y in x) {
                if (y.isDigit() || y == EMPTY_SPOT) {
                    count++
                }
            }
        }

        return count
    }

    /*Returns string that displays field current state*/
    fun getFieldAsString(field: Array<Array<Char>>): String {
        var str: StringBuilder? = StringBuilder("\n")
        for (y in field[0].indices) {
            for (x in field.indices) {
                str?.append(field[x][y])
            }
            str?.append("\n")
        }
        return str.toString()
    }

    /*Checks if all mines on the field are flagged by the user and if any extra
     cells are marked return true if user win the game and false as default */
    fun checkWinCondition(
        hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>
    ): Boolean {
        for (i in hostField.indices) {
            for (j in hostField[0].indices) {
                if (userField[i][j] == UNKNOWN_SPOT) return false
                if (userField[i][j] == FLAG && hostField[i][j] == MINE) continue
                if (userField[i][j] != FLAG && hostField[i][j] == MINE) return false
                if (userField[i][j] == FLAG && hostField[i][j] != MINE) return false
            }
        }
        return true
    }

    /*Open spot and place around, if it's needed*/
    private fun openVision(
        x: Int, y: Int, hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>
    ) {
        if (userField[x][y] != UNKNOWN_SPOT) {               // if spot is already opened - notice about it
            return
        }

        val openVision: (x: Int, y: Int) -> Unit = { x, y ->
            openVision(x, y, hostField, userField)
        }

        if (hostField[x][y] == EMPTY_SPOT) {
            userField[x][y] = EMPTY_SPOT
            if (y > 0) {
                openVision(x, y - 1)
            }
            if (x > 0) {
                openVision(x - 1, y)
            }
            if (y < hostField.size - 1) {
                openVision(x, y + 1)
            }
            if (x < hostField[0].size - 1) {
                openVision(x + 1, y)
            }

            if (y > 0 && x > 0) {
                openVision(x - 1, y - 1)
            }
            if (x > 0 && y < hostField.size - 1) {
                openVision(x - 1, y + 1)
            }
            if (x < hostField[0].size - 1 && y > 0) {
                openVision(x + 1, y - 1)
            }
            if (y < hostField.size - 1 && x < hostField[0].size - 1) {
                openVision(x + 1, y + 1)
            }

        } else userField[x][y] = hostField[x][y]
    }

    /*Fills the field with the specified number of mines*/
    private fun placeMinesOnField(
        array: Array<Array<Char>>,
        minesCount: Int
    ) {
        var minesPlaced = 0
        while (minesPlaced < minesCount) {
            val x = Random().nextInt(array[0].size)
            val y = Random().nextInt(array.size)

            if (array[y][x] == MINE) continue
            array[y][x] = MINE
            minesPlaced++
        }
    }

    /*Fills the field, but can't place on selected cell*/
    private fun placeMinesOnField(
        array: Array<Array<Char>>,
        minesCount: Int,
        xBanned: Int,
        yBanned: Int
    ) {
        var minesPlaced = 0
        while (minesPlaced < minesCount) {
            val x = Random().nextInt(array[0].size)
            val y = Random().nextInt(array.size)

            if (x == xBanned && y == yBanned) continue
            if (array[x][y] == MINE) continue
            array[x][y] = MINE
            minesPlaced++
        }
    }

    /*Fills the whole array with mines*/
    private fun fillAllFieldWithMines(array: Array<Array<Char>>) {
        for (i in array.indices) {
            for (j in array[0].indices) {
                array[j][i] = MINE
            }
        }
    }

    /*Counts how many mines are around each cell and assigns a quantity value to it*/
    private fun placeNumbersOnField(array: Array<Array<Char>>) {
        for (i in array.indices) {
            for (j in array[i].indices) {
                if (array[i][j] != MINE) {
                    var num = 0

                    if (i == 0 && j == 0) {                          // left top
                        if (array[1][1] == MINE) num++
                        if (array[0][1] == MINE) num++
                        if (array[1][0] == MINE) num++
                        if (num > 0) {
                            array[0][0] = num.toString().single()
                        }
                        continue
                    }
                    if (i == array.size - 1 && j == 0) {              // left bottom
                        if (array[i - 1][0] == MINE) num++
                        if (array[i - 1][1] == MINE) num++
                        if (array[i][1] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (i == 0 && j == array[0].size - 1) {             //right top
                        if (array[0][j - 1] == MINE) num++
                        if (array[1][j - 1] == MINE) num++
                        if (array[1][j] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (i == array.size - 1 && j == array[0].size - 1) { // right bottom
                        if (array[i][j - 1] == MINE) num++
                        if (array[i - 1][j - 1] == MINE) num++
                        if (array[i - 1][j] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (j == 0) {                             // left
                        if (array[i + 1][j + 1] == MINE) num++
                        if (array[i - 1][j + 1] == MINE) num++
                        if (array[i][j + 1] == MINE) num++
                        if (array[i + 1][0] == MINE) num++
                        if (array[i - 1][0] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (j == array[0].size - 1) {               // right
                        if (array[i + 1][j - 1] == MINE) num++
                        if (array[i - 1][j - 1] == MINE) num++
                        if (array[i][j - 1] == MINE) num++
                        if (array[i + 1][j] == MINE) num++
                        if (array[i - 1][j] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (i == 0) {                               // top
                        if (array[i][j - 1] == MINE) num++
                        if (array[i][j + 1] == MINE) num++
                        if (array[i + 1][j - 1] == MINE) num++
                        if (array[i + 1][j] == MINE) num++
                        if (array[i + 1][j + 1] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }
                    if (i == array.size - 1) {                   // bottom
                        if (array[i][j - 1] == MINE) num++
                        if (array[i][j + 1] == MINE) num++
                        if (array[i - 1][j - 1] == MINE) num++
                        if (array[i - 1][j] == MINE) num++
                        if (array[i - 1][j + 1] == MINE) num++
                        if (num > 0) {
                            array[i][j] = num.toString().single()
                        }
                        continue
                    }

                    //center
                    if (array[i + 1][j - 1] == MINE) num++
                    if (array[i + 1][j] == MINE) num++
                    if (array[i + 1][j + 1] == MINE) num++
                    if (array[i - 1][j - 1] == MINE) num++
                    if (array[i - 1][j] == MINE) num++
                    if (array[i - 1][j + 1] == MINE) num++
                    if (array[i][j - 1] == MINE) num++
                    if (array[i][j + 1] == MINE) num++
                    if (num > 0) {
                        array[i][j] = num.toString().single()
                    }

                }
            }
        }
    }

}