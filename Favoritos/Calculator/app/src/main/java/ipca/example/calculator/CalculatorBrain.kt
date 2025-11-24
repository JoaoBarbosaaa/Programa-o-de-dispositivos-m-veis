package ipca.example.calculator

import kotlin.math.sqrt

class CalculatorBrain {

    enum class Operation(val symbol: String) {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("×"),
        DIVIDE("÷"),
        EQUAL("="),
        SQRT("√"),
        PERCENTAGE("%"),
        CLEAR("C"),
        BACKSPACE("⌫");

        companion object {
            fun parseOperation(op: String): Operation {
                return when (op) {
                    "+" -> ADD
                    "-" -> SUBTRACT
                    "×" -> MULTIPLY
                    "÷" -> DIVIDE
                    "=" -> EQUAL
                    "√" -> SQRT
                    "%" -> PERCENTAGE
                    "C" -> CLEAR
                    "⌫" -> BACKSPACE
                    else -> EQUAL
                }
            }
        }
    }

    var operand: Double = 0.0
    private var pendingOperation: Operation? = null

    fun doOperation(newOperand: Double, newOperation: Operation, isSqrtActive: Boolean = false): Double {
        var result = newOperand

        if (pendingOperation == Operation.SQRT && isSqrtActive) {
            result = sqrt(newOperand)
            pendingOperation = null
        } else if (pendingOperation != null) {
            result = when (pendingOperation) {
                Operation.ADD -> operand + newOperand
                Operation.SUBTRACT -> operand - newOperand
                Operation.MULTIPLY -> operand * newOperand
                Operation.DIVIDE -> if (newOperand != 0.0) operand / newOperand else Double.NaN
                Operation.PERCENTAGE -> (operand / 100.0) * newOperand
                else -> newOperand
            }
            pendingOperation = null
        }

        when (newOperation) {
            Operation.SQRT -> {
                pendingOperation = Operation.SQRT
                // Não calcula ainda, espera = ou outra op
            }
            Operation.EQUAL -> {
                pendingOperation = null
            }
            Operation.PERCENTAGE -> {
                result = result / 100.0
                pendingOperation = null
            }
            Operation.CLEAR -> {
                clear()
                result = 0.0
            }
            Operation.BACKSPACE -> {
                result = newOperand
            }
            else -> {
                pendingOperation = newOperation
            }
        }

        operand = result
        return result
    }

    fun clear() {
        operand = 0.0
        pendingOperation = null
    }
}